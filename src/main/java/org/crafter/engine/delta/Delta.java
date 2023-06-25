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
package org.crafter.engine.delta;

public final class Delta {
    private static float deltaTime = 0;

    private static long oldTime = System.nanoTime();

    private Delta(){}

    public static void calculateDelta() {
        long time = System.nanoTime();
        deltaTime = (float)(time - oldTime) / 1_000_000_000.0f;
        oldTime = time;
    }

    public static float getDelta() {
        return deltaTime;
    }
}
