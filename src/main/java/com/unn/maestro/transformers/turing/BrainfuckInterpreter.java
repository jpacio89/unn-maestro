package com.unn.maestro.transformers.turing;

import com.unn.common.dataset.Row;

import java.util.Scanner;

public class BrainfuckInterpreter {
    private final int MAX_CYCLES = 1000;

    private static Scanner ob = new Scanner(System.in);
    private int ptr; // Data pointer

    // Max memory limit. It is the highest number which
    // can be represented by an unsigned 16-bit binary
    // number. Many computer programming environments
    // beside brainfuck may have predefined
    // constant values representing 65535.
    private int length = 65535;

    // Array of byte type simulating memory of max
    // 65535 bits from 0 to 65534.
    private byte memory[] = new byte[length];

    // Interpreter function which accepts the code
    // a string parameter
    public BrainfuckInterpreter interpret(String program) {
        //memory[0] = 10;
        //memory[1] = 31;
        int c = 0;
        int cycles = 0;

        // Parsing through each character of the code
        for (int i = 0; i < program.length(); i++, cycles++) {
            // BrainFuck is a tiny language with only
            // eight instructions. In this loop we check
            // and execute all those eight instructions

            // Customizations to the language spec:
            // - adding max cycle count to avoid endless loop (halting problem)
            // - making arithmetic modular and divisor configurable
            if (cycles > MAX_CYCLES) {
                break;
            }

            // > moves the pointer to the right
            if (program.charAt(i) == '>') {
                if (ptr == length - 1)//If memory is full
                    ptr = 0;//pointer is returned to zero
                else
                    ptr ++;
            }

            // < moves the pointer to the left
            else if (program.charAt(i) == '<') {
                if (ptr == 0) // If the pointer reaches zero

                    // pointer is returned to rightmost memory
                    // position
                    ptr = length - 1;
                else
                    ptr --;
            }

            // + increments the value of the memory
            // cell under the pointer
            else if (program.charAt(i) == '+')
                memory[ptr] ++;

            // - decrements the value of the memory cell
            // under the pointer
            else if (program.charAt(i) == '-')
                memory[ptr] --;

            // . outputs the character signified by the
            // cell at the pointer
            else if (program.charAt(i) == '.')
                System.out.println(memory[ptr]);

            // , inputs a character and store it in the
            // cell at the pointer
            else if (program.charAt(i) == ',')
                memory[ptr] = (byte)(ob.next().charAt(0));

            // [ jumps past the matching ] if the cell
            // under the pointer is 0
            else if (program.charAt(i) == '[')
            {
                if (memory[ptr] == 0)
                {
                    i++;
                    while (c > 0 || program.charAt(i) != ']')
                    {
                        if (program.charAt(i) == '[')
                            c++;
                        else if (program.charAt(i) == ']')
                            c--;
                        i ++;
                    }
                }
            }

            // ] jumps back to the matching [ if the
            // cell under the pointer is nonzero
            else if (program.charAt(i) == ']')
            {
                if (memory[ptr] != 0)
                {
                    i --;
                    while (c > 0 || program.charAt(i) != '[')
                    {
                        if (program.charAt(i) == ']')
                            c ++;
                        else if (program.charAt(i) == '[')
                            c --;
                        i --;
                    }
                    i --;
                }
            }
        }

        /*System.out.print("{ ");

        for (int i = 0; i <= ptr; ++i) {
            System.out.print(memory[i]);
            System.out.print(" ");
        }

        System.out.println("}");*/
        return this;
    }

    public BrainfuckInterpreter interpret(String program, String[] args) {
        for (int i = 0; i < args.length; ++i) {
            this.memory[i] = Byte.parseByte(args[i], 10);
        }
        this.interpret(program);
        return this;
    }

    public Row toRow(int maxTvarCount) {
        Row r = new Row();
        String[] values = new String[maxTvarCount];
        for (int i = 0; i < maxTvarCount; ++i) {
            values[i] = Integer.toString(this.memory[i]);
        }
        r.withValues(values);
        return r;
    }
}
