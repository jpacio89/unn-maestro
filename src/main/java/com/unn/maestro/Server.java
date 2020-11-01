package com.unn.maestro;

import com.google.gson.Gson;
import com.unn.common.globals.NetworkConfig;
import com.unn.common.mining.MiningReport;
import com.unn.common.operations.Agent;
import com.unn.common.operations.AgentRole;
import com.unn.common.operations.DatacenterOrigin;
import com.unn.common.operations.MiningTarget;
import com.unn.common.server.StandardResponse;
import com.unn.common.server.StatusResponse;
import com.unn.common.utils.SparkUtils;
import com.unn.common.mining.MinerNotification;
import com.unn.maestro.service.Maestro;

import static spark.Spark.*;

public class Server {
    static final String SUCCESS = new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS));
    static Maestro maestro;

    public Server() { }

    public static void serve() {
        initMaestro();
        initRoutes();
    }

    private static void initMaestro() {
        maestro = new Maestro();
        maestro.init();
        new Thread(new Runnable() {
            @Override
            public void run() {
                maestro.run();
            }
        }).start();
    }

    private static void initRoutes() {
        port(NetworkConfig.MAESTRO_PORT);
        SparkUtils.enableCORS("*", "POST, GET, OPTIONS", null);

        post("/brain/reset", (request, response) -> {
            maestro.reset();
            return SUCCESS;
        });

        // NOTE: subordinated agents report to a Maestro instance and get Datacenter location from it
        get("/datacenter/origin", (request, response) -> {
            DatacenterOrigin locator = new DatacenterOrigin()
                .withProtocol(NetworkConfig.DATACENTER_PROTOCOL)
                .withHost(NetworkConfig.DATACENTER_HOST)
                .withPort(NetworkConfig.DATACENTER_PORT);
            return new Gson().toJson(locator);
        });

        post("/maestro/target", (request, response) -> {
            MiningTarget target = new Gson().fromJson(request.body(), MiningTarget.class);
            maestro.bindTarget(target);
            return SUCCESS;
        });

        post("/agent/register", (request, response) -> {
            Agent agent = new Gson().fromJson(request.body(), Agent.class);
            maestro.bindAgentRole(new AgentRole().withAgent(agent));
            return SUCCESS;
        });

        post("/agent/heartbeat", (request, response) -> {
            AgentRole agent = new Gson().fromJson(request.body(), AgentRole.class);
            maestro.hearbeat(agent);
            return SUCCESS;
        });

        post("/mining/report", (request, response) -> {
            MiningReport report = new Gson().fromJson(request.body(), MiningReport.class);
            maestro.storeMiningReport(report);
            return SUCCESS;
        });

        post("/agent/miner/action/publish/statistics", (request, response) -> {
            MinerNotification notification = new Gson().fromJson(request.body(), MinerNotification.class);
            maestro.setNotification(notification);
            return SUCCESS;
        });

        post("/mining/deadend", (request, response) -> {
            AgentRole role = new Gson().fromJson(request.body(), AgentRole.class);
            maestro.deadEnd(role);
            return SUCCESS;
        });

    }

}
