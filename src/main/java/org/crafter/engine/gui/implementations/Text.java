package org.crafter.engine.gui.implementations;

import org.crafter.engine.gui.components.GUIElement;
import org.crafter.engine.gui.enumerators.Alignment;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;

public abstract class Text extends GUIElement {

    protected String textData = "";

    protected float fontSize;

    protected final Vector3f foreGroundColor = new Vector3f(1,1,1);

    protected final Vector3f shadowColor = new Vector3f(0,0,0);

    protected Text(String textData, float fontSize, Alignment alignment, Vector2f offset) {
        super(alignment, offset);
        if (textData == null) {
            throw new RuntimeException("Text: textData cannot be null!");
        }
        this.textData = textData;
        this.fontSize = fontSize;
    }

    public final void setForeGroundColor(float r, float g, float b) {
        this.foreGroundColor.set(r,g,b);
        recalculateMesh();
    }

    public final void setShadowColor(float r, float b, float g) {
        this.shadowColor.set(r,g,b);
        recalculateMesh();
    }

    public final void setFontSize(float fontSize) {
        this.fontSize = fontSize;
        // System.out.println("Fontsize for " + this.name() + " is " + this.fontSize);
        recalculateMesh();
    }

    public final void setText(String textData) {
        this.textData = textData;
        recalculateMesh();
    }

    public abstract boolean collisionDetect(Vector2fc mousePosition);

    protected abstract void recalculateMesh();
}
