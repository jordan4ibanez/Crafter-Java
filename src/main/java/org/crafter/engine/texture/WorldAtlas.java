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
