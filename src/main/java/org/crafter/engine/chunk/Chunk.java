package org.crafter.engine.chunk;

import org.joml.Vector2i;
import org.joml.Vector2ic;

/**
 * A chunk of map data. It is 16 wide, 128 high, and 16 long.
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
}
