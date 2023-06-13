package org.crafter.engine.world_generation.chunk_mesh_generation;

import org.crafter.engine.world.block.BlockDefinitionContainer;
import org.crafter.engine.world.block.DrawType;
import org.crafter.engine.world.chunk.Chunk;
import org.crafter.engine.world.chunk.ChunkStorage;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.ArrayList;
/**
 * Performance debugging note: This is an object held inside of ChunkMeshGenerator
 */
public class ChunkMeshWorker {

    private final BlockDefinitionContainer definitionContainer;

    private final ChunkFaceGenerator faceGenerator;

    // These chunk neighbors are reused over & over so that the methods do not have huge amounts of parameters
    // Note: These are chunk deep clones
    private Chunk neighborFront = null;
    private Chunk neighborBack = null;
    private Chunk neighborLeft = null;
    private Chunk neighborRight = null;

    // Same reason for chunk neighbors!
    boolean blockNeighborFrontIsBlock = false;
    boolean blockNeighborBackIsBlock = false;
    boolean blockNeighborLeftIsBlock = false;
    boolean blockNeighborRightIsBlock = false;
    boolean blockNeighborBottomIsBlock = false;
    boolean blockNeighborTopIsBlock = false;

    //Todo: Remove this portion
    private long beginTime = System.nanoTime();
    private void startTimer() {
        beginTime = System.nanoTime();
    }
    private void endTimer() {
        final long endTime = System.nanoTime();
        final long durationInMilliseconds = (endTime - beginTime) / 1_000_000;
        System.out.println("ChunkMeshGenerator: timer recorded " + durationInMilliseconds + " milliseconds.");
    }
    //Todo: End removal portion


    public ChunkMeshWorker(BlockDefinitionContainer definitionContainer) {
        this.definitionContainer = definitionContainer;

//        final boolean rewriteThis = true;
//
//        if (rewriteThis) {
//            throw new RuntimeException("Why is this an entire helper object?");
//        }
        this.faceGenerator = new ChunkFaceGenerator(definitionContainer);
    }

    /**
     * @param chunk A Thread-safe clone passed in from the ChunkMeshGenerator.
     * @param positions Mutable reference ArrayList of vertices.
     * @param textureCoordinates Mutable reference ArrayList of texture coordinates.
     * @param indices Mutable reference ArrayList of indices.
     */
    public void process(final int stackPosition, final Chunk chunk, final ArrayList<Float> positions, final ArrayList<Float> textureCoordinates, final ArrayList<Integer> indices) {

        final int STACK_HEIGHT = chunk.getStackHeight();
        final int WIDTH = chunk.getWidth();
        final int DEPTH = chunk.getDepth();

        final Vector2ic chunkPosition = chunk.getPosition();

        // Right-handed coordinate system - Scoped for clarification & so not reused on accident
        {
            final Vector2ic front = new Vector2i(chunkPosition.x(), chunkPosition.y() - 1);
            final Vector2ic back = new Vector2i(chunkPosition.x(), chunkPosition.y() + 1);
            final Vector2ic left = new Vector2i(chunkPosition.x() - 1, chunkPosition.y());
            final Vector2ic right = new Vector2i(chunkPosition.x() + 1, chunkPosition.y());
            neighborFront = ChunkStorage.hasPosition(front) ? ChunkStorage.getThreadSafeChunkClone(front) : null;
            neighborBack = ChunkStorage.hasPosition(back) ? ChunkStorage.getThreadSafeChunkClone(back) : null;
            neighborLeft = ChunkStorage.hasPosition(left) ? ChunkStorage.getThreadSafeChunkClone(left) : null;
            neighborRight = ChunkStorage.hasPosition(right) ? ChunkStorage.getThreadSafeChunkClone(right) : null;
        }






        // FIXME: This is taking 33 MS to process >:(

        /*
        It works its way:
        Left to right (0-15 x)
        -> Front to back (0-15 z)
        --> Bottom to top (0-15 [differs depending on stack] y)
         */

        startTimer();
        final int[] chunkData = chunk.getDataDIRECT();
        for (int y = STACK_HEIGHT * stackPosition; y < STACK_HEIGHT * (stackPosition + 1); y++) {
            for (int z = 0; z < DEPTH; z++) {
                for (int x = 0; x < WIDTH; x++) {
                    branchPathOfGeneration(chunkData, x, y, z, positions, textureCoordinates, indices);
                }
            }
        }
        endTimer();
    }

