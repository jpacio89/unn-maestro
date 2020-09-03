package com.unn.maestro.service;

import com.unn.maestro.models.Agent;
import com.unn.maestro.models.AgentRole;
import com.unn.maestro.models.MinerNotification;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MinerMediator {
    final int HIDDEN_LAYER_COUNT = 10;
    final String TYPE = "miner";
    ArrayList<AgentRole> roles;
    HashMap<Agent, MinerNotification> notifications;

    public MinerMediator() { }

    public void init(ArrayList<Agent> _agents) {
        this.notifications = new HashMap<>();
        this.assignRoles(_agents);
    }

    public void work() {
        for (AgentRole role : this.roles) {
            if (!role.isInSync()) {
                // TODO: run in parallel
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

    void assignRoles(ArrayList<Agent> _agents) {
        this.roles = new ArrayList<>();
        int count = 0;
        for (Agent agent : _agents) {
            if (!this.ofType(agent)) {
                continue;
            }
            this.roles.add(new AgentRole()
                .withAgent(agent)
                .withLayer(0)
            );
            count++;
        }
        int i = 0;
        for (AgentRole agent : this.roles) {
            int layer = this.getLayer(i, count);
            this.roles.get(i)
                .withLayer(layer);
            i++;
        }
    }

    int getLayer(int index, int count) {
        int half = count / 2;
        if (index < half || HIDDEN_LAYER_COUNT <= 0) {
            return 0;
        }
        int step = half / HIDDEN_LAYER_COUNT;
        if (step == 0) {
            return 0;
        }
        return 1 + ((index - half) / step);
    }

    boolean ofType(Agent agent) {
        return TYPE.equals(agent.getType());
    }

    public void setNotification(MinerNotification notification) {
        this.notifications.put(notification.getAgent(), notification);
    }
}
