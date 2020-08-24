package com.unn.maestro.models;

public class PredictorDescriptor {
    String feature;
    String value;

    public String getFeature() {
        return feature;
    }

    public PredictorDescriptor withFeature(String feature) {
        this.feature = feature;
        return this;
    }

    public String getValue() {
        return value;
    }

    public PredictorDescriptor withValue(String value) {
        this.value = value;
        return this;
    }
}
