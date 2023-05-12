package org.crafter.engine.world.chunk

import org.joml.Vector2ic

/**
 * This is where all the chunks live!
 */
object ChunkStorage {
    private val container = HashMap<Vector2ic, Chunk>()

    fun getChunk(position: Vector2ic): Chunk {
        positionCheck(position, "getChunk")
        return container[position]!!
    }

    fun addOrUpdate(chunk: Chunk) {
        val position = chunk.position
        if (hasPosition(position)) {
            println("ChunkStorage: Updated chunk (" + position.x() + ", " + position.y() + ")")
            container[position]!!.streamNewBlockData(chunk.getRawData())
            return
        }
        container[position] = chunk
        println("ChunkStorage: Stored chunk (" + position.x() + ", " + position.y() + ")")
    }

    @Synchronized
    fun getThreadSafeChunkClone(position: Vector2ic): Chunk {
        positionCheck(position, "getThreadSafeChunkClone")
        // Create a deep clone of the chunk
        return container[position]!!.clone()
    }

    private fun positionCheck(position: Vector2ic, methodName: String) {
        if (!hasPosition(position)) {
            throw RuntimeException("ChunkStorage: Tried to get a non-existent chunk with method(" + methodName + ")! (" + position.x() + ", " + position.y() + ") does not exist! Did you check it's existence with (hasPosition)?")
        }
    }

    @Synchronized
    fun hasPosition(position: Vector2ic): Boolean {
        return container.containsKey(position)
    }
}
