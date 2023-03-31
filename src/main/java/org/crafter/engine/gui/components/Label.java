package org.crafter.engine.gui.components;

import org.crafter.engine.gui.actions.GUIElement;
import org.crafter.engine.gui.alignment.Alignment;

/**
 * Holds text data in memory.
 */
public class Label extends GUIElement {
    private String data;

    private float fontSize;

    Label(String data, float fontSize, Alignment alignment) {
        super(null, null, null, alignment);
        this.data = data;
        this.fontSize = fontSize;
    }

}
