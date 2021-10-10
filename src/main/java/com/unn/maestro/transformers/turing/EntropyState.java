package com.unn.maestro.transformers.turing;

import com.unn.common.boosting.TuringConfig;
import com.unn.common.dataset.Dataset;

import java.util.*;

public class EntropyState {
    private enum Outcome { ACCEPT, REJECT };
    HashSet<String> programs;
    HashMap<String, DatasetHolder> selectedPrograms;
    LinkedList<Outcome> outcomes;
    Archive archive;

    public EntropyState(Archive archive) {
        this.programs = new HashSet<>();
        this.selectedPrograms = new HashMap<>();
        this.outcomes = new LinkedList<>();
        this.archive = archive;
    }

    public void mergeDataset(String program, Dataset dataset, Dataset transform, ArrayList<Integer> validFeatureIndexes) {
        this.selectedPrograms.put(program, new DatasetHolder(dataset, validFeatureIndexes));
        this.addOutcome(Outcome.ACCEPT);
        if (program != null) {
            this.archive.save(program, dataset, transform, validFeatureIndexes);
        }
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
        int counter = this.outcomes.size();
        long accepts = this.outcomes.stream()
            .filter(o -> o == Outcome.ACCEPT)
            .count();
        return counter >= TuringConfig.get().OUTCOMES_SIZE && accepts == 0;
    }

    public void resetEntropy() {
        this.outcomes.clear();
    }

    protected void addOutcome(Outcome o) {
        this.outcomes.addLast(o);
        while (this.outcomes.size() > TuringConfig.get().OUTCOMES_SIZE) {
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
