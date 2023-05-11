package org.crafter.engine.texture

import org.joml.Vector4i
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer

/**
 * This is a PURE data storage class.
 * This is similar to ADR's TrueColorImage class in D in interaction.
 */
class RawTextureObject(fileLocation: String) {
    var width = 0
        private set
    var height = 0
        private set
    var channels = 0
        private set
    var buffer: ByteBuffer? = null
        private set

    init {
        MemoryStack.stackPush().use { stack ->
            val stackWidth = stack.mallocInt(1)
            val stackHeight = stack.mallocInt(1)
            val stackChannels = stack.mallocInt(1)

            // Desired channels = 4 = R,G,B,A
            buffer = STBImage.stbi_load(fileLocation, stackWidth, stackHeight, stackChannels, 4)
            if (buffer == null) {
                throw RuntimeException("RawTextureObject: Failed to load (" + fileLocation + ")! Error: " + STBImage.stbi_failure_reason())
            }
            width = stackWidth[0]
            height = stackHeight[0]
            channels = stackChannels[0]
        }
    }

    /**
     * x is left to right
     * y is top to bottom
     */
    fun getPixel(x: Int, y: Int): Vector4i {

        // Let's do a little safety check first
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw RuntimeException(
                """
                    RawTextureObject: ERROR! Accessed out of bounds!
                    Size of texture: $width, $height
                    Attempt: $x, $y
                    """.trimIndent()
            )
        }

        // Always 4 channel, so we need to treat each 4 channel as 1
        val tempWidth = width * 4

        // Bytebuffer is in ubytes in C

        // Use data pack algorithm to grab that pixel
        val index = y * tempWidth + x * 4

        // Now return it as a JOML vec4i
        return Vector4i( // & 0xff to make it a true ubyte in java's int, otherwise, it's garbage data
            buffer!![index].toInt() and 0xff,
            buffer!![index + 1].toInt() and 0xff,
            buffer!![index + 2].toInt() and 0xff,
            buffer!![index + 3].toInt() and 0xff
        )
    }

    fun destroy() {
        // This is useful for debugging
        // System.out.println("RawTextureObject: Freed memory of C int*!");
        STBImage.stbi_image_free(buffer)
    }
}
