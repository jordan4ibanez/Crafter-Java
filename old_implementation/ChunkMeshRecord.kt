package org.crafter.engine.world_generation.chunk_mesh_generation

import org.joml.Vector2ic

/**
 * Raw immutable data that the Chunk Mesh Generator will output into it's return queue after it's finished with it.
 */
data class ChunkMeshRecord(
    val uuid: String,
    val stack: Int,
    val destinationChunkPosition: Vector2ic,
    val positions: FloatArray,
    val textureCoordinates: FloatArray,
    val indices: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChunkMeshRecord) return false

        if (uuid != other.uuid) return false
        if (stack != other.stack) return false
        if (destinationChunkPosition != other.destinationChunkPosition) return false
        if (!positions.contentEquals(other.positions)) return false
        if (!textureCoordinates.contentEquals(other.textureCoordinates)) return false
        return indices.contentEquals(other.indices)
    }

    override fun hashCode(): Int {
        var result = uuid.hashCode()
        result = 31 * result + stack
        result = 31 * result + destinationChunkPosition.hashCode()
        result = 31 * result + positions.contentHashCode()
        result = 31 * result + textureCoordinates.contentHashCode()
        result = 31 * result + indices.contentHashCode()
        return result
    }
}
