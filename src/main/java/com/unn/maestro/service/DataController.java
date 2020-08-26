package com.unn.maestro.service;

import com.google.gson.Gson;
import com.unn.maestro.Config;
import com.unn.maestro.models.*;

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
        get("/datacenter/locator", (request, response) -> {
            DatacenterLocator locator = new DatacenterLocator()
                .withProtocol(Config.DATACENTER_PROTOCOL)
                .withHost(Config.DATACENTER_HOST)
                .withPort(Config.DATACENTER_PORT);
            return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, null, locator));
        });

        post("/agent/register", (request, response) -> {
            Agent agent = new Gson().fromJson(request.body(), Agent.class);
            maestro.bindAgent(agent);
            return SUCCESS;
        });

        post("/maestro/predictor", (request, response) -> {
            PredictorDescriptor predictor = new Gson().fromJson(request.body(), PredictorDescriptor.class);
            maestro.bindPredictor(predictor);
            return SUCCESS;
        });
    }

}
