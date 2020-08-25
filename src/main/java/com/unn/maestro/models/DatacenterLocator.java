package com.unn.maestro.models;

public class DatacenterLocator {
    String protocol;
    String host;
    int port;

    public DatacenterLocator() {

    }

    public String getProtocol() {
        return protocol;
    }

    public DatacenterLocator withProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public String getHost() {
        return host;
    }

    public DatacenterLocator withHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public DatacenterLocator withPort(int port) {
        this.port = port;
        return this;
    }
}
