package com.unn.maestro.service;

import com.unn.maestro.models.Agent;
import com.unn.maestro.models.MinerNotification;
import com.unn.maestro.models.MiningTarget;

import java.util.ArrayList;

public class Maestro {
    final int SLEEP = 5000;
    ArrayList<Agent> agents;
    MiningTarget target;
    MinerMediator minerMediator;

    public Maestro() { }

    public void init() {
        this.agents = new ArrayList<>();
        this.minerMediator = new MinerMediator();
        this.minerMediator.init(this.agents);
    }

    public void run() {
        while (true) {
            try {
                this.minerMediator.work();
                Thread.sleep(SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void bindTarget(MiningTarget _target) {
        // TODO: trigger reset in subordinate agents
        this.target = _target;
    }

    public void bindAgent(Agent _agent) {
        if (!this.agents.contains(_agent)) {
            return;
        }
        this.agents.add(_agent);
    }

    public void setNotification(MinerNotification notification) {
        this.minerMediator.setNotification(notification);
    }
}
