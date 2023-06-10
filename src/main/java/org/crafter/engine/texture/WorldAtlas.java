package org.crafter.engine.texture;

import org.crafter.engine.texture.texture_packer.TexturePacker;
import org.joml.Vector2ic;

import java.nio.ByteBuffer;

/**
 * This is just a minor abstraction to make working with the world atlas easier.
 */
public final class WorldAtlas {

    private static final TexturePacker packer = new TexturePacker();

    private static boolean locked = false;

    private WorldAtlas() {}

    public static TexturePacker getInstance() {
        return packer;
    }

    /**
     * Automates generating a usable "worldAtlas" texture in TextureStorage.
     */
    public static void lock() {
        checkLock();
        checkEmpty();
        ByteBuffer worldAtlasByteData = packer.flush();
        Vector2ic worldAtlasSize = packer.getCanvasSize();
        TextureStorage.createTexture("worldAtlas", worldAtlasByteData, worldAtlasSize);
        locked = true;
    }

    /**
     * If someone is making a custom game and didn't upload literally anything, well, we can't make an atlas out of nothing!
     */
    private static void checkEmpty() {
        // Contains no STBI freestore data if it's empty, so exit error
        if (packer.isEmpty()) {
            throw new RuntimeException("WorldAtlas: Cannot generate world atlas! No block textures were uploaded!");
        }

    }

    private static void checkLock() {
        if (locked) {
            throw new RuntimeException("WorldAtlas: Tried to lock more than once!");
        }
    }
}
