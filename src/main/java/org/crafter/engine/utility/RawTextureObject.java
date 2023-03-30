package org.crafter.engine.utility;

import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.*;

/**
 * This is a PURE data storage class.
 * This is similar to ADR's TrueColorImage class in D in interaction.
 */
public class RawTextureObject {

    final int width;
    final int height;

    final ByteBuffer buffer;

    public RawTextureObject(String fileLocation) {

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer stackWidth = stack.mallocInt(1);
            IntBuffer stackHeight = stack.mallocInt(1);

            IntBuffer channels = stack.mallocInt(1);

            // Desired channels = 4 = R,G,B,A
            buffer = stbi_load(fileLocation, stackWidth, stackHeight, channels, 4);

            if (buffer == null) {
                throw new RuntimeException("RawTextureObject: Failed to load (" + fileLocation + ")! Error: " + stbi_failure_reason());
            }

            width = stackWidth.get(0);
            height = stackHeight.get(0);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public void destroy() {
        System.out.println("RawTextureObject: Freed memory of C int*!");
        stbi_image_free(buffer);
    }

}
