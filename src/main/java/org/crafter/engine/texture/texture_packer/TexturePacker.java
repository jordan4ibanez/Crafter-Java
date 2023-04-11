package org.crafter.engine.texture.texture_packer;

import org.joml.Vector4i;

import java.util.HashMap;

/**
 * This is translated from a D project.
 * <a href="https://github.com/jordan4ibanez/fast_pack/blob/main/source/fast_pack.d">...</a>
 */
public class TexturePacker {
    private boolean fastCanvasExport = true;
    private int padding = 0;
    private final Vector4i edgeColor = new Vector4i(0,0,0,255);
    private final Vector4i blankSpaceColor = new Vector4i(0,0,0,0);
    private int expansionAmount = 100;
    private final boolean showDebugEdge = false;
    private int width = 400;
    private int height = 400;

    private int currentID = 0;
    private final HashMap<String, TexturePackerObject> textures;
    private final Canvas canvas;

    public TexturePacker() {
        textures = new HashMap<>();
        canvas = new Canvas(width, height);
    }
}
