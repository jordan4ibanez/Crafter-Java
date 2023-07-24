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

import org.crafter.engine.world.block.BlockDefinitionContainer;
import org.joml.*;
import org.joml.Math;

import java.util.Arrays;
import java.util.HashMap;

import static org.crafter.Main.getClassicMapSize;

/**
 * This is where all the chunks live!
 * The ECMAScript block manipulation API is integrated into this due to how it functions within the game. It's cleaner this way.
 * The API consists of 2 parts:
 * 1.) Single blocks. Ideal for querying/updating 1 position.
 * 2.) Bulk blocks. Ideal for a bulk query/update.
 */
public final class ChunkStorage {

    private static final HashMap<Vector2ic, Chunk> container = new HashMap<>();
    private static final Vector2i workerVector2i = new Vector2i();
    private static final Vector3i workerVector3i = new Vector3i();
    private static final Vector3f positionWorker = new Vector3f();

    private static final Vector3i validatorMin = new Vector3i();
    private static final Vector3i validatorMax = new Vector3i();

    //todo Block manipulator variables

    // Todo: Attach methods for this to be reused with a limiter somehow
    private static final Vector3ic BLOCK_MANIPULATOR_LIMIT = new Vector3i(64,128,64);
    private static final Vector3i blockManipulatorMin = new Vector3i(0,0,0);
    private static final Vector3i blockManipulatorMax = new Vector3i(0,0,0);
    private static final Vector3i blockManipulatorSize = new Vector3i(0,0,0);
    private static int blockManipulatorYStride = 0;
    private static final int[] blockManipulatorData = new int[BLOCK_MANIPULATOR_LIMIT.x() * BLOCK_MANIPULATOR_LIMIT.y() * BLOCK_MANIPULATOR_LIMIT.z()];

    private ChunkStorage(){}

    public static synchronized Chunk getChunk(final Vector2ic position) {
        positionCheck(position, "getChunk");
        return container.get(position);
    }

    public static synchronized void addOrUpdate(final Chunk chunk) {
        Vector2ic position = chunk.getPosition();
        if (hasChunk(position)) {
            System.out.println("ChunkStorage: Updated chunk (" + position.x() + ", " + position.y() + ")");
            container.get(position).setData(chunk.getData());
            return;
        }
        container.put(position, chunk);
//        System.out.println("ChunkStorage: Stored chunk (" + position.x() + ", " + position.y() + ")");
    }

    public static synchronized Chunk getThreadSafeChunkClone(final Vector2ic position) {
        positionCheck(position, "getThreadSafeChunkClone");
        // Create a deep clone of the chunk
        return container.get(position).deepCopy();
    }

    private static synchronized void positionCheck(final Vector2ic position, final String methodName) {
        if (!hasChunk(position)) {
            throw new RuntimeException("ChunkStorage: Tried to get a non-existent chunk with method(" + methodName + ")! (" + position.x() + ", " + position.y() + ") does not exist! Did you check it's existence with (hasPosition)?");
        }
    }

    /**
     * Check if a chunk exists.
     * @param position Integral chunk position.
     * @return True or false. True if it exists.
     */
    public static synchronized boolean hasChunk(final Vector2ic position) {
        return container.containsKey(position);
    }

    //TODO note: the (GETTER) API methods start here!

    // These methods are aimed at the ECMAScript API, but are SPARSELY used in the internal engine because they can be expensive.
    // One example is: Collision detection. Very hard to optimize this in collision detection, so for now, I'm not going to.
    // This is implemented in ChunkStorage because you need to go into storage to talk to chunks!

    /**
     * Check if a chunk is loaded using a raw in world position. (Using this in bulk can be very expensive)
     * This is primarily aimed at collision detection and scripting API.
     * @param position The raw in world position.
     * @return True or false. True if the chunk is loaded.
     */
    public static synchronized boolean chunkIsLoaded(final Vector3fc position) {
        calculateChunkPosition(position);
        return container.containsKey(workerVector2i);
    }

    /**
     * Check if a chunk is loaded using a raw in world position. (Using this in bulk can be very expensive)
     * This is primarily aimed at collision detection and scripting API.
     * @param x The raw in world X position.
     * @param y The raw in world Y position.
     * @param z The raw in world Z position.
     * @return True or false. True if the chunk is loaded.
     */
    public static synchronized boolean chunkIsLoaded(final float x, final float y, final float z) {
        calculateChunkPosition(positionWorker.set(x,y,z));
        return container.containsKey(workerVector2i);
    }

