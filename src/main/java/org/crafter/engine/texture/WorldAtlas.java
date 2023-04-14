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
        ByteBuffer worldAtlasByteData = packer.flush();
        Vector2ic worldAtlasSize = packer.getCanvasSize();
        TextureStorage.createTexture("worldAtlas", worldAtlasByteData, worldAtlasSize);
        locked = true;
    }

    private static void checkLock() {
        if (locked) {
            throw new RuntimeException("WorldAtlas: Tried to lock more than once!");
        }
    }
}
