package com.unn.maestro.service;

import com.google.gson.Gson;
import com.unn.maestro.models.*;

import static spark.Spark.get;
import static spark.Spark.post;

public class DataController {
    static final String SUCCESS = new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS));
    static DataService service;

    public DataController() { }


    public static void serve() {
        service = new DataService();
        service.init();

        // Loads a specific openml dataset
        post("/dataset/load/openml/:datasetId", (request, response) -> {
            String datasetId = request.params("datasetId");
            return SUCCESS;
        });

    }

}
