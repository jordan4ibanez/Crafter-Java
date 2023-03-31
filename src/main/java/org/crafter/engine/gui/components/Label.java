package org.crafter.engine.gui.components;

import org.crafter.engine.gui.actions.GUIElement;

/**
 * Holds text data in memory.
 */
public class Label extends GUIElement {
    private String data;

    private float fontSize;

    Label(String data, float fontSize) {
        super(null, null, null);
        this.data = data;
        this.fontSize = fontSize;
    }

}
