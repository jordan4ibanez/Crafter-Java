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
import static org.crafter.engine.collision_detection.world_collision.AABBCollision.collideEntityToTerrainY;
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
    private static final Vector3i minPosition = new Vector3i();
    private static final Vector3i maxPosition = new Vector3i();

    // Cache the Block Definition Container's pointer.
    private static BlockDefinitionContainer blockDefinitionContainer = null;

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
        oldPosition.set(currentPosition);
        // Apply gravity
        currentVelocity.y -= delta * entity.getGravity();
        // Apply velocity
        if (currentVelocity.y < -MAX_VELOCITY) {
            currentVelocity.y = -MAX_VELOCITY;
        }

        final Vector2fc entitySize = entity.getSize();

        ChunkStorage.setBlockManipulatorPositions(minPosition, maxPosition);
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


        // Scan the local area to find out which blocks the entity collides with
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

        // Reset onGround state for entity.
        entity.setOnGround(false);

    }

    /**
     * Internal runner method to clean up above code & to make this easier to read, might be faster this way too?
     * @param entity The entity to collide with the world.
     * @param currentPosition The entity's current raw in world position.
     * @param currentVelocity The entity's current raw velocity.
     * @param axis (0,1,2) (X,Y,Z)
     */
    private static void runCollisionDetection(final Entity entity, final Vector3f currentPosition, final Vector3f currentVelocity, final byte axis) {

        switch (axis) {
            case 0 -> currentPosition.x += currentVelocity.x();
            case 1 -> currentPosition.y += currentPosition.y();
            case 2 -> currentVelocity.z += currentVelocity.z();
            default -> throw new RuntimeException("Physics: How did a different axis number even get inserted here? Expected: (0-2) | Got: " + axis);
        }
        currentPosition.add(currentVelocity);

        for (int x = minPosition.x(); x <= maxPosition.x(); x++) {
            for (int z = minPosition.z(); z <= maxPosition.z(); z++) {
                for (int y = minPosition.y(); y <= maxPosition.y(); y++) {


                    // Bulk API
                    final int gottenRawData = ChunkStorage.getBlockManipulatorData(x, y, z);
                    final int gottenBlockID = Chunk.getBlockID(gottenRawData);

                    // Do not try to collide with not walkable blocks!
                    if (!blockDefinitionContainer.getDefinition(gottenBlockID).getWalkable()) {
                        continue;
                    }

                    Vector3f blockPosition = new Vector3f(x,y,z);
                    Vector2f blockSize = new Vector2f(1,1);

                    collideEntityToTerrainY(
                            entity,
                            currentVelocity,
                            oldPosition,
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
            BlockDefinitionContainer.getMainInstance();
        }
    }
}
