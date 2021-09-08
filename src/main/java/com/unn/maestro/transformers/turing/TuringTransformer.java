package com.unn.maestro.transformers.turing;

import com.unn.common.dataset.DatasetDescriptor;
import com.unn.common.dataset.Header;
import com.unn.common.dataset.Row;
import com.unn.common.transformers.RuntimeContext;
import com.unn.common.transformers.Transformer;
import com.unn.common.transformers.TransformerRuntime;
import com.unn.common.utils.MultiplesHashMap;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class TuringTransformer extends Transformer {
    int MEMORY_ROW_COUNT = 5;
    TransformerRuntime runtime;

    public TuringTransformer() { }

    @Override
    public void setRuntime(TransformerRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public RuntimeContext init(List<DatasetDescriptor> namespaces) {
        MultiplesHashMap<String, String> upstreamMapper = new MultiplesHashMap<>();
        HashMap<String, Header> headers = new HashMap<>();
        ArrayList<DatasetDescriptor> turingSources = namespaces.stream()
            .filter(namespace -> namespace != null &&
                !namespace.getNamespace().contains("turing"))
            .sorted(Comparator.comparingInt(Object::hashCode))
            .limit(2)
            .collect(Collectors.toCollection(ArrayList::new));

        String tNamespace = String.format("turing.%s.%s",
            turingSources.get(0).getNamespace(),
            turingSources.get(1).getNamespace());
        initHeader(headers, tNamespace);
        upstreamMapper.put(tNamespace, turingSources.get(0).getNamespace());
        upstreamMapper.put(tNamespace, turingSources.get(1).getNamespace());

        RuntimeContext context = new RuntimeContext();
        context.setUpstreamMapper(upstreamMapper);
        context.setHeaders(headers);
        return context;
    }

    // TODO: implement
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

    private void initHeader(HashMap<String, Header> headers, String transformedNamespace) {
        ArrayList<String> memFeatures = new ArrayList<>();
        memFeatures.add("primer");

        for (int i = 0; i < 10; ++i) {
            String tFeature = String.format("%s_f%d",
                transformedNamespace, i);
            memFeatures.add(tFeature);
        }

        headers.put(transformedNamespace, new Header().withNames(memFeatures.stream()
            .toArray(String[]::new)));
    }

    // TODO: implement
    private Row getRowByPrimer(String tNamespace, int primer) {
        String namespace = runtime.getContext().getUpstreamMapper().get(tNamespace).get(0);
        return this.runtime.getRowByPrimer(namespace, primer);
    }
}
