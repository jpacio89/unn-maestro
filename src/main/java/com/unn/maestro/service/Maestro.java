package com.unn.maestro.service;

import com.unn.common.operations.Agent;
import com.unn.common.operations.MiningTarget;
import com.unn.maestro.models.MinerNotification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Maestro {
    final int SLEEP = 1000;
    ArrayList<Agent> agents;
    List<Agent> pendingAgents;
    MiningTarget target;
    MinerMediator minerMediator;

    public Maestro() { }

    public void init() {
        this.agents = new ArrayList<>();
        this.pendingAgents = new ArrayList<>();
        this.minerMediator = new MinerMediator();
        this.minerMediator.init();
        this.pendingAgents = Collections.synchronizedList(new ArrayList<Agent>());
    }

    public void reset() { }

    public void run() {
        for (;;) {
            try {
                this.handlePendingAgents();
                if (this.target != null) {
                    this.minerMediator.work(this.target);
                }
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

    public void handlePendingAgents() {
        synchronized(this.pendingAgents) {
            ArrayList<Agent> handled = new ArrayList<>();
            for (Agent agent : this.pendingAgents) {
                this.minerMediator.addAgent(agent);
                this.agents.add(agent);
                handled.add(agent);
            }
            this.pendingAgents.removeAll(handled);
        }
    }

    public void bindAgent(Agent _agent) {
        if (this.agents.contains(_agent)) {
            return;
        }
        this.pendingAgents.add(_agent);
    }

    public void setNotification(MinerNotification notification) {
        this.minerMediator.setNotification(notification);
    }
}
