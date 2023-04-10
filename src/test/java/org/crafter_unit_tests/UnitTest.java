package org.crafter_unit_tests;

import org.crafter.engine.chunk.Chunk;
import org.joml.Random;
import org.joml.Vector3ic;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnitTest {

    private static final int testAmount = 65_535;

    @Test
    public void testChunkArrayIndexing() {
        Chunk testChunk = new Chunk(0,0);

        for (int i = 0; i < testChunk.getArraySize(); i++) {
            Vector3ic position = testChunk.indexToPosition(i);
            int index = testChunk.positionToIndex(position);
            assertEquals(index, i);
            Vector3ic doubleCheck = testChunk.indexToPosition(index);
            assertEquals(doubleCheck, position);
        }
    }

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
        }

    }
}
