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
package org.crafter.engine.collision_detection.world_collision;

import static org.crafter.engine.delta.Delta.getDelta;

/**
 * Terrain physics is how an entity moves & collides with the world.
 * Probably needs a better name.
 */
public final class Physics {

    // Max speed is the literal max speed that an entity can move at after the delta has been factored in.
    private final static float MAX_SPEED = 0.85f;
    // Max delta is the literal max delta that can be factored into an entity. 5 FPS or 0.2f.
    private final static float MAX_DELTA = 0.2f;

    private Physics(){}

    public static void entityTerrainPhysics() {

        float delta = getDelta();
        if (delta > MAX_DELTA) {
            delta = MAX_DELTA;
        }

    }
}
