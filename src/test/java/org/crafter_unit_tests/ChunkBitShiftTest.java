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
import org.joml.Random;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChunkBitShiftTest {

    private static final int testAmount = 65_535;

    @Test
    public void testChunkBlockID() {
        Chunk testChunk = new Chunk(0,0);
        Random random = new Random((int) (new Date().getTime()/1000));

        for (int i = 0; i < testAmount; i++) {
            final int chosenID = random.nextInt(65_535);
            final int testBlock = testChunk.setBlockID(0, chosenID);
            final int gottenID = testChunk.getBlockID(testBlock);
            assertEquals(chosenID, gottenID);
        }
    }

    @Test
    public void testChunkBlockLight() {
        Chunk testChunk = new Chunk(0,0);
        Random random = new Random((int) (new Date().getTime()/1000));

        for (int i = 0; i < testAmount; i++) {
            final int chosenLight = random.nextInt(15);
            final int testBlock = testChunk.setBlockLight(0, chosenLight);
            final int gottenLight = testChunk.getBlockLight(testBlock);
            assertEquals(chosenLight, gottenLight);
        }
    }

    @Test
    public void testChunkBlockState() {
        Chunk testChunk = new Chunk(0,0);
        Random random = new Random((int) (new Date().getTime()/1000));

        for (int i = 0; i < testAmount; i++) {
            final int chosenState = random.nextInt(16);
            final int testBlock = testChunk.setBlockState(0, chosenState);
            final int gottenState = testChunk.getBlockState(testBlock);
            assertEquals(chosenState, gottenState);
        }
    }

    /**
     * Now go crazy. Reuse a block over and over and see if it breaks.
     */
    @Test
    public void testAllBitManipulation() {

        Chunk testChunk = new Chunk(0,0);
        Random random = new Random((int) (new Date().getTime()/1000));

        int testBlock = 0;

        for (int x = 0; x < testAmount; x++) {

            final int chosenID = random.nextInt(65_535);
            final int chosenLight = random.nextInt(15);
            final int chosenState = random.nextInt(15);

            testBlock = testChunk.setBlockID(testBlock, chosenID);

            assertEquals(chosenID, testChunk.getBlockID(testBlock));

            testBlock = testChunk.setBlockLight(testBlock, chosenLight);

            assertEquals(chosenID, testChunk.getBlockID(testBlock));
            assertEquals(chosenLight, testChunk.getBlockLight(testBlock));

            testBlock = testChunk.setBlockState(testBlock, chosenState);

            assertEquals(chosenID, testChunk.getBlockID(testBlock));
            assertEquals(chosenLight, testChunk.getBlockLight(testBlock));
            assertEquals(chosenState, testChunk.getBlockState(testBlock));

            // Now we're going in reverse order to double-check

            testBlock = testChunk.setBlockLight(testBlock, chosenLight);

            assertEquals(chosenID, testChunk.getBlockID(testBlock));
            assertEquals(chosenLight, testChunk.getBlockLight(testBlock));
            assertEquals(chosenState, testChunk.getBlockState(testBlock));

            testBlock = testChunk.setBlockID(testBlock, chosenID);

            assertEquals(chosenID, testChunk.getBlockID(testBlock));
            assertEquals(chosenLight, testChunk.getBlockLight(testBlock));
            assertEquals(chosenState, testChunk.getBlockState(testBlock));
        }

    }
}
