package com.unn.maestro.transformers;

import com.unn.common.dataset.Dataset;

public abstract class Transformer {

    public Transformer() {}

    public abstract void init();

    public abstract void run();
}
