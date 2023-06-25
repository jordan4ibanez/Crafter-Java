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
package org.crafter.engine.gui.components;

import org.crafter.engine.gui.GUI;
import org.crafter.engine.gui.enumerators.Alignment;
import org.joml.Vector2f;
import org.joml.Vector2fc;

/**
 * A GUI Mesh is a mesh which exists in 3d, yet in 2d at the same time.
 * Optionally, exists without perspective.
 */
public class GUIMesh extends GUIElement {

    public GUIMesh(String uuid, Alignment alignment, Vector2f offset) {
        super(alignment, offset);
        this._meshUUID = uuid;
    }

    @Override
    public void render() {}

    @Override
    public boolean collisionDetect(Vector2fc mousePosition) {
        return false;
    }

    @Override
    protected void recalculateMesh() {}

    @Override
    public void internalOnStep(GUI gui) {}

    @Override
    protected void recalculatePosition() {}

    @Override
    public void internalOnHover(Vector2fc mousePosition) {}

    @Override
    public void internalOnClick(Vector2fc mousePosition) {}
}
