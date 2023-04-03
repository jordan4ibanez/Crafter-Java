package org.crafter.engine.gui.components;

import org.crafter.engine.gui.GUI;
import org.crafter.engine.gui.actions.*;
import org.crafter.engine.gui.enumerators.Alignment;
import org.crafter.engine.window.Window;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.util.Objects;

public abstract class GUIElement {

    private static float guiScale = 1;

    protected final String _name;

    protected String _meshUUID = null;

    protected OnStep _onStep = null;
    protected Click _click = null;
    protected Hover _hover = null;
    protected KeyInput _keyInput = null;
    protected EnterInput _enterInput = null;

    protected boolean _collide = false;

    protected Alignment _alignment;

    // Size is the width and height of the element
    protected final Vector2f _size = new Vector2f(0,0);

    protected final Vector2f _offset = new Vector2f(0,0);

    protected final Vector2f _position = new Vector2f(0,0);

    protected GUIElement(String name, Alignment alignment, Vector2f offset) {
        if (name == null) {
            throw new RuntimeException("GUIElement : name CANNOT be null!");
        }
        this._name = name;

        _alignment = Objects.requireNonNullElse(alignment, Alignment.DEFAULT);
        /*
         * Offset input is how far off it is from the root alignment.
         */
        if (offset != null) {
            _offset.set(offset);
        }
    }

    public static void recalculateGUIScale() {
        Vector2fc test = Window.getWindowSize();

        // 1080p is the gold standard resolution
        float xCompare = test.x() / 1920.0f;
        float yCompare = test.y() / 1080.0f;

        guiScale = Math.min(xCompare, yCompare);
    }

    public static float getGuiScale() {
        return guiScale;
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

    protected Vector2f getSize() {
        return new Vector2f(_size);
    }

    protected void setSize(Vector2f newVector) {
        this._size.set(newVector);
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

    public final boolean enterInputable() {
        return _enterInput != null;
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

    public final GUIElement addEnterInputCallback(EnterInput enterInput) {
        if (enterInputable()) {
            throw new RuntimeException("GUIElement: ERROR! Attempted to add (enterInput) more than once in element (" + this._name + ")!");
        }
        this._enterInput = enterInput;
        return this;
    }

    public final void setOffset(Vector2f offset) {
        this._offset.x = offset.x;
        this._offset.y = offset.y;
    }

    public final void setAlignment(Alignment alignment) {
        this._alignment = alignment;
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

    public abstract boolean collisionDetect(Vector2fc mousePosition);

    protected abstract void recalculateMesh();

    public abstract void internalOnStep(GUI gui);

    // Enforce recalculation, it's very important to keep gui elements in correct position
    protected abstract void recalculatePosition();

    // Internal point calculation, specifically for mouse. Class member. Utilizes stack.
    protected static boolean pointCollisionDetect(float pointX, float pointY, float posX, float posY, float width, float height) {
        return  pointX >= posX &&
                pointX <= posX + width &&
                pointY >= posY &&
                pointY <= posY + height;
    }
}
