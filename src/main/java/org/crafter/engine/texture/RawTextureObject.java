package org.crafter.engine.texture;

import org.joml.Vector4i;
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

    final int channels;

    final ByteBuffer buffer;

    public RawTextureObject(String fileLocation) {

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer stackWidth = stack.mallocInt(1);
            IntBuffer stackHeight = stack.mallocInt(1);

            IntBuffer stackChannels = stack.mallocInt(1);

            // Desired channels = 4 = R,G,B,A
            buffer = stbi_load(fileLocation, stackWidth, stackHeight, stackChannels, 4);

            if (buffer == null) {
                throw new RuntimeException("RawTextureObject: Failed to load (" + fileLocation + ")! Error: " + stbi_failure_reason());
            }

            width = stackWidth.get(0);
            height = stackHeight.get(0);
            channels = stackChannels.get(0);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getChannels() {
        return channels;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    /**
     * x is left to right
     * y is top to bottom
     */
    public Vector4i getPixel(int x, int y) {

        // Let's do a little safety check first
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new RuntimeException(
                    "RawTextureObject: ERROR! Accessed out of bounds!\n" +
                    "Size of texture: " + width + ", " + height + "\n" +
                    "Attempt: " +  x + ", " + y
            );
        }

        // Always 4 channel, so we need to treat each 4 channel as 1
        final int tempWidth = width * 4;

        // Bytebuffer is in ubytes in C

        // Use data pack algorithm to grab that pixel
        final int index = (y * tempWidth) + (x * 4);

        // Now return it as a JOML vec4i
        return new Vector4i(
                // & 0xff to make it a true ubyte in java's int, otherwise, it's garbage data
                (int)buffer.get(index) & 0xff,
                (int)buffer.get(index + 1) & 0xff,
                (int)buffer.get(index + 2) & 0xff,
                (int)buffer.get(index + 3) & 0xff
        );
    }

    public void destroy() {
        // This is useful for debugging
        // System.out.println("RawTextureObject: Freed memory of C int*!");
        stbi_image_free(buffer);
    }

}
