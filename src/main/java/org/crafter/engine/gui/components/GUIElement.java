package org.crafter.engine.gui.components;

import org.crafter.engine.gui.GUI;
import org.crafter.engine.gui.actions.Click;
import org.crafter.engine.gui.actions.Hover;
import org.crafter.engine.gui.actions.KeyInput;
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
        return new Vector2f(_alignment.value());
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
    public final void hover(GUI gui) {
        if (hoverable()) {
            _hover.action(gui);
        }
    }
    public final void click(GUI gui) {
        if (clickable()) {
            _click.action(gui);
        }
    }
    public final void keyInput(GUI gui) {
        if (keyInputable()) {
            _keyInput.action(gui);
        }
    }
}
