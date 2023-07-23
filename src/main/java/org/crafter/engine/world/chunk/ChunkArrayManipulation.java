/*
 Crafter - A blocky game (engine) written in Java with LWJGL.
 Copyright (C) 2023  jordan4ibanez

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
 * A chunk of map data. It is 16 wide, 128 high, and 16 long.

 */
public abstract class ChunkArrayManipulation extends ChunkBitManipulation {

    // X
    private static final int WIDTH = 16;
    // Y
    private static final int HEIGHT = 128;
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
     * Get the DIRECT data. This needs to be used very carefully!!! This is the MUTABLE internal pointer!!!
     * @return The MUTABLE DIRECT INTERNAL POINTER!!!
     */
    public int[] getDataDIRECT() {
        return data;
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

    public int getBlockData(final int x, final int y, final int z) {
        check(x,y,z);
        return data[positionToIndex(x,y,z)];
    }

    public static int positionToIndex(Vector3ic position) {
        return (position.y() * yStride) + (position.z() * DEPTH) + position.x();
    } // One below is for iterator assembly
    public static int positionToIndex(final int x, final int y, final int z) {
        return (y * yStride) + (z * DEPTH) + x;
    }

    public static Vector3ic indexToPosition(int index) {
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
    private void check(final int x, final int y, final int z) {
        if (!boundsCheck(x,y,z)) {
            throw new RuntimeException("ChunkArrayManipulation: Position (" + x + ", " + y + ", " + z + ") is out of bounds!");
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
    private boolean boundsCheck(final int x, final int y, final int z) {
        return x >= 0 && x < WIDTH &&
                y >= 0 && y < HEIGHT &&
                z >= 0 && z < DEPTH;
    }

    /**
     * This makes it easier to create data and work with chunks!
     */
    public static int getArraySize() {
        return arraySize;
    }

    /**
     * Width of chunk in blocks.
     */
    public static int getWidth() {
        return WIDTH;
    }

    /**
     * Depth of chunk in blocks.
     */
    public static int getDepth() {
        return DEPTH;
    }

    /**
     * Height of chunk in blocks.
     */
    public static int getHeight() {
        return HEIGHT;
    }
}
