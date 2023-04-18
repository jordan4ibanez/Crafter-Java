package org.crafter.engine.world_generation.chunk_mesh_generation;

import org.crafter.engine.delta.DeltaObject;
import org.crafter.engine.world.block.BlockDefinitionContainer;
import org.crafter.engine.world.chunk.Chunk;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChunkMeshGenerator implements Runnable {
    // Class local
    private static ChunkMeshGenerator instance;
    private static Thread thread;

    // Instance local
    private final DeltaObject delta;

    private final BlockDefinitionContainer blockDefinitionContainer;

    private final BlockingQueue<Chunk> meshRequestQueue;
    private final BlockingQueue<ChunkMeshRecord> meshOutputQueue;
    private final AtomicBoolean shouldRun;

//    private float sleepTimer;

    private ChunkMeshGenerator() {
        delta = new DeltaObject();
        blockDefinitionContainer = BlockDefinitionContainer.getThreadSafeDuplicate();
        meshRequestQueue = new LinkedBlockingQueue<>();
        meshOutputQueue = new LinkedBlockingQueue<>();
        shouldRun = new AtomicBoolean(true);
    }

    @Override
    public void run() {
        System.out.println("ChunkMeshGenerator: Started!");
        System.out.println("ChunkMeshGenerator: gotten blocks (" + Arrays.toString(blockDefinitionContainer.getAllBlockNames()) + ")!");
        while (shouldRun.get()) {
            sleepCheck();
            processInputQueue();
        }
    }

    private void processInputQueue() {
        while (!meshRequestQueue.isEmpty()) {
            createMesh(meshRequestQueue.remove());
        }
    }
    private void createMesh(Chunk newChunk) {

        ChunkMeshRecord chunk = blockProcessingProcedure(newChunk);

        meshOutputQueue.add(chunk);
    }

    /**
     * Actual side effects happen here!
     * This is where biomes & blocks are applied into the data container (Chunk)
     */
    private ChunkMeshRecord blockProcessingProcedure(Chunk chunk) {

        String uuid = UUID.randomUUID().toString();

        // todo: this will be created after the array builders have been filled out
        ChunkMeshRecord outputMesh = new ChunkMeshRecord(
                uuid,
                // Separates the pointer internally
                new Vector2i(chunk.getPosition()),
                new float[0],
                new float[0],
                new int[0]
        );

//        System.out.println("ChunkMeshGenerator: Generated Chunk(" + chunk.getX() + ", " + chunk.getY() + ")");

        return outputMesh;
    }

    public boolean checkUpdate() {
        return !meshOutputQueue.isEmpty();
    }
    public ChunkMeshRecord grabUpdate() {
//        debugQueueSizes();
        return meshOutputQueue.remove();
    }

    private void sleepCheck() {
        /*
         The side effect of this version is:
         - Players will have to wait an entire 0.2 seconds (1/5th of second) until thread can begin processing the queue.
         But, I think this is way, way more worth it as power figures ranged from:
         - Sleep: 1.8% - 2.5%
         - Delta poll: 18% - 23%
        */
        if (meshRequestQueue.size() == 0) {
            try {
                Thread.sleep(200);
//                System.out.println("ChunkMeshGenerator: Sleeping...");
            } catch (Exception e) {
                throw new RuntimeException("ChunkMeshGenerator: Thread failed to sleep! " + e);
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

    private void addRequest(Chunk requestedChunk) {
        this.meshRequestQueue.add(requestedChunk);
    }

    private void stopThread() {
        shouldRun.set(false);
    }

    /**
     * This function is helpful in case something ever gets completely mangled.
     */
    private void debugQueueSizes() {
        System.out.println("ChunkMeshGenerator: (INPUT: " + meshRequestQueue.size() + ") | (OUTPUT: " + meshOutputQueue.size() + ")");
    }

    // External statics from here below

    public static void start() {
        if (thread != null) {
            throw new RuntimeException("ChunkMeshGenerator: Cannot start thread! It's already running!");
        }
        // Package the instance into the thread, so it can be talked to
        instance = new ChunkMeshGenerator();
        thread = new Thread(instance);
        thread.start();
    }

    public static void stop() {
        nullCheck("stop");
        instance.stopThread();
    }

    public static void pushRequest(Chunk requestedChunk) {
        nullCheck("pushRequest");
        instance.addRequest(requestedChunk);
    }

    public static boolean hasUpdate() {
        return instance.checkUpdate();
    }
    public static ChunkMeshRecord getUpdate() {
        // This is an extremely important safety check
        if (!hasUpdate()) {
            throw new RuntimeException("ChunkMeshGenerator: You need to check (hasUpdate) before you try to getUpdate!");
        }
        return instance.grabUpdate();
    }

    private static void nullCheck(String methodName) {
        if (thread == null) {
            throw new RuntimeException("ChunkMeshGenerator: Cannot utilize method (" + methodName + ")! The THREAD has not been instantiated!");
        } if (instance == null) {
            throw new RuntimeException("ChunkMeshGenerator: Cannot utilize method (" + methodName + ")! The INSTANCE has not been instantiated!");
        }
        if (!thread.isAlive()) {
            throw new RuntimeException("ChunkMeshGenerator: Thread has crashed! Cannot utilize (" + methodName + ")!");
        }
    }
}
