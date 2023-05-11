package org.crafter.engine.texture

import org.crafter.engine.texture.texture_packer.TexturePacker

/**
 * This is just a minor abstraction to make working with the world atlas easier.
 */
object WorldAtlas {
    val instance = TexturePacker()
    private var locked = false

    /**
     * Automates generating a usable "worldAtlas" texture in TextureStorage.
     */
    fun lock() {
        checkLock()
        checkEmpty()
        val worldAtlasByteData = instance.flush()
        val worldAtlasSize = instance.canvasSize
        TextureStorage.createTexture("worldAtlas", worldAtlasByteData, worldAtlasSize)
        locked = true
    }

    /**
     * If someone is making a custom game and didn't upload literally anything, well, we can't make an atlas out of nothing!
     */
    private fun checkEmpty() {
        // Contains no STBI freestore data if it's empty, so exit error
        if (instance.isEmpty) {
            throw RuntimeException("WorldAtlas: Cannot generate world atlas! No block textures were uploaded!")
        }
    }

    private fun checkLock() {
        if (locked) {
            throw RuntimeException("WorldAtlas: Tried to lock more than once!")
        }
    }
}
