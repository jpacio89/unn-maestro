package com.unn.maestro.service;

import com.unn.maestro.models.Agent;
import com.unn.maestro.models.PredictorDescriptor;

import java.util.ArrayList;

public class Maestro {
    ArrayList<Agent> agents;
    PredictorDescriptor predictor;
    MinerMediator minerMediator;

    public Maestro() { }

    public void init() {
        this.agents = new ArrayList<>();
        this.minerMediator = new MinerMediator();
        this.minerMediator.init(this.agents);
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
