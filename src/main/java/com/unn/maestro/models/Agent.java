package com.unn.maestro.models;

public class Agent {
    String protocol;
    String host;
    int port;
    int id;

    public Agent() {

    }

    public String getProtocol() {
        return protocol;
    }

    public Agent withProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public String getHost() {
        return host;
    }

    public Agent withHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public Agent withPort(int port) {
        this.port = port;
        return this;
    }

    public int getId() {
        return id;
    }

    public Agent withId(int id) {
        this.id = id;
        return this;
    }
}
