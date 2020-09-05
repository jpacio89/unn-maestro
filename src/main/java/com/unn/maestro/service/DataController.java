package com.unn.maestro.service;

import com.google.gson.Gson;
import com.unn.maestro.Config;
import com.unn.maestro.models.*;
import com.unn.maestro.models.MinerNotification;

import static spark.Spark.*;

public class DataController {
    static final String SUCCESS = new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS));
    static Maestro maestro;

    public DataController() { }

    private static void enableCORS(final String origin, final String methods, final String headers) {
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.type("application/json");
        });
    }

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
        port(Config.MAESTRO_PORT);
        enableCORS("*", "POST, GET, OPTIONS", null);

        post("/brain/reset", (request, response) -> {
            maestro.reset();
            return SUCCESS;
        });

        // NOTE: subordinated agents report to a Maestro instance and get Datacenter location from it
        get("/datacenter/origin", (request, response) -> {
            DatacenterOrigin locator = new DatacenterOrigin()
                .withProtocol(Config.DATACENTER_PROTOCOL)
                .withHost(Config.DATACENTER_HOST)
                .withPort(Config.DATACENTER_PORT);
            return new Gson().toJson(locator);
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
