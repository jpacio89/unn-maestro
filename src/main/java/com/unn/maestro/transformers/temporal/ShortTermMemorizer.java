package com.unn.maestro.transformers.temporal;

import com.unn.common.dataset.*;
import com.unn.maestro.transformers.Transformer;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class ShortTermMemorizer extends Transformer {
    int POOL_SIZE = 100;
    int MEMORY_ROW_COUNT = 5;

    List<String> features;
    ArrayList<Pair<Integer, Row>> pool;
    int maxProcessedTime = -1;

    public ShortTermMemorizer() {
        this.pool = new ArrayList<>();
    }

    public void run() {
        // TODO: step 1 -> get feature selection
        // TODO: step 2 -> register listeners
        // TODO: step 3 -> register transformed dataset
    }

    @Override
    public void addDaset(Dataset dataset) {
        this.addToPool(dataset);
        Dataset dataset = this.produceTransformation();
        // TODO: push dataset to datacenter
    }

    private Dataset produceTransformation() {
        String[] memFeatures = getMemoryFeatures();
        ArrayList<Row> rows = new ArrayList<>();
        for (int i = pool.size() - 1; i >= 0; i--) {
            Pair<Integer, Row> entry = pool.get(i);
            Integer time = entry.getKey();
            if (time <= maxProcessedTime) {
                break;
            }
            Row memRow = new Row();
            // TODO: fix cases where i - j - 1 < 0
            ArrayList<String> memValues = new ArrayList<>();
            for (int j = 0; j < MEMORY_ROW_COUNT; ++j) {
                Pair<Integer, Row> memEntry = pool.get(i - j - 1);
                Row row = memEntry.getValue();
                memValues.addAll(Arrays.stream(row.getValues())
                    .collect(Collectors.toCollection(ArrayList::new)));
            }
            memRow.withValues(memValues.stream().toArray(String[]::new));
            rows.add(memRow);
        }
        DatasetDescriptor descriptor = new DatasetDescriptor();
        descriptor.withHeader(new Header().withNames(memFeatures));
        Dataset dataset = new Dataset()
            .withDescriptor(descriptor)
            .withBody(new Body().withRows(rows.stream().toArray(Row[]::new)));
    }

    private String[] getMemoryFeatures() {
        ArrayList<String> memFeatures = new ArrayList<>();
        for (int i = 0; i < MEMORY_ROW_COUNT; ++i) {
            for (int j = 0; j < this.features.size(); ++j) {
                String feature = features.get(j);
                String name = String.format("mem_%s_%d", feature, i + 1);
                memFeatures.add(name);
            }
        }
        return memFeatures.stream().toArray(String[]::new);
    }

    private boolean containsRow(int primerIndex, Row row) {
        // TODO: improve performance
        return this.pool.stream()
            .filter(rowEntry -> rowEntry.getKey() == Integer.parseInt(row.getValues()[primerIndex]))
            .count() > 0;
    }

    private void addToPool(Dataset dataset) {
        this.features = Arrays.asList(dataset.getDescriptor().getHeader().getNames());
        int primerIndex = features.indexOf("primer");
        Arrays.stream(dataset.getBody().getRows()).forEach(row -> {
            if (!containsRow(primerIndex, row)) {
                int timer = Integer.parseInt(row.getValues()[primerIndex]);
                this.pool.add(new Pair<>(timer, row));
            }
        });
        this.pool.sort(Comparator.comparingInt(Pair::getKey));
        if (this.pool.size() > POOL_SIZE) {
            this.pool = this.pool.stream().limit(POOL_SIZE)
                .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    @Override
    public void init() { }
}
