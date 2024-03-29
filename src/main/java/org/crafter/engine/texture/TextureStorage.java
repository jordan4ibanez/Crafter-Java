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
        checkDuplicate(name);
//        System.out.println("TextureStorage: Created texture (" + name + ")");
        container.put(name, new Texture(name, buffer, size));
    }

    // Create a new texture
    public static void createTexture(String fileLocation) {
        checkDuplicate(fileLocation);
//        System.out.println("TextureStorage: Created texture (" + fileLocation + ")");
        container.put(fileLocation, new Texture(fileLocation));
    }

    // Create a new texture with a specific name
    public static void createTexture(String name, String fileLocation) {
        checkDuplicate(name);
//        System.out.println("TextureStorage: Created texture (" + name + ")");
        container.put(name, new Texture(fileLocation));
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
    private static void checkDuplicate(String name) {
        if (container.containsKey(name)) {
            throw new RuntimeException("TextureStorage: Tried to add " + name + " more than once!");
        }
    }
}
