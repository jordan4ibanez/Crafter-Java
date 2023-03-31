package org.crafter.engine.gui.components;

import org.crafter.engine.gui.GUI;
import org.crafter.engine.gui.actions.Click;
import org.crafter.engine.gui.actions.Hover;
import org.crafter.engine.gui.actions.KeyInput;
import org.crafter.engine.gui.actions.Render;
import org.crafter.engine.gui.alignment.Alignment;
import org.joml.Vector2f;

public abstract class GUIElement {
    protected boolean _click;
    protected boolean _hover;
    protected boolean _keyInput;

    protected Alignment _alignment;

    protected GUIElement(Alignment alignment) {
        this._alignment = alignment;
    }

    Vector2f alignment() {
        return new Vector2f(_alignment.value());
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
