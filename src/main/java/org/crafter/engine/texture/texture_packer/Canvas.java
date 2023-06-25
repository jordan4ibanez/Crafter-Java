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

import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector4i;
import org.joml.Vector4ic;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.nio.*;

public class Canvas {
    private ByteBuffer data;
    private final Vector2i size;
    private static final int channels = 4;

    public Canvas(int width, int height) {
        size = new Vector2i(width, height);
        resize(width, height);
    }

    public ByteBuffer getData() {
        return data;
    }

    public Vector2ic getSize() {
        return size;
    }

    public void resize(int width, int height) {
        size.set(width, height);
    }

    public void allocate() {
        data = BufferUtils.createByteBuffer(size.x() * size.y() * channels);
    }

    private Vector4i getPixel(ByteBuffer buffer, int width, int height, int x, int y) {
        // Let's do a little safety check first
        boundaryCheck(width, height, x, y);

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

    public void setPixel(Vector4ic color, int x, int y) {
        colorCheck(color);
        boundaryCheck(size.x(), size.y(), x, y);

        final int tempWidth = size.x() * 4;
        final int index = (y * tempWidth) + (x * 4);

        data.put(index, (byte)color.x());
        data.put(index + 1, (byte)color.y());
        data.put(index + 2, (byte)color.z());
        data.put(index + 3, (byte)color.w());

        // Testing to see if this reset
//        System.out.println("buffer pointer: " + data.position());
    }

    // fixme: might  scrap this this duplicate
    private void internalSetPixel(ByteBuffer buffer, Vector4i color, int width, int height, int x, int y) {
        colorCheck(color);
        boundaryCheck(width, height, x, y);

        final int tempWidth = width * 4;
        final int index = (y * tempWidth) + (x * 4);

        buffer.put(index, (byte)color.x());
        buffer.put(index + 1, (byte)color.y());
        buffer.put(index + 2, (byte)color.z());
        buffer.put(index + 3, (byte)color.w());

        // Testing to see if this reset
//        System.out.println("buffer pointer: " + buffer.position());
    }

    private void boundaryCheck(int width, int height, int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new RuntimeException(
                    "Canvas: ERROR! Accessed out of bounds!\n" +
                            "Size of canvas: " + width + ", " + height + "\n" +
                            "Attempt: " +  x + ", " + y
            );
        }
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
