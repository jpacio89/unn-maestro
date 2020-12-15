package com.unn.maestro.transformers.temporal;

import com.unn.common.dataset.*;
import com.unn.common.server.NetworkUtils;
import com.unn.common.server.services.DatacenterService;
import com.unn.common.utils.CSVHelper;
import com.unn.common.utils.Utils;
import com.unn.maestro.transformers.Transformer;
import javafx.util.Pair;
import retrofit2.Call;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ShortTermMemorizer extends Transformer {
    int POOL_SIZE = 100;
    int MEMORY_ROW_COUNT = 5;

    HashMap<String, MemoryHolder> holders;

    public ShortTermMemorizer() {
        this.holders = new HashMap<>();
    }

    public void run() {
        while (true) {
            ArrayList<String> namespaces = getAllNamespaces();
            namespaces.forEach(namespace -> {
                if (namespace == null) {
                    return;
                }
                if (!this.holders.containsKey(namespace)) {
                    this.holders.put(namespace, new MemoryHolder(namespace));
                }
                MemoryHolder holder = this.holders.get(namespace);
                while (true) {
                    Dataset dataset = getNamespaceData(namespace, holder.getMaxProcessedTime());
                    if (dataset == null || dataset.size() == 0) {
                        break;
                    }
                    this.processDataset(holder, dataset);
                }
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void processDataset(MemoryHolder holder, Dataset dataset) {
        this.addToPool(holder, dataset);
        Dataset transDataset = this.produceTransformation(holder);
        if (holder.getMaxProcessedTime() <= 0) {
            NetworkUtils.registerAgent(transDataset.getDescriptor());
        }
        NetworkUtils.uploadDataset(transDataset);
        if (dataset.getBody().getRows().length > 0) {
            holder.setMaxProcessedTime(getLastTime(holder, dataset));
        }
    }

    private Dataset getNamespaceData(String namespace, int startTime) {
        DatacenterService service = Utils.getDatacenter();
        Dataset dataset = null;
        try {
            dataset = new CSVHelper().parse(service.getNamespaceData(namespace, startTime).execute().body());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataset;
    }

    private ArrayList<String> getAllNamespaces() {
        DatacenterService service = Utils.getDatacenter();
        Call<ArrayList<String>> call = service.getNamespaces();
        try {
            ArrayList<String> namespaces = call.execute().body();
            return namespaces;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getLastTime(MemoryHolder holder, Dataset dataset) {
        String[] memFeatures = getMemoryFeatures(holder);
        ArrayList<String> features = Arrays.stream(memFeatures).collect(Collectors.toCollection(ArrayList::new));
        int index = features.indexOf("primer");
        Row[] rows = dataset.getBody().getRows();
        return Integer.parseInt(rows[rows.length - 1].getValues()[index]);
    }

    private Dataset produceTransformation(MemoryHolder holder) {
        String[] memFeatures = getMemoryFeatures(holder);
        ArrayList<Row> rows = new ArrayList<>();
        for (int i = holder.getPool().size() - 1; i >= 0; i--) {
            Pair<Integer, Row> entry = holder.getPool().get(i);
            Integer time = entry.getKey();
            if (time <= holder.getMaxProcessedTime()) {
                break;
            }
            Row memRow = new Row();
            ArrayList<String> memValues = new ArrayList<>();
            for (int j = 0; j < MEMORY_ROW_COUNT; ++j) {
                if (i - j - 1 < 0) {
                    List<String> unknowns = Collections.nCopies(holder.getFeatures().size() - 2, "-");
                    memValues.addAll(unknowns);
                } else {
                    Pair<Integer, Row> memEntry = holder.getPool().get(i - j - 1);
                    Row row = memEntry.getValue();
                    if (j == 0) {
                        // TODO: improve search for primer
                        memValues.add(row.getValues()[1]);
                    }
                    memValues.addAll(Arrays.stream(row.getValues())
                        .skip(2)
                        .collect(Collectors.toCollection(ArrayList::new)));
                }
            }
            if (memValues.size() != memFeatures.length) {
                System.err.println("Mismatch " + i);
            }
            memRow.withValues(memValues.stream().toArray(String[]::new));
            rows.add(memRow);
        }
        DatasetDescriptor descriptor = new DatasetDescriptor()
            .withNamespace(String.format("shortmem.%s", holder.getNamespace()));
        descriptor.withHeader(new Header().withNames(memFeatures));
        Dataset dataset = new Dataset()
            .withDescriptor(descriptor)
            .withBody(new Body().withRows(rows.stream().toArray(Row[]::new)));
        return dataset;
    }

    private String[] getMemoryFeatures(MemoryHolder holder) {
        ArrayList<String> memFeatures = new ArrayList<>();
        memFeatures.add("primer");
        for (int i = 0; i < MEMORY_ROW_COUNT; ++i) {
            for (int j = 0; j < holder.getFeatures().size(); ++j) {
                String feature = holder.getFeatures().get(j);
                if (feature.equals("id") || feature.equals("primer")) {
                    continue;
                }
                String name = String.format("mem_%s_%d", feature, i + 1);
                memFeatures.add(name);
            }
        }
        return memFeatures.stream().toArray(String[]::new);
    }

    private boolean containsRow(MemoryHolder holder, int primerIndex, Row row) {
        // TODO: improve performance
        return holder.getPool().stream()
            .filter(rowEntry -> rowEntry.getKey() == Integer.parseInt(row.getValues()[primerIndex]))
            .count() > 0;
    }

    private void addToPool(MemoryHolder holder, Dataset dataset) {
        holder.setFeatures(Arrays.asList(dataset.getDescriptor().getHeader().getNames()));
        int primerIndex = holder.getFeatures().indexOf("primer");
        Arrays.stream(dataset.getBody().getRows()).forEach(row -> {
            if (!containsRow(holder, primerIndex, row)) {
                int timer = Integer.parseInt(row.getValues()[primerIndex]);
                holder.getPool().add(new Pair<>(timer, row));
            }
        });
        holder.getPool().sort(Comparator.comparingInt(Pair::getKey));
        if (holder.getPool().size() > POOL_SIZE) {
            holder.setPool(holder.getPool().stream().limit(POOL_SIZE)
                .collect(Collectors.toCollection(ArrayList::new)));
        }
    }

    @Override
    public void init() { }
}
