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

    private int[] currentChunkData = null;

    // These chunk neighbors are reused over & over so that the methods do not have huge amounts of parameters
    // Note: These are chunk deep clones
    private int[] neighborFrontData = null;
    private int[] neighborBackData = null;
    private int[] neighborLeftData = null;
    private int[] neighborRightData = null;

    // Same reason for chunk neighbors!
    private boolean blockNeighborFrontIsBlock = false;
    private boolean blockNeighborBackIsBlock = false;
    private boolean blockNeighborLeftIsBlock = false;
    private boolean blockNeighborRightIsBlock = false;
    private boolean blockNeighborBottomIsBlock = false;
    private boolean blockNeighborTopIsBlock = false;

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
        final int WIDTH = Chunk.getWidth();
        final int DEPTH = Chunk.getDepth();

        final Vector2ic chunkPosition = chunk.getPosition();


        // Right-handed coordinate system - Scoped for clarification & so not reused on accident
        {
            currentChunkData = chunk.getDataDIRECT();

            // Note, this is so fuckin sick that I set this up like this for myself
            final Vector2ic front = new Vector2i(chunkPosition.x(), chunkPosition.y() - 1);
            final Vector2ic back = new Vector2i(chunkPosition.x(), chunkPosition.y() + 1);
            final Vector2ic left = new Vector2i(chunkPosition.x() - 1, chunkPosition.y());
            final Vector2ic right = new Vector2i(chunkPosition.x() + 1, chunkPosition.y());
            neighborFrontData = ChunkStorage.hasPosition(front) ? ChunkStorage.getThreadSafeChunkClone(front).getDataDIRECT() : null;
            neighborBackData = ChunkStorage.hasPosition(back) ? ChunkStorage.getThreadSafeChunkClone(back).getDataDIRECT() : null;
            neighborLeftData = ChunkStorage.hasPosition(left) ? ChunkStorage.getThreadSafeChunkClone(left).getDataDIRECT() : null;
            neighborRightData = ChunkStorage.hasPosition(right) ? ChunkStorage.getThreadSafeChunkClone(right).getDataDIRECT() : null;
        }

        /*
        It works its way:
        Left to right (0-15 x)
        -> Front to back (0-15 z)
        --> Bottom to top (0-15 [differs depending on stack] y)
         */
        for (int y = STACK_HEIGHT * stackPosition; y < STACK_HEIGHT * (stackPosition + 1); y++) {
            for (int z = 0; z < DEPTH; z++) {
                for (int x = 0; x < WIDTH; x++) {
                    branchPathOfGeneration(x, y, z, positions, textureCoordinates, indices);
                }
            }
        }
    }

    private void branchPathOfGeneration(final int x, final int y, final int z, final ArrayList<Float> positions, final ArrayList<Float> textureCoordinates, final ArrayList<Integer> indices) {

        final int ID = Chunk.getBlockID(currentChunkData[Chunk.positionToIndex(x,y,z)]);

        // 0 is reserved for air! Also don't process air drawtype blocks!
        if (ID == 0 || definitionContainer.getDefinition(ID).getDrawType().equals(DrawType.AIR)) {
            return;
        }


        // Note: -Z is facing forwards +X is facing right
        blockNeighborFrontIsBlock = neighborIsBlockDrawType(getNeighbor(x, y, z - 1));
        blockNeighborBackIsBlock = neighborIsBlockDrawType(getNeighbor(x, y, z + 1));
        blockNeighborLeftIsBlock = neighborIsBlockDrawType(getNeighbor(x - 1, y, z));
        blockNeighborRightIsBlock = neighborIsBlockDrawType(getNeighbor(x + 1, y, z));
        blockNeighborBottomIsBlock = neighborIsBlockDrawType(getNeighbor(x, y - 1, z));
        blockNeighborTopIsBlock = neighborIsBlockDrawType(getNeighbor(x, y + 1, z));



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

    private int getNeighbor(final int x, final int y, final int z) {

        // todo: Implement neighbor chunk getter


        if (xyzIsOutOfBoundsCheck(x, y, z)) {

            if (x == Chunk.getWidth() && rightNeighborExists()) {
                return Chunk.getBlockID(neighborRightData[Chunk.positionToIndex(x - Chunk.getWidth(),y,z)]);
            } else if (x == -1 && leftNeighborExists()) {
                return Chunk.getBlockID(neighborLeftData[Chunk.positionToIndex(x + Chunk.getWidth(),y,z)]);
            } else if (z == Chunk.getDepth() && backNeighborExists()) {
                return Chunk.getBlockID(neighborBackData[Chunk.positionToIndex(x,y,z - Chunk.getDepth())]);
            } else if (z == -1 && frontNeighborExists()) {
                return Chunk.getBlockID(neighborFrontData[Chunk.positionToIndex(x,y,z + Chunk.getDepth())]);
            }
            // Out of bounds within the chunk.
            // No neighbor exists.
            // Zero is reserved for air
            return 0;

        } else {
            return Chunk.getBlockID(currentChunkData[Chunk.positionToIndex(x,y,z)]);
        }
    }

    private boolean xyzIsOutOfBoundsCheck(final int x, final int y, final int z) {
        return x < 0 || x >= Chunk.getWidth() ||
                y < 0 || y >= Chunk.getHeight() ||
                z < 0 || z >= Chunk.getDepth();
    }

    private boolean frontNeighborExists() {
        return neighborFrontData != null;
    }
    private boolean backNeighborExists() {
        return neighborBackData != null;
    }
    private boolean leftNeighborExists() {
        return neighborLeftData != null;
    }
    private boolean rightNeighborExists() {
        return neighborRightData != null;
    }
}
