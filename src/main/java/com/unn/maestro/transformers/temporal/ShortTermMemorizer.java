package com.unn.maestro.transformers.temporal;

import com.unn.common.dataset.*;
import com.unn.common.utils.MultiplesHashMap;
import com.unn.maestro.transformers.Transformer;
import javafx.util.Pair;
import java.util.*;
import java.util.stream.Collectors;

public class ShortTermMemorizer extends Transformer {
    Header header;
    TransformerRuntime runtime;
    List<String> namespaces;
    int MEMORY_ROW_COUNT = 5;
    HashMap<String, Header> headers;
    MultiplesHashMap<String, String> relevantNamespaces;

    public ShortTermMemorizer(TransformerRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public MultiplesHashMap<String, String> init(List<String> namespaces) {
        this.relevantNamespaces = new MultiplesHashMap<>();
        this.namespaces = namespaces;
        namespaces.stream()
            .filter(namespace -> namespace != null && !namespace.contains("shortmem"))
            .forEach(namespace -> {
                String tNamespace = String.format("shortmem.%s", namespace);
                this.relevantNamespaces.put(tNamespace, namespace);
            });
        return this.relevantNamespaces;
    }

    @Override
    public DatasetDescriptor getDescriptor(String tNamespace) {
        this.relevantNamespaces.get(tNamespace)
            .forEach(namespace -> this.initHeader(namespace, tNamespace));
        DatasetDescriptor descriptor = new DatasetDescriptor()
            .withNamespace(tNamespace);
        descriptor.withHeader(this.headers.get(tNamespace));
        return descriptor;
    }

    @Override
    public Pair<Integer, Row> process(String tNamespace, int primer) {
        Row memRow = new Row();
        ArrayList<String> memValues = new ArrayList<>();
        Row currentRow = this.getRowByPrimer(tNamespace, primer);
        // TODO: improve search for primer
        int currentPrimer = Integer.parseInt(currentRow.getValues()[1]);
        memValues.add(currentRow.getValues()[1]);

        // TODO: what if step is not 1?
        for (int step = 0; step < MEMORY_ROW_COUNT; ++step) {
            int index = primer - step;
            if (index < 0) {
                List<String> unknowns = Collections.nCopies(this.header.getNames().length - 2, "-");
                memValues.addAll(unknowns);
            } else {
                Row row = this.getRowByPrimer(tNamespace, index);
                memValues.addAll(Arrays.stream(row.getValues())
                    .skip(2)
                    .collect(Collectors.toCollection(ArrayList::new)));
            }
        }

        if (memValues.size() != this.header.getNames().length) {
            System.err.println("Mismatch " + primer);
        }

        memRow.withValues(memValues.stream().toArray(String[]::new));
        return new Pair<>(currentPrimer, memRow);
    }

    private void initHeader(String namespace, String transformedNamespace) {
        this.headers = new HashMap<>();
        ArrayList<String> features = this.runtime.getFeatures(namespace);
        ArrayList<String> memFeatures = new ArrayList<>();
        memFeatures.add("primer");
        for (int i = 0; i < MEMORY_ROW_COUNT; ++i) {
            for (int j = 0; j < features.size(); ++j) {
                String feature = features.get(j);
                if (feature.equals("id") || feature.equals("primer")) {
                    continue;
                }
                String name = String.format("mem_%s_%d", feature, i + 1);
                memFeatures.add(name);
            }
        }
        this.headers.put(transformedNamespace, new Header().withNames(memFeatures.stream()
            .toArray(String[]::new)));
    }

    private Row getRowByPrimer(String tNamespace, int primer) {
        String namespace = relevantNamespaces.get(tNamespace).get(0);
        return this.runtime.getRowByPrimer(namespace, primer);
    }
}
