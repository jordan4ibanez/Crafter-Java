package org.crafter.engine.world_generation.chunk_mesh_generation

import org.joml.Vector2ic

/**
 * Raw immutable data that the Chunk Mesh Generator will output into it's return queue after it's finished with it.
 */
@JvmRecord
data class ChunkMeshRecord(
    val uuid: String,
    val stack: Int,
    val destinationChunkPosition: Vector2ic,
    val positions: FloatArray,
    val textureCoordinates: FloatArray,
    val indices: IntArray
)
