package com.unn.maestro.service;

import com.unn.common.operations.AgentRole;
import com.unn.common.server.StandardResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MinerService {
    @POST("/miner/role")
    Call<StandardResponse> setRole(@Body AgentRole role);

    @POST("/miner/ping")
    Call<String> ping();

    @POST("/miner/reset")
    Call<String> reset();
}
