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
    private static final Vector3f positionWorker = new Vector3f();

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

    //TODO note: the (GETTER) API methods start here!

    /**
     * Check if a chunk exists.
     * @param position Integral chunk position.
     * @return True or false. True if it exists.
     */
    public static synchronized boolean hasChunk(Vector2ic position) {
        return container.containsKey(position);
    }

    // These methods are aimed at the ECMAScript API, but are SPARSELY used in the internal engine because they can be expensive.
    // One example is: Collision detection. Very hard to optimize this in collision detection, so for now, I'm not going to.
    // This is implemented in ChunkStorage because you need to go into storage to talk to chunks!

    /**
     * Get the RAW block data using a raw in world position. Only use this if you know what you're doing. (Using this in bulk can be very expensive)
     * @param position The raw in world position.
     * @return The RAW block data.
     */
    public static synchronized int getBlockRAW(Vector3fc position) {
        calculatePositionalData(position, "getBlockID");
        return getRawBlockData();
    }

    /**
     * Get the RAW block data using a raw in world position. Only use this if you know what you're doing. (Using this in bulk can be very expensive)
     * @param x The raw in world X position.
     * @param y The raw in world Y position.
     * @param z The raw in world Z position.
     * @return The RAW block data.
     */
    public static synchronized int getBlockRAW(final float x, final float y, final float z) {
        calculatePositionalData(positionWorker.set(x,y,z), "getBlockID");
        return getRawBlockData();
    }


    /**
     * Get the block ID using a raw in world position. (Using this in bulk can be very expensive)
     * @param position The raw in world position.
     * @return The block ID.
     */
    public static synchronized int getBlockID(Vector3fc position) {
        calculatePositionalData(position, "getBlockID");
        return Chunk.getBlockID(getRawBlockData());
    }

    /**
     * Get the block ID using a raw in world position. (Using this in bulk can be very expensive)
     * @param x The raw in world X position.
     * @param y The raw in world Y position.
     * @param z The raw in world Z position.
     * @return The block ID.
     */
    public static synchronized int getBlockID(final float x, final float y, final float z) {
        calculatePositionalData(positionWorker.set(x,y,z), "getBlockID");
        return Chunk.getBlockID(getRawBlockData());
    }

    /**
     * Get the block light level using a raw in world position. (Using this in bulk can be very expensive)
     * @param position The raw in world position.
     * @return The block light level.
     */
    public static synchronized int getBlockLight(Vector3fc position) {
        calculatePositionalData(position, "getBlockLight");
        return Chunk.getBlockLight(getRawBlockData());
    }

    /**
     * Get the block light level using a raw in world position. (Using this in bulk can be very expensive)
     * @param x The raw in world X position.
     * @param y The raw in world Y position.
     * @param z The raw in world Z position.
     * @return The block light level
     */
    public static synchronized int getBlockLight(final float x, final float y, final float z) {
        calculatePositionalData(positionWorker.set(x,y,z), "getBlockLight");
        return Chunk.getBlockLight(getRawBlockData());
    }

    /**
     * Get the block state using a raw in world position. (Using this in bulk can be very expensive)
     * @param position The raw in world position.
     * @return The block state.
     */
    public static synchronized int getBlockSate(Vector3fc position) {
        calculatePositionalData(position, "getBlockState");
        return Chunk.getBlockState(getRawBlockData());
    }

    /**
     * Get the block state using a raw in world position. (Using this in bulk can be very expensive)
     * @param x The raw in world X position.
     * @param y The raw in world Y position.
     * @param z The raw in world Z position.
     * @return The block state.
     */
    public static synchronized int getBlockSate(final float x, final float y, final float z) {
        calculatePositionalData(positionWorker.set(x,y,z), "getBlockState");
        return Chunk.getBlockState(getRawBlockData());
    }

    // Everything below this is SPECIFICALLY tailored to make the API elements easier to write out.

    /**
     * Retrieves the RAW block data (integer) from the raw in world coordinate provided to calculatePositionalData.
     * @return The RAW block data. Will need to work with ChunkBitManipulation to use it!
     */
    private static int getRawBlockData() {
        return container.get(workerVector2i).getBlockData(workerVector3i);
    }

    /**
     * Automates calculations for all required positional data. Throws an error if the chunk does not exist.
     * @param position The raw in world position.
     * @param methodName The method which this method was called from.
     */
    private static void calculatePositionalData(Vector3fc position, String methodName) {
        calculateChunkPosition(position);
        positionCheck(workerVector2i, methodName);
        calculateInternalPosition(position);
    }

    /**
     * Automates calculations to retrieve the chunk from the raw in world position supplied.
     * @param position The raw in world position.
     */
    private static void calculateChunkPosition(Vector3fc position) {
        final int chunkX = toChunkX(position.x());
        final int chunkZ = toChunkZ(position.z());
        workerVector2i.set(chunkX, chunkZ);
    }

    /**
     * Automates calculations to retrieve the block inside the chunk from the raw in world position supplied.
     * @param position The raw in world position.
     */
    private static void calculateInternalPosition(Vector3fc position) {
        final int internalChunkX = internalX(position.x());
        final int internalChunkZ = internalZ(position.z());
        final int internalChunkY = (int) Math.floor(position.y());
        workerVector3i.set(internalChunkX,internalChunkY,internalChunkZ);
    }

    /**
     * Calculate which chunk raw in world coordinates are in on the X axis.
     * @param x The raw in world X position.
     * @return The X position of the chunk.
     */
    private static int toChunkX(final float x) {
        return (int) Math.floor(x / Chunk.getWidth());
    }

    /**
     * Calculate which chunk raw in world coordinates are in on the Z axis.
     * @param z The raw in world Z position.
     * @return The Z position of the chunk.
     */
    private static int toChunkZ(final float z) {
        return (int) Math.floor(z / Chunk.getDepth());
    }

    /**
     * Calculate the position from raw in world coordinates inside a chunk on the X axis.
     * @param x The raw in world X position.
     * @return The X position inside the chunk.
     */
    private static int internalX(float x) {
        x = x < 0 ? Math.abs(x) - 1 : x;
        return (int) Math.floor(x % Chunk.getWidth());
    }

    /**
     * Calculate the position from raw in world coordinates inside a chunk on the Z axis.
     * @param z The raw in world Z position.
     * @return The Z position inside the chunk.
     */
    private static int internalZ(float z) {
        z = z < 0 ? Math.abs(z) - 1 : z;
        return (int) Math.floor(z % Chunk.getDepth());
    }

    // This is specifically utilized for making sure this DOES NOT change!
    public static int UNIT_TEST_VERIFICATION_CHUNK_X(final float x) {
        return toChunkX(x);
    }
    public static int UNIT_TEST_VERIFICATION_INTERNAL_POSITION_X(final float x) {
        return internalX(x);
    }
    public static int UNIT_TEST_VERIFICATION_CHUNK_Z(final float z) {
        return toChunkZ(z);
    }
    public static int UNIT_TEST_VERIFICATION_INTERNAL_POSITION_Z(final float z) {
        return internalZ(z);
    }

}
