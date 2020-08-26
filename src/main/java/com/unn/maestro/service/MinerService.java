package com.unn.maestro.service;

import com.unn.maestro.models.AgentRole;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MinerService {
    @GET("/miner/role")
    Call<String> setRole(
        @Body AgentRole role
    );
}
