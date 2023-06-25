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

import java.util.UUID;

public final class ColorRectangleFactory {

    private ColorRectangleFactory(){}

    public static String createColorRectangleMesh(float width, float height, float r, float g, float b) {
        return createColorRectangleMesh(width,height,r,g,b,1);
    }

    public static String createColorRectangleMesh(float width, float height, float r, float g, float b, float a) {
        final float[] vertices = new float[]{
                0.0f,  0.0f,
                0.0f,  height,
                width, height,
                width, 0.0f
        };

        final float[] textureCoordinates = new float[]{
                0,0,
                0,1,
                1,1,
                1,0
        };

        final int[] indices = new int[]{
                0,1,2,2,3,0
        };

        final float[] colors = new float[]{
                r,g,b,a,
                r,g,b,a,
                r,g,b,a,
                r,g,b,a
        };

        String uuid = UUID.randomUUID().toString();

        MeshStorage.newMesh(
                uuid,
                vertices,
                textureCoordinates,
                indices,
                null,
                colors,
                "textures/blank_pixel.png",
                true
        );

//        System.out.println("ColorRectangleFactory: Shipping out UUID (" + uuid + ")!");

        return uuid;
    }
}
