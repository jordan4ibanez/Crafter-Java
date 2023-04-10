package org.crafter.engine.chunk;

import org.crafter.engine.utility.NumberTools;
import org.joml.Vector2i;
import org.joml.Vector2ic;

/**
 * A chunk of map data. It is 16 wide, 128 high, and 16 long.
 *
 * Some notes:
 * << shifts to the left X amount, so << 3 of 0001 in a byte now represents 1000
 * >> shifts to the right X amount
 *
 * Chunk represented as:
 * [16 bit] block | [4 bit lightLevel] | [4 bit blockState] | [ 8 bits left over for additional functionality]
 */
public class Chunk {

    private static final int arraySize = 16*16*128;
    private final Vector2ic position;



    // Consists of bit shifted integral values
    private final int[] data;

    public Chunk(int x, int y) {
        this(new Vector2i(x,y));
    }
    public Chunk(Vector2i position) {
        this.position = position;
        this.data = new int[arraySize];
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
        int test = 3 << 3;

        NumberTools.printBits(test);
    }

}
