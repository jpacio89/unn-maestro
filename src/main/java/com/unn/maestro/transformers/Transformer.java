package com.unn.maestro.transformers;

import com.unn.common.dataset.Dataset;
import com.unn.common.dataset.DatasetDescriptor;
import com.unn.common.dataset.Row;
import com.unn.common.utils.MultiplesHashMap;
import com.unn.maestro.transformers.temporal.TransformerRuntime;
import javafx.util.Pair;

import java.util.List;

public abstract class Transformer {
    public Transformer() {}
    public abstract void setRuntime(TransformerRuntime runtime);
    public abstract RuntimeContext init(List<DatasetDescriptor> namespaces);
    public abstract Pair<Integer, Row> process(RuntimeContext context, String tNamespace, int primer);
    public abstract DatasetDescriptor getDescriptor(String tNamespace);
}
