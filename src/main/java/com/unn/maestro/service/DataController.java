package com.unn.maestro.service;

import com.google.gson.Gson;
import com.unn.maestro.Config;
import com.unn.maestro.models.*;
import com.unn.maestro.models.MinerNotification;

import static spark.Spark.get;
import static spark.Spark.post;

public class DataController {
    static final String SUCCESS = new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS));
    static Maestro maestro;

    public DataController() { }

    public static void serve() {
        initMaestro();
        initRoutes();
    }

    private static void initMaestro() {
        maestro = new Maestro();
        new Thread(new Runnable() {
            @Override
            public void run() {
                maestro.run();
            }
        }).start();
    }

    private static void initRoutes() {
        // NOTE: subordinated agents report to a Maestro instance and get Datacenter location from it
        get("/datacenter/origin", (request, response) -> {
            DatacenterOrigin locator = new DatacenterOrigin()
                .withProtocol(Config.DATACENTER_PROTOCOL)
                .withHost(Config.DATACENTER_HOST)
                .withPort(Config.DATACENTER_PORT);
            return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, null, locator));
        });

        post("/maestro/target", (request, response) -> {
            MiningTarget target = new Gson().fromJson(request.body(), MiningTarget.class);
            maestro.bindTarget(target);
            return SUCCESS;
        });

        post("/agent/register", (request, response) -> {
            Agent agent = new Gson().fromJson(request.body(), Agent.class);
            maestro.bindAgent(agent);
            return SUCCESS;
        });

        post("/agent/miner/action/publish/statistics", (request, response) -> {
            MinerNotification notification = new Gson().fromJson(request.body(), MinerNotification.class);
            maestro.setNotification(notification);
            return SUCCESS;
        });
    }

}
