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

import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * Raw AABB calculation methods.
 * This is NOT thread safe, as this is handled on the main thread alone!
 */
final class AABBCollision {

    // This is going to be a bit complex, unfortunately. All this for the sake of optimization.
    private static final Vector3f min1 = new Vector3f();
    private static final Vector3f max1 = new Vector3f();
    private static final Vector3f min1Old = new Vector3f();
    private static final Vector3f max1Old = new Vector3f();
    private static final Vector3f min2 = new Vector3f();
    private static final Vector3f max2 = new Vector3f();

    private AABBCollision(){}

    /**
     * This needs to be run AFTER the entity moves! You must keep track of the old position and insert it into this!
     * This code is ONLY to be used to collide entities into the terrain!
     * @param oldPosition1 Entity 1's old position.
     * @param position1 Entity 1's current position. (Mutable)
     * @param size1 Entity 1's size.
     * @param position2 Entity 2's current position.
     * @param size2 Entity 2's size.
     * @return True or false. True if the entity intersects.
     */
    public static boolean collideEntityToTerrain(Vector3fc oldPosition1, Vector3f position1, Vector2fc size1, Vector3fc position2, Vector2fc size2) {

        // I said this was gonna be complicated, didn't I?
        min1Old.set(oldPosition1.x() - size1.x(), oldPosition1.y(), oldPosition1.z() - size1.x());
        max1Old.set(oldPosition1.x() + size1.x(), oldPosition1.y() + size1.y(), oldPosition1.z() + size1.x());
        min1.set(position1.x() - size1.x(), position1.y(), position1.z() - size1.x());
        max1.set(position1.x() + size1.x(), position1.y() + size1.y(), position1.z() + size1.x());

        min2.set(position2.x() - size2.x(), position2.y(), position2.z() - size2.x());
        max2.set(position2.x() + size2.x(), position2.y() + size2.y(), position2.z() + size2.x());
        

        // These are 1D collision detections
        boolean bottomWasNotIn = min1Old.y() > max2.y();
        boolean bottomIsNowIn = min1.y() <= max2.y() && min1.y() >= min2.y();
        boolean topWasNotIn = max1Old.y() < min2.y();
        boolean topIsNowIn = max1.y() <= max2.y() && max1.y() >= min2.y();

        boolean leftWasNotIn = min1Old.x() > max2.x();
        boolean leftIsNowIn = min1.x() <= max2.x() && min1.x() >= min2.x();
        boolean rightWasNotIn = max1Old.x() < min2.x();
        boolean rightIsNowIn = max1.x() <= max2.x() && max1.x() >= min2.x();

        boolean backWasNotIn = min1Old.z() > max2.z();
        boolean backIsNowIn = min1.z() <= max2.z() && min1.z() >= min2.z();
        boolean frontWasNotIn = max1Old.z() < min2.z();
        boolean frontIsNowIn = max1.z() <= max2.z() && max1.z() >= min2.z();


        /// y check first
        if (bottomWasNotIn && bottomIsNowIn) {
            thisEntity.position.y = max2.y() + thisEntity.size.y + 0.001;
            thisEntity.wasOnGround = true;

            thisEntity.velocity.y = 0;
        } else if (topWasNotIn && topIsNowIn) {
            thisEntity.position.y = min2.y - thisEntity.size.y - 0.001;
            thisEntity.velocity.y = 0;
        }
        // then x
        else if (leftWasNotIn && leftIsNowIn) {
            thisEntity.position.x = max2.x() + thisEntity.size.x + 0.001;
            thisEntity.velocity.x = 0;
        } else if (rightWasNotIn && rightIsNowIn) {
            thisEntity.position.x = min2.x - thisEntity.size.x - 0.001;
            thisEntity.velocity.x = 0;
        }

        // finally z
        else if (backWasNotIn && backIsNowIn) {
            thisEntity.position.z = max2.z + thisEntity.size.z + 0.001;
            thisEntity.velocity.z = 0;
        } else if (frontWasNotIn && frontIsNowIn) {
            thisEntity.position.z = min2.z - thisEntity.size.z - 0.001;
            thisEntity.velocity.z = 0;
        }
    }

}
