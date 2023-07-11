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

import static org.crafter.engine.camera.Camera.getCameraMatrix;
import static org.crafter.engine.camera.Camera.getObjectMatrix;

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

    private FrustumCulling(){}

//    public static boolean insideFrustumSphere(float boundingRadius) {
//        return FrustumIntersection(
//                Matrix4d()
//                        .set(Camera.getCameraMatrix())
//                        .mul(Camera.getObjectMatrix())
//        ).testSphere(0, 0, 0, boundingRadius);
//    }

    public static boolean insideFrustumAABB(Vector3f min, Vector3f max){
        return workerIntersection.set(
                workerMatrix
                    .zero()
                    .set(getCameraMatrix())
                    .mul(getObjectMatrix())
        ).testAab(min,max);
    }

}
