package org.crafter.engine.gui.actions;

public abstract class GUIElement {
    Hover hover;

    Click click;

    KeyInput keyInput;

    protected GUIElement(){}

    protected GUIElement(Hover hover, Click click, KeyInput keyInput) {
        this.hover = hover;
        this.click = click;
        this.keyInput = keyInput;
    }

}
