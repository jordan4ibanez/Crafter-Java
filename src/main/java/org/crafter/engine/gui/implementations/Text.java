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

    @Override
    public void internalOnHover(Vector2fc mousePosition) {}

    @Override
    public void internalOnClick(Vector2fc mousePosition) {}
}