    /**
     * Get the RAW block data using a raw in world position. Only use this if you know what you're doing. (Using this in bulk can be very expensive)
     * @param position The raw in world position.
     * @return The RAW block data.
     */
    public static synchronized int getBlockRAW(final Vector3fc position) {
        calculatePositionalData(position, "getBlockID");
        return internalGetRawBlockData();
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
        return internalGetRawBlockData();
    }

    /**
     * Get the block internal name using a raw in world position. (Using this in bulk can be very expensive)
     * @param position The raw in world position.
     * @return The block internal name.
     */
    public static synchronized String getBlockName(final Vector3fc position) {
        calculatePositionalData(position, "getBlockName");
        return internalGetBlockName();
    }

    /**
     * Get the block internal name using a raw in world position. (Using this in bulk can be very expensive)
     * @param x The raw in world X position.
     * @param y The raw in world Y position.
     * @param z The raw in world Z position.
     * @return The block internal name.
     */
    public static synchronized String getBlockName(final float x, final float y, final float z) {
        calculatePositionalData(positionWorker.set(x,y,z), "getBlockName");
        return internalGetBlockName();
    }

    /**
     * Get the block ID using a raw in world position. (Using this in bulk can be very expensive)
     * @param position The raw in world position.
     * @return The block ID.
     */
    public static synchronized int getBlockID(final Vector3fc position) {
        calculatePositionalData(position, "getBlockID");
        return Chunk.getBlockID(internalGetRawBlockData());
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
        return Chunk.getBlockID(internalGetRawBlockData());
    }

    /**
     * Get the block light level using a raw in world position. (Using this in bulk can be very expensive)
     * @param position The raw in world position.
     * @return The block light level.
     */
    public static synchronized int getBlockLightLevel(final Vector3fc position) {
        calculatePositionalData(position, "getBlockLight");
        return Chunk.getBlockLightLevel(internalGetRawBlockData());
    }

    /**
     * Get the block light level using a raw in world position. (Using this in bulk can be very expensive)
     * @param x The raw in world X position.
     * @param y The raw in world Y position.
     * @param z The raw in world Z position.
     * @return The block light level.
     */
    public static synchronized int getBlockLightLevel(final float x, final float y, final float z) {
        calculatePositionalData(positionWorker.set(x,y,z), "getBlockLight");
        return Chunk.getBlockLightLevel(internalGetRawBlockData());
    }

    /**
     * Get the block state using a raw in world position. (Using this in bulk can be very expensive)
     * @param position The raw in world position.
     * @return The block state.
     */
    public static synchronized int getBlockState(final Vector3fc position) {
        calculatePositionalData(position, "getBlockState");
        return Chunk.getBlockState(internalGetRawBlockData());
    }

    /**
     * Get the block state using a raw in world position. (Using this in bulk can be very expensive)
     * @param x The raw in world X position.
     * @param y The raw in world Y position.
     * @param z The raw in world Z position.
     * @return The block state.
     */
    public static synchronized int getBlockState(final float x, final float y, final float z) {
        calculatePositionalData(positionWorker.set(x,y,z), "getBlockState");
        return Chunk.getBlockState(internalGetRawBlockData());
    }

    //TODO note: The (SETTER) API methods begin here!

    /**
     * Set the RAW block data using a raw in world position. (Using this in bulk can be very expensive)
     * ONLY use this if you know what you are doing!
     * @param position The raw in world position.
     * @param rawData The new RAW data to set the block to.
     */
    public static synchronized void setBlockRAW(final Vector3fc position, final int rawData) {
        calculatePositionalData(position, "setBlockRAW");
        internalSetBlockRAWData(rawData);
    }

    /**
     * Set the RAW block data using a raw in world position. (Using this in bulk can be very expensive)
     *ONLY use this if you know what you are doing!
     * @param x The raw in world X position.
     * @param y The raw in world Y position.
     * @param z The raw in world Z position.
     * @param rawData The new RAW data to set the block to.
     */
    public static synchronized void setBlockRAW(final float x, final float y, final float z, final int rawData) {
        calculatePositionalData(positionWorker.set(x,y,z), "setBlockRAW");
        internalSetBlockRAWData(rawData);
    }

    /**
     * Set the block internal name using a raw in world position. (Using this in bulk can be very expensive)
     * @param position The raw in world position.
     * @param newName The new internal name to set the block to.
     */
    public static synchronized void setBlockName(final Vector3fc position, final String newName) {
        calculatePositionalData(position, "setBlockName");
        internalSetBlockName(newName);
    }

