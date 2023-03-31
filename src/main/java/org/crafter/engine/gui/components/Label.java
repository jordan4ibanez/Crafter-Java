package org.crafter.engine.gui.components;

import org.crafter.engine.gui.GUI;
import org.crafter.engine.gui.alignment.Alignment;

/**
 * Holds text data in memory.
 */
public class Label extends GUIElement {
    private String data;

    private float fontSize;

    public Label(String name, String data, float fontSize, Alignment alignment) {
        super(name, false, false, false, false, alignment);
        this.data = data;
        this.fontSize = fontSize;
    }

    @Override
    public void onStep(GUI gui) {
        System.out.println("I'm on step woo");
    }

    @Override
    public void hover(GUI gui) {

    }

    @Override
    public void click(GUI gui) {

    }

    @Override
    public void keyInput(GUI gui) {

    }

    @Override
    public void render() {

    }
}
