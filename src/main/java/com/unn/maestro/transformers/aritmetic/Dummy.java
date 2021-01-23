package com.unn.maestro.transformers.aritmetic;

import com.unn.common.dataset.DatasetDescriptor;
import com.unn.common.dataset.Row;
import com.unn.common.transformers.RuntimeContext;
import com.unn.common.transformers.Transformer;
import com.unn.common.transformers.TransformerRuntime;
import javafx.util.Pair;
import java.util.List;

public class Dummy extends Transformer {
    @Override
    public void setRuntime(TransformerRuntime runtime) {
        System.out.println("Testing Dummy compiler");
    }

    @Override
    public RuntimeContext init(List<DatasetDescriptor> namespaces) {
        return null;
    }

    @Override
    public Pair<Integer, Row> process(RuntimeContext context, String tNamespace, int primer) {
        return null;
    }
}
