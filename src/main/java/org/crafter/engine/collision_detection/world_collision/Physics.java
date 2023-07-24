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

    private Physics(){}

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
        // Apply velocity
        currentVelocity.y -= delta * entity.getGravity();
        if (currentVelocity.y < -MAX_VELOCITY) {
            currentVelocity.y = -MAX_VELOCITY;
        }
        currentPosition.add(currentVelocity);

        System.out.println(currentVelocity.y);

        final Vector2fc entitySize = entity.getSize();

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

        ChunkStorage.setBlockManipulatorPositions(minPosition, maxPosition);
        ChunkStorage.blockManipulatorReadData();

        //FIXME: this should be stored in the class not in this function.
        // This might cause severe performance problems with a lot of entities!
        final BlockDefinitionContainer blockDefinitionContainer = BlockDefinitionContainer.getMainInstance();

        // Reset onGround state for entity.
        entity.setOnGround(false);

        // Now scan the entity's collision box for collisions
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

                    Vector3f blockPosition = new Vector3f(x,y,z).floor();
                    Vector2f blockSize = new Vector2f(1,1);

                    final boolean wasOnGround = entity.isOnGround();

                    collideEntityToTerrain(
                            entity,
                            currentVelocity,
                            oldPosition,
                            currentPosition,
                            entity.getSize(),
                            blockPosition,
                            blockSize
                    );

                    // FIXME: This is debug remove it
//                    if (!wasOnGround && entity.isOnGround()) {
//                        System.out.println(blockDefinitionContainer.getDefinition(gottenBlockID).getReadableName());
//                        System.out.println("gotten block ID: " + gottenBlockID);
//                    }
                }
            }
        }
    }
}
