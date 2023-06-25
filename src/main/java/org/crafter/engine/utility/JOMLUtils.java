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

import org.joml.*;

/**
 * This is just a mini helper utility class.
 * Why?
 * I don't feel like shoveling in formats into every toString() for JOML objects. That's it.
 *
 * There are no mutable states in this, should be good for threading.
 */
public final class JOMLUtils {

    public static void printVec(Object input) {
        printVec("", input);
    }
    public static void printVec(String debugPreface, Object input) {

        final String className = input.getClass().toString().split(" ")[1];

        switch (className) {
            case "org.joml.Vector3i",
                 "org.joml.Vector3ic" -> printVector3i(debugPreface, (Vector3ic) input);

            case "org.joml.Vector3f",
                 "org.joml.Vector3fc" -> printVector3f(debugPreface, (Vector3fc) input);

            case "org.joml.Vector2i",
                 "org.joml.Vector2ic" -> printVector2i(debugPreface, (Vector2ic) input);

            case "org.joml.Vector2f",
                 "org.joml.Vector2fc" -> printVector2f(debugPreface, (Vector2fc) input);

            default -> println("JOMLUtils (printVec): Attempted to print something else. :(");
        }
    }

    private static void printVector3i(String debugPreface, Vector3ic input) {
        println(debugPreface + "(" + input.x() + ", " + input.y() + ", " + input.z() + ")");
    }
    private static void printVector3f(String debugPreface, Vector3fc input) {
        println(debugPreface + "(" + input.x() + ", " + input.y() + ", " + input.z() + ")");
    }

    private static void printVector2i(String debugPreface, Vector2ic input) {
        println(debugPreface + "(" + input.x() + ", " + input.y() + ")");
    }
    private static void printVector2f(String debugPreface, Vector2fc input) {
        println(debugPreface + "(" + input.x() + ", " + input.y() + ")");
    }

    /**
     * And this one just exists because I don't feel like typing System.out here. :P
     * @param input String to print.
     */
    private static void println(String input) {
        System.out.println(input);
    }
    
}
