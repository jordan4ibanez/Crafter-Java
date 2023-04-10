package org.crafter.engine.chunk;

/**
 * The Basis for working with Chunk's internal Array.
 * Chunks are basically fancy arrays of data.
 * This class goes into Chunk, finalizing this snowball of inheritance.
 */
public abstract class ChunkArrayManipulation {

    private static final int width = 16;
    private static final int height = 128;
    private static final int depth = 16;
    private static final int arraySize = width*height*depth;

    // Consists of bit shifted integral values
    private final int[] data;

    public ChunkArrayManipulation() {
        this.data = new int[arraySize];
    }

}
