package org.crafter.engine.texture.texture_packer;

import org.crafter.engine.texture.RawTextureObject;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector4ic;

public class TexturePackerObject {
    private Vector2ic position;
    private final Vector2ic size;

    private final RawTextureObject data;

    private final String fileLocation;

    public TexturePackerObject(String fileLocation) {
        this.fileLocation = fileLocation;
        data = new RawTextureObject(fileLocation);
        size = new Vector2i(data.getWidth(), data.getHeight());
    }

    public void setPosition(Vector2i newPosition) {
        if (this.position != null) {
            throw new RuntimeException("TexturePackerObject: Tried to set position of object more than once!");
        }
        this.position = newPosition;
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
}