    /**
     * Set the block internal name using a raw in world position. (Using this in bulk can be very expensive)
     * @param x The raw in world X position.
     * @param y The raw in world Y position.
     * @param z The raw in world Z position.
     * @param newName The new internal name to set the block to.
     */
    public static synchronized void setBlockName(final float x, final float y, final float z, final String newName) {
        calculatePositionalData(positionWorker.set(x,y,z), "setBlockName");
        internalSetBlockName(newName);
    }


    /**
     * Set the block ID using a raw in world position. (Using this in bulk can be very expensive)
     * @param position The raw in world position.
     * @param newID The new ID to set the block to.
     */
    public static synchronized void setBlockID(final Vector3fc position, final int newID) {
        calculatePositionalData(position, "setBlockID");
        internalSetBlockIDChecked(newID);
    }

    /**
     * Set the block ID using a raw in world position. (Using this in bulk can be very expensive)
     * @param x The raw in world X position.
     * @param y The raw in world Y position.
     * @param z The raw in world Z position.
     * @param newID The new ID to set the block to.
     */
    public static synchronized void setBlockID(final float x, final float y, final float z, final int newID) {
        calculatePositionalData(positionWorker.set(x,y,z), "setBlockID");
        internalSetBlockIDChecked(newID);
    }

    /**
     * Set the block light level using a raw in world position. (Using this in bulk can be very expensive)
     * @param position The raw in world position.
     * @param newLightLevel The new light level to set the block to.
     */
    public static synchronized void setBlockLightLevel(final Vector3fc position, final int newLightLevel) {
        calculatePositionalData(position, "setBlockLight");
        internalSetBlockLightLevel(newLightLevel);
    }

    /**
     * Set the block light level using a raw in world position. (Using this in bulk can be very expensive)
     * @param x The raw in world X position.
     * @param y The raw in world Y position.
     * @param z The raw in world Z position.
     * @param newLightLevel The new light level to set the block to.
     */
    public static synchronized void setBlockLightLevel(final float x, final float y, final float z, final int newLightLevel) {
        calculatePositionalData(positionWorker.set(x,y,z), "setBlockLight");
        internalSetBlockLightLevel(newLightLevel);
    }

    /**
     * Set the block state using a raw in world position. (Using this in bulk can be very expensive)
     * @param position The raw in world position.
     * @param newState The new state to set the block to.
     */
    public static synchronized void setBlockState(final Vector3fc position, final int newState) {
        calculatePositionalData(position, "setBlockState");
        internalSetBlockState(newState);
    }

    /**
     * Set the block state using a raw in world position. (Using this in bulk can be very expensive)
     * @param x The raw in world X position.
     * @param y The raw in world Y position.
     * @param z The raw in world Z position.
     * @param newState The new state to set the block to.
     */
    public static synchronized void setBlockState(final float x, final float y, final float z, final int newState) {
        calculatePositionalData(positionWorker.set(x,y,z), "setBlockState");
        internalSetBlockState(newState);
    }

    //todo NOTE: The bulk block (GETTER) API methods start here!

    /**
     * Set the Block Manipulator min and max positions.
     * @param min Min raw in world position.
     * @param max Max raw in position.
     */
    public static synchronized void setBlockManipulatorPositions(final Vector3ic min, final Vector3ic max) {
        checkBlockManipulatorCubicArea(min,max);
        checkBlockManipulatorMinMaxValidity(min,max);
        checkBlockManipulatorSizeValidity(min,max);
        checkBlockManipulatorYAxisValidity(min,max);
        checkClassicOnlyBlockManipulatorMapBoundaries(min,max);

        // All safety checks have passed, now set the required data
        blockManipulatorMin.set(min);
        blockManipulatorMax.set(max);

        blockManipulatorSize.set(Math.abs(max.x() - min.x()), Math.abs(max.y() - min.y()), Math.abs(max.z() - min.z()));
        blockManipulatorYStride = blockManipulatorSize.x() * blockManipulatorSize.z();
    }

