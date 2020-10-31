package com.unn.maestro.service;

import com.unn.common.operations.Agent;
import com.unn.common.operations.AgentRole;
import com.unn.common.operations.MiningTarget;
import com.unn.common.mining.MinerNotification;
import com.unn.common.server.services.MinerService;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MinerMediator {
    final int LAYER_COUNT = 5;
    final int[] LAYER_RATIOS = { 50, 25, 10, 10, 5 };
    final String TYPE = "miner";
    ArrayList<AgentRole> roles;
    HashMap<Agent, MinerNotification> notifications;

    public MinerMediator() { }

    public void init() {
        this.roles = new ArrayList<>();
        this.notifications = new HashMap<>();
    }

    public void addAgent(AgentRole agent) {
        int layer = this.getLayer();
        this.roles.add(agent.withLayer(layer));
    }

    public void removeAgent(AgentRole agent) {
        this.roles.removeIf((AgentRole role) -> role.equals(agent));
    }

    public void work(MiningTarget target) {
        if (this.roles == null) {
            return;
        }
        for (AgentRole role : this.roles) {
            if (!role.isInSync()) {
                // TODO: run in parallel
                role.withTarget(target);
                this.syncRole(role);
            }
            // TODO: reset low performing miners
        }
    }

    private void syncRole(AgentRole role) {
        // TODO: Retrofit cache?
        Agent agent = role.getAgent();
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(String.format("%s://%s:%d",
                    agent.getProtocol(),
                    agent.getHost(),
                    agent.getPort()))
            .addConverterFactory(JacksonConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        MinerService service = retrofit.create(MinerService.class);
        try {
            service.setRole(role).execute();
            role.withSync(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int getLayer() {
        long roleCount = this.roles.size();
        if (roleCount == 0) {
            return 0;
        }
        for (int layer = 0; layer < LAYER_COUNT; ++layer) {
            long count = this.countByLayer(layer);
            long ratio = count * 100 / roleCount;
            if (ratio < LAYER_RATIOS[layer]) {
                return layer;
            }
        }
        return 0;
    }

    private long countByLayer(int _layer) {
        return this.roles.stream().filter((AgentRole role) -> {
            return role.getLayer() == _layer;
        }).count();
    }

    boolean ofType(Agent agent) {
        return TYPE.equals(agent.getType());
    }

    public void setNotification(MinerNotification notification) {
        this.notifications.put(notification.getAgent(), notification);
    }
}
