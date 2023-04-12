package org.crafter.engine.texture;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * This class holds all the textures!
 * A neat little house for them.
 */
public final class TextureStorage {

    // Here's where all the textures live!
    private static final HashMap<String, Texture> container = new HashMap<>();

    private TextureStorage(){}

    // Create a new texture from a buffer directly
    public static void createTexture(String name, ByteBuffer buffer, Vector2ic size) {
        if (container.containsKey(name)) {
            throw new RuntimeException("TextureStorage: Tried to add " + name + " more than once!");
        }
        System.out.println("TextureStorage: Created texture (" + name + ")");
        container.put(name, new Texture(name, buffer, size));
    }

    // Create a new texture
    public static void createTexture(String fileLocation) {
        if (container.containsKey(fileLocation)) {
            throw new RuntimeException("TextureStorage: Tried to add " + fileLocation + " more than once!");
        }
//        System.out.println("TextureStorage: Created texture (" + fileLocation + ")");
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

    // Get Vector2f(width, height) of texture - Useful for mapping
    public static Vector2f getFloatingSize(String fileLocation) {
        checkExistence(fileLocation);
        return container.get(fileLocation).getFloatingSize();
    }
    
    // This shall ONLY be called after the main loop is finished!
    public static void destroyAll() {
        for (Texture texture : container.values()) {
            texture.destroy();
        }
        container.clear();
    }

    // Internal check to make sure nothing stupid is happening
    private static void checkExistence(String fileLocation) {
        if (!container.containsKey(fileLocation)) {
            // Automatically upload texture if it doesn't exist
            createTexture(fileLocation);
//            throw new RuntimeException("TextureStorage: Tried to access nonexistent texture (" + fileLocation + ")!");
        }
    }
}
