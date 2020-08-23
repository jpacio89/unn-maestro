package com.unn.maestro.service;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DatacenterService {
    @GET("/dataset/{namespace}/store/raw")
    Call<String> storeDataset(
        @Path("namespace") String namespace,
        @Body String body
    );
}
