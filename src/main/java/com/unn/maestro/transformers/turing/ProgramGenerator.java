package com.unn.maestro.transformers.turing;

import com.unn.common.utils.RandomManager;

import java.util.ArrayList;
import java.util.Arrays;

public class ProgramGenerator {

    public ProgramGenerator() {

    }

    public String next() {
        int programLength = 10;
        int minLoopSize = 4;
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
                if (i > programLength - minLoopSize) {
                    i--;
                    continue;
                }
                builder.append(el);
                depth++;
                lastLoopStart = i;
            } else if (el == ']') {
                if (i - lastLoopStart > minLoopSize && depth > 0) {
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
