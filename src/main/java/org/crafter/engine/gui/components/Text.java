package org.crafter.engine.gui.components;

import org.crafter.engine.gui.actions.GUIElement;

/**
 * Holds text data in memory.
 */
public class Text extends GUIElement {
    private String data;

    private float fontSize;

    Text (String data, float fontSize) {
        super(null, null, null);
        this.data = data;
        this.fontSize = fontSize;
    }

}
