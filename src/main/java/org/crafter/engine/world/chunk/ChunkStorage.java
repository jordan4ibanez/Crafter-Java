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

    public static Chunk getChunk(Vector2ic position) {
        positionCheck(position, "getChunk");
        return container.get(position);
    }

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
        positionCheck(position, "getThreadSafeChunkClone");
        // Create a deep clone of the chunk
        return SerializationUtils.clone(container.get(position));
    }

    private static void positionCheck(final Vector2ic position, String methodName) {
        if (!hasPosition(position)) {
            throw new RuntimeException("ChunkStorage: Tried to get a non-existent chunk with method(" + methodName + ")! (" + position.x() + ", " + position.y() + ") does not exist! Did you check it's existence with (hasPosition)?");
        }
    }

    public static synchronized boolean hasPosition(Vector2ic position) {
        return container.containsKey(position);
    }
}
