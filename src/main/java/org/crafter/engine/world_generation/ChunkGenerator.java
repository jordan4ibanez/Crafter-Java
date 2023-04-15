package org.crafter.engine.world_generation;

import org.crafter.engine.utility.FastNoise;
import org.crafter.engine.world.block.BlockDefinitionContainer;
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
    private final FastNoise noise;

    private final BlockDefinitionContainer blockDefinitionContainer;

    BlockingQueue<Vector2ic> chunkQueue;
    AtomicBoolean shouldRun;

    private ChunkGenerator() {
        noise = new FastNoise();
        blockDefinitionContainer = BlockDefinitionContainer.getThreadSafeDuplicate();
        chunkQueue = new LinkedBlockingQueue<>();
        shouldRun = new AtomicBoolean(true);
    }

    @Override
    public void run() {
        System.out.println("ChunkGenerator: Started!");
        System.out.println("ChunkGenerator: gotten blocks (" + Arrays.toString(blockDefinitionContainer.getAllBlockNames()) + ")!");
        while (shouldRun.get()) {
            System.out.println("ChunkGenerator: I am a runner");
        }
    }

    private void addRequest(Vector2ic requestedChunk) {
        this.chunkQueue.add(requestedChunk);
    }

    private void stopThread() {
        shouldRun.set(false);
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

    public static void add(Vector2ic requestedChunk) {
        nullCheck("add");
        instance.addRequest(requestedChunk);
    }

    private static void nullCheck(String methodName) {
        if (thread == null) {
            throw new RuntimeException("ChunkGenerator: Cannot utilize method (" + methodName + ")! The THREAD has not been instantiated!");
        } if (instance == null) {
            throw new RuntimeException("ChunkGenerator: Cannot utilize method (" + methodName + ")! The INSTANCE has not been instantiated!");
        }
    }
}
