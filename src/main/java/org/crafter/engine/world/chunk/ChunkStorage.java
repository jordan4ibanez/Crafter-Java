package org.crafter.engine.world.chunk;

import org.joml.Vector2ic;

import java.util.HashMap;

/**
 * This is where all the chunks live!
 */
public final class ChunkStorage {

    private static final HashMap<Vector2ic, Chunk> container = new HashMap<>();

    private ChunkStorage(){}


    public static void addOrUpdate(Chunk chunk) {
        Vector2ic position = chunk.getPosition();
        if (hasPosition(position)) {
            System.out.println("ChunkStorage: Updated chunk (" + position.x() + ", " + position.y() + ")");
            container.get(position).setData(chunk.getData());
            return;
        }
        container.put(position, chunk);
        System.out.println("ChunkStorage: Stored chunk (" + position.x() + ", " + position.y() + ")");
    }

    private static boolean hasPosition(Vector2ic position) {
        return container.containsKey(position);
    }
}
