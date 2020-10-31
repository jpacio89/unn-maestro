package com.unn.maestro.service;

import com.unn.common.mining.MiningReport;
import com.unn.common.operations.Agent;
import com.unn.common.operations.AgentRole;
import com.unn.common.operations.MiningTarget;
import com.unn.common.mining.MinerNotification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Maestro {
    final int MAX_AWOL_TIME = 30000;
    final int SLEEP = 1000;
    ArrayList<AgentRole> agents;
    List<AgentRole> pendingAgents;
    MiningTarget target;
    MinerMediator minerMediator;
    HashMap<AgentRole, Long> aliveTimes;
    HashMap<AgentRole, MiningReport> reports;

    public Maestro() { }

    public void init() {
        this.agents = new ArrayList<>();
        this.pendingAgents = new ArrayList<>();
        this.minerMediator = new MinerMediator();
        this.aliveTimes = new HashMap<>();
        this.reports = new HashMap<>();
        this.minerMediator.init();
        this.pendingAgents = Collections.synchronizedList(new ArrayList<>());
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
            ArrayList<AgentRole> handled = new ArrayList<>();
            for (AgentRole agent : this.pendingAgents) {
                System.out.println(String.format("|Maestro| Binding agent %s", agent.getAgent().getUuid()));
                this.minerMediator.addAgent(agent);
                this.agents.add(agent);
                this.aliveTimes.put(agent, System.currentTimeMillis());
                handled.add(agent);
            }
            this.pendingAgents.removeAll(handled);
        }
    }

    public void bindAgentRole(AgentRole _agent) {
        if (this.agents.contains(_agent)) {
            return;
        }
        System.out.println(String.format("|Maestro| Request to bind agent %s", _agent.getAgent().getUuid()));
        this.pendingAgents.add(_agent);
    }

    public void setNotification(MinerNotification notification) {
        this.minerMediator.setNotification(notification);
    }

    public void hearbeat(AgentRole agent) {
        System.out.println(String.format("|Maestro| Heartbeat %s", agent.getAgent().getUuid()));
        this.aliveTimes.put(agent, System.currentTimeMillis());
    }

    public void cleanupAgents() {
        ArrayList<AgentRole> toRemove = new ArrayList<>();
        this.agents.stream()
            .filter((AgentRole agent) -> !this.reports.containsKey(agent) &&
                this.aliveTimes.get(agent) < System.currentTimeMillis() - MAX_AWOL_TIME)
            .forEach((AgentRole agent) -> {
                toRemove.add(agent);
            });
        for (AgentRole agent : toRemove) {
            System.out.println(String.format("|Maestro| Removing agent (AWOL) %s", agent.getAgent().getUuid()));
            this.minerMediator.removeAgent(agent);
            this.aliveTimes.remove(agent);
            this.agents.remove(agent);
        }
    }

    public void storeMiningReport(MiningReport report) {
        this.reports.put(report.getRole(), report);
        this.printReports();
    }

    public void printReports() {
        String s = this.reports.entrySet().stream()
            .map(entry -> {
                AgentRole role = entry.getKey();
                String uuid = role.getAgent().getUuid();
                int layer = role.getLayer();
                String report = entry.getValue().toString();
                return String.format("%s (layer %d)\n%s\n\n", uuid, layer, report);
            })
            .reduce("", String::concat);
        System.out.println(s);
    }
}
