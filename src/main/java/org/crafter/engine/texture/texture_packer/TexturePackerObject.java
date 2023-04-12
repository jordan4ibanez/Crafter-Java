package org.crafter.engine.texture.texture_packer;

import org.crafter.engine.texture.RawTextureObject;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector4ic;

import java.util.UUID;

import static java.util.UUID.randomUUID;

public class TexturePackerObject {
    private Vector2ic position;

    private final Vector2ic size;

    private final RawTextureObject data;

    private final String fileLocation;

    private final UUID uuid;

    private boolean packed = false;

    public TexturePackerObject(String fileLocation) {
        this.fileLocation = fileLocation;
        data = new RawTextureObject(fileLocation);
        size = new Vector2i(data.getWidth(), data.getHeight());
        position = new Vector2i(0,0);
        uuid = randomUUID();
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

    public UUID getUuid() {
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
