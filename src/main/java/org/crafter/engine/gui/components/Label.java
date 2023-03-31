package org.crafter.engine.gui.components;

import org.crafter.engine.gui.enumerators.Alignment;
import org.joml.Vector2f;

import java.util.Objects;

/**
 * Holds text data in memory.
 */
public class Label extends GUIElement {
    private String data;

    private float fontSize;

    Vector2f offset;

    public Label(String name, String data, float fontSize, Alignment alignment, Vector2f offset) {
        super(name, alignment);
        this.data = data;
        this.fontSize = fontSize;

        /**
         * Offset input is how far off it is from the root
         * Then required offset is an internal function that calls into the font library to find the text size so it stays locked to it's position
         */
        this.offset = Objects.requireNonNullElseGet(offset, () -> new Vector2f(0, 0));
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public void setData(String newData) {
        this.data = newData;
    }

    @Override
    public void render() {
//        System.out.println("rendering: " + this.name());

    }

    @Override
    public boolean collisionDetect() {
        // Does nothing
        return false;
    }
}
