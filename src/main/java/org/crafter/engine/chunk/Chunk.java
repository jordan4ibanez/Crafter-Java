package org.crafter.engine.chunk;

import org.joml.Vector2i;
import org.joml.Vector2ic;

/**
 * Inheritance Chain: ChunkBitManipulation -> ChunkArrayManipulation -> Chunk
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
public class Chunk extends ChunkArrayManipulation {
    private final Vector2ic position;

    public Chunk(int x, int y) {
        this(new Vector2i(x,y));
    }
    public Chunk(Vector2i position) {
        this.position = position;
    }

    public Vector2ic getPosition() {
        return position;
    }

    public int getX() {
        return position.x();
    }

    public int getY() {
        return position.y();
    }



    //Todo: idea: metadata arraymap
    //Todo: bitshift light, block id, state

    public void debugZero() {
//        int test = internalSetBlockID(0, 65_535);

//        numberTools.printBits(test);
//        System.out.println(getBlockID(test));
//        numberTools.printBits(getBlockID(test));

        int test = internalSetBlockLight(0, 6);

        test = internalSetBlockState(test, 9);

        printBits(test);

        printBits(getBlockState(test));
    }


}
