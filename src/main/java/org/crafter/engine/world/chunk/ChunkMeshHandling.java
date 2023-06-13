package org.crafter.engine.world.chunk;

import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.world_generation.chunk_mesh_generation.ChunkMeshRecord;

import java.io.Serializable;

public class ChunkMeshHandling extends ChunkArrayManipulation implements Serializable {
    private static final int STACK_HEIGHT = 16;
    private static final int STACKS = HEIGHT / STACK_HEIGHT;

    private final String[] meshes;

    public ChunkMeshHandling() {
//        System.out.println("ChunkMeshHandling: Stacks: " + STACKS);
        meshes = new String[8];
    }

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
    public int getStacks() {
        return STACKS;
    }

    protected String getMesh(int stack) {
        return meshes[stack];
    }

    public int getStackHeight() {
        return STACK_HEIGHT;
    }
}
