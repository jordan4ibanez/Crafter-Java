package org.crafter.engine.texture.texture_packer

import org.joml.Vector2i
import org.joml.Vector2ic
import org.joml.Vector4i
import org.joml.Vector4ic
import org.lwjgl.BufferUtils
import java.nio.ByteBuffer

class Canvas(width: Int, height: Int) {
    var data: ByteBuffer? = null
        private set
    private val size: Vector2i

    init {
        size = Vector2i(width, height)
        resize(width, height)
    }

    fun getSize(): Vector2ic {
        return size
    }

    fun resize(width: Int, height: Int) {
        size[width] = height
    }

    fun allocate() {
        data = BufferUtils.createByteBuffer(size.x() * size.y() * channels)
    }

    private fun getPixel(buffer: ByteBuffer, width: Int, height: Int, x: Int, y: Int): Vector4i {
        // Let's do a little safety check first
        boundaryCheck(width, height, x, y)
        val tempWidth = width * 4
        val index = y * tempWidth + x * 4
        return Vector4i( // & 0xff to make it a true ubyte in java's int, otherwise, it's garbage data
            buffer[index].toInt() and 0xff,
            buffer[index + 1].toInt() and 0xff,
            buffer[index + 2].toInt() and 0xff,
            buffer[index + 3].toInt() and 0xff
        )
    }

    fun setPixel(color: Vector4ic, x: Int, y: Int) {
        colorCheck(color)
        boundaryCheck(size.x(), size.y(), x, y)
        val tempWidth = size.x() * 4
        val index = y * tempWidth + x * 4
        data!!.put(index, color.x().toByte())
        data!!.put(index + 1, color.y().toByte())
        data!!.put(index + 2, color.z().toByte())
        data!!.put(index + 3, color.w().toByte())

        // Testing to see if this reset
//        System.out.println("buffer pointer: " + data.position());
    }

    // fixme: might  scrap this this duplicate
    private fun internalSetPixel(buffer: ByteBuffer, color: Vector4i, width: Int, height: Int, x: Int, y: Int) {
        colorCheck(color)
        boundaryCheck(width, height, x, y)
        val tempWidth = width * 4
        val index = y * tempWidth + x * 4
        buffer.put(index, color.x().toByte())
        buffer.put(index + 1, color.y().toByte())
        buffer.put(index + 2, color.z().toByte())
        buffer.put(index + 3, color.w().toByte())

        // Testing to see if this reset
//        System.out.println("buffer pointer: " + buffer.position());
    }

    private fun boundaryCheck(width: Int, height: Int, x: Int, y: Int) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw RuntimeException(
                """
                    Canvas: ERROR! Accessed out of bounds!
                    Size of canvas: $width, $height
                    Attempt: $x, $y
                    """.trimIndent()
            )
        }
    }

    private fun colorCheck(color: Vector4ic) {
        val colorNames = arrayOf("Red", "Blue", "Green", "Alpha")
        val gottenColors = intArrayOf(color.x(), color.y(), color.z(), color.w())
        for (i in 0..3) {
            val colorComponent = gottenColors[i]
            if (colorComponent < 0 || colorComponent > 255) {
                throw RuntimeException("Canvas: (" + colorNames[i] + ") is invalid! Gotten: (" + colorComponent + "). Range: Min 0 | Max 255")
            }
        }
    }

    companion object {
        private const val channels = 4
    }
}
