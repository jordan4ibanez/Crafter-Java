package org.crafter.engine.gui.components;

import org.crafter.engine.gui.GUI;
import org.crafter.engine.gui.alignment.Alignment;
import org.joml.Vector2f;

public abstract class GUIElement {
    protected boolean _click;
    protected boolean _hover;
    protected boolean _keyInput;

    protected boolean _onStep;

    protected Alignment _alignment;

    protected GUIElement(boolean clickable, boolean hoverable, boolean keyInputable, boolean onStepable, Alignment alignment) {
        this._click = clickable;
        this._hover = hoverable;
        this._keyInput = keyInputable;
        this._onStep = onStepable;
        this._alignment = alignment;
    }

    Vector2f alignment() {
        return new Vector2f(_alignment.value());
    }

    public final boolean onStepable() {
        return _onStep;
    }

    public final boolean hoverable() {
        return _hover;
    }
    public final boolean clickable() {
        return _click;
    }
    public final boolean keyInputable() {
        return _keyInput;
    }

    // What the GUI element can do when nothing is happening, cool effect, etc
    public abstract void onStep(GUI gui);

    public abstract void hover(GUI gui);
    public abstract void click(GUI gui);
    public abstract void keyInput(GUI gui);

    public abstract void render();
}
