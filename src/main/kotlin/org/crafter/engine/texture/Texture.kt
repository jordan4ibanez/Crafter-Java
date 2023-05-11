package org.crafter.engine.texture

import org.joml.Vector2f
import org.joml.Vector2i
import org.joml.Vector2ic
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL30
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer

/**
 * The actual texture object. To access into it, you must talk to texture storage!
 */
internal class Texture {
    var textureID = 0
        private set

    // This one is pretty much only for debugging
    val name: String
    val size: Vector2i
    val floatingSize: Vector2f

    constructor(name: String, buffer: ByteBuffer?, size: Vector2ic?) {
        this.name = name
        this.size = Vector2i(size)
        floatingSize = Vector2f(size)
        runGLTextureFunction(name, buffer)
    }

    constructor(fileLocation: String) {
        MemoryStack.stackPush().use { stack ->
            name = fileLocation
            val rawData = RawTextureObject(fileLocation)
            size = Vector2i(rawData.width, rawData.height)
            floatingSize = Vector2f(rawData.width.toFloat(), rawData.height.toFloat())
            runGLTextureFunction(fileLocation, rawData.buffer)

            // Free the C memory
            rawData.destroy()
        }
    }

    fun runGLTextureFunction(name: String, buffer: ByteBuffer?) {
        // Begin OpenGL upload
        textureID = GL11.glGenTextures()
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID)

        // Enable texture clamping to edge
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_BORDER)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_BORDER)

        // Border color is nothing - This is a GL REQUIRED float
        val borderColor = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f)
        GL11.glTexParameterfv(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_BORDER_COLOR, borderColor)

        // Add in nearest neighbor texture filtering
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1)
        GL11.glTexImage2D(
            GL11.GL_TEXTURE_2D,
            0,
            GL11.GL_RGBA,
            size.x,
            size.y,
            0,
            GL11.GL_RGBA,
            GL11.GL_UNSIGNED_BYTE,
            buffer
        )

        // If this gets called, the driver is probably borked
        if (!GL11.glIsTexture(textureID)) {
            throw RuntimeException("Texture: OpenGL failed to upload $name into GPU memory!")
        }
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)

        // End OpenGL upload
    }

    fun select() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID)
    }

    fun destroy() {
        GL11.glDeleteTextures(textureID)
    }
}
