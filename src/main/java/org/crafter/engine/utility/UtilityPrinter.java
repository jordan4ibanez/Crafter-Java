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

/**
 * Literally all this does is allow you to not have to type "System.out" before "println" or "print".
 */
public final class UtilityPrinter {

    private UtilityPrinter(){}

    /**
     * Print a boolean.
     * @param b A boolean.
     */
    public static void print(boolean b){
        System.out.print(b);
    }
    /**
     * Print a char.
     * @param c A Char.
     */
    public static void print(char c) {
        System.out.print(c);
    }

    /**
     * Print an integer.
     * @param i An integer.
     */
    public static void print(int i) {
        System.out.print(i);
    }

    /**
     * Print a long.
     * @param l A long.
     */
    public static void print(long l) {
        System.out.print(l);
    }

    /**
     * Print a float.
     * @param f A float.
     */
    public static void print(float f) {
        System.out.print(f);
    }

    /**
     * Print a double.
     * @param d A double.
     */
    public static void print(double d) {
        System.out.print(d);
    }

    /**
     * Print a char array.
     * @param s A char array.
     */
    public static void print(char[] s) {
        System.out.print(s);
    }

    /**
     * Print a String.
     * @param s A String.
     */
    public static void print(String s) {
        System.out.print(s);
    }

    /**
     * Print an Object.
     * @param obj An Object.
     */
    public static void print(Object obj) {
        System.out.print(obj);
    }

    /**
     * Print a new line.
     */
    public static void println() {
        System.out.println();
    }

    /**
     * Print a boolean with a new line.
     * @param b A boolean.
     */
    public static void println(boolean b) {
        System.out.println(b);
    }

    /**
     * Print an integer with a new line.
     * @param i An integer.
     */
    public static void println(int i) {
        System.out.println(i);
    }

    /**
     * Print a long with a new line.
     * @param l A long.
     */
    public static void println(long l) {
        System.out.println(l);
    }

    /**
     * Print a float with a new line.
     * @param f A float.
     */
    public static void println(float f) {
        System.out.println(f);
    }

    /**
     * Print a double with a new line.
     * @param d A double.
     */
    public static void println(double d) {
        System.out.println(d);
    }

    /**
     * Print a char array with a new line.
     * @param s A char array.
     */
    public static void println(char[] s) {
        System.out.println(s);
    }

    /**
     * Print a String with a new line.
     * @param s A String.
     */
    public static void println(String s) {
        System.out.println(s);
    }

    /**
     * Print an Object with a new line.
     * @param obj An Object.
     */
    public static void println(Object obj) {
        System.out.println(obj);
    }
}
