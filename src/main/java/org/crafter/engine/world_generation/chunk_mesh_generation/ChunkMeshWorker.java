package org.crafter.engine.world_generation.chunk_mesh_generation;

import org.crafter.engine.world.block.BlockDefinitionContainer;
import org.crafter.engine.world.block.DrawType;
import org.crafter.engine.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.HashMap;

public class ChunkMeshWorker {

    private final BlockDefinitionContainer definitionContainer;

    private final ChunkFaceGenerator faceGenerator;


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
        final int WIDTH = chunk.getWidth();
        final int DEPTH = chunk.getDepth();

        /*
        It works its way:
        Left to right (0-15 x)
        -> Front to back (0-15 z)
        --> Bottom to top (0-15 [differs depending on stack] y)
         */
        for (int y = STACK_HEIGHT * stackPosition; y < STACK_HEIGHT * (stackPosition + 1); y++) {
            for (int z = 0; z < DEPTH; z++) {
                for (int x = 0; x < WIDTH; x++) {

                    branchPathOfGeneration(chunk, x, y, z, positions, textureCoordinates, indices);
                }
            }
        }
    }

    private void branchPathOfGeneration(final Chunk chunk, final int x, final int y, final int z, final ArrayList<Float> positions, final ArrayList<Float> textureCoordinates, final ArrayList<Integer> indices) {
        // Fixme: This might be a bit too slow

        final int[] chunkData = chunk.getData();

        final int ID = chunk.getBlockID(chunkData[chunk.positionToIndex(x,y,z)]);

        // 0 is reserved for air! Also don't process air drawtype blocks!
        if (ID == 0 || definitionContainer.getDefinition(ID).getDrawType().equals(DrawType.AIR)) {
            return;
        }

        // Testing
//        chunk.printBits(ID);
//        String internalName = definitionContainer.getDefinition(ID).getInternalName();
//        System.out.println("Block (" + internalName + ") is at: (" + x + ", " + y + ", " + z + ")");

        // Note: -Z is facing forwards +X is facing right
        //TODO: implement this
        final int neighborFront = getNeighbor(chunk, x, y, z - 1);
        final int neighborBack = getNeighbor(chunk, x, y, z + 1);
        final int neighborLeft = getNeighbor(chunk, x - 1, y, z);
        final int neighborRight = getNeighbor(chunk, x + 1, y, z);
        final int neighborBottom = getNeighbor(chunk, x, y - 1, z);
        final int neighborTop = getNeighbor(chunk, x, y + 1, z);

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
            case DEFAULT ->
                    throw new RuntimeException("ChunkMeshWorker: A block definition has a DEFAULT drawtype!");

            default ->
                    throw new IllegalStateException("Unexpected value: " + definitionContainer.getDefinition(ID).getDrawType());
        }
    }

    private void blockDrawType(final int ID, final int x, final int y, final int z, final ArrayList<Float> positions, final ArrayList<Float> textureCoordinates, final ArrayList<Integer> indices) {
        //Fixme: This would check neighbors etc

        faceGenerator.attachBack(ID, x, y, z, positions, textureCoordinates, indices);
    }

    private int getNeighbor(Chunk chunk, final int x, final int y, final int z) {
        // todo: replace with neighbor edge chunk
        if (xyzIsOutOfBoundsCheck(x, y, z, chunk.getWidth(), chunk.getHeight(), chunk.getDepth())) {
            // Out of bounds within the chunk
            // Zero is reserved for air
            return 0;
        }

        return chunk.getBlockID(chunk.getBlockData(chunk.positionToIndex(x,y,z)));
    }

    private boolean xyzIsOutOfBoundsCheck(final int x, final int y, final int z, final int width, final int height, final int depth) {
        return x < 0 || x >= width ||
                y < 0 || y >= height ||
                z < 0 || z >= depth;
    }
}
