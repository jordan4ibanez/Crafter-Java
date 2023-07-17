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

import org.joml.*;
import org.joml.Math;

import java.util.HashMap;

/**
 * This is where all the chunks live!
 */
public final class ChunkStorage {

    private static final HashMap<Vector2ic, Chunk> container = new HashMap<>();
    private static final Vector2i workerVector2i = new Vector2i();
    private static final Vector3i workerVector3i = new Vector3i();

    private ChunkStorage(){}

    public static Chunk getChunk(Vector2ic position) {
        positionCheck(position, "getChunk");
        return container.get(position);
    }

    public static void addOrUpdate(Chunk chunk) {
        Vector2ic position = chunk.getPosition();
        if (hasChunk(position)) {
            System.out.println("ChunkStorage: Updated chunk (" + position.x() + ", " + position.y() + ")");
            container.get(position).setData(chunk.getData());
            return;
        }
        container.put(position, chunk);
//        System.out.println("ChunkStorage: Stored chunk (" + position.x() + ", " + position.y() + ")");
    }

    public static synchronized Chunk getThreadSafeChunkClone(Vector2ic position) {
        positionCheck(position, "getThreadSafeChunkClone");
        // Create a deep clone of the chunk
        return container.get(position).deepCopy();
    }

    private static void positionCheck(final Vector2ic position, String methodName) {
        if (!hasChunk(position)) {
            throw new RuntimeException("ChunkStorage: Tried to get a non-existent chunk with method(" + methodName + ")! (" + position.x() + ", " + position.y() + ") does not exist! Did you check it's existence with (hasPosition)?");
        }
    }

    public static synchronized boolean hasChunk(Vector2ic position) {
        return container.containsKey(position);
    }

    // These methods are aimed at the ECMAScript API, but are SPARSELY used in the internal engine because they can be expensive.
    // One example is: Collision detection. Very hard to optimize this in collision detection, so for now, I'm not going to.
    // This is implemented in ChunkStorage because you need to go into storage to talk to chunks!

    public static synchronized int getBlock(Vector3fc position) {
        final int chunkX = rawPositionXToChunk(position.x());
        final int chunkZ = rawPositionZToChunk(position.z());
        positionCheck(workerVector2i.set(chunkX, chunkZ), "getBlock");

        final int positionWithinChunkX = rawPositionXToPositionWithinChunkX(position.x());
        final int positionWithinChunkZ = rawPositionZToPositionWithinChunkZ(position.z());
        final int positionWithinChunkY = (int) Math.floor(position.y());

        return container.get(workerVector2i).getBlockData(workerVector3i.set(positionWithinChunkX,positionWithinChunkY,positionWithinChunkZ));
    }

    private static int rawPositionXToChunk(final float x) {
        return (int) Math.floor(x / Chunk.getWidth());
    }

    private static int rawPositionZToChunk(final float z) {
        return (int) Math.floor(z / Chunk.getDepth());
    }

    private static int rawPositionXToPositionWithinChunkX(float x) {
        x = x < 0 ? Math.abs(x) - 1 : x;
        return (int) Math.floor(x % Chunk.getWidth());
    }

    private static int rawPositionZToPositionWithinChunkZ(float z) {
        z = z < 0 ? Math.abs(z) - 1 : z;
        return (int) Math.floor(z % Chunk.getDepth());
    }


}
