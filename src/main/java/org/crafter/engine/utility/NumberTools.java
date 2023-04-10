package org.crafter.engine.utility;

public final class NumberTools {
    private static final StringBuilder output = new StringBuilder();
    private NumberTools(){}

    public static void printBits(int input) {
        for (int i = 31; i >= 0; i--) {
            output.append((input & (1 << i)) == 0 ? "0" : "1");
        }
        System.out.println("Literal (" + input + ") | binary: " + output);
        output.setLength(0);
    }
}
