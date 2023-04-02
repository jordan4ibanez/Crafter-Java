package org.crafter.engine.gui.components;

import org.crafter.engine.gui.GUI;
import org.crafter.engine.gui.actions.Click;
import org.crafter.engine.gui.actions.Hover;
import org.crafter.engine.gui.actions.KeyInput;
import org.crafter.engine.gui.actions.OnStep;
import org.crafter.engine.gui.enumerators.Alignment;
import org.joml.Vector2f;

public abstract class GUIElement {
    protected final String _name;

    protected String _meshUUID = null;

    protected OnStep _onStep = null;
    protected Click _click = null;
    protected Hover _hover = null;
    protected KeyInput _keyInput = null;

    protected boolean _collide = false;

    protected Alignment _alignment;

    // Centering vector is the size of the element, so it can remain in its position with alignment
    protected final Vector2f _centeringVector = new Vector2f(0,0);

    protected final Vector2f _offset = new Vector2f(0,0);

    protected final Vector2f _renderPosition = new Vector2f(0,0);

    protected GUIElement(String name, Alignment alignment, Vector2f offset) {
        if (name == null) {
            throw new RuntimeException("GUIElement: name CANNOT be null!");
        }
        if (alignment == null) {
            throw new RuntimeException("GUIElement: alignment CANNOT be null!");
        }
        /*
         * Offset input is how far off it is from the root alignment.
         */
        if (offset == null) {
            throw new RuntimeException("GUIElement: offset CANNOT be null!");
        }
        this._name = name;
        this._alignment = alignment;
        this._offset.set(offset);
    }


    protected String getMeshUUID() {
        return _meshUUID;
    }

    protected void setMeshUUID(String meshUUID) {
        this._meshUUID = meshUUID;
    }

    protected Vector2f alignment() {
        return new Vector2f(_alignment.value());
    }

    protected Vector2f offset() {
        // I like to have +y be up when setting offset, so I made it like this
        return new Vector2f(_offset.x, -_offset.y);
    }

    protected Vector2f getCenteringVector() {
        return new Vector2f(_centeringVector);
    }

    protected void setCenteringVector(Vector2f newVector) {
        this._centeringVector.set(newVector);
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

    // Callbacks are only available on element creation
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

    public final void setOffset(Vector2f offset) {
        this._offset.x = offset.x;
        this._offset.y = offset.y;
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

    protected abstract void recalculateMesh();

    public abstract void internalOnStep();

    // Enforce recalculation, it's very important to keep gui elements in correct position
    protected abstract void recalculatePosition();
}
