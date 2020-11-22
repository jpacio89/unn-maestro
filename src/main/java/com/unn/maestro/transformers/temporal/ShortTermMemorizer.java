package com.unn.maestro.transformers.temporal;

import com.unn.common.dataset.Dataset;
import com.unn.common.dataset.Row;
import com.unn.maestro.transformers.Transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShortTermMemorizer extends Transformer {
    int MEMORY_ROW_COUNT = 5;
    ArrayList<Row> pool;

    public ShortTermMemorizer() {
        this.pool = new ArrayList<>();
    }

    public void run() {
        // TODO: step 1 -> get feature selection?
        // TODO: step 2 -> register listeners?
        // TODO: step 3 -> register listeners?
    }

    @Override
    public void addDaset(Dataset dataset) {
        this.addToPool(dataset);
    }

    private void addToPool(Dataset dataset) {
        List<String> features = Arrays.asList(dataset.getDescriptor().getHeader().getNames());
        int idx = features.indexOf("primer");
        this.pool.addAll(Arrays.asList(dataset.getBody().getRows()));
        this.pool.sort((x, y) -> {
            int xInt = Integer.parseInt(x.getValues()[idx]);
            int yInt = Integer.parseInt(y.getValues()[idx]);
            return xInt - yInt;
        });
        // TODO: crop
    }

    @Override
    public void init() { }
}
