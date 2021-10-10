package com.unn.maestro.transformers.turing;

import com.unn.common.boosting.TuringConfig;
import com.unn.common.dataset.Dataset;
import com.unn.common.utils.Serializer;
import com.unn.common.utils.Utils;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

public class Archive implements Serializable {
    UUID uuid;
    String folderPath;

    HashSet<String> memory = new HashSet<>();

    public Archive() {
        this.uuid = UUID.randomUUID();
        this.folderPath = String.format("./archives/%s", uuid.toString());
    }

    public Archive(String _uuid) {
        this.uuid = UUID.fromString(_uuid);
        this.folderPath = String.format("./archives/%s", uuid.toString());
    }

    public void init() {
        File theDir = new File(folderPath);
        if (!theDir.exists()){
            theDir.mkdirs();
        }
        String iniPath = String.format("%s/%s", folderPath, "_archive");
        File ini = new File(iniPath);
        if (ini.exists()) {
            this.preload();
            return;
        }
        Serializer.write(TuringConfig.get(), iniPath, "conf");
    }

    public void save(String program, Dataset dataset,
                     Dataset transform, ArrayList<Integer> validFeatureIndexes) {
        String hash = this.getHashByProgram(program);
        if (hash == null) {
            return;
        }
        Serializer.write(
                new ArchiveRecord(program, dataset, transform, validFeatureIndexes),
                String.format("%s/%s", folderPath, hash),
                "booster");
    }

    public void preload() {
        File archiveDir = new File(this.folderPath);
        File[] files = archiveDir.listFiles((File dir, String name) ->
                name.endsWith(".booster"));
        this.memory.clear();
        Arrays.stream(files).forEach(file -> this.memory.add(
                file.getName().replace(".booster", "")));
    }

    public boolean hasProgram(String program) {
        String hash = this.getHashByProgram(program);
        if (hash == null) {
            return false;
        }
        return this.memory.contains(hash);
    }

    public String getHashByProgram(String program) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        byte[] encodedhash = digest.digest(
                program.getBytes(StandardCharsets.UTF_8));
        String hash = Utils.bytesToHex(encodedhash);
        return hash;
    }

    private class ArchiveRecord implements Serializable {
        String program;
        Dataset dataset;
        Dataset transform;
        ArrayList<Integer> validFeatureIndexes;

        public ArchiveRecord(String _program, Dataset _dataset,
                             Dataset _transform, ArrayList<Integer> _validFeatureIndexes) {
            this.program = _program;
            this.dataset = _dataset;
            this.transform = _transform;
            this.validFeatureIndexes = _validFeatureIndexes;
        }
    }
}
