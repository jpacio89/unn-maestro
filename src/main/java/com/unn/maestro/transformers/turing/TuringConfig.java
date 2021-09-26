package com.unn.maestro.transformers.turing;

import java.io.Serializable;

public class TuringConfig implements Serializable {
    private static TuringConfig config;

    // Minimum acceptable instruction count inside loops
    public int MIN_LOOP_SIZE = 4;

    // Minimum and Maximum parameter count for each randomly-generated program
    public int MIN_VAR_COUNT = 20;

    public int MAX_VAR_COUNT = 20;

    // Minimum and Maximum instruction count for each randomly-generated program
    public int MIN_PROGRAM_LENGTH = 5;
    public int MAX_PROGRAM_LENGTH = 20;

    // Number of samples used to check if a program is acceptable or not
    public int DATA_ROW_COUNT = 1000;

    // Number of output features for each randomly-generated program
    public int MAX_TVAR_COUNT = 10;

    // Maximum value used in the BF memory registers
    public int VAR_MAX_VALUE = 1;

    // Amount of program  generation outomes (ACCEPT / REJECT)
    public int OUTCOMES_SIZE = 1000;

    // Maximum amount of instructions executed before BF execution is halted (hack to avoid the halting problem)
    public int MAX_CYCLES = 1000;

    // Metrics used to determine if a program is acceptable or not - percentages [0,100]
    public int INTRA_ACCEPTANCE_RATIO = 10;
    public int MIN_DIFFERENCE_ABSOLUTE = 10;

    public TuringConfig() {

    }

    public static TuringConfig get() {
        if (config == null) {
            config = new TuringConfig();
        }
        return config;
    }

    public static void set(TuringConfig _conf) {
        config = _conf;
    }
}
