package com.unn.maestro.transformers;

import com.unn.common.dataset.Dataset;
import com.unn.common.dataset.DatasetDescriptor;
import com.unn.common.dataset.Row;
import com.unn.maestro.transformers.temporal.TransformerRuntime;
import javafx.util.Pair;

import java.util.List;

public abstract class Transformer {
    public Transformer() {}
    public abstract List<String> init(List<String> namespaces);
    public abstract Pair<Integer, Row> process(String tNamespace, int primer);
    public abstract DatasetDescriptor getDescriptor(String tNamespace);
}
