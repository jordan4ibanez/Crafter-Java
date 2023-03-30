package org.crafter.engine.texture;

import org.crafter.engine.utility.RawTextureObject;
import org.joml.Vector2i;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

/**
 * The actual texture object. To access into it, you must talk to texture storage!
 */
class Texture {

    private final int textureID;

    private final String name;

    private final Vector2i size;

    Texture (String fileLocation) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.name = fileLocation;

            RawTextureObject rawData = new RawTextureObject(fileLocation);

            size = new Vector2i(rawData.getWidth(), rawData.getHeight());

            // Begin OpenGL upload

            textureID = glGenTextures();

            glBindTexture(GL_TEXTURE_2D, textureID);

            // Enable texture clamping to edge
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);

            // Border color is nothing - This is a GL REQUIRED float
            float[] borderColor = {0.0f,0.0f,0.0f,0.0f};

            glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);

            // Add in nearest neighbor texture filtering
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, size.x, size.y, 0, GL_RGBA, GL_UNSIGNED_BYTE, rawData.getBuffer());

            // If this gets called, the driver is probably borked
            if (!glIsTexture(textureID)) {
                throw new RuntimeException("Texture: OpenGL failed to upload " + fileLocation + " into GPU memory!");
            }

            glGenerateMipmap(GL_TEXTURE_2D);

            // End OpenGL upload

            // Free the C memory
            rawData.destroy();
        }
    }

    void select() {
        glBindTexture(GL_TEXTURE_2D, textureID);
    }

    int getTextureID() {
        return textureID;
    }

    // This one is pretty much only for debugging
    String getName() {
        return name;
    }

    Vector2i getSize() {
        return size;
    }

    void destroy() {
        glDeleteTextures(textureID);
    }
}
