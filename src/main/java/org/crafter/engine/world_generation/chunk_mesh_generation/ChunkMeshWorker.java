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

                    final int index = chunk.positionToIndex(x,y,z);

                    int ID = chunk.getBlockID(chunkData[index]);

//                    chunk.printBits(ID);


                    String internalName = definitionContainer.getDefinition(ID).getInternalName();


                    System.out.println("Block (" + internalName + ") is at: (" + x + ", " + y + ", " + z + ")");

                }
            }
        }





    }
}
