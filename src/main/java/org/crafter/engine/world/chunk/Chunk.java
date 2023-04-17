package org.crafter.engine.world.chunk;

import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.io.Serializable;

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
public class Chunk extends ChunkArrayManipulation implements Serializable {
    private final Vector2ic position;

    public Chunk(int x, int y) {
        this(new Vector2i(x,y));
    }
    public Chunk(Vector2ic position) {
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



}
