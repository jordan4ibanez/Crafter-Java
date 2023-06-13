package org.crafter.engine.world.chunk;

import java.io.Serializable;

/**
 * The Basis for chunks.
 * Chunks are basically fancy arrays of data.
 * This class goes into ChunkArrayManipulation, then gets finalized into Chunk in the inheritance chain.
 */
public abstract class ChunkBitManipulation implements Serializable {
    StringBuilder output;

    public ChunkBitManipulation(){
        output = new StringBuilder();
    }
    public void printBits(int input) {
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
    public static int getBlockLight(int input) {
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
    public static int setBlockLight(int input, int newLight) {
        if (newLight > 15 || newLight < 0) {
            throw new RuntimeException("ChunkBitManipulation: Attempted to exceed 4 bit limit for light! Attempted to input value: (" + newLight + ")" );
        }
        int blockID = parseBlockID(input);
        int light = shiftLight(newLight);
        int state = parseBlockState(input);
        return combine(blockID, light, state);
    }
    public static int setBlockState(int input, int newState) {
        if (newState > 15 || newState < 0) {
            throw new RuntimeException("ChunkBitManipulation: Attempted to exceed 4 bit limit for light! Attempted to input value: (" + newState + ")");
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
    private static int shiftLight(int input) {
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
