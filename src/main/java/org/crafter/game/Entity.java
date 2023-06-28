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
package org.crafter.game;

import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * An entity in this game is defined as having:
 *  1. Position
 *  2. Size/Shape (cuboid)
 *  3. Velocity
 *  Anything can inherit from this. But SHOULD it, is the main thing to think about before it is done.
 *  Size is based from it's base position (0,0,0) and scales OUT and UP.
 *
 *
 * Todo: Maybe particles can be ghosts or points or something. This might be too much infrastructure for them.
 *
 * README:
 * TODO: Is this an engine component or a game component? Give this some thought.
 */
public class Entity {
    private final Vector3f position = new Vector3f();

    // 2D size. Width/Length are identical. So Vector2f(Width/Length, Height) (x,y)
    private final Vector2f size = new Vector2f();
    private final Vector3f velocity = new Vector3f();

    // FIXME: Entities might need to super into this. If this happens, remove this blank constructor.
    public Entity() { // number one :P
    }

    public Vector3f getPosition() {
        return position;
    }
    public void setPosition(float x, float y, float z) {
        position.set(x,y,z);
    }
    public void setPosition(Vector3fc position) {
        this.position.set(position);
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

    public Vector3f getVelocity() {
        return velocity;
    }
    public void setVelocity(float x, float y, float z) {
        velocity.set(x,y,z);
    }
    public void setVelocity(Vector3fc velocity) {
        this.velocity.set(velocity);
    }
}
