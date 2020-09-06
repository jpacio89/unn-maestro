package com.unn.maestro.service;

import com.unn.common.operations.Agent;
import com.unn.common.operations.MiningTarget;
import com.unn.common.mining.MinerNotification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Maestro {
    final int MAX_AWOL_TIME = 15000;
    final int SLEEP = 1000;
    ArrayList<Agent> agents;
    List<Agent> pendingAgents;
    MiningTarget target;
    MinerMediator minerMediator;
    HashMap<Agent, Long> aliveTimes;

    public Maestro() { }

    public void init() {
        this.agents = new ArrayList<>();
        this.pendingAgents = new ArrayList<>();
        this.minerMediator = new MinerMediator();
        this.aliveTimes = new HashMap<>();
        this.minerMediator.init();
        this.pendingAgents = Collections.synchronizedList(new ArrayList<Agent>());
    }

    public void reset() { }

    public void run() {
        for (;;) {
            try {
                this.cleanupAgents();
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
                System.out.println(String.format("|Maestro| Binding agent %s", agent.getUuid()));
                this.minerMediator.addAgent(agent);
                this.agents.add(agent);
                this.aliveTimes.put(agent, System.currentTimeMillis());
                handled.add(agent);
            }
            this.pendingAgents.removeAll(handled);
        }
    }

    public void bindAgent(Agent _agent) {
        if (this.agents.contains(_agent)) {
            return;
        }
        System.out.println(String.format("|Maestro| Request to bind agent %s", _agent.getUuid()));
        this.pendingAgents.add(_agent);
    }

    public void setNotification(MinerNotification notification) {
        this.minerMediator.setNotification(notification);
    }

    public void hearbeat(Agent agent) {
        this.aliveTimes.put(agent, System.currentTimeMillis());
    }

    public void cleanupAgents() {
        ArrayList<Agent> toRemove = new ArrayList<>();
        this.agents.stream()
            .filter((Agent agent) -> this.aliveTimes.get(agent) < System.currentTimeMillis() - MAX_AWOL_TIME)
            .forEach((Agent agent) -> {
                toRemove.add(agent);
            });
        for (Agent agent : toRemove) {
            System.out.println(String.format("|Maestro| Removing agent (AWOL) %s", agent.getUuid()));
            this.minerMediator.removeAgent(agent);
            this.aliveTimes.remove(agent);
            this.agents.remove(agent);
        }
    }
}
