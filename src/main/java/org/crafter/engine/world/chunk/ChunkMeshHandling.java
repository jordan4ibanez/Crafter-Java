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
package org.crafter.engine.world.chunk;

import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.world_generation.chunk_mesh_generation.ChunkMeshRecord;

import java.io.Serializable;

public class ChunkMeshHandling extends ChunkArrayManipulation {
    private static final int STACK_HEIGHT = 16;
    private static final int STACKS = getHeight() / STACK_HEIGHT;

    private final String[] meshes;

    public ChunkMeshHandling() {
//        System.out.println("ChunkMeshHandling: Stacks: " + STACKS);
        meshes = new String[8];
    }

    /**
     * This takes the RAW data in the form of a ChunkMeshRecord and processes it into the GPU utilizing MeshStorage.
     * @param stack 0-7, the position in the chunk where this mesh will be stored.
     * @param newMesh ChunkMeshRecord, the raw data for the mesh in this chunk mesh stack position.
     */
    public void setMesh(int stack, ChunkMeshRecord newMesh) {

        if (meshes[stack] != null) {
            MeshStorage.destroy(meshes[stack]);
        }

        MeshStorage.newMesh(
                newMesh.uuid(),
                newMesh.positions(),
                newMesh.textureCoordinates(),
                newMesh.indices(),
                null,
                // Todo: Colors can be an easy way to implement light values!
                null,
                "worldAtlas",
                false
        );

//        System.out.println("ChunkMeshHandling: Chunk (" + newMesh.destinationChunkPosition().x() + ", " + newMesh.destinationChunkPosition().y() + ") stack (" + stack + ") has uuid (" + newMesh.uuid() + ")");

        meshes[stack] = newMesh.uuid();
    }

    /**
     * Stacks, as in, mesh stacks. There are 8 individual meshes which make up a chunk, for speed of processing the chunk.
     * TODO: Give this a better name!
     * @return integral position in array. Literal position is bottom to top 0-7
     */
    public static int getStacks() {
        return STACKS;
    }

    protected String getMesh(int stack) {
        return meshes[stack];
    }

    public int getStackHeight() {
        return STACK_HEIGHT;
    }
}
