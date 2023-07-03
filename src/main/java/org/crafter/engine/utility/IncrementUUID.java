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

/**
 * A pure NOT thread safe UUID String generator.
 */
public final class IncrementUUID {

    // Someone's going to have to run the game for a looooong time to break this.
    private static final StringBuilder output = new StringBuilder();
    private static long counter = 0;

    private IncrementUUID() {}

    /**
     * Gets a Unique UUID String. WARNING: If the game is run for more than 292 years this will break!
     * @return A UUID of the current counter as a bit string.
     */
    public static String getIncrementalUUID() {
        for (int i = 63; i >= 0; i--) {
            output.append((counter & (1L << i)) == 0 ? "0" : "1");
        }
        System.out.println("Literal (" + counter + ") | binary: " + output);
        final String newUUID = output.toString();
        output.setLength(0);
        counter++;
        return newUUID;
    }

}
