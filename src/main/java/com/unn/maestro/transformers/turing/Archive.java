package com.unn.maestro.transformers.turing;

import com.unn.common.dataset.Dataset;
import com.unn.common.utils.Serializer;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class Archive {
    UUID uuid;

    public Archive() {
        this.uuid = UUID.randomUUID();
    }

    public void init() {
        String folderPath = String.format("./archives/%s", uuid.toString());
        File theDir = new File(folderPath);
        if (!theDir.exists()){
            theDir.mkdirs();
        }
        Serializer.write(TuringConfig.get(), String.format("%s/%s", folderPath, ""), "conf");
    }

    public void save(String program, Dataset dataset, Dataset transform, ArrayList<Integer> validFeatureIndexes) {

    }
}
