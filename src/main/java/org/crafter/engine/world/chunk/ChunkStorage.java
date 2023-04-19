package org.crafter.engine.world.chunk;

import org.apache.commons.lang3.SerializationUtils;
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

    public static synchronized Chunk getThreadSafeChunkClone(Vector2ic position) {
        if (!hasPosition(position)) {
            throw new RuntimeException("ChunkStorage: Tried to get a non-existent chunk! (" + position.x() + ", " + position.y() + ") does not exist! Did you check it's existence with (hasPosition)?");
        }
        // Create a deep clone of the chunk
        return SerializationUtils.clone(container.get(position));
    }

    public static boolean hasPosition(Vector2ic position) {
        return container.containsKey(position);
    }
}
