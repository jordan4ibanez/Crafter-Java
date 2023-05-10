@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package org.crafter.engine.texture

import org.joml.Vector2f
import org.joml.Vector2i
import org.joml.Vector2ic
import java.nio.ByteBuffer

/**
 * This class holds all the textures!
 * A neat little house for them.
 */
object TextureStorage {
    // Here's where all the textures live!
    private val container = HashMap<String, Texture>()

    // Create a new texture from a buffer directly
    fun createTexture(name: String, buffer: ByteBuffer, size: Vector2ic) {
        checkDuplicate(name)
        //        System.out.println("TextureStorage: Created texture (" + name + ")");
        container[name] = Texture(name, buffer, size)
    }

    // Create a new texture
    fun createTexture(fileLocation: String) {
        checkDuplicate(fileLocation)
        //        System.out.println("TextureStorage: Created texture (" + fileLocation + ")");
        container[fileLocation] = Texture(fileLocation)
    }

    // Create a new texture with a specific name
    fun createTexture(name: String, fileLocation: String) {
        checkDuplicate(name)
        //        System.out.println("TextureStorage: Created texture (" + name + ")");
        container[name] = Texture(fileLocation)
    }

    // Bind context to the selected OpenGL texture
    fun select(fileLocation: String) {
        checkExistence(fileLocation)
        container[fileLocation]!!.select()
    }

    // Get the OpenGL ID of a texture
    fun getID(fileLocation: String): Int {
        checkExistence(fileLocation)
        return container[fileLocation]?.textureID ?: throw RuntimeException("TextureStorage: ERROR! Texture ID was null! for (" + container[fileLocation]?.name + ")!")
    }

    // Get Vector2i(width, height) of texture - Useful for mapping
    fun getSize(fileLocation: String): Vector2i {
        checkExistence(fileLocation)
        return container[fileLocation]!!.size
    }

    // Get Vector2f(width, height) of texture - Useful for mapping
    fun getFloatingSize(fileLocation: String): Vector2f {
        checkExistence(fileLocation)
        return container[fileLocation]!!.floatingSize
    }

    // This shall ONLY be called after the main loop is finished!
    fun destroyAll() {
        for (texture in container.values) {
            texture.destroy()
        }
        container.clear()
    }

    // Internal check to make sure nothing stupid is happening
    private fun checkExistence(fileLocation: String) {
        if (!container.containsKey(fileLocation)) {
            // Automatically upload texture if it doesn't exist
            createTexture(fileLocation)
            //            throw new RuntimeException("TextureStorage: Tried to access nonexistent texture (" + fileLocation + ")!");
        }
    }

    private fun checkDuplicate(name: String) {
        if (container.containsKey(name)) {
            throw RuntimeException("TextureStorage: Tried to add $name more than once!")
        }
    }
}
