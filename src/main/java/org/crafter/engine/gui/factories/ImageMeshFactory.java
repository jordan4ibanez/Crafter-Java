package org.crafter.engine.gui.factories;

import org.crafter.engine.gui.records.ImageTrim;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.texture.TextureStorage;
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
                width, 0.0f,
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

    /**
     * I could have made the above function do a combo of this.
     * But I think it's easier to understand if it's more explicit.
     */
    public static void createTrimmedImageMesh(float scale, String fileLocation) {

    }

    private static ImageTrim trimImage(String fileLocation) {
        float width = 0;
        float height = 0;
        float startX = 0;
        float endX = 0;
        float startY = 0;
        float endY = 0;



        return new ImageTrim(width,height,startX,endX,startY,endY);
    }
}
