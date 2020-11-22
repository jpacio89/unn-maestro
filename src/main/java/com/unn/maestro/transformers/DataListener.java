package com.unn.maestro.transformers;

import com.unn.common.dataset.Dataset;

import java.util.ArrayList;

public class DataListener {
    ArrayList<String> namespaces;
    ArrayList<Transformer> transformers;

    public DataListener() {
        this.transformers = new ArrayList<>();
    }

    public void init(ArrayList<Transformer> transformers, ArrayList<String> namespaces) {
        this.transformers = transformers;
        this.transformers.forEach((t) -> t.init());
        this.namespaces = namespaces;
        // TODO: register listener in datacenter
    }

    public DataListener addTransformer(Transformer t) {
        transformers.add(t);
        return this;
    }

    public void processDataset(Dataset dataset) {
        this.transformers.forEach(t -> t.addDaset(dataset));
    }


    public void run() {

    }
}
