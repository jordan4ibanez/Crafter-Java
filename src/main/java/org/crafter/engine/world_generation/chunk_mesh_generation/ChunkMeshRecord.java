package org.crafter.engine.world_generation.chunk_mesh_generation;

/**
 * Raw immutable data that the Chunk Mesh Generator will output into it's return queue after it's finished with it.
 */
public record ChunkMeshRecord(String name, float[] positions, float[] textureCoordinates, int[] indices) {}
