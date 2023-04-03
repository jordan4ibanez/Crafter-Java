package org.crafter.engine.gui.components;

import org.crafter.engine.gui.enumerators.Alignment;
import org.crafter.engine.gui.implementations.Text;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class TextBox extends Text {

    

    public TextBox(String name, String textData, float fontSize, Alignment alignment, Vector2f offset) {
        super(name, textData, fontSize, alignment, offset);

    }

    @Override
    public void render() {

    }

    @Override
    public void internalOnStep() {

    }

    @Override
    protected void recalculatePosition() {

    }

    @Override
    public boolean collisionDetect(Vector2fc mousePosition) {
        return false;
    }

    @Override
    protected void recalculateMesh() {

    }
}
