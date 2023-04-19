package org.crafter.engine.world_generation.chunk_mesh_generation;

import org.crafter.engine.delta.DeltaObject;
import org.crafter.engine.world.block.BlockDefinitionContainer;
import org.crafter.engine.world.chunk.Chunk;
import org.crafter.engine.world.chunk.ChunkStorage;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.ArrayList;
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

    private final BlockingQueue<Vector2ic> meshRequestQueue;
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

        System.out.println("ChunkMeshGenerator: Stopped!");
    }

    private void processInputQueue() {
        while (!meshRequestQueue.isEmpty()) {
            createMesh(meshRequestQueue.remove());
        }
    }
    private void createMesh(Vector2ic position) {

        ChunkMeshRecord chunk = blockProcessingProcedure(position);

        meshOutputQueue.add(chunk);
    }

    /**
     * Actual side effects happen here!
     * This is where biomes & blocks are applied into the data container (Chunk)
     */
    private ChunkMeshRecord blockProcessingProcedure(Vector2ic position) {

        String uuid = UUID.randomUUID().toString();

        Chunk threadSafeClone = ChunkStorage.getThreadSafeChunkClone(position);

        System.out.println("ChunkMeshGenerator: Processing (" + position.x() + ", " + position.y() + ")");

        // Todo: Note! Perhaps a linked list would be more performant?

        // TODO: NOTE! REUSE THIS! UTILIZE (vertices.clear();) FOR EXAMPLE!

        ArrayList<Float> positionsBuilder = new ArrayList<>();
        ArrayList<Float> textureCoordinatesBuilder = new ArrayList<>();
        ArrayList<Integer> indicesBuilder = new ArrayList<>();

        // Insert block builder here

        // Fixme: get rid of this super verbose test - it's a square - but FOV will make it look rectangular

        // vertex points

        // top left
        positionsBuilder.add(-0.5f); // x
        positionsBuilder.add( 0.5f); // y
        positionsBuilder.add( 0.0f); // z
        // bottom left
        positionsBuilder.add(-0.5f); // x
        positionsBuilder.add(-0.5f); // y
        positionsBuilder.add( 0.0f); // z
        // bottom right
        positionsBuilder.add( 0.5f); // x
        positionsBuilder.add(-0.5f); // y
        positionsBuilder.add( 0.0f); // z
        // top right
        positionsBuilder.add( 0.5f); // x
        positionsBuilder.add( 0.5f); // y
        positionsBuilder.add( 0.0f); // z


        // texture coordinates

        // top left of image
        textureCoordinatesBuilder.add(0.0f); // x
        textureCoordinatesBuilder.add(0.0f); // y
        // bottom left of image
        textureCoordinatesBuilder.add(0.0f); // x
        textureCoordinatesBuilder.add(1.0f); // y
        // bottom right of image
        textureCoordinatesBuilder.add(1.0f); // x
        textureCoordinatesBuilder.add(1.0f); // y
        // top right of image
        textureCoordinatesBuilder.add(1.0f); // x
        textureCoordinatesBuilder.add(0.0f); // y

        // indices

        // Tri 1
        indicesBuilder.add(0);
        indicesBuilder.add(1);
        indicesBuilder.add(2);

        // Tri 2
        indicesBuilder.add(2);
        indicesBuilder.add(3);
        indicesBuilder.add(0);

        // FIXME: end verbose mess here

        // End block builder here


        // NOTE: This is a new piece of memory, it must be a new array
        float[] positions = new float[positionsBuilder.size()];
        for (int i = 0; i < positions.length; i++) {
            positions[i] = positionsBuilder.get(i);
        }
        float[] textureCoordinates = new float[textureCoordinatesBuilder.size()];
        for (int i = 0; i < textureCoordinates.length; i++) {
            textureCoordinates[i] = textureCoordinatesBuilder.get(i);
        }
        int[] indices = new int[indicesBuilder.size()];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = indicesBuilder.get(i);
        }

        // todo: this will be created after the array builders have been filled out
        ChunkMeshRecord outputMesh = new ChunkMeshRecord(
                uuid,
                // Separates the pointer internally
                new Vector2i(threadSafeClone.getPosition()),
                positions,
                textureCoordinates,
                indices
        );

        System.out.println("ChunkMeshGenerator: Generated Chunk(" + outputMesh.destinationChunkPosition().x() + ", " + outputMesh.destinationChunkPosition().y() + ")");

        return outputMesh;
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
                Thread.sleep(200);
            } catch (Exception e) {
                throw new RuntimeException("ChunkMeshGenerator: Thread failed to sleep! " + e);
            }
        }
    }

    private void addRequest(Vector2ic position) {
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

    public static void pushRequest(Vector2ic position) {
        nullCheck("pushRequest");
        // Separate out thread data internal pointers
        instance.addRequest(new Vector2i(position));
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
