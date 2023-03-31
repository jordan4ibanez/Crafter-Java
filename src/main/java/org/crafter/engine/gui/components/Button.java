package org.crafter.engine.gui.components;

import org.crafter.engine.gui.GUI;
import org.crafter.engine.gui.alignment.Alignment;

public class Button extends GUIElement {

    public Button(String name, Alignment alignment) {
        super(name, true,true, true, false, true, alignment);
    }

    @Override
    public void onStep(GUI gui) {

    }

    @Override
    public void onHover(GUI gui) {

    }

    @Override
    public void onClick(GUI gui) {

    }

    @Override
    public void onKeyInput(GUI gui, int keyboardKey) {

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
