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

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

/**
 * The actual texture object. To access into it, you must talk to texture storage!
 */
class Texture {

    private int textureID;

    private final String name;

    private final Vector2i size;

    private final Vector2f floatingSize;

    Texture (String name, ByteBuffer buffer, Vector2ic size) {
        this.name = name;
        this.size = new Vector2i(size);
        this.floatingSize = new Vector2f(size);

        runGLTextureFunction(name, buffer);
    }

    Texture (String fileLocation) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.name = fileLocation;

            RawTextureObject rawData = new RawTextureObject(fileLocation);

            size = new Vector2i(rawData.getWidth(), rawData.getHeight());
            floatingSize = new Vector2f(rawData.getWidth(), rawData.getHeight());


            runGLTextureFunction(fileLocation, rawData.getBuffer());

            // Free the C memory
            rawData.destroy();
        }
    }

    void runGLTextureFunction(String name, ByteBuffer buffer) {
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
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, size.x, size.y, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        // If this gets called, the driver is probably borked
        if (!glIsTexture(textureID)) {
            throw new RuntimeException("Texture: OpenGL failed to upload " + name + " into GPU memory!");
        }

        glGenerateMipmap(GL_TEXTURE_2D);

        // End OpenGL upload
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
        return new Vector2i(size);
    }

    Vector2f getFloatingSize() {
        return new Vector2f(floatingSize);
    }

    void destroy() {
        glDeleteTextures(textureID);
    }
}
