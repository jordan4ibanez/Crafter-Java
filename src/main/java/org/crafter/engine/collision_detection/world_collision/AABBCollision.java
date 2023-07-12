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
    private static final Vector3f min2Old = new Vector3f();
    private static final Vector3f max2Old = new Vector3f();

    private AABBCollision(){}

    /**
     * This needs to be run AFTER the entity moves! You must keep track of the old position and insert it into this!
     * @param oldPosition1 Entity 1's old position.
     * @param position1 Entity 1's current position.
     * @param size1 Entity 1's size.
     * @param oldPosition2 Entity 2's old position.
     * @param position2 Entity 2's current position.
     * @param size2 Entity 2's size.
     * @return True or false. True if the entity intersects.
     */
    public static boolean collide(Vector3fc oldPosition1, Vector3fc position1, Vector2fc size1, Vector3fc oldPosition2, Vector3fc position2, Vector2fc size2) {

        // I said this was gonna be complicated, didn't I?
        min1Old.set(oldPosition1.x() - size1.x(), oldPosition1.y(), oldPosition1.z() - size1.x());
        max1Old.set(oldPosition1.x() + size1.x(), oldPosition1.y() + size1.y(), oldPosition1.z() + size1.x());
        min1.set(position1.x() - size1.x(), position1.y(), position1.z() - size1.x());
        max1.set(position1.x() + size1.x(), position1.y() + size1.y(), position1.z() + size1.x());

        min2Old.set(oldPosition2.x() - size2.x(), oldPosition2.y(), oldPosition2.z() - size2.x());
        max2Old.set(oldPosition2.x() + size2.x(), oldPosition2.y() + size2.y(), oldPosition2.z() + size2.x());
        min2.set(position2.x() - size2.x(), position2.y(), position2.z() - size2.x());
        max2.set(position2.x() + size2.x(), position2.y() + size2.y(), position2.z() + size2.x());

        foreach(otherEntity; thisQuadrant.entitiesWithin.filter !(o = > o != thisEntity)){

            BoundingBox otherBox = otherEntity.getBoundingBox();

            if (CheckCollisionBoxes(thisBox, otherBox)) {

                // These are 1D collision detections
                boolean bottomWasNotIn = oldBox.min.y > otherBox.max.y;
                boolean bottomIsNowIn = thisBox.min.y <= otherBox.max.y && thisBox.min.y >= otherBox.min.y;
                boolean topWasNotIn = oldBox.max.y < otherBox.min.y;
                boolean topIsNowIn = thisBox.max.y <= otherBox.max.y && thisBox.max.y >= otherBox.min.y;

                boolean leftWasNotIn = oldBox.min.x > otherBox.max.x;
                boolean leftIsNowIn = thisBox.min.x <= otherBox.max.x && thisBox.min.x >= otherBox.min.x;
                boolean rightWasNotIn = oldBox.max.x < otherBox.min.x;
                boolean rightIsNowIn = thisBox.max.x <= otherBox.max.x && thisBox.max.x >= otherBox.min.x;

                boolean backWasNotIn = oldBox.min.z > otherBox.max.z;
                boolean backIsNowIn = thisBox.min.z <= otherBox.max.z && thisBox.min.z >= otherBox.min.z;
                boolean frontWasNotIn = oldBox.max.z < otherBox.min.z;
                boolean frontIsNowIn = thisBox.max.z <= otherBox.max.z && thisBox.max.z >= otherBox.min.z;


                /// y check first
                // This allows entities to clip, but this isn't a voxel game so we won't worry about that
                if (bottomWasNotIn && bottomIsNowIn) {
                    thisEntity.position.y = otherBox.max.y + thisEntity.size.y + 0.001;
                    thisEntity.wasOnGround = true;

                    thisEntity.velocity.y = 0;
                } else if (topWasNotIn && topIsNowIn) {
                    thisEntity.position.y = otherBox.min.y - thisEntity.size.y - 0.001;
                    thisEntity.velocity.y = 0;
                }
                // then x
                else if (leftWasNotIn && leftIsNowIn) {
                    thisEntity.position.x = otherBox.max.x + thisEntity.size.x + 0.001;
                    thisEntity.velocity.x = 0;
                } else if (rightWasNotIn && rightIsNowIn) {
                    thisEntity.position.x = otherBox.min.x - thisEntity.size.x - 0.001;
                    thisEntity.velocity.x = 0;
                }

                // finally z
                else if (backWasNotIn && backIsNowIn) {
                    thisEntity.position.z = otherBox.max.z + thisEntity.size.z + 0.001;
                    thisEntity.velocity.z = 0;
                } else if (frontWasNotIn && frontIsNowIn) {
                    thisEntity.position.z = otherBox.min.z - thisEntity.size.z - 0.001;
                    thisEntity.velocity.z = 0;
                }
            }
        }
    }

}
