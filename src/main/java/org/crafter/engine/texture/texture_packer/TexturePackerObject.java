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
package org.crafter.engine.texture.texture_packer;

import org.crafter.engine.texture.RawTextureObject;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector4i;
import org.joml.Vector4ic;

import java.util.UUID;


public class TexturePackerObject {
    private Vector2ic position;

    private final Vector2ic size;

    private final RawTextureObject data;

    private final String fileLocation;

    private final String uuid;

    private boolean packed = false;

    public TexturePackerObject(String fileLocation) {
        this.fileLocation = fileLocation;
        data = new RawTextureObject(fileLocation);
        size = new Vector2i(data.getWidth(), data.getHeight());
        position = new Vector2i(0,0);
        uuid = UUID.randomUUID().toString();
    }

    public Vector4ic getPositionAndSize() {
        return new Vector4i(position.x(), position.y(), size.x(), size.y());
    }

    public void setPosition(int x, int y) {
        if (packed) {
            throw new RuntimeException("TexturePackerObject: Tried to set position of object that's already packed!");
        }
        this.position = new Vector2i(x, y);
    }

    public Vector2ic getPosition() {
        return position;
    }

    public Vector2ic getSize() {
        return size;
    }

    public Vector4ic getPixel(int x, int y) {
        return data.getPixel(x,y);
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void destroy() {
        data.destroy();
    }

    public String getUuid() {
        return uuid;
    }

    // One way flag
    public void setPacked() {
        packed = true;
    }

    public boolean getPacked() {
        return packed;
    }
}
