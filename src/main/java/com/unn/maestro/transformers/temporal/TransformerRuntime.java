package com.unn.maestro.transformers.temporal;

import com.unn.common.dataset.*;
import com.unn.common.server.NetworkUtils;
import com.unn.common.server.services.DatacenterService;
import com.unn.common.utils.CSVHelper;
import com.unn.common.utils.MultiplesHashMap;
import com.unn.common.utils.Utils;
import com.unn.maestro.transformers.Transformer;
import javafx.util.Pair;
import retrofit2.Call;

import java.io.IOException;
import java.util.*;

public class TransformerRuntime extends Transformer {
    int POOL_SIZE = 100;
    int MEMORY_ROW_COUNT = 5;

    MultiplesHashMap<String, Row> rowContainer;
    HashMap<String, MemoryHolder> holders;
    Transformer transformer;
    Square sd;

    public TransformerRuntime(Transformer transformer) {
        this.transformer = transformer;
        this.holders = new HashMap<>();
        this.rowContainer = new MultiplesHashMap();
    }

    public void run() {
        while (true) {
            ArrayList<String> namespaces = getAllNamespaces();
            // TODO: only first time
            List<String> tNamespaces = this.transformer.init(namespaces);
            namespaces.forEach(namespace -> {
                if (!this.holders.containsKey(namespace)) {
                    this.holders.put(namespace, new MemoryHolder(namespace));
                }
                MemoryHolder holder = this.holders.get(namespace);
                while (true) {
                    Dataset dataset = getNamespaceData(namespace, holder.getMaxProcessedTime());
                    if (dataset == null || dataset.size() == 0) {
                        break;
                    }
                    this.addToPool(holder, dataset);
                    for (Row row : dataset.getBody().getRows()) {
                        int primer = Integer.parseInt(row.getValues()[1]);
                        for (String tNamespace : tNamespaces) {
                            // TODO: ignore if transformed primer already added to the pool
                            Pair<Integer, Row> item = this.transformer.process(tNamespace, primer);
                            if (item != null) {
                                this.rowContainer.put(tNamespace, item.getValue());
                            }
                        }
                    }

                    this.transformer.onDataset(dataset);
                    Dataset publishable =
                    // TODO: missing holder
                    // this.processDataset(holder, dataset);
                }
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void processDataset(String tNamespace, MemoryHolder holder) {
        DatasetDescriptor tDescriptor = this.transformer.getDescriptor(tNamespace);

        // TODO: move this elsewhere
        if (holder.getMaxProcessedTime() <= 0) {
            NetworkUtils.registerAgent(tDescriptor);
        }

        ArrayList<Row> rows = this.rowContainer.get(tNamespace);
        Body body = new Body().withRows(rows.stream().toArray(Row[]::new));
        Dataset tDataset = new Dataset()
            .withBody(body)
            .withDescriptor(tDescriptor);
        NetworkUtils.uploadDataset(tDataset);

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
        //if (holder.getPool().size() > POOL_SIZE) {
            //holder.setPool(holder.getPool().stream().limit(POOL_SIZE)
            //    .collect(Collectors.toCollection(ArrayList::new)));
        //}
    }

    @Override
    public void init() { }
}
