package org.crafter.engine.gui.components;

import org.crafter.engine.gui.enumerators.Alignment;
import org.crafter.engine.gui.implementations.Text;
import org.joml.Vector2f;

import java.util.Objects;

/**
 * Holds text data in memory.
 */
public class Label extends GUIElement implements Text {

    private String textData = "";

    private float fontSize = 24.0f;

    public Label(String name, String textData, float fontSize, Alignment alignment, Vector2f offset) {
        super(name, alignment);
        this.setText(textData);
        this.fontSize = fontSize;

        /**
         * Offset input is how far off it is from the root
         * Then required offset is an internal function that calls into the font library to find the text size so it stays locked to it's position
         */
        this.setOffset(Objects.requireNonNullElseGet(offset, () -> new Vector2f(0, 0)));
    }

    @Override
    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
        // System.out.println("Fontsize for " + this.name() + " is " + this.fontSize);
        recalculate();
    }

    @Override
    public void setText(String textData) {
        this.textData = textData;
        recalculate();
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

    @Override
    protected void recalculate() {
        System.out.println("Label: generating a new mesh");
    }
}