    private void branchPathOfGeneration(final int[] chunkData, final int x, final int y, final int z, final ArrayList<Float> positions, final ArrayList<Float> textureCoordinates, final ArrayList<Integer> indices) {

        final int ID = Chunk.getBlockID(chunkData[Chunk.positionToIndex(x,y,z)]);

        // 0 is reserved for air! Also don't process air drawtype blocks!
        if (ID == 0 || definitionContainer.getDefinition(ID).getDrawType().equals(DrawType.AIR)) {
            return;
        }


        // Note: -Z is facing forwards +X is facing right
        blockNeighborFrontIsBlock = neighborIsBlockDrawType(getNeighbor(chunkData, x, y, z - 1));
        blockNeighborBackIsBlock = neighborIsBlockDrawType(getNeighbor(chunkData, x, y, z + 1));
        blockNeighborLeftIsBlock = neighborIsBlockDrawType(getNeighbor(chunkData, x - 1, y, z));
        blockNeighborRightIsBlock = neighborIsBlockDrawType(getNeighbor(chunkData, x + 1, y, z));
        blockNeighborBottomIsBlock = neighborIsBlockDrawType(getNeighbor(chunkData, x, y - 1, z));
        blockNeighborTopIsBlock = neighborIsBlockDrawType(getNeighbor(chunkData, x, y + 1, z));



        //fixme: for now, just render out each normal block brute force

        switch (definitionContainer.getDefinition(ID).getDrawType()) {
            case BLOCK -> {
                blockDrawType(ID, x, y, z, positions, textureCoordinates, indices);
            }
            case GLASS, PLANT, TORCH, LEAVES, BLOCK_BOX, LIQUID_FLOW, LIQUID_SOURCE -> {
                //todo;
            }
            case AIR -> {
                // do nothing
                return;
            }

            default ->
                    throw new IllegalStateException("Unexpected value: " + definitionContainer.getDefinition(ID).getDrawType());
        }
    }

    private void blockDrawType(final int ID, final int x, final int y, final int z, final ArrayList<Float> positions, final ArrayList<Float> textureCoordinates, final ArrayList<Integer> indices) {
        //Fixme: This will check neighbors etc when completed

        // Note: Right handed coordinate system - to + all axes
        if (!blockNeighborFrontIsBlock) {
            faceGenerator.attachFront(ID, x, y, z, positions, textureCoordinates, indices);
        }
        if (!blockNeighborBackIsBlock) {
            faceGenerator.attachBack(ID, x, y, z, positions, textureCoordinates, indices);
        }
        if (!blockNeighborLeftIsBlock) {
            faceGenerator.attachLeft(ID, x, y, z, positions, textureCoordinates, indices);
        }
        if (!blockNeighborRightIsBlock) {
            faceGenerator.attachRight(ID, x, y, z, positions, textureCoordinates, indices);
        }
        if (!blockNeighborBottomIsBlock) {
            faceGenerator.attachBottom(ID, x, y, z, positions, textureCoordinates, indices);
        }
        if (!blockNeighborTopIsBlock) {
            faceGenerator.attachTop(ID, x, y, z, positions, textureCoordinates, indices);
        }
    }


    private boolean neighborIsBlockDrawType(int inputID) {
        return definitionContainer.getDefinition(inputID).getDrawType().equals(DrawType.BLOCK);
    }

    private int getNeighbor(int[] chunkData, final int x, final int y, final int z) {

        // todo: Implement neighbor chunk getter

        if (xyzIsOutOfBoundsCheck(x, y, z, Chunk.getWidth(), Chunk.getHeight(), Chunk.getDepth())) {
            // Out of bounds within the chunk
            // Zero is reserved for air
            return 0;
        }

        return Chunk.getBlockID(chunkData[Chunk.positionToIndex(x,y,z)]);
    }

    private boolean xyzIsOutOfBoundsCheck(final int x, final int y, final int z, final int width, final int height, final int depth) {
        return x < 0 || x >= width ||
                y < 0 || y >= height ||
                z < 0 || z >= depth;
    }

    private boolean frontNeighborExists() {
        return neighborFront != null;
    }
    private boolean backNeighborExists() {
        return neighborBack != null;
    }
    private boolean leftNeighborExists() {
        return neighborLeft != null;
    }
    private boolean rightNeighborExists() {
        return neighborRight != null;
    }
}
