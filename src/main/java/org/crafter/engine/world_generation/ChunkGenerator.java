package org.crafter.engine.world_generation;

import org.crafter.engine.delta.DeltaObject;
import org.crafter.engine.utility.FastNoise;
import org.crafter.engine.world.block.BlockDefinition;
import org.crafter.engine.world.block.BlockDefinitionContainer;
import org.crafter.engine.world.chunk.Chunk;
import org.joml.Vector2ic;
import org.joml.Vector3ic;

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

    private final BlockingQueue<Vector2ic> chunkRequestQueue;
    private final BlockingQueue<Chunk> chunkOutputQueue;
    private final AtomicBoolean shouldRun;

//    private float sleepTimer;

    private ChunkGenerator() {
        delta = new DeltaObject();
        noise = new FastNoise();
        blockDefinitionContainer = BlockDefinitionContainer.getThreadSafeDuplicate();
        chunkRequestQueue = new LinkedBlockingQueue<>();
        chunkOutputQueue = new LinkedBlockingQueue<>();
        shouldRun = new AtomicBoolean(true);
//        sleepTimer = 0.0f;
    }

    @Override
    public void run() {
        System.out.println("ChunkGenerator: Started!");
        System.out.println("ChunkGenerator: gotten blocks (" + Arrays.toString(blockDefinitionContainer.getAllBlockNames()) + ")!");
        while (shouldRun.get()) {
            sleepCheck();
            processInputQueue();
        }
    }

    private void processInputQueue() {
//        System.out.println("------------------ BEGIN REQUEST PROCESSING ----------------");
//        boolean processed = false;
        while (!chunkRequestQueue.isEmpty()) {
//            processed = true;
            generateChunk(chunkRequestQueue.remove());
            // Output will always start at 1 because a chunk was just inserted
//            debugQueueSizes();
        }
//        if (processed) {
//            System.out.println("ChunkGenerator: Done!");
//        }
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
        for (int i = 0; i < chunk.getArraySize(); i++) {
            int chunkData = chunk.getBlockData(i);
            // Successful test of failure state in java
            BlockDefinition definition = blockDefinitionContainer.getDefinition("crafter:stone");
            chunk.setBlockID(chunkData, definition.getID());
            chunk.setBlockData(i, chunkData);
            Vector3ic blockPositionInChunk = chunk.indexToPosition(i);
//            System.out.println("ChunkGenerator: block (" + blockPositionInChunk.x() + ", " + blockPositionInChunk.y() + ", " + blockPositionInChunk.z() + ") is (" + definition.getInternalName() + " aka " + definition.getID() + ")");
        }
        System.out.println("ChunkGenerator: Generated Chunk(" + chunk.getX() + ", " + chunk.getY() + ")");

        return chunk;
    }

    public boolean checkUpdate() {
        return !chunkOutputQueue.isEmpty();
    }
    public Chunk grabUpdate() {
//        debugQueueSizes();
        return chunkOutputQueue.remove();
    }

    private void sleepCheck() {
        /*
         The side effect of this version is:
         - Players will have to wait an entire 0.2 seconds (1/5th of second) until thread can begin processing the queue.
         But, I think this is way, way more worth it as power figures ranged from:
         - Sleep: 1.8% - 2.5%
         - Delta poll: 18% - 23%
        */
        if (chunkRequestQueue.size() == 0) {
            try {
                Thread.sleep(200);
//                System.out.println("ChunkGenerator: Sleeping...");
            } catch (Exception e) {
                throw new RuntimeException("ChunkGenerator: Thread failed to sleep! " + e);
            }
        }

        /* This version is very cpu intensive! But it can be modified to be very responsive.
        Idea: Maybe an optional thread sleep variable? Give warning that it will just chomp cpu to no end
        if (chunkQueue.size() == 0) {
            delta.calculateDelta();
            sleepTimer += delta.getDelta();
            if (sleepTimer < 0.5f) {
                return true;
            }
            sleepTimer = 0.0f;
            return false;
        }
        return false;
         */
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
        instance.addRequest(requestedChunk);
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
