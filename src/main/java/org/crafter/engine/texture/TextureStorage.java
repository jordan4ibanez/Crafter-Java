package org.crafter.engine.texture;

import java.util.HashMap;

/**
 * This class holds all the textures!
 * A neat little house for them.
 */
public final class TextureStorage {

    // Here's where all the texures live!
    private static final HashMap<String, Texture> container = new HashMap<>();

    private TextureStorage(){}

    public static void createTexture(String fileLocation) {
        if (container.containsKey(fileLocation)) {
            throw new RuntimeException("TextureStorage: Tried to add " + fileLocation + " more than once!");
        }
        container.put(fileLocation, new Texture(fileLocation));
    }








    public static void destroy() {
        for (Texture texture : container.values()) {
            texture.destroy();
        }
    }

    // Internal check to make sure nothing stupid is happening
    private static void checkExistence(String textureName) {
        if (!container.containsKey(textureName)) {
            throw new RuntimeException("TextureStorage: Tried to access nonexistent texture (" + textureName + "!");
        }
    }
}
