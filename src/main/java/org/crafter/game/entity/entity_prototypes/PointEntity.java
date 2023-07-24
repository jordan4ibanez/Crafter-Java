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

import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * A point entity in this game is defined as having:
 * 1. Position
 * 2. Velocity
 * Anything can inherit from this. But SHOULD it, is the main thing to think about before it is done.
 * Point entities do not have size, they are a single point in the world, hence the name.
 */
public class PointEntity {

    private final Vector3f position = new Vector3f();

    private final Vector3f velocity = new Vector3f();

    private float gravity = 10.0f;
    private float friction = 3;

    private boolean onGround = false;

    public PointEntity() {
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

    public Vector3f getVelocity() {
        return velocity;
    }
    public void setVelocity(float x, float y, float z) {
        velocity.set(x,y,z);
    }
    public void setVelocity(Vector3fc velocity) {
        this.velocity.set(velocity);
    }

    public float getGravity() {
        return gravity / 100.0f;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
}
