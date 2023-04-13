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
