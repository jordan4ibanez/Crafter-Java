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

import org.joml.Math;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class GameMath {

    private static final Vector3f workerVector = new Vector3f();

    // For some reason, JOML does not expose this variable
    private static final float PIHalf_f = (float) (Math.PI * 0.5);
    private static final float PI2 = (float) (Math.PI * 2.0);

    private GameMath(){}

    /**
     * Utilized for calculation of 2d movement from yaw
     */
    public static Vector3fc getHorizontalDirection(final float yaw) {
        startWork();

        workerVector.x = Math.sin(-yaw);
        workerVector.z = Math.cos(yaw);

        return getWork();
    }

    public static float yawToLeft(final float yaw) {
        return yaw - PIHalf_f;
    }


    /**
     * This is simply to neaten up the plain english readability of this.
     * I also don't want to forget to clear out the old data
     */
    private static void startWork() {
        workerVector.zero();
    }
    /**
     * this is because I don't feel like casting the worker vector into 3ic every time
     */
    private static Vector3fc getWork() {
        return workerVector;
    }

    /**
     * This is for printing out JOML vector3fs because it's simply unusable toString & doing this is annoying
     */
    public static void printVector(Vector3fc i) {
        System.out.println(i.x() + ", " + i.y() + ", " + i.z());
    }

    /**
     * Ditto
     */
    public static void printVector(String info, Vector3fc i) {
        System.out.println(info + ": " + i.x() + ", " + i.y() + ", " + i.z());
    }

    /**
     * This is for printing out JOML vector2fs because it's simply unusable toString & doing this is annoying
     */
    public static void printVector(Vector2fc i) {
        System.out.println(i.x() + ", " + i.y());
    }

    /**
     * Ditto
     */
    public static void printVector(String info, Vector2fc i) {
        System.out.println(info + ": " + i.x() + ", " + i.y());
    }

    public static float getPIHalf_f() {
        return PIHalf_f;
    }
    public static float getPi2() {
        return PI2;
    }
}
