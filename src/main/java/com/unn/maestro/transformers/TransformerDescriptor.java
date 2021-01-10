package com.unn.maestro.transformers;

public class TransformerDescriptor {
    private String code;

    public TransformerDescriptor() { }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public TransformerDescriptor withCode(String code) {
        this.code = code;
        return this;
    }


}
