package com.unn.maestro.transformers.temporal;

import com.unn.common.dataset.*;
import com.unn.common.utils.MultiplesHashMap;
import com.unn.maestro.transformers.RuntimeContext;
import com.unn.maestro.transformers.Transformer;
import javafx.util.Pair;
import java.util.*;
import java.util.stream.Collectors;

public class ShortTermMemorizer extends Transformer {
    int MEMORY_ROW_COUNT = 5;
    TransformerRuntime runtime;

    public ShortTermMemorizer() { }

    @Override
    public void setRuntime(TransformerRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public RuntimeContext init(List<DatasetDescriptor> namespaces) {
        MultiplesHashMap<String, String> upstreamMapper = new MultiplesHashMap<>();
        HashMap<String, Header> headers = new HashMap<>();
        namespaces.stream()
            .filter(namespace -> namespace != null && !namespace.getNamespace().contains("shortmem"))
            .forEach(namespace -> {
                String tNamespace = String.format("shortmem.%s", namespace.getNamespace());
                initHeader(headers, namespace, tNamespace);
                upstreamMapper.put(tNamespace, namespace.getNamespace());
            });
        RuntimeContext context = new RuntimeContext();
        context.setUpstreamMapper(upstreamMapper);
        context.setHeaders(headers);
        return context;
    }

    @Override
    public Pair<Integer, Row> process(RuntimeContext context, String tNamespace, int primer) {
        Row memRow = new Row();
        ArrayList<String> memValues = new ArrayList<>();
        Row currentRow = this.getRowByPrimer(tNamespace, primer);
        // TODO: improve search for primer
        int currentPrimer = Integer.parseInt(currentRow.getValues()[1]);
        memValues.add(currentRow.getValues()[1]);

        // TODO: what if step is not 1?
        for (int step = 0; step < MEMORY_ROW_COUNT; ++step) {
            int shiftedPrimer = primer - step;
            Row row = this.getRowByPrimer(tNamespace, shiftedPrimer);
            if (row == null) {
                String namespace = context.getUpstreamMapper().get(tNamespace).get(0);
                int unknownCount = (this.runtime.getFeatures(namespace).size() - 2);
                List<String> unknowns = Collections.nCopies(unknownCount, "-");
                memValues.addAll(unknowns);
            } else {
                memValues.addAll(Arrays.stream(row.getValues())
                    .skip(2)
                    .collect(Collectors.toCollection(ArrayList::new)));
            }
        }

        memRow.withValues(memValues.stream().toArray(String[]::new));
        return new Pair<>(currentPrimer, memRow);
    }

    private void initHeader(HashMap<String, Header> headers, DatasetDescriptor descriptor, String transformedNamespace) {
        String[] features = descriptor.getHeader().getNames();
        ArrayList<String> memFeatures = new ArrayList<>();
        memFeatures.add("primer");
        for (int i = 0; i < MEMORY_ROW_COUNT; ++i) {
            for (int j = 0; j < features.length; ++j) {
                String feature = features[j];
                if (feature.equals("id") || feature.equals("primer")) {
                    continue;
                }

                String name = String.format("mem_%s_%d", feature, i + 1);
                memFeatures.add(name);
            }
        }
        headers.put(transformedNamespace, new Header().withNames(memFeatures.stream()
            .toArray(String[]::new)));
    }

    private Row getRowByPrimer(String tNamespace, int primer) {
        String namespace = runtime.getContext().getUpstreamMapper().get(tNamespace).get(0);
        return this.runtime.getRowByPrimer(namespace, primer);
    }
}
