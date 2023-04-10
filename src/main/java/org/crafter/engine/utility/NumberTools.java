package org.crafter.engine.utility;

public class NumberTools {
    private final StringBuilder output;
    public NumberTools(){
        output = new StringBuilder();
    }
    public void printBits(int input) {
        for (int i = 31; i >= 0; i--) {
            output.append((input & (1 << i)) == 0 ? "0" : "1");
        }
        System.out.println("Literal (" + input + ") | binary: " + output);
        output.setLength(0);
    }
}
