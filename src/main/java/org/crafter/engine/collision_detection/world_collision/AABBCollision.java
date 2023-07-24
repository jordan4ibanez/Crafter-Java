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
import org.joml.Math;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * Raw AABB calculation methods.
 * This is NOT thread safe, as this is handled on the main thread alone!
 */
final class AABBCollision {

    // This is going to be a bit complex, unfortunately. All this for the sake of optimization.
    private static final Vector3f minEntity = new Vector3f();
    private static final Vector3f maxEntity = new Vector3f();
    private static final Vector3f minEntityOld = new Vector3f();
    private static final Vector3f maxEntityOld = new Vector3f();
    private static final Vector3f minBlock = new Vector3f();
    private static final Vector3f maxBlock = new Vector3f();

    private AABBCollision(){}

    /**
     * This needs to be run AFTER the entity moves! You must keep track of the old position and insert it into this!
     * This code is ONLY to be used to collide entities into the terrain!
     * @param oldPosition Entity 1's old position.
     * @param position1 Entity 1's current position. (Mutable)
     * @param size1 Entity 1's size.
     * @param position2 Entity 2's current position.
     * @param size2 Entity 2's size.
     * @return True or false. FIXME: (TEMP) True if the entity is on the ground (for now).
     */
    public static void collideEntityToTerrain(final Entity entity, final Vector3f currentVelocity, final Vector3fc oldPosition, final Vector3f position1, final Vector2fc size1, final Vector3fc position2, final Vector2fc size2) {

        /*
         * FIXME This needs to be fixed to take in a pure block size defined by min max
         *
         * FOR NOW: this is "good enough"
         */

        // I said this was gonna be complicated, didn't I?

        // Entity
        minEntityOld.set(oldPosition.x() - size1.x(),    oldPosition.y(),             oldPosition.z() - size1.x());
        maxEntityOld.set(oldPosition.x() + size1.x(), oldPosition.y() + size1.y(), oldPosition.z() + size1.x());

        minEntity.set(position1.x() - size1.x(),    position1.y(),             position1.z() - size1.x());
        maxEntity.set(position1.x() + size1.x(), position1.y() + size1.y(), position1.z() + size1.x());

        // Block
        minBlock.set(position2.x(),    position2.y(),             position2.z());
        maxBlock.set(position2.x() + size2.x(), position2.y() + size2.y(), position2.z() + size2.x());
        

        // These are 1D collision detections
        final boolean bottomWasNotIn = minEntityOld.y() > maxBlock.y();
        final boolean bottomIsNowIn = minEntity.y() <= maxBlock.y();
        final boolean topWasNotIn = maxEntityOld.y() < minBlock.y();
        final boolean topIsNowIn = maxEntity.y() >= minBlock.y();

        final boolean leftWasNotIn = minEntityOld.x() > maxBlock.x();
        final boolean leftIsNowIn = minEntity.x() <= maxBlock.x();
        final boolean rightWasNotIn = maxEntityOld.x() < minBlock.x();
        final boolean rightIsNowIn = maxEntity.x() >= minBlock.x();

        final boolean backWasNotIn = minEntityOld.z() > maxBlock.z();
        final boolean backIsNowIn = minEntity.z() <= maxBlock.z();
        final boolean frontWasNotIn = maxEntityOld.z() < minBlock.z();
        final boolean frontIsNowIn = maxEntity.z() >= minBlock.z();


        boolean onGround = false;

        /// y check first
        if (bottomWasNotIn && bottomIsNowIn) {
            position1.y = maxBlock.y() + 0.001f;
            onGround = true;
            //todo Note: Current falling velocity needs to be slightly down so that jumping will never fail when on ground!
            // If this is set to 0, the client player will miss jump keystrokes because they float for a frame (or multiple).
            currentVelocity.y = -0.001f;
        } else if (topWasNotIn && topIsNowIn) {
            position1.y = minBlock.y - size1.y() - 0.001f;
            currentVelocity.y = 0;
        }



        // then x
        if (leftWasNotIn && leftIsNowIn) {
            position1.x = maxBlock.x() + size1.x() + 0.001f;
            currentVelocity.x = 0;
            System.out.println("COLLIDE X LEFT");
        } else if (rightWasNotIn && rightIsNowIn) {
            System.out.println("COLLIDE X RIGHT");
            position1.x = minBlock.x() - size1.x() - 0.001f;
            currentVelocity.x = 0;
        }



//
//        // finally z
//        else if (backWasNotIn && backIsNowIn) {
//            position1.z = max2.z + size1.x() + 0.001f;
////            thisEntity.velocity.z = 0;
//        } else if (frontWasNotIn && frontIsNowIn) {
//            position1.z = min2.z - size1.x() - 0.001f;
////            thisEntity.velocity.z = 0;
//        }

        // 1 way gate for onGround trigger
        if (onGround) {
            entity.setOnGround(true);
        }
    }

}
