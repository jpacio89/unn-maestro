package com.unn.maestro.service;

import com.unn.maestro.Config;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

import java.io.IOException;

public class DataService {
    Retrofit retrofit;
    DatacenterService service;

    public DataService() { }

    public void init() {
        this.retrofit = new Retrofit.Builder()
            .baseUrl(String.format("%s://%s:%s",
                Config.DATACENTER_PROTOCOL,
                Config.DATACENTER_HOST,
                Config.DATACENTER_PORT))
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        this.service = retrofit.create(DatacenterService.class);
    }

}
