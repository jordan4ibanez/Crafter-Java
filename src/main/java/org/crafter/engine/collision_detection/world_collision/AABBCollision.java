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
    // I'll try to document as best as I can.
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
     * @param position Entity 1's current position. (Mutable)
     * @param size Entity 1's size.
     * @param blockPosition Block's position.
     * @param blockSize Block's size.
     */
    public static void collideEntityToTerrain(
            final int axis,
            final Entity entity,
            final Vector3f currentVelocity,
            final Vector3fc oldPosition,
            final Vector3f position,
            final Vector2fc size,
            final Vector3fc blockPosition,
            final Vector2fc blockSize) {

        /*
         * FIXME This needs to be fixed to take in a pure block size defined by min max
         *
         * FOR NOW: this is "good enough"
         */

        // Entity
        minEntityOld.set(oldPosition.x() - size.x(),    oldPosition.y(),            oldPosition.z() - size.x());
        maxEntityOld.set(oldPosition.x() + size.x(), oldPosition.y() + size.y(), oldPosition.z() + size.x());

        minEntity.set(position.x() - size.x(),    position.y(),            position.z() - size.x());
        maxEntity.set(position.x() + size.x(), position.y() + size.y(), position.z() + size.x());

        // Block
        minBlock.set( blockPosition.x(),                      blockPosition.y(),                   blockPosition.z());
        maxBlock.set(blockPosition.x() + blockSize.x(), blockPosition.y() + blockSize.y(), blockPosition.z() + blockSize.x());

        switch (axis) {
            case 0 -> collideX(
                    entity,
                    currentVelocity,
                    position,
                    size
            );
            case 1 -> collideY(
                entity,
                currentVelocity,
                position,
                size
            );
            case 2 -> collideZ(
                    entity,
                    currentVelocity,
                    position,
                    size
            );
            default -> throw new RuntimeException("AABBCollision: How did a different axis number even get inserted here? Expected: (0-2) | Got: " + axis);
        }
    }

    private static void collideY(
            final Entity entity,
            final Vector3f currentVelocity,
            final Vector3f position,
            final Vector2fc size
    ) {

        // These are 1D collision detections
        final boolean bottomInside = collide(minEntity.y(), minBlock.y(), maxBlock.y());
        final boolean topInside = collide(maxEntity.y(), minBlock.y(), maxBlock.y());

        boolean onGround = false;

        if (bottomInside) {
            position.y = maxBlock.y() + 0.001f;
            onGround = true;
            //todo Note: Current falling velocity needs to be slightly down so that jumping will never fail when on ground!
            // If this is set to 0, the client player will miss jump keystrokes because they float for a frame (or multiple).
            currentVelocity.y = -0.001f;
        } else if (topInside) {
            position.y = minBlock.y - size.y() - 0.001f;
            currentVelocity.y = 0;
        }

        // 1 way gate for onGround trigger
        if (onGround) {
            entity.setOnGround(true);
        }
    }

    private static void collideX(
            final Entity entity,
            final Vector3f currentVelocity,
            final Vector3f position,
            final Vector2fc size
    ) {

        // These are 1D collision detections
        final boolean leftInside = collide(minEntity.x(), minBlock.x(), maxBlock.x());
        final boolean rightInside = collide(maxEntity.x(), minBlock.x(), maxBlock.x());

        if (leftInside) {
            position.x = maxBlock.x() + size.x() + 0.001f;
            currentVelocity.x = 0;
            System.out.println("COLLIDE X LEFT");
        } else if (rightInside) {
            position.x = minBlock.x() - size.x() - 0.001f;
            currentVelocity.x = 0;
            System.out.println("COLLIDE X RIGHT");
        }
    }

    private static void collideZ(
            final Entity entity,
            final Vector3f currentVelocity,
            final Vector3f position,
            final Vector2fc size
    ) {

        // These are 1D collision detections
        final boolean frontInside = collide(minEntity.z(), minBlock.z(), maxBlock.z());
        final boolean backInside = collide(maxEntity.z(), minBlock.z(), maxBlock.z());

        if (backInside) {
            position.z = maxBlock.z() + size.x() + 0.001f;
            currentVelocity.z = 0;
        } else if (frontInside) {
            position.z = maxBlock.z() - size.x() - 0.001f;
            currentVelocity.z = 0;
        }
    }


    /**
     * One dimensional collision check.
     * @param position 1D position of entity on an axis.
     * @param minBlock 1D min position of block on an axis.
     * @param maxBlock 1D max position of a block on an axis.
     * @return If it intersects.
     */
    private static boolean collide(final float position, final float minBlock, final float maxBlock) {
        return position <= maxBlock && position >= minBlock;
    }

}
