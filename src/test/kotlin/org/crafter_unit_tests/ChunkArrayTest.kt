package org.crafter_unit_tests

import org.crafter.engine.world.chunk.Chunk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ChunkArrayTest {
    @Test
    fun testChunkArrayIndexing() {
        val testChunk = Chunk(0, 0)
        for (i in 0 until testChunk.arraySize) {
            val position = testChunk.indexToPosition(i)
            val index = testChunk.positionToIndex(position)
            Assertions.assertEquals(index, i)
            val doubleCheck = testChunk.indexToPosition(index)
            Assertions.assertEquals(doubleCheck, position)
        }
    }

    @Test
    fun testConversions() {
        val testChunk = Chunk(0, 0)

        // Basic test
        for (i in 0 until testChunk.arraySize) {
            testChunk.setBlockData(i, i)
            var gottenBlockData = testChunk.getBlockData(i)
            Assertions.assertEquals(i, gottenBlockData)
            val position = testChunk.indexToPosition(i)
            val newIndex = testChunk.positionToIndex(position)
            gottenBlockData = testChunk.getBlockData(position)
            Assertions.assertEquals(newIndex, gottenBlockData)
            Assertions.assertEquals(i, newIndex)
            Assertions.assertEquals(i, gottenBlockData)
        }

        // Now test data stream
        val workerChunkData = testChunk.data
        for (i in 0 until testChunk.arraySize) {
            workerChunkData[i] = i + 10
        }
        testChunk.data = workerChunkData
        for (i in 0 until testChunk.arraySize) {
            val gottenBlockData = testChunk.getBlockData(i)
            Assertions.assertEquals(i + 10, gottenBlockData)
        }

        // Now test it again
        for (i in 0 until testChunk.arraySize) {
            testChunk.setBlockData(i, i)
        }
        val doubleCheck = testChunk.data
        for (i in 0 until testChunk.arraySize) {
            Assertions.assertEquals(doubleCheck[i], testChunk.getBlockData(i))
        }
    }
}
