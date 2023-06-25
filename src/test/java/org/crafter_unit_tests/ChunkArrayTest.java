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
import org.joml.Vector3ic;
import org.junit.jupiter.api.Test;

import static org.crafter.engine.utility.JOMLUtils.printVec;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChunkArrayTest {

    @Test
    public void testChunkArrayIndexing() {

        for (int i = 0; i < Chunk.getArraySize(); i++) {
            Vector3ic position = Chunk.indexToPosition(i);
//            printVec(position);
            int index = Chunk.positionToIndex(position);
            assertEquals(index, i);
            Vector3ic doubleCheck = Chunk.indexToPosition(index);
            assertEquals(doubleCheck, position);
        }
    }

    @Test
    public void testConversions() {
        Chunk testChunk = new Chunk(0,0);

        // Basic test
        for (int i = 0; i < Chunk.getArraySize(); i++) {
            testChunk.setBlockData(i, i);
            int gottenBlockData = testChunk.getBlockData(i);

            assertEquals(i, gottenBlockData);

            Vector3ic position = Chunk.indexToPosition(i);
            final int newIndex = Chunk.positionToIndex(position);
            gottenBlockData = testChunk.getBlockData(position);

            assertEquals(newIndex, gottenBlockData);
            assertEquals(i, newIndex);
            assertEquals(i, gottenBlockData);
        }

        // Now test data stream
        final int[] workerChunkData = testChunk.getData();
        for (int i = 0; i < Chunk.getArraySize(); i++) {
            workerChunkData[i] = i + 10;
        }
        testChunk.setData(workerChunkData);
        for (int i = 0; i < Chunk.getArraySize(); i++) {
            final int gottenBlockData = testChunk.getBlockData(i);
            assertEquals(i + 10, gottenBlockData);
        }

        // Now test it again
        for (int i = 0; i < Chunk.getArraySize(); i++) {
            testChunk.setBlockData(i, i);
        }
        final int[] doubleCheck = testChunk.getData();
        for (int i = 0; i < Chunk.getArraySize(); i++) {
            assertEquals(doubleCheck[i], testChunk.getBlockData(i));
        }
    }
}
