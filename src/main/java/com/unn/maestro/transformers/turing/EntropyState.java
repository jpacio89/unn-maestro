package com.unn.maestro.transformers.turing;

import com.unn.common.dataset.Dataset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class EntropyState {
    HashSet<String> programs;
    HashMap<String, DatasetHolder> selectedPrograms;

    public EntropyState() {
        this.programs = new HashSet<>();
        this.selectedPrograms = new HashMap<>();
    }

    public void mergeDataset(String program, Dataset dataset, ArrayList<Integer> validFeatureIndexes) {
        this.selectedPrograms.put(program, new DatasetHolder(dataset, validFeatureIndexes));
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
