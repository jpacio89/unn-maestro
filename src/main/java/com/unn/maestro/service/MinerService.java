package com.unn.maestro.service;

import com.unn.maestro.models.AgentRole;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MinerService {
    @POST("/miner/role")
    Call<String> setRole(@Body AgentRole role);

    @POST("/miner/ping")
    Call<String> ping();

    @POST("/miner/reset")
    Call<String> reset();
}
