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

import org.crafter.engine.world.block.BlockDefinitionContainer;
import org.crafter.engine.world.chunk.Chunk;
import org.crafter.engine.world.chunk.ChunkStorage;
import org.crafter.game.entity.entity_prototypes.Entity;
import org.joml.*;
import org.joml.Math;

import static org.crafter.engine.collision_detection.world_collision.AABBCollision.collideEntityToTerrain;
import static org.crafter.engine.delta.Delta.getDelta;
import static org.crafter.engine.utility.JOMLUtils.printVec;
import static org.crafter.engine.utility.UtilityPrinter.println;

/**
 * Terrain physics is how an entity moves & collides with the world.
 * This is NOT thread safe!
 * Probably needs a better name.
 */
public final class Physics {

    // Max speed is the literal max velocity that an entity can move at after the delta has been factored in.
    private static final float MAX_VELOCITY = 100.0f;
    // Max delta is the literal max delta that can be factored into an entity's velocity. 5 FPS or 0.2f.
    private static final float MAX_DELTA = 0.2f;
    private static final Vector3i minPosition = new Vector3i();
    private static final Vector3i maxPosition = new Vector3i();

    // Cache the Block Definition Container's pointer.
    private static BlockDefinitionContainer blockDefinitionContainer = null;
    // Order in which to collision detect (Y,X,Z)
    private static final int[] collisionOrder = new int[]{1,0,2};

    private Physics(){}

    /**
     * Run an entity's physics tick for the "server" tick. (server and client are one at the moment of typing this)
     * @param entity An entity in the world.
     */
    public static void entityPhysics(Entity entity) {

        Vector3f currentPosition = entity.getPosition();

        // If the chunk is unloaded, the entity gets frozen in place until it's loaded.
        if (!ChunkStorage.chunkIsLoaded(currentPosition)) {
            return;
        }


        float delta = getDelta();
        if (delta > MAX_DELTA) {
            delta = MAX_DELTA;
        }

        Vector3f currentVelocity = entity.getVelocity();

        // Apply gravity
        currentVelocity.y -= entity.getGravity() * delta;

//        System.out.println(currentVelocity.y);

        // Apply velocity
        if (currentVelocity.y < -MAX_VELOCITY) {
            System.out.println("Hit max vel");
            currentVelocity.y = -MAX_VELOCITY;
        }

        final Vector2fc entitySize = entity.getSize();


        // Scan the local area to find out which blocks the entity collides with.
        // Also, we're predicting where the entity will collide.

        currentPosition.add(
                currentVelocity.x() * delta,
                currentVelocity.y() * delta,
                currentVelocity.z() * delta
        );
        minPosition.set(
                (int) Math.floor(currentPosition.x() - entitySize.x()),
                (int) Math.floor(currentPosition.y()),
                (int) Math.floor(currentPosition.z() - entitySize.x())
        );
        maxPosition.set(
                (int) Math.floor(currentPosition.x() + entitySize.x()),
                (int) Math.floor(currentPosition.y() + entitySize.y()),
                (int) Math.floor(currentPosition.z() + entitySize.x())
        );
        currentPosition.sub(
                currentVelocity.x() * delta,
                currentVelocity.y() * delta,
                currentVelocity.z() * delta
        );

        ChunkStorage.setBlockManipulatorPositions(minPosition, maxPosition, true);
        ChunkStorage.blockManipulatorReadData();

        checkIfBlockDefinitionContainerCached();

        /*
        A little explanation:
        Due to the fact that I'm not very good at collision detection, this AABB collision is quite simple.
        It happens in 3 easy steps:
        AXIS move -> detect -> correct
        This happens in this order: Y, X, Z
        Why? Because I like it in this order basically.
        If you know how to make this work in one sweep, PLEASE, open a PR.
         */

        // Reset onGround state for entity.
        entity.setOnGround(false);

        for (int axis : collisionOrder) {
            runCollisionDetection(delta, entity, currentPosition, currentVelocity, axis);
        }

    }

    /**
     * Internal runner method to clean up above code & to make this easier to read, might be faster this way too?
     * @param entity The entity to collide with the world.
     * @param currentPosition The entity's current raw in world position.
     * @param currentVelocity The entity's current raw velocity.
     * @param axis (0,1,2) (X,Y,Z)
     */
    private static void runCollisionDetection(final float delta, final Entity entity, final Vector3f currentPosition, final Vector3f currentVelocity, final int axis) {

        switch (axis) {
            case 0 -> currentPosition.x += currentVelocity.x() * delta;
            case 1 -> currentPosition.y += currentVelocity.y() * delta;
            case 2 -> currentPosition.z += currentVelocity.z() * delta;
            default -> throw new RuntimeException("Physics: How did a different axis number even get inserted here? Expected: (0-2) | Got: " + axis);
        }

        for (int x = minPosition.x(); x <= maxPosition.x(); x++) {
            for (int z = minPosition.z(); z <= maxPosition.z(); z++) {
                for (int y = minPosition.y(); y <= maxPosition.y(); y++) {

                    final int gottenBlockIDSINGLE = ChunkStorage.getBlockID(x,y,z);

                    // Bulk API
                    final int gottenRawData = ChunkStorage.getBlockManipulatorData(x, y, z);
                    final int gottenBlockIDBULK = Chunk.getBlockID(gottenRawData);

                    if (gottenBlockIDBULK != gottenBlockIDSINGLE) {
                        System.out.println("ERROR IN: " + x + ", " + y + ", " + z);
                        throw new RuntimeException("Something is wrong with the BULK API! Expected: " + gottenBlockIDSINGLE + " | Received: " + gottenBlockIDBULK);
                    }

                    // Do not try to collide with not walkable blocks!
                    if (!blockDefinitionContainer.getDefinition(gottenBlockIDBULK).getWalkable()) {
                        continue;
                    }

                    // FIXME: THESE VALUES NEED TO BE CACHED!! THIS IS CREATING 2 NEW OBJECTS MULTIPLE TIMES PER ENTITY!
                    Vector3f blockPosition = new Vector3f(x, y, z);
                    Vector2f blockSize = new Vector2f(1,1);


                    collideEntityToTerrain(
                            axis,
                            entity,
                            currentVelocity,
                            currentPosition,
                            entity.getSize(),
                            blockPosition,
                            blockSize
                    );
                }
            }
        }
    }

    /**
     * This is a simple performance optimization to shorten the lookup amount by a tiny amount.
     */
    private static void checkIfBlockDefinitionContainerCached() {
        if (blockDefinitionContainer == null) {
            blockDefinitionContainer = BlockDefinitionContainer.getMainInstance();
        }
    }
}
