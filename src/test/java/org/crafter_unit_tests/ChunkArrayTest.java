package org.crafter_unit_tests;

import org.crafter.engine.chunk.Chunk;
import org.joml.Vector3ic;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChunkArrayTest {

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
    public void testConversions() {
        Chunk testChunk = new Chunk(0,0);

        // Basic test
        for (int i = 0; i < testChunk.getArraySize(); i++) {
            testChunk.setBlockData(i, i);
            int gottenBlockData = testChunk.getBlockData(i);

            assertEquals(i, gottenBlockData);

            Vector3ic position = testChunk.indexToPosition(i);
            final int newIndex = testChunk.positionToIndex(position);
            gottenBlockData = testChunk.getBlockData(position);

            assertEquals(newIndex, gottenBlockData);
            assertEquals(i, newIndex);
            assertEquals(i, gottenBlockData);
        }

        // Now test data stream
        final int[] workerChunkData = testChunk.getData();
        for (int i = 0; i < testChunk.getArraySize(); i++) {
            workerChunkData[i] = i + 10;
        }
        testChunk.setData(workerChunkData);
        for (int i = 0; i < testChunk.getArraySize(); i++) {
            final int gottenBlockData = testChunk.getBlockData(i);
            assertEquals(i + 10, gottenBlockData);
        }

        // Now test it again
        for (int i = 0; i < testChunk.getArraySize(); i++) {
            testChunk.setBlockData(i, i);
        }
        final int[] doubleCheck = testChunk.getData();
        for (int i = 0; i < testChunk.getArraySize(); i++) {
            assertEquals(i, testChunk.getBlockData(i));
        }

    }
}
