package org.crafter.engine.world_generation.chunk_mesh_generation;

import org.crafter.engine.world.block.BlockDefinition;
import org.crafter.engine.world.block.BlockDefinitionContainer;
import org.crafter.engine.world.chunk.Chunk;
import org.joml.Vector3ic;

import java.util.ArrayList;

public class ChunkMeshWorker {

    private final BlockDefinitionContainer definitionContainer;
    public ChunkMeshWorker(BlockDefinitionContainer definitionContainer) {
        this.definitionContainer = definitionContainer;
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

        final int[] chunkData = chunk.getData();

        for (int y = STACK_HEIGHT * stackPosition; y < STACK_HEIGHT * (stackPosition + 1); y++) {
//            System.out.println(y);
            for (int x = 0; x < WIDTH; x++) {
                for (int z = 0; z < DEPTH; z++) {

                    // Fixme: This might be a bit too slow

                    final int ID = chunk.getBlockID(chunkData[chunk.positionToIndex(x,y,z)]);
                    // Testing
//                    chunk.printBits(ID);
//                    String internalName = definitionContainer.getDefinition(ID).getInternalName();
//                    System.out.println("Block (" + internalName + ") is at: (" + x + ", " + y + ", " + z + ")");

                    // Note: -Z is facing forwards +X is facing right
                    final int neighborFront = getNeighbor(chunk, x, y, z - 1);
                    final int neighborBack = getNeighbor(chunk, x, y, z + 1);
                    final int neighborLeft = getNeighbor(chunk, x - 1, y, z);
                    final int neighborRight = getNeighbor(chunk, x + 1, y, z);
                    final int neighborBottom = getNeighbor(chunk, x, y - 1, z);
                    final int neighborTop = getNeighbor(chunk, x, y + 1, z);


                }
            }
        }
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
