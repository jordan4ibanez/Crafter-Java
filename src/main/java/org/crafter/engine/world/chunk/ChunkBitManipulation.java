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

/**
 * The Basis for chunks.
 * Chunks are basically fancy arrays of data.
 * This class goes into ChunkArrayManipulation, then gets finalized into Chunk in the inheritance chain.
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
public abstract class ChunkBitManipulation {
    private static final StringBuilder output = new StringBuilder();;

    public ChunkBitManipulation(){}

    public static void printBits(int input) {
        for (int i = 31; i >= 0; i--) {
            if ((i + 1) % 4 == 0) {
                output.append("|");
            }
            output.append((input & (1 << i)) == 0 ? "0" : "1");
        }
        output.append("|");
        System.out.println("Literal (" + input + ") | binary: " + output);
        output.setLength(0);
    }

    /**
     * These are user-friendly direct value getters
     */
    public static int getBlockID(int input) {
        return input >>> 16;
    }
    public static int getBlockLightLevel(int input) {
        return input << 16 >>> 28;
    }
    public static int getBlockState(int input) {
        return input << 20 >>> 28;
    }

    /**
     * These are internalized anti boilerplate methods for working with integers that represent a block.
     * Public so they can be used dynamically externally.
     */
    public static int setBlockID(int input, int newID) {
        if (newID > 65_535 || newID < 0) {
            throw new RuntimeException("ChunkBitManipulation: Attempted to exceed ushort limit for block ID! Attempted to input value: (" + newID + ")");
        }
        int blockID = shiftBlock(newID);
        int light = parseLightLevel(input);
        int state = parseBlockState(input);
        return combine(blockID, light, state);
    }
    public static int setBlockLightLevel(int input, int newLightLevel) {
        if (newLightLevel > 15 || newLightLevel < 0) {
            throw new RuntimeException("ChunkBitManipulation: Attempted to exceed 4 bit limit for light level! Attempted to input value: (" + newLightLevel + ")" );
        }
        int blockID = parseBlockID(input);
        int light = shiftLightLevel(newLightLevel);
        int state = parseBlockState(input);
        return combine(blockID, light, state);
    }
    public static int setBlockState(int input, int newState) {
        if (newState > 15 || newState < 0) {
            throw new RuntimeException("ChunkBitManipulation: Attempted to exceed 4 bit limit for state! Attempted to input value: (" + newState + ")");
        }
        int blockID = parseBlockID(input);
        int light = parseLightLevel(input);
        int state = shiftState(newState);
        return combine(blockID, light, state);
    }

    /**
     * Get integral bit data raw.
     * These do not give out the true number, just the data held in that section of the buffer.
     */
    public static int parseBlockID(int input) {
        // Clear out right 16 bits
        return input >>> 16 << 16;
    }
    public static int parseLightLevel(int input) {
        // Clear out left 16 bits
        input = input << 16 >>> 16;
        // Clear out right 12 bits
        input = input >>> 12 << 12;
        return input;
    }
    public static int parseBlockState(int input) {
        // Clear out left 20 bits
        input = input << 20 >>> 20;
        // Clear out right 8 bits
        input = input >>> 8 << 8;
        return input;
    }

    /**
     * Set integral bit data raw. Used for chaining. This is at the bottom because it's just boilerplate bit manipulation
     */
    private static int shiftBlock(int input) {
        return input << 16;
    }
    private static int shiftLightLevel(int input) {
        return input << 12;
    }
    private static int shiftState(int input) {
        return input << 8;
    }

    /**
     * Mini boilerplate combination method, makes code easier to read
     */
    private static int combine(int blockID, int light, int state) {
        return blockID | light | state;
    }
}
