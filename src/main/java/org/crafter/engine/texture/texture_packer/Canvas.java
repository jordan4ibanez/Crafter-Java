package org.crafter.engine.texture.texture_packer;

import org.joml.Vector2i;
import org.joml.Vector4i;
import org.joml.Vector4ic;

import java.nio.*;

public class Canvas {
    private ByteBuffer data;

    private final Vector2i size;
    private final Vector2i oldSize;

    private static final int channels = 4;

    public Canvas(int width, int height) {
        oldSize = new Vector2i(0,0);
        size = new Vector2i(width, height);
        resize();
    }


    public void resize() {

        ByteBuffer newData = ByteBuffer.allocate(size.x() * size.y() * channels);

        if (data != null) {
            for (int x = 0; x < oldSize.x(); x++) {
                for (int y = 0; y < oldSize.y(); y++) {
                    Vector4i pixelColor = getPixel(data, oldSize.x(), oldSize.y(), x, y);

                }
            }
        }

        data = newData;
    }

    private Vector4i getPixel(ByteBuffer buffer, int width, int height, int x, int y) {
        // Let's do a little safety check first
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new RuntimeException(
                    "Canvas: ERROR! Accessed out of bounds!\n" +
                            "Size of canvas: " + width + ", " + height + "\n" +
                            "Attempt: " +  x + ", " + y
            );
        }
        final int tempWidth = width * 4;
        final int index = (y * tempWidth) + (x * 4);
        return new Vector4i(
                // & 0xff to make it a true ubyte in java's int, otherwise, it's garbage data
                (int)buffer.get(index) & 0xff,
                (int)buffer.get(index + 1) & 0xff,
                (int)buffer.get(index + 2) & 0xff,
                (int)buffer.get(index + 3) & 0xff
        );
    }

    private void setPixel(ByteBuffer buffer, Vector4i color, int width, int height, int x, int y) {
        colorCheck(color);
        


    }

    private void colorCheck(Vector4ic color) {
        final String[] colorNames = new String[]{"Red", "Blue", "Green", "Alpha"};
        final int[] gottenColors = new int[]{color.x(), color.y(), color.z(), color.w()};
        for (int i = 0; i < 4; i++) {
            final int colorComponent = gottenColors[i];
            if (colorComponent < 0 || colorComponent > 255) {
                throw new RuntimeException("Canvas: (" + colorNames[i] + ") is invalid! Gotten: (" + colorComponent + "). Range: Min 0 | Max 255");
            }
        }
    }



}
