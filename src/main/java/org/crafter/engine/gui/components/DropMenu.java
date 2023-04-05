package org.crafter.engine.gui.components;

import org.crafter.engine.gui.GUI;
import org.crafter.engine.gui.enumerators.Alignment;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class DropMenu extends GUIElement {

    // We don't want the size to change basically
    private final Vector2fc boxSize;
    private int selectedOption = 0;

    private final String[] options;

    protected DropMenu(Vector2f boxSize, String[] options, float fontSize, Alignment alignment, Vector2f offset) {
        super(alignment, offset);

        if (boxSize == null) {
            throw new RuntimeException("DropMenu: You must specify a boxSize for your drop menu!");
        }
        if (options.length < 2) {
            throw new RuntimeException("DropMenu: You must have more than one option in your drop menu!");
        }
        this.boxSize = boxSize;
        this.options = options;

    }

    public void setCurrentOption() {

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
}
