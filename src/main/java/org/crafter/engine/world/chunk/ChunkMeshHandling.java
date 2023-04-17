package org.crafter.engine.world.chunk;

import org.crafter.engine.mesh.MeshStorage;

public class ChunkMeshHandling extends ChunkArrayManipulation {
    private static final int STACK_HEIGHT = 16;
    private static final int STACKS = height / STACK_HEIGHT;

    private final String[] meshes;

    public ChunkMeshHandling() {
        System.out.println("ChunkMeshHandling: Stacks: " + STACKS);
        meshes = new String[8];
    }

    public void setMesh(int stack, String newMeshUUID) {
        if (meshes[stack] != null) {
            MeshStorage.destroy(meshes[stack]);
        }
        meshes[stack] = newMeshUUID;
    }
    public void render() {
        for (String mesh : meshes) {
            System.out.println(mesh);
        }
    }

}
