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

        System.out.println("ColorRectangleFactory: Shipping out UUID (" + uuid + ")!");

        return uuid;
    }
}
