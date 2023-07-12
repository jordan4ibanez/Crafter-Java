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
package org.crafter.game.entity.entity_prototypes;

import org.joml.Vector2f;
import org.joml.Vector2fc;

/**
 * An entity in this game is defined as having:
 *  1. Position
 *  2. Size/Shape (cuboid)
 *  3. Velocity
 *  Anything can inherit from this. But SHOULD it, is the main thing to think about before it is done.
 *  Size is based from it's base position (0,0,0) and scales OUT and UP.
 *
 *  Entity extends PointEntity because it is simply a point entity with size.
 */
public class Entity extends PointEntity{

    // 2D size. Width/Length are identical. So Vector2f(Width/Length, Height) (x,y)
    private final Vector2f size = new Vector2f();

    public Entity() { // number one :P
    }

    public Vector2f getSize() {
        return size;
    }
    public void setSize(float x, float y) {
        size.set(x,y);
    }
    public void setSize(Vector2fc size) {
        this.size.set(size);
    }
}
