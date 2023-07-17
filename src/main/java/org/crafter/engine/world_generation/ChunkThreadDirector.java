/*
 Crafter - A blocky game (engine) written in Java with LWJGL.
 Copyright (C) 2023  jordan4ibanez

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.crafter.engine.world_generation;

import org.crafter.engine.world.chunk.Chunk;
import org.crafter.engine.world.chunk.ChunkStorage;
import org.crafter.engine.world_generation.chunk_generation.ChunkGenerator;
import org.crafter.engine.world_generation.chunk_mesh_generation.ChunkMeshGenerator;
import org.crafter.engine.world_generation.chunk_mesh_generation.ChunkMeshRecord;
import org.joml.Vector2i;
import org.joml.Vector2ic;

/**
 * Utility class.
 * The Chunk Thread Director is a CRUCIAL program within the game. It consists of 3 basic tasks:
 * 1.) Receive and store newly generated Chunks from the Chunk Generator.
 * 2.) Request and receive Chunk Meshes from the Chunk Mesh Generator.
 * 3.) Update existing neighbor Chunk Meshes via step 2.
 * This is basically an inter-thread communication utility class.
 */
public final class ChunkThreadDirector {
    private ChunkThreadDirector(){}

    public static void runLogic() {
        parseChunkGenerator();
        parseChunkMeshGenerator();
    }

    private static void parseChunkGenerator() {
        while (ChunkGenerator.hasUpdate()) {

            Chunk generatedChunk = ChunkGenerator.getUpdate();

//            System.out.println("Main: Received chunk (" + generatedChunk.getPositionString() + ")!");

            ChunkStorage.addOrUpdate(generatedChunk);

            Vector2ic position = generatedChunk.getPosition();

            //fixme: needs to iterate 0-7
            // Render stack 0 (y coordinate 0 to 15)
            generateFullChunkMesh(position.x(), position.y());

            // Now we update neighbors.
            // Right handed coordinate system.
            // Basic if branch because why not.
            // ChunkMeshGenerator automatically !NOW! REJECTS duplicates - this might cause horrible performance.
            // FIXME: this is the cause if performance is brutal.
            // So now we blindly shovel in requests.
            // This is scoped to auto GC if hit fails. It also allows to be more explicit.

            checkAndUpdateNeighbors(position);
        }
    }

    private static void checkAndUpdateNeighbors(Vector2ic position) {
        updateNeighborFront(position);
        updateNeighborBack(position);
        updateNeighborLeft(position);
        updateNeighborRight(position);
    }

    private static void updateNeighborFront(Vector2ic position) {
        // Front
        Vector2ic neighborFront = new Vector2i(position.x(), position.y() - 1);
        if (ChunkStorage.hasChunk(neighborFront)) {
            generateFullChunkMesh(neighborFront.x(), neighborFront.y());
        }
    }
    private static void updateNeighborBack(Vector2ic position) {
        // Back
        Vector2ic neighborBack = new Vector2i(position.x(), position.y() + 1);
        if (ChunkStorage.hasChunk(neighborBack)) {
            generateFullChunkMesh(neighborBack.x(), neighborBack.y());
        }
    }
    private static void updateNeighborLeft(Vector2ic position) {
        // Left
        Vector2ic neighborLeft = new Vector2i(position.x() - 1, position.y());
        if (ChunkStorage.hasChunk(neighborLeft)) {
            generateFullChunkMesh(neighborLeft.x(), neighborLeft.y());
        }
    }
    private static void updateNeighborRight(Vector2ic position) {
        // Right
        Vector2ic neighborRight = new Vector2i(position.x() + 1, position.y());
        if (ChunkStorage.hasChunk(neighborRight)) {
            generateFullChunkMesh(neighborRight.x(), neighborRight.y());
        }
    }

    private static void parseChunkMeshGenerator() {

        while (ChunkMeshGenerator.hasUpdate()) {
            ChunkMeshRecord generatedMesh = ChunkMeshGenerator.getUpdate();

            // Fixme: This is a debug for one simple chunk, make sure this is removed so it doesn't cause a random red herring
            // TODO: Make sure this is done within the main thread!

            final Vector2ic destinationPosition = generatedMesh.destinationChunkPosition();

            if (ChunkStorage.hasChunk(destinationPosition)) {
                ChunkStorage.getChunk(destinationPosition).setMesh(generatedMesh.stack(), generatedMesh);
            } // Else nothing happens to it and the raw ChunkMeshRecord is garbage collected.
        }
    }

    /**
     * Generates chunk mesh stacks (0-7)
     * @param x world position on X axis (literal)
     * @param z world position on Z axis (literal)
     */
    private static void generateFullChunkMesh(int x, int z) {
        for (int i = 0; i < Chunk.getStacks(); i++) {
//                System.out.println(i);
            ChunkMeshGenerator.pushRequest(x, i, z);
        }
    }

}
