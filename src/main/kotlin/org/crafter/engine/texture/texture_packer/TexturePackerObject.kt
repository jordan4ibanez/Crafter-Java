package org.crafter.engine.texture.texture_packer

import org.crafter.engine.texture.RawTextureObject
import org.joml.Vector2i
import org.joml.Vector2ic
import org.joml.Vector4i
import org.joml.Vector4ic
import java.util.*

class TexturePackerObject(fileLocation: String) {
    var position: Vector2ic
        private set
    val size: Vector2ic
    private val data: RawTextureObject = RawTextureObject(fileLocation)
    val uuid: UUID
    var packed = false
        private set

    init {
        size = Vector2i(data.width, data.height)
        position = Vector2i(0, 0)
        uuid = UUID.randomUUID()
    }

    val positionAndSize: Vector4ic
        get() = Vector4i(position.x(), position.y(), size.x(), size.y())

    fun setPosition(x: Int, y: Int) {
        if (packed) {
            throw RuntimeException("TexturePackerObject: Tried to set position of object that's already packed!")
        }
        position = Vector2i(x, y)
    }

    fun getPixel(x: Int, y: Int): Vector4ic {
        return data.getPixel(x, y)
    }

    fun destroy() {
        data.destroy()
    }

    // One way flag
    fun setPacked() {
        packed = true
    }
}
