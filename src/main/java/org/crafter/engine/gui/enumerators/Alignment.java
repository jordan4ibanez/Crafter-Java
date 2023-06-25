/*
 Crafter - A blocky game (engine) written in Java with LWJGL.
 Copyright (C) 2023  jordan4ibanez

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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

    public static Alignment[] asArray() {
        return new Alignment[]{TOP_LEFT, TOP_CENTER, TOP_RIGHT, CENTER_LEFT, CENTER, CENTER_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT};
    }
}
