package org.crafter.engine.mesh;

import java.util.HashMap;

/**
 * This class holds all the meshes in game!
 * To create a mesh, you have to talk to this class.
 */
public class MeshStorage {

    private static final HashMap<String,Mesh> container = new HashMap<>();

    private MeshStorage(){};

    // Create a new mesh
    public static void newMesh(String meshName, float[] positions, float[] textureCoordinates, int[] indices, int[] bones, float[] colors, String textureFileLocation) {
        if (container.containsKey(meshName)) {
            throw new RuntimeException("MeshStorage: Tried to create mesh (" + meshName + ") more than once!");
        }
        container.put(meshName, new Mesh(meshName, positions, textureCoordinates, indices, bones, colors, textureFileLocation));
    }

    // Swap a mesh's texture
    public static void swapTexture(String meshName, String newTextureLocation) {
        checkExistence(meshName);
        container.get(meshName).swapTexture(newTextureLocation);
    }

    // Render a mesh
    public static void render(String meshName) {
        checkExistence(meshName);
        container.get(meshName).render();
    }

    // Destroy a SINGLE mesh in the container
    public static void destroy(String meshName) {
        checkExistence(meshName);
        container.get(meshName).destroy();
        container.remove(meshName);
    }

    // Destroys ALL meshes in the container - Only run this AFTER the main loop has run
    public static void destroyAll() {
        for (Mesh mesh : container.values()) {
            mesh.destroy();
        }
        container.clear();
    }

    // Helper method for preventing undefined behavior
    private static void checkExistence(String meshName) {
        if (!container.containsKey(meshName)) {
            throw new RuntimeException("MeshStorage: Tried to access nonexistent mesh (" + meshName + "!");
        }
    }

}
