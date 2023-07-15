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

import org.crafter.game.entity.entity_prototypes.Entity;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;

import static org.crafter.engine.collision_detection.world_collision.AABBCollision.collideEntityToTerrain;
import static org.crafter.engine.delta.Delta.getDelta;
import static org.crafter.engine.utility.UtilityPrinter.println;

/**
 * Terrain physics is how an entity moves & collides with the world.
 * This is NOT thread safe!
 * Probably needs a better name.
 */
public final class Physics {

    // Max speed is the literal max velocity that an entity can move at after the delta has been factored in.
    private static final float MAX_VELOCITY = 0.05f;
    // Max delta is the literal max delta that can be factored into an entity. 5 FPS or 0.2f.
    private static final float MAX_DELTA = 0.2f;

    private static final Vector3f oldPosition = new Vector3f();

    private Physics(){}

    public static void entityPhysics(Entity entity) {

        float delta = getDelta();
        if (delta > MAX_DELTA) {
            delta = MAX_DELTA;
        }

        Vector3f currentVelocity = entity.getVelocity();
        Vector3f currentPosition = entity.getPosition();

        oldPosition.set(currentPosition);

        currentVelocity.y -= delta * entity.getGravity();

        if (currentVelocity.y < -MAX_VELOCITY) {
            currentVelocity.y = -MAX_VELOCITY;
        }

//        println("delta: " + delta);
//        println("current velocity Y: " + currentVelocity.y);

        currentPosition.add(currentVelocity);


        //FIXME Placeholder test
        Vector3f blockPosition = new Vector3f(0,0,0);
        Vector2f blockSize = new Vector2f(1,1);


        collideEntityToTerrain(
                oldPosition,
                currentPosition,
                entity.getSize(),
                blockPosition,
                blockSize
        );



    }
}
