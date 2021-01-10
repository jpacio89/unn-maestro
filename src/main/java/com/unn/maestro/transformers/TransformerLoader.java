package com.unn.maestro.transformers;

import com.unn.common.server.services.DatacenterService;
import com.unn.common.utils.Utils;
import com.unn.maestro.transformers.temporal.TransformerRuntime;
import retrofit2.Call;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class TransformerLoader {

    public TransformerLoader() {}

    public static ArrayList<TransformerRuntime> load() {
        ArrayList<TransformerDescriptor> tDescriptors = getAllTransformers();
        return tDescriptors.stream()
            .parallel().map((tDescriptor) -> {
                try {
                    return TransformerCompiler.runClass(null);
                } catch(ClassNotFoundException e) {
                    return TransformerCompiler.process(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            })
            .filter(t -> t != null)
            .map(t -> new TransformerRuntime(t))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    private static ArrayList<TransformerDescriptor> getAllTransformers() {
        DatacenterService service = Utils.getDatacenter();
        Call<ArrayList<TransformerDescriptor>> call = service.getTransformers();
        try {
            ArrayList<TransformerDescriptor> transformers = call.execute().body();
            return transformers;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