    /**
     * Set the Block Manipulator min and max positions.
     * @param minX Min raw in world X position.
     * @param minY Min raw in world Y position.
     * @param minZ Min raw in world Z position.
     * @param maxX Max raw in world X position.
     * @param maxY Max raw in world Y position.
     * @param maxZ Max raw in world Z position.
     */
    public static synchronized void setBlockManipulatorPositions(final int minX, final int minY, final int minZ, final int maxX, final int maxY, final int maxZ) {
        setBlockManipulatorPositions(validatorMin.set(minX,minY,minZ), validatorMax.set(maxX, maxY, maxZ));
    }

    /**
     * Read map data into the Block Manipulator array.
     */
    public static synchronized void blockManipulatorReadData() {

        final int chunkXMin = toChunkX(blockManipulatorMin.x());
        final int chunkXMax = toChunkX(blockManipulatorMax.x());
        final int chunkZMin = toChunkZ(blockManipulatorMin.z());
        final int chunkZMax = toChunkZ(blockManipulatorMax.z());

        //TODO: FORCE LOAD UP MAP CHUNKS!

        // fixme: this is a highly unoptimized prototype procedure to ensure this works
        final Vector2i tempPosition = new Vector2i();
        for (int chunkX = chunkXMin; chunkX <= chunkXMax; chunkX++) {
            for (int chunkZ = chunkZMin; chunkZ <= chunkZMax; chunkZ++) {

                // fixme: this will crash if the chunk isn't loaded!
                final Chunk tempWorker = container.get(tempPosition.set(chunkX,chunkZ));

                // todo: this needs some sort of automated internal positioning within the Block Manipulator array

                for (int x = blockManipulatorMin.x(); x <= blockManipulatorMax.x(); x++) {
                    for (int z = blockManipulatorMin.z(); z <= blockManipulatorMax.z(); z++) {

                        //TODO: THIS IS WHERE THE INTERNAL BOUNDARY SCAN IF THE CURRENT POSITION IS WITHIN THE CHUNK!
                        // THE POSITION SIMPLY NEEDS TO RUN A BOUNDARY CHECK ON THIS X,Z AXIS BECAUSE CHUNKS ARE 2D!
                        // THIS SHOULD SPEED UP PROCESSING BY QUITE A BIT.
                        // IF THE POSITION IS OUT OF BOUNDS FROM tempWorker CHUNK THEN CONTINUE!
                        // If the 2D position is not within this chunk, discard polling.
                        if (chunkX != toChunkX(x) || chunkZ != toChunkZ(z)) {
                            continue;
                        }

                        // Y is last because it is the only positioning that does not need to be checked if it walked into a new chunk
                        for (int y = blockManipulatorMin.y(); y <= blockManipulatorMax.y(); y++) {

                            //todo: This is where the Block Manipulator array will be getting set based on the contents
                            // of the tempWorker chunk.
                            // A very important note: You can utilize the existing math from Chunk.ArrayManipulation to
                            // figure out how to do this. Setting will be the exact same, only you will be setting data
                            // into the chunk instead of getting date.

                            blockManipulatorData[positionToBlockManipulatorArrayPosition(x,y,z)] = tempWorker.getBlockData(internalX(x), y, internalZ(z));

                        }
                    }
                }


                // Placeholder
                System.out.println("BlockManipulator: Reading chunk (" + chunkX + " " + chunkZ + ")");

                //todo: this is where chunk updates get dispatched
                // Dispatch chunks here ()
            }
        }
    }

    /**
     * Get raw data within the Block Manipulator array.
     * @param position Raw in world position.
     * @return Raw block data.
     */
    public static synchronized int getBlockManipulatorData(final Vector3ic position) {
        return blockManipulatorData[positionToBlockManipulatorArrayPosition(position.x(),position.y(),position.z())];
    }

    /**
     * Get raw data within the Block Manipulator array.
     * @param x Raw in world X position.
     * @param y Raw in world Y position.
     * @param z Raw in world Z position.
     * @return Raw block data.
     */
    public static synchronized int getBlockManipulatorData(final int x, final int y, final int z) {
        return blockManipulatorData[positionToBlockManipulatorArrayPosition(x,y,z)];
    }

    /**
     * Set raw data within the Block Manipulator array.
     * @param position Raw in world position.
     * @param rawData Raw block data.
     */
    public static synchronized void setBlockManipulatorData(final Vector3ic position, final int rawData) {
        blockManipulatorData[positionToBlockManipulatorArrayPosition(position.x(), position.y(), position.z())] = rawData;
    }

