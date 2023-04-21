package org.crafter.engine.world_generation.chunk_mesh_generation;

import java.util.ArrayList;

public class ChunkMeshWorker {

    public ChunkMeshWorker() {
        System.out.println("Hello! I am a chunk mesh worker!");
    }

    /**
     * All ArrayLists are mutable pointers internally, basically.
     */
    public void process(ArrayList<Float> positions, ArrayList<Float> textureCoordinates, ArrayList<Integer> indices) {
        // Fixme: get rid of this super verbose test - it's a square - but FOV will make it look rectangular

        // vertex points

        // top left
        positions.add(-0.5f); // x
        positions.add( 0.5f); // y
        positions.add( 0.0f); // z
        // bottom left
        positions.add(-0.5f); // x
        positions.add(-0.5f); // y
        positions.add( 0.0f); // z
        // bottom right
        positions.add( 0.5f); // x
        positions.add(-0.5f); // y
        positions.add( 0.0f); // z
        // top right
        positions.add( 0.5f); // x
        positions.add( 0.5f); // y
        positions.add( 0.0f); // z


        // texture coordinates

        // top left of image
        textureCoordinates.add(0.0f); // x
        textureCoordinates.add(0.0f); // y
        // bottom left of image
        textureCoordinates.add(0.0f); // x
        textureCoordinates.add(1.0f); // y
        // bottom right of image
        textureCoordinates.add(1.0f); // x
        textureCoordinates.add(1.0f); // y
        // top right of image
        textureCoordinates.add(1.0f); // x
        textureCoordinates.add(0.0f); // y

        // indices

        // Tri 1
        indices.add(0);
        indices.add(1);
        indices.add(2);

        // Tri 2
        indices.add(2);
        indices.add(3);
        indices.add(0);

        // FIXME: end verbose mess here

    }
}
