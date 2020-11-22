package com.unn.maestro.transformers;

import com.unn.common.dataset.Dataset;

public abstract class Transformer {

    public Transformer() {}

    public abstract void addDaset(Dataset dataset);

    public abstract void init();
}
