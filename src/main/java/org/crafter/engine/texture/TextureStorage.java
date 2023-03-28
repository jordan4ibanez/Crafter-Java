package org.crafter.engine.texture;

import org.joml.Vector2i;

import java.util.HashMap;

/**
 * This class holds all the textures!
 * A neat little house for them.
 */
public final class TextureStorage {

    // Here's where all the textures live!
    private static final HashMap<String, Texture> container = new HashMap<>();

    private TextureStorage(){}

    // Create a new texture
    public static void createTexture(String fileLocation) {
        if (container.containsKey(fileLocation)) {
            throw new RuntimeException("TextureStorage: Tried to add " + fileLocation + " more than once!");
        }
        container.put(fileLocation, new Texture(fileLocation));
    }

    // Bind context to the selected OpenGL texture
    public static void select(String fileLocation) {
        checkExistence(fileLocation);
        container.get(fileLocation).select();
    }

    // Get the OpenGL ID of a texture
    public static int getID(String fileLocation) {
        checkExistence(fileLocation);
        return container.get(fileLocation).getTextureID();
    }

    // Get Vector2i(width, height) of texture - Useful for mapping
    public static Vector2i getSize(String fileLocation) {
        checkExistence(fileLocation);
        return container.get(fileLocation).getSize();
    }
    
    // This shall ONLY be called after the main loop is finished!
    public static void destroyAll() {
        for (Texture texture : container.values()) {
            texture.destroy();
        }
        container.clear();
    }

    // Internal check to make sure nothing stupid is happening
    private static void checkExistence(String textureName) {
        if (!container.containsKey(textureName)) {
            throw new RuntimeException("TextureStorage: Tried to access nonexistent texture (" + textureName + "!");
        }
    }
}
