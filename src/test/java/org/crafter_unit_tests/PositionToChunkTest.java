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
package org.crafter_unit_tests;

import org.crafter.engine.world.chunk.Chunk;
import org.joml.Math;
import org.junit.jupiter.api.Test;

import static org.crafter.engine.utility.Range.range;
import static org.crafter.engine.utility.UtilityPrinter.println;

public class PositionToChunkTest {

    // Baseline standard function
    static void positionToChunkX(float rawPositionX) {
        final int chunkX = (int) Math.floor(rawPositionX / Chunk.getWidth());
        // Negative positions need an adjustment since there are essentially duplicate 0 coordinates on -1,0 border
        rawPositionX = rawPositionX < 0 ? Math.abs(rawPositionX) - 1 : rawPositionX;
        final int positionInChunkX = (int) Math.floor(rawPositionX % Chunk.getWidth());
        println("input: " + rawPositionX + " | in chunk: " + chunkX + " | in pos: " + positionInChunkX);
    }

    @Test
    public void positionToChunk() {
        for (int x : range(-16, 16)) {
            positionToChunkX(x /*auto cast to float*/);
        }
    }
}
