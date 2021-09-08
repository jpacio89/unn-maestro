package com.unn.maestro.transformers.turing;

import com.unn.common.dataset.Dataset;
import com.unn.common.dataset.Row;

public class EntropyGenerator {
    final int MIN_VAR_COUNT = 1;
    final int MAX_VAR_COUNT = 10;
    final int MIN_PROGRAM_LENGTH = 5;
    final int MAX_PROGRAM_LENGTH = 10;
    final int DATA_ROW_COUNT = 1000;
    final int MAX_TVAR_COUNT = 10;

    public EntropyGenerator() {

    }

    public void run() {
        for (int i = MIN_VAR_COUNT; i < MAX_VAR_COUNT; ++i) {
            EntropyState state = new EntropyState();
            Dataset dataset = getRandomDataset();
            state.mergeDataset(null, dataset, validFeatureIndexes);

            for (int j = MIN_PROGRAM_LENGTH; j < MAX_PROGRAM_LENGTH; ++j) {
                while (!this.enoughEntropy()) {
                    String program = new ProgramGenerator().next();

                    if (this.isProgramValid(program)) {
                        Dataset transform = transformDataset(program, dataset);
                        int[] validFeatureIndexes = this.validFeatureCount(state,dataset, transform);
                        if (validFeatureIndexes.length > 0) {
                            state.mergeDataset(program, transform, validFeatureIndexes);
                        } else {
                            this.rejectTransform(program);
                        }
                    }

                    System.out.println(String.format("Var count: %d", i));
                    System.out.println(String.format("Program count: %d", state.getProgramCount()));
                }
            }
        }
    }

    private int[] validFeatureCount(EntropyState state, Dataset dataset, Dataset transform) {
        // TODO: implement
        return new int[0];
    }

    private boolean isProgramValid(String program) {
        // TODO: implement
        return true;
    }

    private void rejectTransform(String program) {
        // TODO: implement
    }

    public boolean enoughEntropy() {
        // TODO: implement
        return false;
    }

    private Dataset getRandomDataset() {
        // TODO: implement
        return null;
    }

    private Dataset transformDataset(String program, Dataset dataset) {
        Row[] rows = dataset.getBody().getRows();
        Dataset t = new Dataset();
        Row[] tRows = new Row[rows.length];
        for (int i = 0; i < rows.length; ++i) {
            Row r = rows[i];
            tRows[i] = new BrainfuckInterpreter()
                .interpret(program, r.getValues())
                .toRow(MAX_TVAR_COUNT);

        }

        // TODO: implement
        return null;
    }

    private boolean isTransformAcceptable(EntropyState state, Dataset dataset, Dataset transform) {
        // TODO: implement
        return true;
    }
}