    /**
     * Set raw data within the Block Manipulator array.
     * @param x Raw in world X position.
     * @param y Raw in world Y position.
     * @param z Raw in world Z position.
     * @param rawData Raw block data.
     */
    public static synchronized void setBlockManipulatorData(final int x, final int y, final int z, final int rawData) {
        blockManipulatorData[positionToBlockManipulatorArrayPosition(x,y,z)] = rawData;
    }

    /**
     * write the Block Manipulator array data into the world.
     */
    public static synchronized void writeManipulatorWriteData() {

        final int chunkXMin = toChunkX(blockManipulatorMin.x());
        final int chunkXMax = toChunkX(blockManipulatorMax.x());
        final int chunkZMin = toChunkZ(blockManipulatorMin.z());
        final int chunkZMax = toChunkZ(blockManipulatorMax.z());

        //TODO: FORCE LOAD UP MAP CHUNKS!

        // fixme: this is a highly unoptimized prototype procedure to ensure this works
        final Vector2i tempPosition = new Vector2i();
        for (int chunkX = chunkXMin; chunkX <= chunkXMax; chunkX++) {
            for (int chunkZ = chunkZMin; chunkZ <= chunkZMax; chunkZ++) {

                // fixme: this will crash if the chunk isn't loaded!
                final Chunk tempWorker = container.get(tempPosition.set(chunkX,chunkZ));

                // todo: this needs some sort of automated internal positioning within the Block Manipulator array

                for (int x = blockManipulatorMin.x(); x <= blockManipulatorMax.x(); x++) {
                    for (int z = blockManipulatorMin.z(); z <= blockManipulatorMax.z(); z++) {

                        if (chunkX != toChunkX(x) || chunkZ != toChunkZ(z)) {
                            continue;
                        }

                        // Y is last because it is the only positioning that does not need to be checked if it walked into a new chunk
                        for (int y = blockManipulatorMin.y(); y <= blockManipulatorMax.y(); y++) {

                            // This mess writes the data into the chunk
                            tempWorker.setBlockData(internalX(x),y,internalZ(z), blockManipulatorData[positionToBlockManipulatorArrayPosition(x,y,z)]);
                        }
                    }
                }

                // Placeholder
                System.out.println("BlockManipulator: Writing chunk (" + chunkX + " " + chunkZ + ")");

                //todo: this is where chunk updates get dispatched
                // Dispatch chunks here ()
            }
        }
    }


    //todo NOTE: Everything below this is SPECIFICALLY tailored to make the API elements easier to read/write.


    // ----- BLOCK MANIPULATOR GETTER/SETTER HELPERS BEGIN HERE -----

    /**
     * INTERNAL ONLY translates a raw position real world position within the Block Manipulator into an index.
     * WARNING: UNCHECKED!
     * @param positionX 0 based X position.
     * @param positionY 0 based Y position.
     * @param positionZ 0 based Z position.
     * @return Index into the Block Manipulator array.
     */
    private static int positionToBlockManipulatorArrayPosition(final int positionX, final int positionY, final int positionZ) {
        // This x,y,z portion transforms the real position into a base 0 position.
        final int x = positionX - blockManipulatorMin.x();
        final int y = positionY - blockManipulatorMin.y();
        final int z = positionZ - blockManipulatorMin.z();

        return (y * blockManipulatorYStride) + (z * blockManipulatorSize.z()) + x;
    }

    /**
     * INTERNAL ONLY translates a raw index into the Block Manipulator array into a raw real world position.
     * WARNING: UNCHECKED!
     * @param index Index into the Block Manipulator array.
     * @return Raw real world position.
     */
    public static Vector3ic indexToPosition(int index) {
        return new Vector3i(
                index % blockManipulatorSize.x(),
                (index / blockManipulatorYStride) % blockManipulatorSize.y(),
                (index / blockManipulatorSize.z()) % blockManipulatorSize.z()
        );
    }

    /**
     * CLASSIC ONLY, INTERNAL ONLY check for the map boundaries within classic.
     * @param min Min position.
     * @param max Max position.
     */
    private static void checkClassicOnlyBlockManipulatorMapBoundaries(final Vector3ic min, final Vector3ic max) {
        final int mapSizeInChunks = getClassicMapSize();
        final int xLimit = Chunk.getWidth() * mapSizeInChunks;
        final int zLimit = Chunk.getDepth() * mapSizeInChunks;
        if (Math.abs(min.x()) > xLimit) {
            throwClassicBlockManipulatorOutOfBoundsError("min", "X", -xLimit, min.x());
        } else if (Math.abs(max.x()) > xLimit) {
            throwClassicBlockManipulatorOutOfBoundsError("max", "X", xLimit, max.x());
        } else if (Math.abs(min.z()) > zLimit) {
            throwClassicBlockManipulatorOutOfBoundsError("min", "Z", -zLimit, min.z());
        } else if (Math.abs(max.z()) > zLimit) {
            throwClassicBlockManipulatorOutOfBoundsError("max", "Z", zLimit, max.z());
        }
    }

