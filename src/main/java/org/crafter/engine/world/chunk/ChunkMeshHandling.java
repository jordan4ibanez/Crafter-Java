package org.crafter.engine.world.chunk;

import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.world_generation.chunk_mesh_generation.ChunkMeshRecord;

public class ChunkMeshHandling extends ChunkArrayManipulation {
    private static final int STACK_HEIGHT = 16;
    private static final int STACKS = height / STACK_HEIGHT;

    private final String[] meshes;

    public ChunkMeshHandling() {
        System.out.println("ChunkMeshHandling: Stacks: " + STACKS);
        meshes = new String[8];
    }

    public void setMesh(int stack, ChunkMeshRecord newMesh) {

        if (meshes[stack] != null) {
            MeshStorage.destroy(meshes[stack]);
        }

        // FIXME: Handle talking to Mesh Storage here!
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

        System.out.println("ChunkMeshHandling: Chunk (" + newMesh.destinationChunkPosition().x() + ", " + newMesh.destinationChunkPosition().y() + ") stack (" + stack + ") has uuid (" + newMesh.uuid() + ")");

        meshes[stack] = newMesh.uuid();
    }
    public void render() {
        for (String mesh : meshes) {
            System.out.println(mesh);
        }
    }

}
