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

import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.texture.TextureStorage;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.util.HashMap;
import java.util.UUID;

public final class FramedMeshFactory {

    // This is a reuser field
    private static final Vector2f size = new Vector2f(0,0);

    // This gets auto initialized
    private static final HashMap<String, Vector2fc> textureSizes = new HashMap<>();

    private FramedMeshFactory(){}

    /**
     * Button Mesh Factory does exactly what it says on the tin.
     * It's sole existence is to generate the mesh for the Button component.
     * This keeps the Button class clean as a whistle.
     * Note: The comments are from the original D project.
     */
    public static String generateMesh(Vector2fc textSize, final float padding, final float pixelEdge, final float borderScale, final String fileLocation) {
        // Pixel padding between the edge of the button texture, and the text texture
//        final float padding = Button.getPadding();

        // The guide edges for buttons, keeps texture edges from stretching
        // So think of this of like: How many pixels does your button texture use before getting to the text part.
//        final float pixelEdge = Button.getPixelEdge();

        // Border scalar just makes the button border more pronounced/visible
//        final float borderScale = Button.getBorderScale();

        size.set(
                textSize.x() + (padding * 2),
                textSize.y() + (padding * 2)
        );

        Vector2fc buttonTextureSize = textureSizes.get(fileLocation);
        // Auto initialize
        if (buttonTextureSize == null) {
            buttonTextureSize = TextureStorage.getFloatingSize(fileLocation);

            textureSizes.put(fileLocation, buttonTextureSize);
        }

        // We're going to use the height to create the consistent layout

        float centerBorder = (size.y / buttonTextureSize.y()) * pixelEdge * borderScale;

        /*
         This is each point on the horizontal 1d array of the button background.

         0  1                                 2  3
          _______________________________________
         |  ___________________________________  |
         | |                                   | |
         */
        //                                           0  1             2                      3
        final float[] horizontalVertex = new float[]{0, centerBorder, size.x - centerBorder, size.x};

        /*
         This is each point on the vertical 1d array of button background.
         0  ________
           |
         1 |    ____
           |   |
           |   |
           |   |
         2 |   |_____
           |
         3 |_________
         */

        //                                         0  1             2                      3
        final float[] verticalVertex = new float[]{0, centerBorder, size.y - centerBorder, size.y};


        final float[] vertices = new float[]{
                // Top left
                horizontalVertex[0], verticalVertex[0],
                horizontalVertex[0], verticalVertex[1],
                horizontalVertex[1], verticalVertex[1],
                horizontalVertex[1], verticalVertex[0],

                // Top center
                horizontalVertex[1], verticalVertex[0],
                horizontalVertex[1], verticalVertex[1],
                horizontalVertex[2], verticalVertex[1],
                horizontalVertex[2], verticalVertex[0],

                // Top right
                horizontalVertex[2], verticalVertex[0],
                horizontalVertex[2], verticalVertex[1],
                horizontalVertex[3], verticalVertex[1],
                horizontalVertex[3], verticalVertex[0],

                // Center left
                horizontalVertex[0], verticalVertex[1],
                horizontalVertex[0], verticalVertex[2],
                horizontalVertex[1], verticalVertex[2],
                horizontalVertex[1], verticalVertex[1],

                // Center center
                horizontalVertex[1], verticalVertex[1],
                horizontalVertex[1], verticalVertex[2],
                horizontalVertex[2], verticalVertex[2],
                horizontalVertex[2], verticalVertex[1],

                // Center right
                horizontalVertex[2], verticalVertex[1],
                horizontalVertex[2], verticalVertex[2],
                horizontalVertex[3], verticalVertex[2],
                horizontalVertex[3], verticalVertex[1],

                // Bottom left
                horizontalVertex[0], verticalVertex[2],
                horizontalVertex[0], verticalVertex[3],
                horizontalVertex[1], verticalVertex[3],
                horizontalVertex[1], verticalVertex[2],

                // Bottom center
                horizontalVertex[1], verticalVertex[2],
                horizontalVertex[1], verticalVertex[3],
                horizontalVertex[2], verticalVertex[3],
                horizontalVertex[2], verticalVertex[2],

                // Bottom right
                horizontalVertex[2], verticalVertex[2],
                horizontalVertex[2], verticalVertex[3],
                horizontalVertex[3], verticalVertex[3],
                horizontalVertex[3], verticalVertex[2]
        };

        /*
         So the texture coordinates work exactly as explained above, only we're mapping to the texture
         instead of generating the vertices.
         */

        //                                            0     1                                2                                                        3
        final float[] horizontalTexture = new float[]{0.0f, pixelEdge / buttonTextureSize.x(), (buttonTextureSize.x() - pixelEdge) / buttonTextureSize.x(), 1.0f};

        //                                          0     1                                2                                                        3
        final float[] verticalTexture = new float[]{0.0f, pixelEdge / buttonTextureSize.y(), (buttonTextureSize.y() - pixelEdge) / buttonTextureSize.y(), 1.0f};

        final float[] textureCoordinates = new float[]{
                // Top left
                horizontalTexture[0], verticalTexture[0],
                horizontalTexture[0], verticalTexture[1],
                horizontalTexture[1], verticalTexture[1],
                horizontalTexture[1], verticalTexture[0],

                // Top center
                horizontalTexture[1], verticalTexture[0],
                horizontalTexture[1], verticalTexture[1],
                horizontalTexture[2], verticalTexture[1],
                horizontalTexture[2], verticalTexture[0],

                // Top right
                horizontalTexture[2], verticalTexture[0],
                horizontalTexture[2], verticalTexture[1],
                horizontalTexture[3], verticalTexture[1],
                horizontalTexture[3], verticalTexture[0],

                // Center left
                horizontalTexture[0], verticalTexture[1],
                horizontalTexture[0], verticalTexture[2],
                horizontalTexture[1], verticalTexture[2],
                horizontalTexture[1], verticalTexture[1],

                // Center center
                horizontalTexture[1], verticalTexture[1],
                horizontalTexture[1], verticalTexture[2],
                horizontalTexture[2], verticalTexture[2],
                horizontalTexture[2], verticalTexture[1],

                // Center right
                horizontalTexture[2], verticalTexture[1],
                horizontalTexture[2], verticalTexture[2],
                horizontalTexture[3], verticalTexture[2],
                horizontalTexture[3], verticalTexture[1],

                // Bottom left
                horizontalTexture[0], verticalTexture[2],
                horizontalTexture[0], verticalTexture[3],
                horizontalTexture[1], verticalTexture[3],
                horizontalTexture[1], verticalTexture[2],

                // Bottom center
                horizontalTexture[1], verticalTexture[2],
                horizontalTexture[1], verticalTexture[3],
                horizontalTexture[2], verticalTexture[3],
                horizontalTexture[2], verticalTexture[2],

                // Bottom right
                horizontalTexture[2], verticalTexture[2],
                horizontalTexture[2], verticalTexture[3],
                horizontalTexture[3], verticalTexture[3],
                horizontalTexture[3], verticalTexture[2]
        };

        int[] indices = new int[]{
                // Top left
                0, 1, 2, 2, 3, 0,
                // Top center
                4, 5, 6, 6, 7, 4,
                // Top right
                8, 9, 10, 10, 11, 8,
                // Center left
                12, 13, 14, 14, 15, 12,
                // Center
                16, 17, 18, 18, 19, 16,
                // Center right
                20, 21, 22, 22, 23, 20,
                // Bottom left
                24, 25, 26, 26, 27, 24,
                // Bottom center
                28, 29, 30, 30, 31, 28,
                // Bottom right
                32, 33, 34, 34, 35, 32
        };

        // Fully blank, the shader takes care of blank color space
        final float[] colors = new float[144];

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

        // System.out.println("ButtonMeshFactory: Shipping out UUID (" + uuid + ")!");

        return uuid;
    }
}
