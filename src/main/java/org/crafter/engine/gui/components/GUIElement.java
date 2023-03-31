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

    public final GUIElement addOnStepCallback(OnStep onStep) {
        if (onStepable()) {
            throw new RuntimeException("GUIElement: ERROR! Attempted to add (onStep) more than once in element (" + this._name + ")!");
        }
        this._onStep = onStep;
        return this;
    }
    public final GUIElement addHoverCallback(Hover hover) {
        if (hoverable()) {
            throw new RuntimeException("GUIElement: ERROR! Attempted to add (hover) more than once in element (" + this._name + ")!");
        }
        this._hover = hover;
        return this;
    }
    public final GUIElement addClickCallback(Click click) {
        if (clickable()) {
            throw new RuntimeException("GUIElement: ERROR! Attempted to add (click) more than once in element (" + this._name + ")!");
        }
        this._click = click;
        return this;
    }
    public final GUIElement addKeyInputCallback(KeyInput keyInput) {
        if (keyInputable()) {
            throw new RuntimeException("GUIElement: ERROR! Attempted to add (keyInput) more than once in element (" + this._name + ")!");
        }
        this._keyInput = keyInput;
        return this;
    }

    public String name() {
        return _name;
    }

    // What the GUI element can do when nothing is happening, cool effect, etc
    public void onStep(GUI gui) {
        if (this.onStepable()) {
            this._onStep.action(gui, this);
        }
    }

    public void onHover(GUI gui) {
        if (this.hoverable()) {
            this._hover.action(gui, this);
        }
    }
    public void onClick(GUI gui) {
        if (this.clickable()) {
            this._click.action(gui, this);
        }
    }
    public void onKeyInput(GUI gui, int keyboardKey /*Replace with real input*/) {
        if (this.keyInputable()) {
            this._keyInput.action(gui,this, keyboardKey);
        }
    }

    public abstract void render();

    public abstract boolean collisionDetect();
}