    /**
     * CLASSIC ONLY, INTERNAL ONLY boilerplate reducer for throwing an error if the Block Manipulator is set out of bounds.
     * @param minMax Min or max as a String.
     * @param axis X or z as a String.
     * @param limit Integral limit on the x or z axis.
     * @param gotten The gotten position on that axis.
     */
    private static void throwClassicBlockManipulatorOutOfBoundsError(final String minMax, final String axis, final int limit, final int gotten) {
        throw new RuntimeException("ChunkStorage: ERROR! Tried to set axis (" + axis + " " + minMax + ") to (" + gotten + ")! Limit is: " + limit + "!");
    }

    /**
     * INTERNAL ONLY validator for the min and max Y axis position to ensure it is within the map's boundaries.
     * @param min Min position.
     * @param max Max position.
     */
    private static void checkBlockManipulatorYAxisValidity(final Vector3ic min, final Vector3ic max) {
        if (min.y() < 0) {
            throwBlockManipulatorYAxisError("min", min.y());
        } else if (max.y() >= Chunk.getHeight()) {
            throwBlockManipulatorYAxisError("max", max.y());
        }
    }

    /**
     * INTERNAL ONLY boilerplate reducer for throwing an error if the Block Manipulator Y axis is set out of bounds.
     * @param minMax Min or max as a String.
     * @param yPosition Invalid Y position.
     */
    private static void throwBlockManipulatorYAxisError(final String minMax, final int yPosition) {
        throw new RuntimeException("ChunkStorage: Attempted to set (" + minMax + ") BlockManipulator outside of map (Y axis) boundaries. Limit: 0 - " + (Chunk.getHeight() - 1) +" | Attempt: " + yPosition);
    }

    /**
     * INTERNAL ONLY validator for the min and max positions to ensure it makes a cuboid.
     * @param min Min position.
     * @param max Max position.
     */
    private static void checkBlockManipulatorSizeValidity(final Vector3ic min, final Vector3ic max) {
        if (Math.abs(max.x() - min.x()) >= BLOCK_MANIPULATOR_LIMIT.x()) {
            throwBlockManipulatorSizeError("X", BLOCK_MANIPULATOR_LIMIT.x());
        } else if (Math.abs(max.y() - min.y()) >= BLOCK_MANIPULATOR_LIMIT.y()) {
            throwBlockManipulatorSizeError("Y", BLOCK_MANIPULATOR_LIMIT.y());
        } else if (Math.abs(max.z() - min.z()) >= BLOCK_MANIPULATOR_LIMIT.z()) {
            throwBlockManipulatorSizeError("Z", BLOCK_MANIPULATOR_LIMIT.z());
        }
    }

    /**
     * INTERNAL ONLY boilerplate reducer for throwing an error if the Block Manipulator is set too big.
     * @param axis X,Y,Z as String.
     * @param axisLimit Integral axis limit.
     */
    private static void throwBlockManipulatorSizeError(final String axis, final int axisLimit) {
        throw new RuntimeException("ChunkStorage: Attempted to set Block Manipulator past size limit! (" + axis +" axis) limit is " + axisLimit + "!");
    }

    /**
     * INTERNAL ONLY validator for the min and max positions assigned to the Block Manipulator by the ECMAScript API.
     * @param min Min position.
     * @param max Max position.
     */
    private static void checkBlockManipulatorMinMaxValidity(final Vector3ic min, final Vector3ic max) {
        // Min is not allowed to be further than max, but, it can be equal.
        // The cubic size is tested in checkBlockManipulatorCubicArea().
        if (min.x() > max.x()) {
            throwBlockManipulatorPositionError("X");
        } else if (min.y() > max.y()) {
            throwBlockManipulatorPositionError("Y");
        } else if (min.z() > max.z()) {
            throwBlockManipulatorPositionError("Z");
        }
    }

