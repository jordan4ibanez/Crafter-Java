package org.crafter.engine.gui.components;

import org.crafter.engine.gui.GUI;
import org.crafter.engine.gui.enumerators.Alignment;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class GUIMesh extends GUIElement{

    protected GUIMesh(Alignment alignment, Vector2f offset) {
        super(alignment, offset);
    }

    @Override
    public void render() {

    }

    @Override
    public boolean collisionDetect(Vector2fc mousePosition) {
        return false;
    }

    @Override
    protected void recalculateMesh() {

    }

    @Override
    public void internalOnStep(GUI gui) {

    }

    @Override
    protected void recalculatePosition() {

    }

    @Override
    public void internalOnHover(Vector2fc mousePosition) {

    }

    @Override
    public void internalOnClick(Vector2fc mousePosition) {

    }
}
