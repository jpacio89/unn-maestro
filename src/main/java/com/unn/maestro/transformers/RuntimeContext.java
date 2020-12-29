package com.unn.maestro.transformers;

import com.unn.common.dataset.Header;
import com.unn.common.utils.MultiplesHashMap;

import java.util.HashMap;

public class RuntimeContext {
    HashMap<String, Header> headers;
    MultiplesHashMap<String, String> upstreamMapper;

    public HashMap<String, Header> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, Header> headers) {
        this.headers = headers;
    }

    public MultiplesHashMap<String, String> getUpstreamMapper() {
        return upstreamMapper;
    }

    public void setUpstreamMapper(MultiplesHashMap<String, String> upstreamMapper) {
        this.upstreamMapper = upstreamMapper;
    }
}
