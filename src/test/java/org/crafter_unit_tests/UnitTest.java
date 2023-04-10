package org.crafter_unit_tests;

import org.crafter.engine.chunk.Chunk;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnitTest {
    @Test
    public void testChunkBitShifting() {
        Chunk testChunk = new Chunk(0,0);
        for (int i = 0; i <= 65_535; i++) {
            // assertEquals(testChunk.getBlockID(testChunk.setBlockID(0, i)), i);
        }
    }
}
