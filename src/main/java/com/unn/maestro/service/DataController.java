package com.unn.maestro.service;

import com.google.gson.Gson;
import com.unn.maestro.models.*;

import static spark.Spark.get;
import static spark.Spark.post;

public class DataController {
    static final String SUCCESS = new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS));
    static DataService service;
    static Maestro maestro;

    public DataController() { }

    public static void serve() {
        initService();
        initRoutes();
        initMaestro();
    }

    private static void initService() {
        service = new DataService();
        service.init();
    }

    private static void initMaestro() {
        maestro = new Maestro();
        maestro.run();
    }

    private static void initRoutes() {

    }

}
