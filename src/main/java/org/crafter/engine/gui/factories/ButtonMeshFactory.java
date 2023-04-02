package org.crafter.engine.gui.factories;

import org.crafter.engine.gui.components.Button;
import org.joml.Vector2fc;

public final class ButtonMeshFactory {

    private ButtonMeshFactory(){}

    /**
     * Button Mesh Factory does exactly what it says on the tin.
     * It's sole existence is to generate the mesh for the Button component.
     * This keeps the Button class clean as a whistle.
     */
    public static String generateMesh(Vector2fc textSize) {
        final float padding = Button.padding;
        final float pixelEdge = Button.pixelEdge;
        final float borderScale = Button.borderScale;

        

        return "test";
    }
}
