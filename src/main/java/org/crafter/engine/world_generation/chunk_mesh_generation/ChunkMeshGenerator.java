package org.crafter.engine.world_generation.chunk_mesh_generation;

import org.crafter.engine.delta.DeltaObject;
import org.crafter.engine.world.block.BlockDefinitionContainer;
import org.crafter.engine.world.chunk.Chunk;
import org.crafter.engine.world.chunk.ChunkStorage;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Performance debugging note: This is the actual thread of the chunk mesh generator
 */
public class ChunkMeshGenerator implements Runnable {
    // Class local
    private static ChunkMeshGenerator instance;
    private static Thread thread;

    // Instance local
    private final DeltaObject delta;

    // This and meshWorker share the instance of this BlockDefinitionContainer!
    private final BlockDefinitionContainer blockDefinitionContainer;

    private final BlockingQueue<Vector3ic> meshRequestQueue;
    private final BlockingQueue<ChunkMeshRecord> meshOutputQueue;
    private final AtomicBoolean shouldRun;
    private final ChunkMeshWorker meshWorker;
    private final ArrayList<Float> positionsBuilder = new ArrayList<>();
    private final ArrayList<Float> textureCoordinatesBuilder = new ArrayList<>();
    private final ArrayList<Integer> indicesBuilder = new ArrayList<>();


    //Todo: Remove this portion
    private long beginTime = System.nanoTime();
    private void startTimer() {
        beginTime = System.nanoTime();
    }
    private void endTimer() {
        final long endTime = System.nanoTime();
//        final long durationInMilliseconds = (endTime - beginTime) / 1_000_000;
//        System.out.println("ChunkMeshGenerator: timer recorded " + durationInMilliseconds + " milliseconds.");
        final long durationInNanoseconds = (endTime - beginTime);
        System.out.println("ChunkMeshGenerator: timer recorded " + durationInNanoseconds + " nanoseconds.");
    }
    //Todo: End removal portion

    private ChunkMeshGenerator() {
        delta = new DeltaObject();
        blockDefinitionContainer = BlockDefinitionContainer.getThreadSafeDuplicate();
        meshRequestQueue = new LinkedBlockingQueue<>();
        meshOutputQueue = new LinkedBlockingQueue<>();
        shouldRun = new AtomicBoolean(true);
        // This and the meshWorker share the BlockDefinitionContainer
        meshWorker = new ChunkMeshWorker(blockDefinitionContainer);
    }

    @Override
    public void run() {
        System.out.println("ChunkMeshGenerator: Started!");
        System.out.println("ChunkMeshGenerator: gotten blocks (" + Arrays.toString(blockDefinitionContainer.getAllBlockNames()) + ")!");
        while (shouldRun.get()) {
            sleepCheck();
            processInputQueue();
        }

        System.out.println("ChunkMeshGenerator: Stopped!");
    }

    private void processInputQueue() {
        if (!meshRequestQueue.isEmpty()) {

            // There is a performance bottleneck in this method :(
            createMesh(meshRequestQueue.remove());

            // 33-ish milliseconds is very bad :(

        }
    }
    private void createMesh(Vector3ic position) {

        ChunkMeshRecord chunk = blockProcessingProcedure(position);

        meshOutputQueue.add(chunk);
    }

    /**
     * Actual side effects happen here!
     * This is where biomes & blocks are applied into the data container (Chunk)
     */
    private ChunkMeshRecord blockProcessingProcedure(Vector3ic position) {

        final String uuid = UUID.randomUUID().toString();

        final Chunk threadSafeClone = ChunkStorage.getThreadSafeChunkClone(new Vector2i(position.x(), position.z()));

        // Mutably pass the references to the arraylists into the ChunkMeshWorker so this doesn't become thousands of lines long.
        meshWorker.process(position.y(), threadSafeClone, positionsBuilder, textureCoordinatesBuilder, indicesBuilder);

        // NOTE: This is a new piece of memory, it must be a new array
        final float[] positions = new float[positionsBuilder.size()];
        for (int i = 0; i < positions.length; i++) {
            positions[i] = positionsBuilder.get(i);
        }
        final float[] textureCoordinates = new float[textureCoordinatesBuilder.size()];
        for (int i = 0; i < textureCoordinates.length; i++) {
            textureCoordinates[i] = textureCoordinatesBuilder.get(i);
        }
        final int[] indices = new int[indicesBuilder.size()];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = indicesBuilder.get(i);
        }

        positionsBuilder.clear();
        textureCoordinatesBuilder.clear();
        indicesBuilder.clear();

//        System.out.println("ChunkMeshGenerator: Generated Chunk(" + outputMesh.destinationChunkPosition().x() + ", " + outputMesh.destinationChunkPosition().y() + ")");

        return new ChunkMeshRecord(
                uuid,
                position.y(),
                // Separates the pointer internally
                new Vector2i(position.x(), position.z()),
                positions,
                textureCoordinates,
                indices
        );
    }

    public boolean checkUpdate() {
        return !meshOutputQueue.isEmpty();
    }
    public ChunkMeshRecord grabUpdate() {
        return meshOutputQueue.remove();
    }

    private void sleepCheck() {
        if (meshRequestQueue.size() == 0) {
            try {
//                System.out.println("ChunkMeshGenerator: Sleeping");
                Thread.sleep(200);
            } catch (Exception e) {
                throw new RuntimeException("ChunkMeshGenerator: Thread failed to sleep! " + e);
            }
        }
    }

    private void addRequest(Vector3ic position) {
        //Fixme: test if traversing the list causes severe performance penalty
        if (meshRequestQueue.contains(position)) {
            return;
        }
        this.meshRequestQueue.add(position);
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

    public static void pushRequest(int x, int stack, int z) {
        nullCheck("pushRequest");
        if (stack < 0 || stack > 7) {
            throw new RuntimeException("ChunkMeshGenerator: Stack is out of bounds! Got: (" + stack + ") | Min: 0 | Max: 7");
        }
        // Separate out thread data internal pointers
        instance.addRequest(new Vector3i(x, stack, z));
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
