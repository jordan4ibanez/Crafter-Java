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
package org.crafter.engine.collision_detection;

import org.joml.*;

import static org.crafter.engine.camera.Camera.*;
import static org.crafter.engine.utility.UtilityPrinter.println;
import static org.crafter.engine.world.chunk.ChunkArrayManipulation.getDepth;
import static org.crafter.engine.world.chunk.ChunkArrayManipulation.getWidth;
import static org.crafter.engine.world.chunk.ChunkMeshHandling.getStackHeight;

/**
 * AABB camera Matrix4f collision detection library.
 * Will detect if a collision box is within the viewing range.
 * Using this will MASSIVELY improve FPS!
 * Warning: This is NOT thread safe! This is only for using in OpenGL single threaded rendering!
 * If you are using this for Vulkan, localize the cached objects!
 */
public final class FrustumCulling {

    private static final Matrix4f workerMatrix = new Matrix4f();
    private static final FrustumIntersection workerIntersection = new FrustumIntersection();
    private static final Matrix4f chunkMeshWorkerMatrix = new Matrix4f();
    private static final Vector3f minWorker = new Vector3f();
    private static final Vector3f maxWorker = new Vector3f();

    private FrustumCulling(){}

    /**
     * Check if a point entity is within view. This is pretty dang simple since it's 1 position in 3D space.
     * @param position Where the Point Entity is in 3D space.
     * @return True or false. If the object is within view, this is true.
     */
    public static boolean pointEntityWithinFrustum(Vector3fc position) {

        // Fixme: This needs testing!
        println("pointEntityWithinFrustum is untested!");

        return workerIntersection.set(
                workerMatrix
                        .set(getCameraMatrix())
                        .mul(getObjectMatrix())
        ).testPoint(position);
    }

    /**
     * Check if an Entity is within view. This is based on the entity's collision box!
     * @param position The entity's position.
     * @param size The entity's size.
     * @return True or false. If the object is within view, this is true.
     */
    public static boolean entityWithinFrustum(Vector3fc position, Vector2fc size) {

        // Fixme: This needs testing!
        println("entityWithinFrustum is untested!");

        final float halfWidth = size.x() / 2.0f;

        return workerIntersection.set(
                workerMatrix
                        .set(getCameraMatrix())
                        .mul(getObjectMatrix())
        ).testAab(
                minWorker.set(position.x() - halfWidth, position.y(), position.z() - halfWidth),
                maxWorker.set(position.x() + halfWidth, position.y() + size.y(), position.z() + halfWidth)
        );
    }

    /**
     * The render frustum culling (optimization) for CHUNKS STACKS ONLY!
     * Remember: Camera.setObjectMatrix() MUST be called BEFORE running this!
     * @param x Real X position in 3D space.
     * @param y Real Y position in 3D space.
     * @param z Real Z position in 3D space.
     * @return True or false. If the object is within view, this is true.
     */
    public static boolean chunkStackWithinFrustum(final float x, final float y, final float z){

        updateInternalChunkRenderMatrix(x,y,z);

        return workerIntersection.set(
                workerMatrix
                    .set(getCameraMatrix())
                    .mul(chunkMeshWorkerMatrix)
        ).testAab(
                minWorker.set(0,0,0),
                maxWorker.set(getWidth(),getStackHeight(),getDepth())
        );
    }


    // This is a workaround due to all chunk stack meshes being base position 0.
    // This is a manual implementation of the calculation that happens in the GPU.
    private static void updateInternalChunkRenderMatrix(final float x, final float y, final float z) {

        Vector3fc cameraPosition = getPosition();

        chunkMeshWorkerMatrix
            .identity()
            .translate(
                    x - cameraPosition.x(),
                    y - cameraPosition.y(),
                    z - cameraPosition.z()
            );
    }

}
