package com.unn.maestro.transformers.turing;

import com.unn.common.boosting.TuringConfig;
import com.unn.common.utils.RandomManager;

import java.util.ArrayList;
import java.util.Arrays;

public class ProgramGenerator {
    TuringConfig config;
    int programLength;

    public ProgramGenerator() {
        this.programLength = 10;
    }

    public ProgramGenerator(int _programLength) {
        this.programLength = _programLength;
    }

    public String next() {
        ArrayList<Character> operators = new ArrayList<>(
            Arrays.asList('>', '<', '+', '-', '[', ']')
        );
        int lastLoopStart = 0;
        int depth = 0;
        StringBuilder builder = new StringBuilder();
        //builder.append(',');
        //builder.append(',');
        for (int i = 0; i < programLength; ++i) {
            char el = RandomManager.getOne(operators);
            if (el == '[') {
                if (i > programLength - TuringConfig.get().MIN_LOOP_SIZE) {
                    i--;
                    continue;
                }
                builder.append(el);
                depth++;
                lastLoopStart = i;
            } else if (el == ']') {
                if (i - lastLoopStart > TuringConfig.get().MIN_LOOP_SIZE && depth > 0) {
                    builder.append(el);
                    depth--;
                } else {
                    i--;
                }
            } else {
                builder.append(el);
            }
        }
        for (int i = 0; i < depth; ++i) {
            builder.append(']');
        }
        //builder.append('.');
        return builder.toString();
    }
}
