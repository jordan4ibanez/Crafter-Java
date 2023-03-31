package org.crafter.engine.gui.components;

import org.crafter.engine.gui.actions.Click;
import org.crafter.engine.gui.actions.GUIElement;
import org.crafter.engine.gui.actions.Hover;
import org.crafter.engine.gui.actions.KeyInput;
import org.crafter.engine.gui.alignment.Alignment;

public class Button extends GUIElement {
    public Button(Hover hover, Click click, KeyInput keyInput, Alignment alignment) {
        super(hover, click, keyInput, alignment);
    }
}
