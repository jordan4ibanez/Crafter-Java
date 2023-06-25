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
package org.crafter.engine.gui.factories;

import org.crafter.engine.gui.records.ImageTrim;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.texture.TextureStorage;
import org.crafter.engine.texture.RawTextureObject;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.util.HashMap;
import java.util.UUID;

/**
 * This generates a rectangle mesh image. That's it.
 */
public final class ImageMeshFactory {

    private static final HashMap<String, Vector2fc> textureSizes = new HashMap<>();

    private static final HashMap<String, ImageTrim> textureTrims = new HashMap<>();

    private ImageMeshFactory(){}

    public static String createImageMesh(float scale, String fileLocation) {

        Vector2fc imageTextureSize = textureSizes.get(fileLocation);

        if (imageTextureSize == null) {
            imageTextureSize = TextureStorage.getFloatingSize(fileLocation);

            textureSizes.put(fileLocation, imageTextureSize);
        }

        final float width = imageTextureSize.x() * scale;
        final float height = imageTextureSize.y() * scale;

        final float[] vertices = new float[]{
                0.0f,  0.0f,
                0.0f,  height,
                width, height,
                width, 0.0f
        };

        final float[] textureCoordinates = new float[]{
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
        };

        final int[] indices = new int[]{
                0,1,2,2,3,0
        };

        // Fully blank, the shader takes care of blank color space
        final float[] colors = new float[16];

        String uuid = UUID.randomUUID().toString();

        MeshStorage.newMesh(
                uuid,
                vertices,
                textureCoordinates,
                indices,
                null,
                colors,
                fileLocation,
                true
        );

//        System.out.println("ImageMeshFactory: Shipping out UUID (" + uuid + ")!");

        return uuid;
    }

    /**
     * I could have made the above function do a combo of this.
     * But I think it's easier to understand if it's more explicit.
     */
    public static String createTrimmedImageMesh(float scale, String fileLocation) {

        ImageTrim imageTrim = textureTrims.get(fileLocation);

        if (imageTrim == null) {
            imageTrim = trimImage(fileLocation);
            textureTrims.put(fileLocation, imageTrim);
        }

        final float width = imageTrim.width() * scale;
        final float height = imageTrim.height() * scale;

        final float[] vertices = new float[]{
                0.0f,  0.0f,
                0.0f,  height,
                width, height,
                width, 0.0f,
        };

        final float[] textureCoordinates = new float[]{
                imageTrim.startX(), imageTrim.startY(),
                imageTrim.startX(), imageTrim.endY(),
                imageTrim.endX(),   imageTrim.endY(),
                imageTrim.endX(),   imageTrim.startY()
        };

        final int[] indices = new int[]{
                0,1,2,2,3,0
        };

        // Fully blank, the shader takes care of blank color space
        final float[] colors = new float[12];

        String uuid = UUID.randomUUID().toString();

        MeshStorage.newMesh(
                uuid,
                vertices,
                textureCoordinates,
                indices,
                null,
                colors,
                fileLocation,
                true
        );

//        System.out.println("ImageMeshFactory: Shipping out UUID (" + uuid + ")!");

        return uuid;
    }

    public static Vector2f getSizeOfTrimmed(float scale, String fileLocation) {
        if (!textureTrims.containsKey(fileLocation)) {
            throw new RuntimeException("ImageMeshFactory: attempted to access size of null trimmed texture! (" + fileLocation +")");
        }
        ImageTrim thisTrim = textureTrims.get(fileLocation);
        return new Vector2f(thisTrim.width() * scale, thisTrim.height() * scale);
    }

    private static ImageTrim trimImage(String fileLocation) {
        float width;
        float height;
        float startX = 0;
        float endX = 0;
        float startY = 0;
        float endY = 0;

        final RawTextureObject tempImageObject = new RawTextureObject(fileLocation);

        final float tempWidth = tempImageObject.getWidth();
        final float tempHeight = tempImageObject.getHeight();

        // This check only has to run once
        boolean blank = true;

        boolean found = false;

        // StartX
        for (int x = 0; x < tempWidth; x++) {
            for (int y = 0; y < tempHeight; y++) {
                if (tempImageObject.getPixel(x,y).w > 0) {
                    blank = false;
                    found = true;
                    break;
                }
            }
            if (found) {
                startX = x;
                break;
            }
        }

        if (blank) {
            throw new RuntimeException("ImageMeshFactory: Tried to trim a blank image!");
        }

        found = false;

        // EndX
        for (int x = (int)tempWidth - 1; x >= 0; x--) {
            for (int y = 0; y < tempHeight; y++) {
                if (tempImageObject.getPixel(x,y).w > 0) {
                    found = true;
                    break;
                }
            }
            if (found) {
                endX = x + 1;
                break;
            }
        }

        found = false;

        // StartY
        for (int y = 0; y < tempHeight; y++) {
            for (int x = 0; x < tempWidth; x++) {
                if (tempImageObject.getPixel(x,y).w > 0) {
                    found = true;
                    break;
                }
            }
            if (found) {
                startY = y;
                break;
            }
        }

        found = false;

        // EndY
        for (int y = (int)tempHeight - 1; y >= 0; y--) {
            for (int x = 0; x < tempWidth; x++) {
                if (tempImageObject.getPixel(x,y).w > 0) {
                    found = true;
                    break;
                }
            }
            if (found) {
                endY = y + 1;
                break;
            }
        }

        // Now finish calculations

        width = endX - startX;
        height = endY - startY;

        startX /= tempWidth;
        endX /= tempWidth;

        startY /= tempHeight;
        endY /= tempHeight;

        // Delete that C memory
        tempImageObject.destroy();

        return new ImageTrim(width,height,startX,endX,startY,endY);
    }
}
