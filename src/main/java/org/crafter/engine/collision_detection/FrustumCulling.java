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

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import static org.crafter.engine.camera.Camera.*;
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
     * The render frustum culling (optimization) for CHUNKS STACKS ONLY!
     * Remember: Camera.setObjectMatrix() MUST be called BEFORE running this!
     * @param x Real X position in 3D space.
     * @param y Real Y position in 3D space.
     * @param z Real Z position in 3D space.
     * @return True or false. If the object is within view, this is true.
     */
    public static boolean insideFrustumChunkStack(final float x, final float y, final float z){

        /*
        Note: utilize the new system for entities to quickly create worker implementations for this.
        with position and size instead of this horrific mess!
         */
        updateInternalChunkRenderMatrix(x,y,z);

        return workerIntersection.set(
                workerMatrix
                    .zero()
                    .set(getCameraMatrix())
                    .mul(chunkMeshWorkerMatrix)
        ).testAab(
                minWorker.set(0,0,0),
                maxWorker.set(getWidth(),getStackHeight(),getDepth())
        );
    }


    // This is a workaround due to all chunk stack meshes being base position 0
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
