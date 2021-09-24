package com.unn.maestro.transformers.turing;

public class TuringConfig {
    // Minimum acceptable instruction count inside loops
    public static final int MIN_LOOP_SIZE = 4;

    // Minimum and Maximum parameter count for each randomly-generated program
    public static final int MIN_VAR_COUNT = 5;
    public static final int MAX_VAR_COUNT = 5;

    // Minimum and Maximum instruction count for each randomly-generated program
    public static final int MIN_PROGRAM_LENGTH = 5;
    public static final int MAX_PROGRAM_LENGTH = 20;

    // Number of samples used to check if a program is acceptable or not
    public static final int DATA_ROW_COUNT = 1000;

    // Number of output features for each randomly-generated program
    public static final int MAX_TVAR_COUNT = 10;

    // Maximum value used in the BF memory registers
    public static final int VAR_MAX_VALUE = 127;

    // Amount of program  generation outomes (ACCEPT / REJECT)
    public static final int OUTCOMES_SIZE = 1000;

    // Maximum amount of instructions executed before BF execution is halted (hack to avoid the halting problem)
    public static final int MAX_CYCLES = 1000;

    // Metrics used to determine if a program is acceptable or not
    public static final int MIN_CARDINALITY_ABSOLUTE = 10;
    public static final int MIN_CARDINALITY_RELATIVE = 10;
    public static final int MIN_DIFFERENCE_ABSOLUTE = 1;
}
