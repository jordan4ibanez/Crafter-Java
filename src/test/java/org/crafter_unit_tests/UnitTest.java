package org.crafter_unit_tests;

import org.crafter.engine.chunk.Chunk;
import org.joml.Vector3ic;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnitTest {
    @Test
    public void testChunkArrayIndexing() {
        Chunk testChunk = new Chunk(0,0);
        for (int i = 0; i < testChunk.getArraySize(); i++) {

            Vector3ic position = testChunk.indexToPosition(i);
//            System.out.println(position.x() + ", " + position.y() + ", " + position.z());
            int index = testChunk.positionToIndex(position);
//            System.out.println("actual: " + i + " | gotten: " + index);
            assertEquals(index, i);
            Vector3ic doubleCheck = testChunk.indexToPosition(index);
            assertEquals(doubleCheck, position);
        }
    }
}
