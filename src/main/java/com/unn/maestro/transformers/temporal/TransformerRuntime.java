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
import java.util.stream.Collectors;

public class TransformerRuntime {
    MultiplesHashMap<String, Row> rowContainer;
    HashMap<String, MemoryHolder> holders;
    Transformer transformer;
    MultiplesHashMap<String, String> tNamespaces;

    public TransformerRuntime(Transformer transformer) {
        this.transformer = transformer;
        this.holders = new HashMap<>();
        this.rowContainer = new MultiplesHashMap();
    }

    public void run() {
        while (true) {
            ArrayList<String> namespaces = getAllNamespaces();

            if (this.tNamespaces == null) {
                this.tNamespaces = this.transformer.init(namespaces);
            }

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
                    int maxPrimer = 0;

                    for (Row row : dataset.getBody().getRows()) {
                        int primer = Integer.parseInt(row.getValues()[1]);
                        maxPrimer = Math.max(primer, maxPrimer);

                        for (String tNamespace : this.tNamespaces.keys()) {
                            ArrayList<String> upstreamNamespaces = this.tNamespaces.get(tNamespace);

                            if (!upstreamNamespaces.contains(namespace)) {
                                continue;
                            }

                            // TODO: ignore if transformed primer already added to the pool
                            Pair<Integer, Row> item = this.transformer.process(tNamespace, primer);

                            if (item != null) {
                                if (this.rowContainer.containsKey(tNamespace) &&
                                    holder.getMaxProcessedTime() <= 0) {
                                    NetworkUtils.registerAgent(this.transformer.getDescriptor(tNamespace));
                                }
                                this.rowContainer.put(tNamespace, item.getValue());
                            }
                        }
                    }

                    if (dataset.getBody().getRows().length > 0) {
                        holder.setMaxProcessedTime(maxPrimer);
                    }

                    for (String tNamespace : tNamespaces.keys()) {
                        this.processDataset(tNamespace);
                    }
                }
            });

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void processDataset(String tNamespace) {
        ArrayList<Row> rows = this.rowContainer.get(tNamespace);

        if (rows == null || rows.size() == 0) {
            return;
        }

        DatasetDescriptor tDescriptor = this.transformer.getDescriptor(tNamespace);
        Body body = new Body().withRows(rows.stream().toArray(Row[]::new));
        Dataset tDataset = new Dataset()
            .withBody(body)
            .withDescriptor(tDescriptor);
        NetworkUtils.uploadDataset(tDataset);
    }

    public ArrayList<String> getFeatures(String namespace) {
        return holders.get(namespace).getFeatures().stream()
            .collect(Collectors.toCollection(ArrayList::new));
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
}