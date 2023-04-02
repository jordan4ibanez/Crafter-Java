package org.crafter.engine.gui.enumerators;

import org.joml.Vector2f;

public enum Alignment {
    TOP_LEFT(new Vector2f(0.0f,0.0f)),
    TOP_CENTER(new Vector2f(0.5f,0.0f)),
    TOP_RIGHT(new Vector2f(1.0f,0.0f)),

    CENTER_LEFT(new Vector2f(0.0f,0.5f)),
    CENTER(new Vector2f(0.5f,0.5f)),
    CENTER_RIGHT(new Vector2f(1.0f,0.5f)),

    BOTTOM_LEFT(new Vector2f(0.0f,1.0f)),
    BOTTOM_CENTER(new Vector2f(0.5f,1.0f)),
    BOTTOM_RIGHT(new Vector2f(1.0f,1.0f)),
    DEFAULT(TOP_LEFT.value());

    final Vector2f value;

    Alignment(Vector2f value) {
        this.value = value;
    }

    public Vector2f value() {
        return new Vector2f(this.value);
    }

    public float getX() {
        return this.value.x;
    }
    public float getY() {
        return this.value.y;
    }
}
