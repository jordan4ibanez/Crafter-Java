package org.crafter.engine.gui.components;

import org.crafter.engine.gui.actions.Click;
import org.crafter.engine.gui.actions.GUIElement;
import org.crafter.engine.gui.actions.Hover;
import org.crafter.engine.gui.actions.KeyInput;

public class Button extends GUIElement {
    public Button(Hover hover, Click click, KeyInput keyInput) {
        super(hover, click, keyInput);
    }
}
