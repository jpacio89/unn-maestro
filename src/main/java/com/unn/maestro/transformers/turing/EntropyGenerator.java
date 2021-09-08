package com.unn.maestro.transformers.turing;

import java.util.ArrayList;

public class EntropyGenerator {
    final int MAX_VAR_COUNT = 10;
    final int MAX_PROGRAM_LENGTH = 10;
    final int DATA_ROW_COUNT = 1000;
    final int MAX_TVAR_COUNT = 10;

    public EntropyGenerator() {

    }

    public run() {
        ArrayList<State> states = new ArrayList<State>();
        for (int i = 0; i < MAX_VAR_COUNT; ++i) {

            for (int j = 0; j < MAX_PROGRAM_LENGTH; ++j) {

                while (!this.enoughEntropy()) {
                    String program = new ProgramGenerator().next();
                    ArrayList<Integer[]> dataset = getRandomDataset();
                    ArrayList<Integer[]> transform = transformDataset(program, dataset);

                    if (this.isTransformAcceptable(states, dataset, transform)) {
                        copyTransfomedVars();
                    }
                }
            }
        }
    }

    public boolean enoughEntropy() {
        return false;
    }

    private ArrayList<Integer[]> getRandomDataset() {
        return null;
    }

    private ArrayList<Integer[]> transformDataset(String program, ArrayList<Integer[]> dataset) {
        return null;
    }

    private boolean isTransformAcceptable(ArrayList<Integer[]> dataset, ArrayList<Integer[]> transform) {
        return true;
    }
}
