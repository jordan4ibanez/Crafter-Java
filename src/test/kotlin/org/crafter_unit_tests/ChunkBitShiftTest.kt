package org.crafter_unit_tests

import org.crafter.engine.world.chunk.Chunk
import org.joml.Random
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class ChunkBitShiftTest {
    @Test
    fun testChunkBlockID() {
        val testChunk = Chunk(0, 0)
        val random = Random((Date().time / 1000).toInt().toLong())
        for (i in 0 until testAmount) {
            val chosenID = random.nextInt(65535)
            val testBlock = testChunk.setBlockID(0, chosenID)
            val gottenID = testChunk.getBlockID(testBlock)
            Assertions.assertEquals(chosenID, gottenID)
        }
    }

    @Test
    fun testChunkBlockLight() {
        val testChunk = Chunk(0, 0)
        val random = Random((Date().time / 1000).toInt().toLong())
        for (i in 0 until testAmount) {
            val chosenLight = random.nextInt(15)
            val testBlock = testChunk.setBlockLight(0, chosenLight)
            val gottenLight = testChunk.getBlockLight(testBlock)
            Assertions.assertEquals(chosenLight, gottenLight)
        }
    }

    @Test
    fun testChunkBlockState() {
        val testChunk = Chunk(0, 0)
        val random = Random((Date().time / 1000).toInt().toLong())
        for (i in 0 until testAmount) {
            val chosenState = random.nextInt(16)
            val testBlock = testChunk.setBlockState(0, chosenState)
            val gottenState = testChunk.getBlockState(testBlock)
            Assertions.assertEquals(chosenState, gottenState)
        }
    }

    /**
     * Now go crazy. Reuse a block over and over and see if it breaks.
     */
    @Test
    fun testAllBitManipulation() {
        val testChunk = Chunk(0, 0)
        val random = Random((Date().time / 1000).toInt().toLong())
        var testBlock = 0
        for (x in 0 until testAmount) {
            val chosenID = random.nextInt(65535)
            val chosenLight = random.nextInt(15)
            val chosenState = random.nextInt(15)
            testBlock = testChunk.setBlockID(testBlock, chosenID)
            Assertions.assertEquals(chosenID, testChunk.getBlockID(testBlock))
            testBlock = testChunk.setBlockLight(testBlock, chosenLight)
            Assertions.assertEquals(chosenID, testChunk.getBlockID(testBlock))
            Assertions.assertEquals(chosenLight, testChunk.getBlockLight(testBlock))
            testBlock = testChunk.setBlockState(testBlock, chosenState)
            Assertions.assertEquals(chosenID, testChunk.getBlockID(testBlock))
            Assertions.assertEquals(chosenLight, testChunk.getBlockLight(testBlock))
            Assertions.assertEquals(chosenState, testChunk.getBlockState(testBlock))

            // Now we're going in reverse order to double-check
            testBlock = testChunk.setBlockLight(testBlock, chosenLight)
            Assertions.assertEquals(chosenID, testChunk.getBlockID(testBlock))
            Assertions.assertEquals(chosenLight, testChunk.getBlockLight(testBlock))
            Assertions.assertEquals(chosenState, testChunk.getBlockState(testBlock))
            testBlock = testChunk.setBlockID(testBlock, chosenID)
            Assertions.assertEquals(chosenID, testChunk.getBlockID(testBlock))
            Assertions.assertEquals(chosenLight, testChunk.getBlockLight(testBlock))
            Assertions.assertEquals(chosenState, testChunk.getBlockState(testBlock))
        }
    }

    companion object {
        private const val testAmount = 65535
    }
}
