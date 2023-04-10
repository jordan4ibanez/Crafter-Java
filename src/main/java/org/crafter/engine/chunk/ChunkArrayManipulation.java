package org.crafter.engine.chunk;

import org.joml.Vector3i;
import org.joml.Vector3ic;

/**
 * The Basis for working with Chunk's internal Array.
 * Chunks are basically fancy arrays of data.
 * This class goes into Chunk, finalizing this snowball of inheritance.
 */
public abstract class ChunkArrayManipulation extends ChunkBitManipulation {

    // X
    private static final int width = 16;
    // Y
    private static final int height = 128;
    // Z
    private static final int depth = 16;
    private static final int yStride = width * height;
    private static final int arraySize = width*height*depth;

    // Consists of bit shifted integral values
    private final int[] data;

    public ChunkArrayManipulation() {
        this.data = new int[arraySize];
    }

    public void setBlock(int index, int blockData) {


    }

    public int positionToIndex(Vector3ic position) {
        return (position.x() * yStride) + (position.z() * height) + position.y();
    }

    public Vector3ic indexToPosition(int index) {
        return new Vector3i(
                index % 16,
                (index % yStride) / width,
                index / yStride
        );
    }

    private void check(int index) {
        if (!boundsCheck(index)) {
            throw new RuntimeException("ChunkArrayManipulation: Index (" + index + ") is out of bounds!");
        }
    }
    private void check(Vector3ic position) {
        if (!boundsCheck(position)) {
            throw new RuntimeException("ChunkArrayManipulation: Position (" + position.x() + ", " + position.y() + ", " + position.z() + ") is out of bounds!");
        }
    }
    private boolean boundsCheck(Vector3ic position) {
        return position.x() >= 0 && position.x() < width &&
                position.y() >= 0 && position.y() < height &&
                position.z() >= 0 && position.z() < depth;
    }
    private boolean boundsCheck(int index) {
        return index >= 0 && index < arraySize;
    }
}
