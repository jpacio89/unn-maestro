package com.unn.maestro.service;

import com.unn.maestro.models.Agent;
import com.unn.maestro.models.PredictorDescriptor;

import java.util.ArrayList;

public class Maestro {
    final PredictorDescriptor PREDICTOR = new PredictorDescriptor()
        .withFeature("A")
        .withValue("B");
    ArrayList<Agent> agents;

    public Maestro() { }

    public void run() {
        while (true) {
            this.updateAgentList();

        }
    }

    private void updateAgentList() {
        if (this.agents != null) {
            return;
        }
        this.agents = new ArrayList<>();
        int agentCount = 10;
        for (int i = 0; i < agentCount; ++i) {
            this.agents.add(new Agent()
                .withHost("http")
                .withHost("localhost")
                .withPort(9002)
                .withId(1+i));
        }
    }
}
