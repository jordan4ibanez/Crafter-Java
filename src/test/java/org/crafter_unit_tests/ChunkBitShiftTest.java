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

        Random random = new Random((int) (new Date().getTime()/1000));

        for (int i = 0; i < testAmount; i++) {
            final int chosenID = random.nextInt(65_535);
            final int testBlock = Chunk.setBlockID(0, chosenID);
            final int gottenID = Chunk.getBlockID(testBlock);
            assertEquals(chosenID, gottenID);
        }
    }

    @Test
    public void testChunkBlockLight() {

        Random random = new Random((int) (new Date().getTime()/1000));

        for (int i = 0; i < testAmount; i++) {
            final int chosenLight = random.nextInt(15);
            final int testBlock = Chunk.setBlockLight(0, chosenLight);
            final int gottenLight = Chunk.getBlockLight(testBlock);
            assertEquals(chosenLight, gottenLight);
        }
    }

    @Test
    public void testChunkBlockState() {

        Random random = new Random((int) (new Date().getTime()/1000));

        for (int i = 0; i < testAmount; i++) {
            final int chosenState = random.nextInt(16);
            final int testBlock = Chunk.setBlockState(0, chosenState);
            final int gottenState = Chunk.getBlockState(testBlock);
            assertEquals(chosenState, gottenState);
        }
    }

    /**
     * Now go crazy. Reuse a block over and over and see if it breaks.
     */
    @Test
    public void testAllBitManipulation() {

        Random random = new Random((int) (new Date().getTime()/1000));

        int testBlock = 0;

        for (int x = 0; x < testAmount; x++) {

            final int chosenID = random.nextInt(65_535);
            final int chosenLight = random.nextInt(15);
            final int chosenState = random.nextInt(15);

            testBlock = Chunk.setBlockID(testBlock, chosenID);

            assertEquals(chosenID, Chunk.getBlockID(testBlock));

            testBlock = Chunk.setBlockLight(testBlock, chosenLight);

            assertEquals(chosenID, Chunk.getBlockID(testBlock));
            assertEquals(chosenLight, Chunk.getBlockLight(testBlock));

            testBlock = Chunk.setBlockState(testBlock, chosenState);

            assertEquals(chosenID, Chunk.getBlockID(testBlock));
            assertEquals(chosenLight, Chunk.getBlockLight(testBlock));
            assertEquals(chosenState, Chunk.getBlockState(testBlock));

            // Now we're going in reverse order to double-check

            testBlock = Chunk.setBlockLight(testBlock, chosenLight);

            assertEquals(chosenID, Chunk.getBlockID(testBlock));
            assertEquals(chosenLight, Chunk.getBlockLight(testBlock));
            assertEquals(chosenState, Chunk.getBlockState(testBlock));

            testBlock = Chunk.setBlockID(testBlock, chosenID);

            assertEquals(chosenID, Chunk.getBlockID(testBlock));
            assertEquals(chosenLight, Chunk.getBlockLight(testBlock));
            assertEquals(chosenState, Chunk.getBlockState(testBlock));
        }

    }
}
