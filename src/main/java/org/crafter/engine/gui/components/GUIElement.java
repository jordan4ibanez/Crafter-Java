package org.crafter.engine.gui.components;

import org.crafter.engine.gui.GUI;
import org.crafter.engine.gui.actions.Click;
import org.crafter.engine.gui.actions.Hover;
import org.crafter.engine.gui.actions.KeyInput;
import org.crafter.engine.gui.actions.OnStep;
import org.crafter.engine.gui.alignment.Alignment;
import org.joml.Vector2f;

public abstract class GUIElement {
    protected final String _name;

    protected OnStep _onStep = null;
    protected Click _click = null;
    protected Hover _hover = null;
    protected KeyInput _keyInput = null;

    protected boolean _collide = false;

    protected Alignment _alignment;

    protected Vector2f _offset;

    protected GUIElement(String name, Alignment alignment) {
        this._name = name;
        this._alignment = alignment;
    }

    Vector2f alignment() {
        return new Vector2f(_alignment.value());
    }

    Vector2f offset() {
        return new Vector2f(_offset);
    }

    public final boolean onStepable() {
        return _onStep != null;
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

    public final boolean collideable() {
        return _collide;
    }

    public final void addOnStepCallback(OnStep onStep) {
        this._onStep = onStep;
    }
    public final void addHoverCallback(Hover hover) {
        this._hover = hover;
    }
    public final void addClickCallback(Click click) {
        this._click = click;
    }
    public final void addKeyInputCallback(KeyInput keyInput) {
        this._keyInput = keyInput;
    }

    public String name() {
        return _name;
    }

    // What the GUI element can do when nothing is happening, cool effect, etc
    public void onStep(GUI gui) {
        if (this.onStepable()) {
            this._onStep.action(gui);
        }
    }

    public void onHover(GUI gui) {
        if (this.hoverable()) {
            this._hover.action(gui);
        }
    }
    public void onClick(GUI gui) {
        if (this.clickable()) {
            this._click.action(gui);
        }
    }
    public void onKeyInput(GUI gui, int keyboardKey /*Replace with real input*/) {
        if (this.keyInputable()) {
            this._keyInput.action(gui,keyboardKey);
        }
    }

    public abstract void render();

    public abstract boolean collisionDetect();
}
