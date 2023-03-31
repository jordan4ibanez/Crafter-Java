package org.crafter.engine.gui.components;

import org.crafter.engine.gui.alignment.Alignment;

/**
 * Holds text data in memory.
 */
public class Label extends GUIElement {
    private String data;

    private float fontSize;

    public Label(String name, String data, float fontSize, Alignment alignment) {
        super(name, alignment);
        this.data = data;
        this.fontSize = fontSize;

        /**
         * Offset input is how far off it is from the root
         * Then required offset is an internal function that calls into the font library to find the text size so it stays locked to it's position
         */
    }

    @Override
    public void render() {

    }

    @Override
    public boolean collisionDetect() {
        // Does nothing
        return false;
    }
}
