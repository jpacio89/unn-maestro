package com.unn.maestro.transformers.turing;

import com.unn.common.dataset.*;
import com.unn.common.utils.RandomManager;

import java.util.ArrayList;
import java.util.HashSet;

public class EntropyGenerator {

    public EntropyGenerator() {

    }

    public void run() {
        for (int i = TuringConfig.MIN_VAR_COUNT; i <= TuringConfig.MAX_VAR_COUNT; ++i) {
            EntropyState state = new EntropyState();
            Dataset dataset = getRandomDataset(i);
            state.mergeDataset(null, dataset, null);

            for (int j = TuringConfig.MIN_PROGRAM_LENGTH; j < TuringConfig.MAX_PROGRAM_LENGTH; ++j) {
                while (!state.enoughEntropy()) {
                    String program = new ProgramGenerator(j).next();

                    if (!state.hasProgramBeenProcessed(program)) {
                        Dataset transform = transformDataset(program, dataset);
                        ArrayList<Integer> validFeatureIndexes = this.getValidFeatures(state, transform);

                        if (validFeatureIndexes.size() > 0) {
                            state.mergeDataset(program, transform, validFeatureIndexes);
                        } else {
                            state.rejectProgram(program);
                        }
                    } else {
                        state.rejectProgram(program);
                    }

                    System.out.println(String.format("Var count: %d", i));
                    System.out.println(String.format("Program length: %d", j));
                    System.out.println(String.format("Program count: %d", state.getProgramCount()));
                }
                state.resetEntropy();
            }
        }
    }

    private Dataset getRandomDataset(int varCount) {
        Row[] rows = new Row[TuringConfig.DATA_ROW_COUNT];
        for (int i = 0; i < rows.length; ++i) {
            String[] values = new String[varCount];
            for (int j = 0; j < varCount; j++) {
                values[j] = Integer.toString(RandomManager.rand(0, TuringConfig.VAR_MAX_VALUE));
            }
            rows[i] = new Row().withValues(values);
        }
        ArrayList<String> names = new ArrayList<>();
        for (int counter = 0; counter < varCount; ++counter) {
            names.add(String.format("arg-%d", counter));
        }
        return new Dataset()
            .withDescriptor(new DatasetDescriptor()
                    .withHeader(new Header().withNames(names.stream().toArray(String[]::new))))
            .withBody(new Body().withRows(rows));
    }

    private Dataset transformDataset(String program, Dataset dataset) {
        Row[] rows = dataset.getBody().getRows();
        Row[] tRows = new Row[rows.length];

        System.out.println(program);

        for (int i = 0; i < rows.length; ++i) {
            Row r = rows[i];
            tRows[i] = new BrainfuckInterpreter()
                .interpret(program, r.getValues())
                .toRow(TuringConfig.MAX_TVAR_COUNT);

            //System.out.print(i);
            //System.out.print(Arrays.toString(rows[i].getValues()));
            //System.out.println(Arrays.toString(tRows[i].getValues()));
            // TODO: improvement -> if already transformed rows fail to reach acceptance criteria, break loop
        }

        ArrayList<String> names = new ArrayList<>();
        for (int counter = 0; counter < TuringConfig.MAX_TVAR_COUNT; ++counter) {
            names.add(String.format("booster-%d", counter));
        }

        return new Dataset()
            .withDescriptor(new DatasetDescriptor()
                .withHeader(new Header()
                    .withNames(names.stream()
                        .toArray(String[]::new))))
            .withBody(new Body().withRows(tRows));
    }

    private ArrayList<Integer> getValidFeatures(EntropyState state, Dataset transform) {
        ArrayList<Integer> validTColumns = new ArrayList<>();
        HashSet<Integer> ignoreTColumns = new HashSet<>();
        int tColumnCount = transform.getDescriptor().getHeader().getNames().length;
        for (int i = 0; i < tColumnCount; ++i) {
            if (ignoreTColumns.contains(i)) {
                continue;
            }
            boolean isIntra = checkIntraAcceptance(transform, i);
            if (!isIntra) {
                continue;
            }
            boolean isInter = true;
            ArrayList<EntropyState.DatasetHolder> datasets = state.getDatasets();
            for (EntropyState.DatasetHolder dataset : datasets) {
                isInter = checkInterAcceptance(dataset.dataset, transform, i);
                if (!isInter) {
                    break;
                }
            }
            if (!isInter) {
                continue;
            }
            validTColumns.add(i);
            for (int j = i + 1; j < tColumnCount; ++j) {
                if (ignoreTColumns.contains(j)) {
                    continue;
                }
                boolean isCheck = checkInterAcceptance(transform, transform, j);
                if (!isCheck) {
                    ignoreTColumns.add(j);
                }
            }
        }
        return validTColumns;
    }

    private boolean checkInterAcceptance(Dataset dataset, Dataset transform, int columnIndex) {
        Header h = dataset.getDescriptor().getHeader();
        int columnCount = h.getNames().length;
        Row[] rows = dataset.getBody().getRows();
        Row[] tRows = transform.getBody().getRows();
        boolean accept = true;

        for (int i = 0; i < columnCount; ++i) {
            if (!accept) {
                break;
            }
            double diffSum = 0.0;
            for (int j = 0; j < rows.length; ++j) {
                Row row = rows[j];
                Row tRow = tRows[j];
                int val = Integer.parseInt(row.getValues()[i]);
                int tVal = Integer.parseInt(tRow.getValues()[columnIndex]);
                diffSum += Math.abs(val - tVal) / TuringConfig.VAR_MAX_VALUE;
            }
            diffSum = diffSum * 100 / rows.length;
            accept = diffSum >= TuringConfig.MIN_DIFFERENCE_ABSOLUTE;
        }

        return accept;
    }

    private boolean checkIntraAcceptance(Dataset transform, int columnIndex) {
        HashSet<String> cache = new HashSet<>();
        Row[] rows = transform.getBody().getRows();
        for (Row row : rows) {
            cache.add(row.getValues()[columnIndex]);
        }
        double ratio = cache.size() * 100 / (TuringConfig.VAR_MAX_VALUE + 1.0);
        return ratio >= TuringConfig.INTRA_ACCEPTANCE_RATIO;
    }
}
