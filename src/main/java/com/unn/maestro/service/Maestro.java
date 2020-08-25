package com.unn.maestro.service;

import com.unn.maestro.models.Agent;
import com.unn.maestro.models.PredictorDescriptor;

import java.util.ArrayList;

public class Maestro {
    ArrayList<Agent> agents;
    PredictorDescriptor predictor;

    public Maestro() { }

    public void init() {
        this.agents = new ArrayList<>();
    }

    public void run() {
        while (true) {


        }
    }

    public void bindPredictor(PredictorDescriptor _predictor) {
        // TODO: trigger reset in subordinate agents
        this.predictor = _predictor;
    }

    public void bindAgent(Agent _agent) {
        if (!this.agents.contains(_agent)) {
            return;
        }
        this.agents.add(_agent);
    }
}
