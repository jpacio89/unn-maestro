package com.unn.maestro.transformers.temporal;

import com.unn.common.dataset.DatasetDescriptor;
import com.unn.common.dataset.Row;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class MemoryHolder {
    List<String> features;
    DatasetDescriptor memDescriptor;
    ArrayList<Pair<Integer, Row>> pool;
    int maxProcessedTime = -1;

    public MemoryHolder() {
        this.pool = new ArrayList<>();
    }

    public DatasetDescriptor getMemDescriptor() {
        return memDescriptor;
    }

    public void setMemDescriptor(DatasetDescriptor memDescriptor) {
        this.memDescriptor = memDescriptor;
    }

    public ArrayList<Pair<Integer, Row>> getPool() {
        return pool;
    }

    public void setPool(ArrayList<Pair<Integer, Row>> pool) {
        this.pool = pool;
    }

    public int getMaxProcessedTime() {
        return maxProcessedTime;
    }

    public void setMaxProcessedTime(int maxProcessedTime) {
        this.maxProcessedTime = maxProcessedTime;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }
}
