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

/**
 * Raw AABB calculation methods.
 * This is NOT thread safe, as this is handled on the main thread alone!
 */
final class AABBCollision {

    private AABBCollision(){}

    BoundingBox thisBox = thisEntity.getBoundingBox();

    foreach (otherEntity; thisQuadrant.entitiesWithin.filter!(o => o != thisEntity)) {

        BoundingBox otherBox = otherEntity.getBoundingBox();

        if(CheckCollisionBoxes(thisBox, otherBox)) {

            // These are 1D collision detections
            bool bottomWasNotIn = oldBox.min.y > otherBox.max.y;
            bool bottomIsNowIn = thisBox.min.y <= otherBox.max.y && thisBox.min.y >= otherBox.min.y;
            bool topWasNotIn = oldBox.max.y < otherBox.min.y;
            bool topIsNowIn = thisBox.max.y <= otherBox.max.y && thisBox.max.y >= otherBox.min.y;

            bool leftWasNotIn = oldBox.min.x > otherBox.max.x;
            bool leftIsNowIn = thisBox.min.x <= otherBox.max.x && thisBox.min.x >= otherBox.min.x;
            bool rightWasNotIn = oldBox.max.x < otherBox.min.x;
            bool rightIsNowIn = thisBox.max.x <= otherBox.max.x && thisBox.max.x >= otherBox.min.x;

            bool backWasNotIn = oldBox.min.z > otherBox.max.z;
            bool backIsNowIn = thisBox.min.z <= otherBox.max.z && thisBox.min.z >= otherBox.min.z;
            bool frontWasNotIn = oldBox.max.z < otherBox.min.z;
            bool frontIsNowIn = thisBox.max.z <= otherBox.max.z && thisBox.max.z >= otherBox.min.z;



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
                thisEntity.position.x = otherBox.max.x +thisEntity.size.x + 0.001;
                thisEntity.velocity.x = 0;
            } else if (rightWasNotIn && rightIsNowIn) {
                thisEntity.position.x = otherBox.min.x - thisEntity.size.x - 0.001;
                thisEntity.velocity.x = 0;
            }

            // finally z
            else if (backWasNotIn && backIsNowIn) {
                thisEntity.position.z = otherBox.max.z +thisEntity.size.z + 0.001;
                thisEntity.velocity.z = 0;
            } else if (frontWasNotIn && frontIsNowIn) {
                thisEntity.position.z = otherBox.min.z - thisEntity.size.z - 0.001;
                thisEntity.velocity.z = 0;
            }
        }
    }

}
