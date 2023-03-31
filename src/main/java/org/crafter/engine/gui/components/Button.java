package org.crafter.engine.gui.components;

import org.crafter.engine.gui.enumerators.Alignment;

public class Button extends GUIElement {

    public Button(String name, Alignment alignment) {
        super(name, alignment);
    }

    @Override
    public void render() {

    }

    @Override
    public boolean collisionDetect() {

        // This needs a return
        return false;
    }

}
