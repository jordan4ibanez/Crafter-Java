package org.crafter.engine.gui.factories;

import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.util.HashMap;

public final class ImageMeshFactory {

    // Reuser field
    private static final Vector2f size = new Vector2f(0,0);

    private static final HashMap<String, Vector2fc> textureSizes = new HashMap<>();



    private ImageMeshFactory(){}

    public static String createImageMesh(float scale, String fileLocation) {

        Vector2fc imageTextureSize = textureSizes.get(fileLocation);

        return "test";
    }
}
