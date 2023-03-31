package org.crafter.engine.gui.actions;

import org.crafter.engine.gui.alignment.Alignment;
import org.joml.Vector2f;

public abstract class GUIElement {
    private Hover _hover;
    private Click _click;
    private KeyInput _keyInput;

    private Alignment _alignment;
    private GUIElement(){}
    protected GUIElement(Hover hover, Click click, KeyInput keyInput, Alignment alignment) {
        _hover = hover;
        _click = click;
        _keyInput = keyInput;
        _alignment = alignment;
    }
    Vector2f alignment() {
        return _alignment.value();
    }
    public final boolean hoverable() {
        return _hover != null;
    }
    public final boolean clickable() {
        return _click != null;
    }
    public final boolean keyInputable() {
        return _keyInput != null;
    }
    public final void hover() {
        if (hoverable()) {
            _hover.action();
        }
    }
    public final void click() {
        if (clickable()) {
            _click.action();
        }
    }
    public final void keyInput() {
        if (keyInputable()) {
            _keyInput.action();
        }
    }
}
