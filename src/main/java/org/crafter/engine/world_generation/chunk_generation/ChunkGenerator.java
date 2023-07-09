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
package org.crafter.engine.world_generation.chunk_generation;

import org.crafter.engine.delta.DeltaObject;
import org.crafter.engine.utility.FastNoise;
import org.crafter.engine.world.biome.BiomeDefinitionContainer;
import org.crafter.engine.world.block.BlockDefinitionContainer;
import org.crafter.engine.world.chunk.Chunk;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The Chunk Generator is basically a thread facade.
 * You talk into it through this class, but you can't talk directly to the instance.
 */
public class ChunkGenerator implements Runnable {

    // Class local
    private static ChunkGenerator instance;
    private static Thread thread;

    // Instance local
    private final DeltaObject delta;
    private final FastNoise noise;

    private final BlockDefinitionContainer blockDefinitionContainer;

    private final BiomeDefinitionContainer biomeDefinitionContainer;

    private final BlockingQueue<Vector2ic> chunkRequestQueue;
    private final BlockingQueue<Chunk> chunkOutputQueue;
    private final AtomicBoolean shouldRun;

    private ChunkGenerator() {
        delta = new DeltaObject();
        noise = new FastNoise();
        blockDefinitionContainer = BlockDefinitionContainer.getThreadSafeDuplicate();
        biomeDefinitionContainer = BiomeDefinitionContainer.getThreadSafeDuplicate();
        chunkRequestQueue = new LinkedBlockingQueue<>();
        chunkOutputQueue = new LinkedBlockingQueue<>();
        shouldRun = new AtomicBoolean(true);
    }

    @Override
    public void run() {
        System.out.println("ChunkGenerator: Started!");
        System.out.println("ChunkGenerator: gotten blocks (" + Arrays.toString(blockDefinitionContainer.getAllBlockNames()) + ")!");
        while (shouldRun.get()) {
            sleepCheck();
            processInputQueue();
        }
        System.out.println("ChunkGenerator: Stopped!");
    }

    private void processInputQueue() {
        while (!chunkRequestQueue.isEmpty()) {
            generateChunk(chunkRequestQueue.remove());
        }
    }
    private void generateChunk(Vector2ic position) {
        //TODO: biome registration
        //TODO: noise generation
        Chunk chunk = processBiomesAndBlocks(new Chunk(position));

        chunkOutputQueue.add(chunk);
    }

    /**
     * Actual side effects happen here!
     * This is where biomes & blocks are applied into the data container (Chunk)
     */
    private Chunk processBiomesAndBlocks(Chunk chunk) {

//        Random random = new Random((int) (new Date().getTime()/1000));

        final int grass = blockDefinitionContainer.getDefinition("crafter:grass").getID(); //"crafter:grass"
        final int dirt = blockDefinitionContainer.getDefinition("crafter:dirt").getID(); //"crafter:dirt"
        final int stone = blockDefinitionContainer.getDefinition("crafter:stone").getID(); //"crafter:stone"

        final int xOffset = chunk.getX() * Chunk.getWidth();
        // Y is Z in 2d!
        final int zOffset = chunk.getZ() * Chunk.getDepth();

        for (int x = 0; x < Chunk.getWidth(); x++) {
            for (int z = 0; z < Chunk.getDepth(); z++) {

                final float calculatedNoise = noise.GetSimplex(x + xOffset,z + zOffset) + 0.5f;

//                System.out.println("test: " + test);

                final int height = (int)(calculatedNoise * 20.0f) + 40;

                for (int y = 0; y < Chunk.getHeight(); y++) {

                    int id = 0; // Start off as air

                    if (y < height - 6) {
                        id = stone;
                    }  else if (y < height - 1) {
                        id = dirt;
                    } else if (y < height) {
                        id = grass;
                    }
                    int index = Chunk.positionToIndex(x,y,z);
                    int blockData = chunk.getBlockData(index);

                    blockData = Chunk.setBlockID(blockData, id);
                    chunk.setBlockData(index, blockData);
                }
            }
        }
//        for (int i = 0; i < chunk.getArraySize(); i++) {
//            int blockData = chunk.getBlockData(i);
//
//
////            int randomlyChosen = random.nextInt(5);
//            int randomlyChosen = 0;
//
//            if (chunk.indexToPosition(i).y() < 30) {
//                randomlyChosen = 1;
//            }
//
//
//
//            BlockDefinition definition = blockDefinitionContainer.getDefinition(randomlyChosen); //"crafter:grass"
//            blockData = chunk.setBlockID(blockData, definition.getID());
//            chunk.setBlockData(i, blockData);
//        }
//        System.out.println("ChunkGenerator: Generated Chunk(" + chunk.getX() + ", " + chunk.getY() + ")");

        return chunk;
    }

    public boolean checkUpdate() {
        return !chunkOutputQueue.isEmpty();
    }
    public Chunk grabUpdate() {
        return chunkOutputQueue.remove();
    }

    private void sleepCheck() {
        if (chunkRequestQueue.size() == 0) {
            try {
                Thread.sleep(200);
            } catch (Exception e) {
                throw new RuntimeException("ChunkGenerator: Thread failed to sleep! " + e);
            }
        }
    }

    private void addRequest(Vector2ic requestedChunk) {
        this.chunkRequestQueue.add(requestedChunk);
    }

    private void stopThread() {
        shouldRun.set(false);
    }

    /**
     * This function is helpful in case something ever gets completely mangled.
     */
    private void debugQueueSizes() {
        System.out.println("ChunkGenerator: (INPUT: " + chunkRequestQueue.size() + ") | (OUTPUT: " + chunkOutputQueue.size() + ")");
    }

    // External statics from here below

    public static void start() {
        if (thread != null) {
            throw new RuntimeException("ChunkGenerator: Cannot start thread! It's already running!");
        }
        // Package the instance into the thread, so it can be talked to
        instance = new ChunkGenerator();
        thread = new Thread(instance);
        thread.start();
    }

    public static void stop() {
        nullCheck("stop");
        instance.stopThread();
    }

    public static void pushRequest(Vector2ic requestedChunk) {
        nullCheck("pushRequest");
        // Separate out thread data internal pointers
        instance.addRequest(new Vector2i(requestedChunk));
    }

    public static boolean hasUpdate() {
        return instance.checkUpdate();
    }
    public static Chunk getUpdate() {
        // This is an extremely important safety check
        if (!hasUpdate()) {
            throw new RuntimeException("ChunkGenerator: You need to check (hasUpdate) before you try to getUpdate!");
        }
        return instance.grabUpdate();
    }

    private static void nullCheck(String methodName) {
        if (thread == null) {
            throw new RuntimeException("ChunkGenerator: Cannot utilize method (" + methodName + ")! The THREAD has not been instantiated!");
        } if (instance == null) {
            throw new RuntimeException("ChunkGenerator: Cannot utilize method (" + methodName + ")! The INSTANCE has not been instantiated!");
        }
        if (!thread.isAlive()) {
            throw new RuntimeException("ChunkGenerator: Thread has crashed! Cannot utilize (" + methodName + ")!");
        }
    }
}
