package com.unn.maestro.transformers.turing;

import com.unn.common.dataset.Dataset;

import java.util.*;

public class EntropyState {
    private final int OUTCOMES_SIZE = 10;
    private enum Outcome { ACCEPT, REJECT };
    HashSet<String> programs;
    HashMap<String, DatasetHolder> selectedPrograms;
    LinkedList<Outcome> outcomes;

    public EntropyState() {
        this.programs = new HashSet<>();
        this.selectedPrograms = new HashMap<>();
        this.outcomes = new LinkedList<>();
    }

    public void mergeDataset(String program, Dataset dataset, ArrayList<Integer> validFeatureIndexes) {
        this.selectedPrograms.put(program, new DatasetHolder(dataset, validFeatureIndexes));
        this.addOutcome(Outcome.ACCEPT);
    }

    public int getProgramCount() {
        return this.selectedPrograms.size();
    }

    public ArrayList<DatasetHolder> getDatasets() {
        return new ArrayList<>(this.selectedPrograms.values());
    }

    public boolean hasProgramBeenProcessed(String program) {
        return this.programs.contains(program);
    }

    public void rejectProgram(String program) {
        this.programs.add(program);
        this.addOutcome(Outcome.REJECT);
    }

    public boolean enoughEntropy() {
        final int STOP_RATIO = 20;
        int counter = 0;
        long accepts = this.outcomes.stream()
            .filter(o -> o == Outcome.ACCEPT)
            .count();
        return counter > 0 && (accepts == 0 || counter / accepts > STOP_RATIO);
    }

    protected void addOutcome(Outcome o) {
        this.outcomes.addLast(o);
        while (this.outcomes.size() > OUTCOMES_SIZE) {
            this.outcomes.removeFirst();
        }
    }

    protected class DatasetHolder {
        Dataset dataset;
        ArrayList<Integer> validFeatureIndexes;

        public DatasetHolder(Dataset _dataset, ArrayList<Integer> _validFeatureIndexes) {
            this.dataset = _dataset;
            this.validFeatureIndexes = _validFeatureIndexes;
        }
    }
}
