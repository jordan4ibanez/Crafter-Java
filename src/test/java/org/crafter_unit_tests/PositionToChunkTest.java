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
import static org.crafter.engine.world.chunk.ChunkStorage.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PositionToChunkTest {

    //TODO WARNING! THIS IS WRITTEN OUT WET TO ENSURE STABILITY WITHIN THE INTERNAL API!!!
    // DO NOT CHANGE THIS UNDER _ANY_ CIRCUMSTANCES!!!

    // Baseline standard function
    static void positionToChunkX(float rawPositionX, final int GOTTEN_CHUNK_X, final int GOTTEN_POSITION_X) {
        final int chunkX = (int) Math.floor(rawPositionX / Chunk.getWidth());
        // Negative positions need an adjustment since there are essentially duplicate 0 coordinates on -1,0 border
        final int width = Chunk.getWidth();
        rawPositionX = rawPositionX < 0 ? (width - (int) Math.floor(Math.abs(rawPositionX + 1) % width)) - 1 : (int) Math.floor(rawPositionX % width);
        final int positionInChunkX = (int) Math.floor(rawPositionX % Chunk.getWidth());

//        println("input: " + rawPositionX + " | in chunk: " + chunkX + " | in pos: " + positionInChunkX);

        assertEquals(chunkX,GOTTEN_CHUNK_X);
        assertEquals(positionInChunkX, GOTTEN_POSITION_X);

    }
    static void positionToChunkZ(float rawPositionZ, final int GOTTEN_CHUNK_Z, final int GOTTEN_POSITION_Z) {
        final int chunkZ = (int) Math.floor(rawPositionZ / Chunk.getDepth());
        // Negative positions need an adjustment since there are essentially duplicate 0 coordinates on -1,0 border
        final int depth = Chunk.getDepth();
        rawPositionZ = rawPositionZ < 0 ? (depth - (int) Math.floor(Math.abs(rawPositionZ + 1) % depth)) - 1 : (int) Math.floor(rawPositionZ % depth);
        final int positionInChunkZ = (int) Math.floor(rawPositionZ % Chunk.getWidth());
//        println("input: " + rawPositionZ + " | in chunk: " + chunkZ + " | in pos: " + positionInChunkZ);

        assertEquals(chunkZ,GOTTEN_CHUNK_Z);
        assertEquals(positionInChunkZ, GOTTEN_POSITION_Z);
    }

    @Test
    public void positionToChunk() {
        final int testSize = 65535;
        for (int x : range(-testSize, testSize)) {
            final int CHUNK_X_VERIFIER = UNIT_TEST_VERIFICATION_CHUNK_X(x);
            final int POSITION_X_VERIFIER = UNIT_TEST_VERIFICATION_INTERNAL_POSITION_X(x);
            positionToChunkX(x, CHUNK_X_VERIFIER, POSITION_X_VERIFIER);
        }

        for (int z : range(-testSize, testSize)) {
            final int CHUNK_Z_VERIFIER = UNIT_TEST_VERIFICATION_CHUNK_Z(z);
            final int POSITION_Z_VERIFIER = UNIT_TEST_VERIFICATION_INTERNAL_POSITION_Z(z);
            positionToChunkZ(z, CHUNK_Z_VERIFIER, POSITION_Z_VERIFIER);
        }
    }
}
