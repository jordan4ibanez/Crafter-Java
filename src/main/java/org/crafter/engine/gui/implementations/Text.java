package org.crafter.engine.gui.implementations;

import org.crafter.engine.gui.components.GUIElement;
import org.crafter.engine.gui.enumerators.Alignment;
import org.joml.Vector3f;

public abstract class Text extends GUIElement {

    protected String textData = "";

    protected float fontSize = 24.0f;

    protected final Vector3f foreGroundColor = new Vector3f(1,1,1);

    protected final Vector3f shadowColor = new Vector3f(0,0,0);

    protected Text(String name, String textData, float fontSize, Alignment alignment) {
        super(name, alignment);
        this.textData = textData;
        this.fontSize = fontSize;
    }

    public abstract boolean collisionDetect();

    protected abstract void recalculateMesh();

    public abstract void setFontSize(float fontSize);

    public abstract void setText(String textData);
}