    /**
     * INTERNAL ONLY boilerplate reducer for throwing an error setting the Block Manipulator positions.
     * @param axis X,Y,Z axis as String.
     */
    private static void throwBlockManipulatorPositionError(final String axis) {
        throw new RuntimeException("ChunkStorage: Attempted to set Block Manipulator to invalid min/max! (" + axis + " axis) min is greater than or equal to max!");
    }

    /**
     * INTERNAL ONLY validator to ensure that the cubic area for the Block Manipulator is greater than 1.
     * @param min Min position.
     * @param max Max position.
     */
    private static void checkBlockManipulatorCubicArea(final Vector3ic min, final Vector3ic max) {
        // This is 0 counted so it must be + 1
        if ((Math.abs(max.x() - min.x()) + 1) * (Math.abs(max.y() - min.y()) + 1) * (Math.abs(max.z() - min.z()) + 1) <= 1 ) {
            throw new RuntimeException("ChunkStorage: Attempted to set the Block Manipulator to 1 block cubic area! Use the single block getters/setters instead!");
        }
    }


    // ----- SINGLE BLOCK GETTER/SETTER HELPERS BEGIN HERE -----

    /**
     * INTERNAL ONLY check to ensure light level is within acceptable range.
     * @param newLightLevel The new block light level.
     */
    private static void internalCheckLightLevelForRAW(final int newLightLevel) {
        if (newLightLevel < 0 || newLightLevel > 15) {
            throw new RuntimeException("ChunkStorage: Attempted to set a block's light level to (" + newLightLevel + ")! Acceptable range: 0-15");
        }
    }

    /**
     * INTERNAL ONLY usage of getting block INTERNAL NAME. Used to clean up API methods above.
     * @return The INTERNAL NAME of the block.
     */
    private static String internalGetBlockName() {
        final int blockID = Chunk.getBlockID(internalGetRawBlockData());
        // Todo: optimize this - Can get a main instance once, then talk to the internal pointer automatically without having to get it every time
        return BlockDefinitionContainer.getMainInstance().getDefinition(blockID).getInternalName();
    }

    /**
     * INTERNAL ONLY usage of getting the RAW block data (integer) from the raw in world coordinate provided to calculatePositionalData.
     * @return The RAW block data. Will need to work with ChunkBitManipulation to use it!
     */
    private static int internalGetRawBlockData() {
        return container.get(workerVector2i).getBlockData(workerVector3i);
    }

    /**
     * INTERNAL ONLY usage of setting RAW block data. Used to clean up API methods above.
     * @param rawData The RAW block data to set. (Checked)
     */
    private static void internalSetBlockRAWData(final int rawData) {
        final int newID = Chunk.getBlockID(rawData);
        BlockDefinitionContainer.getMainInstance().checkExistence(newID);
        internalCheckLightLevelForRAW(Chunk.getBlockLightLevel(rawData));
        container.get(workerVector2i).setBlockData(workerVector3i, rawData);
    }

    /**
     * INTERNAL ONLY usage of setting block ID. Used to clean up API methods above.
     * @param newID The new block ID.
     */
    private static void internalSetBlockID(final int newID, final boolean checkID) {
        if (checkID) {
            BlockDefinitionContainer.getMainInstance().checkExistence(newID);
        }
        final Chunk currentChunk = container.get(workerVector2i);
        final int workerData = Chunk.setBlockID(currentChunk.getBlockData(workerVector3i), newID);
        currentChunk.setBlockData(workerVector3i, workerData);
    }

    /**
     * INTERNAL ONLY bolt on chainer to allow cleaner code in method above.
     * THIS METHOD CHECKS IF THE ID EXISTS!
     * @param newID The new block ID.
     */
    private static void internalSetBlockIDChecked(final int newID) {
        internalSetBlockID(newID, true);
    }

    /**
     * INTERNAL ONLY bolt on chainer to allow cleaner code in method above.
     * THIS METHOD DOES NOT CHECK IF THE ID EXISTS!
     * @param newID The new block ID.
     */
    private static void internalSetBlockIDUnchecked(final int newID) {
        internalSetBlockID(newID, false);
    }

    /**
     * INTERNAL ONLY usage of setting block ID by name. Used to clean up API methods above.
     * @param newName The new block name.
     */
    private static void internalSetBlockName(final String newName) {
        // Todo: optimize this - Can get a main instance once, then talk to the internal pointer automatically without having to get it every time
        final int newID = BlockDefinitionContainer.getMainInstance().getDefinition(newName).getID();
        internalSetBlockIDUnchecked(newID);
    }

