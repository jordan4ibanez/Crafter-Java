package org.crafter.engine.world.chunk;

import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.io.Serializable;
import java.util.Arrays;

/**
 * The Basis for working with Chunk's internal Array.
 * Chunks are basically fancy arrays of data.
 * This class goes into Chunk, finalizing this snowball of inheritance.
 *
 *
 * A chunk of map data. It is 16 wide, 128 high, and 16 long.
 *
 * Some notes:
 * << shifts to the left X amount, so << 3 of 0001 (1) in a byte now represents 1000 (8)
 * >> shifts to the right X amount
 *
 * Chunk represented as:
 * [16 bit] block | [4 bit lightLevel] | [4 bit blockState] | [ 8 bits left over for additional functionality]
 * This is literal, here is an exact representation:
 * | 0000 0000 0000 0000 | 0000 | 0000 | 0000 0000 |
 */
public abstract class ChunkArrayManipulation extends ChunkBitManipulation implements Serializable {

    // X
    private static final int WIDTH = 16;
    // Y
    static final int HEIGHT = 128;
    // Z
    private static final int DEPTH = 16;
    private static final int yStride = WIDTH * DEPTH;
    private static final int arraySize = WIDTH * HEIGHT * DEPTH;

    // Consists of bit shifted integral values
    private final int[] data;

    public ChunkArrayManipulation() {
        this.data = new int[arraySize];
    }

    /**
     * Stream the new data into the chunk memory.
     * @param newData is an array of length 32_768 with bit-manipulated block data.
     */
    public void setData(int[] newData) {
        check(newData);
        System.arraycopy(newData, 0, data, 0, newData.length);
    }

    /**
     * Get a copy of the data array.
     * @return is a copy of the internal array of block data.
     */
    public int[] getData() {
        return Arrays.copyOf(data, data.length);
    }

    /**
     * Set a single block, think of this as minetest.set_node();
     * For bulk setting, it is currently recommended to use the array methods.
     * @param position is the 3D position in the internal array.
     * @param blockData is the constructed bit manipulated integer that represents a block.
     */
    public void setBlockData(Vector3ic position, int blockData) {
        check(position);
        data[positionToIndex(position)] = blockData;
    }

    /**
     * Set a single block, think of this as minetest.set_node();
     * For bulk setting, it is currently recommended to use the array methods.
     * @param index is the 1D index in the internal array.
     * @param blockData is the constructed bit manipulated integer that represents a block.
     */
    public void setBlockData(int index, int blockData) {
        check(index);
        data[index] = blockData;
    }

    /**
     * Get a single block, think of this as minetest.get_node();
     * For bulk getting, it is currently recommended to use the array methods.
     * @param index is the 1D position in the internal array.
     * @return is the bit manipulated data value.
     */
    public int getBlockData(int index) {
        check(index);
        return data[index];
    }

    /**
     * Get a single block, think of this as minetest.get_node();
     * For bulk getting, it is currently recommended to use the array methods.
     * @param position is the 3D position in the internal array.
     * @return is the bit manipulated data value.
     */
    public int getBlockData(Vector3ic position) {
        check(position);
        return data[positionToIndex(position)];
    }

    public int positionToIndex(Vector3ic position) {
        return (position.y() * yStride) + (position.z() * DEPTH) + position.x();
    } // One below is for iterator assembly
    public int positionToIndex(final int x, final int y, final int z) {
        return (y * yStride) + (z * DEPTH) + x;
    }

    public Vector3ic indexToPosition(int index) {
        return new Vector3i(
                index % WIDTH,
                (index / yStride) % HEIGHT,
                (index / DEPTH) % DEPTH
        );
    }

    private void check(int[] array) {
        if (!boundsCheck(array)) {
            throw new RuntimeException("ChunkArrayManipulation: Tried to set internal data to an array length of (" + array.length + ")!");
        }
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
    private boolean boundsCheck(int[] array) {
        return array.length == arraySize;
    }
    private boolean boundsCheck(Vector3ic position) {
        return position.x() >= 0 && position.x() < WIDTH &&
                position.y() >= 0 && position.y() < HEIGHT &&
                position.z() >= 0 && position.z() < DEPTH;
    }
    private boolean boundsCheck(int index) {
        return index >= 0 && index < arraySize;
    }

    /**
     * This makes it easier to create data and work with chunks!
     */
    public int getArraySize() {
        return arraySize;
    }

    /**
     * Width of chunk in blocks.
     */
    public int getWidth() {
        return WIDTH;
    }

    /**
     * Depth of chunk in blocks.
     */
    public int getDepth() {
        return DEPTH;
    }

    /**
     * Height of chunk in blocks.
     */
    public int getHeight() {
        return HEIGHT;
    }
}
