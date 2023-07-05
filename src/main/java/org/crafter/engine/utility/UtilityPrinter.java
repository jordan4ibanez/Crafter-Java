/*
 Crafter - A blocky game (engine) written in Java with LWJGL.
 Copyright (C) 2023  jordan4ibanez

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.crafter.engine.utility;

import java.io.PrintStream;

public final class UtilityPrinter {

    private static final PrintStream printStream = new PrintStream(System.out);

    private UtilityPrinter(){}

    public static void print(boolean b){
        printStream.print(b);
    }
    public static void print(char c) {
        printStream.print(c);
    }
    public static void print(int i) {
        printStream.print(i);
    }
    public static void print(long l) {
        printStream.print(l);
    }
    public static void print(float f) {
        printStream.print(f);
    }
    public static void print(double d) {
        printStream.print(d);
    }
    public static void print(char[] s) {
        printStream.print(s);
    }
    public static void print(String s) {
        printStream.print(s);
    }
    public static void print(Object obj) {
        printStream.print(obj);
    }

    public static void println() {
        printStream.println();
    }
    public static void println(boolean x) {
        printStream.println(x);
    }
    public static void println(int x) {
        printStream.println(x);
    }
    public static void println(long x) {
        printStream.println(x);
    }
    public static void println(float x) {
        printStream.println(x);
    }
    public static void println(double x) {
        printStream.println(x);
    }
    public static void println(char[] x) {
        printStream.println(x);
    }
    public static void println(String x) {
        printStream.println(x);
    }
    public static void println(Object x) {
        printStream.println(x);
    }
}