    /**
     * INTERNAL ONLY usage of getting & setting block light. Used to clean up API methods above.
     * @param newLightLevel The new block light level.
     */
    private static void internalSetBlockLightLevel(final int newLightLevel) {
        final Chunk currentChunk = container.get(workerVector2i);
        final int workerData = Chunk.setBlockLight(currentChunk.getBlockData(workerVector3i), newLightLevel);
        currentChunk.setBlockData(workerVector3i, workerData);
    }

    /**
     * INTERNAL ONLY usage of getting & setting block state. Used to clean up API methods above.
     * @param newState The new block state.
     */
    private static void internalSetBlockState(final int newState) {
        final Chunk currentChunk = container.get(workerVector2i);
        final int workerData = Chunk.setBlockState(currentChunk.getBlockData(workerVector3i), newState);
        currentChunk.setBlockData(workerVector3i, workerData);
    }

    /**
     * INTERNAL ONLY.
     * Automates calculations for all required positional data. Throws an error if the chunk does not exist.
     * @param position The raw in world position.
     * @param methodName The method which this method was called from.
     */
    private static void calculatePositionalData(final Vector3fc position, final String methodName) {
        calculateChunkPosition(position);
        positionCheck(workerVector2i, methodName);
        calculateInternalPosition(position);
    }

    /**
     * INTERNAL ONLY.
     * Automates calculations to retrieve the chunk from the raw in world position supplied.
     * @param position The raw in world position.
     */
    private static void calculateChunkPosition(final Vector3fc position) {
        final int chunkX = toChunkX(position.x());
        final int chunkZ = toChunkZ(position.z());
        workerVector2i.set(chunkX, chunkZ);
    }

    /**
     * INTERNAL ONLY.
     * Automates calculations to retrieve the block inside the chunk from the raw in world position supplied.
     * @param position The raw in world position.
     */
    private static void calculateInternalPosition(final Vector3fc position) {
        final int internalChunkX = internalX(position.x());
        final int internalChunkZ = internalZ(position.z());
        final int internalChunkY = (int) Math.floor(position.y());
        workerVector3i.set(internalChunkX,internalChunkY,internalChunkZ);
    }

    /**
     * INTERNAL ONLY.
     * Calculate which chunk raw in world coordinates are in on the X axis.
     * @param x The raw in world X position.
     * @return The X position of the chunk.
     */
    private static int toChunkX(final float x) {
        return (int) Math.floor(x / Chunk.getWidth());
    }

    /**
     * INTERNAL ONLY.
     * Calculate which chunk raw in world coordinates are in on the Z axis.
     * @param z The raw in world Z position.
     * @return The Z position of the chunk.
     */
    private static int toChunkZ(final float z) {
        return (int) Math.floor(z / Chunk.getDepth());
    }

    /**
     * INTERNAL ONLY.
     * Calculate the position from raw in world coordinates inside a chunk on the X axis.
     * @param x The raw in world X position.
     * @return The X position inside the chunk.
     */
    private static int internalX(float x) {
        x = x < 0 ? Math.abs(x) - 1 : x;
        return (int) Math.floor(x % Chunk.getWidth());
    }

    /**
     * INTERNAL ONLY.
     * Calculate the position from raw in world coordinates inside a chunk on the Z axis.
     * @param z The raw in world Z position.
     * @return The Z position inside the chunk.
     */
    private static int internalZ(float z) {
        z = z < 0 ? Math.abs(z) - 1 : z;
        return (int) Math.floor(z % Chunk.getDepth());
    }



    // TESTING ONLY. DO NOT USE THESE!

    // This is specifically utilized for making sure this DOES NOT change!
    /**
     * DO NOT USE
     */
    public static int UNIT_TEST_VERIFICATION_CHUNK_X(final float x) {
        return toChunkX(x);
    }
    /**
     * DO NOT USE
     */
    public static int UNIT_TEST_VERIFICATION_INTERNAL_POSITION_X(final float x) {
        return internalX(x);
    }
    /**
     * DO NOT USE
     */
    public static int UNIT_TEST_VERIFICATION_CHUNK_Z(final float z) {
        return toChunkZ(z);
    }
    /**
     * DO NOT USE
     */
    public static int UNIT_TEST_VERIFICATION_INTERNAL_POSITION_Z(final float z) {
        return internalZ(z);
    }

    public static void UNIT_TEST_VERIFICATION_RESET_BLOCK_MANIPULATOR() {
        Arrays.fill(blockManipulatorData, 0);
    }

}
