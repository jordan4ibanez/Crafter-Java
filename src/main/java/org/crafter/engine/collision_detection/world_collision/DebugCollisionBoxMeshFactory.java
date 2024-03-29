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

import org.crafter.engine.mesh.MeshStorage;
import org.joml.Vector2fc;
import org.joml.Vector3f;

import java.util.UUID;

import static org.crafter.engine.utility.JOMLUtils.printVec;

/**
 * This is absolutely NOT thread safe.
 * Gets you a line drawing capable 3d box representing the AABB collision box.
 * This is in here because it's literal only use case is for debugging the AABB collision box!
 */
public final class DebugCollisionBoxMeshFactory {
    private static final Vector3f min = new Vector3f();
    private static final Vector3f max = new Vector3f();

    private DebugCollisionBoxMeshFactory(){}

    public static String generateCollisionBox(Vector2fc size) {
        final float[] rawVertices = generateRawVertices(size);
        final int[] rawIndices = generateRawIndices();
        final float[] rawPlaceholderTextureCoordinates = generateRawPlaceholderTextureCoordinates();

        final String uuid = UUID.randomUUID().toString();

        MeshStorage.newMesh(
                uuid,
                rawVertices,
                rawPlaceholderTextureCoordinates,
                rawIndices,
                null,
                null,
                "textures/debug.png",
                false
        );
        return uuid;
    }

    private static float[] generateRawVertices(Vector2fc size) {
        printVec("DEBUG SIZE: ", size);
        min.set(-size.x(), 0, -size.x());
        max.set( size.x(), size.y(), size.x());
        return new float[]{
            // Bottom square
            min.x, min.y, min.z, // 0
            min.x, min.y, max.z, // 1
            max.x, min.y, max.z, // 2
            max.x, min.y, min.z, // 3
            // Top square
            min.x, max.y, min.z, // 4
            min.x, max.y, max.z, // 5
            max.x, max.y, max.z, // 6
            max.x, max.y, min.z  // 7
        };
    }

    private static int[] generateRawIndices() {
        return new int[]{
            // Bottom Square
            0, 1, 1, 2, 2, 3, 3, 0,
            // Top square
            4, 5, 5, 6, 6, 7, 7, 4,
            // Sides
            0, 4, 1, 5, 2, 6, 3, 7
        };
    }

    private static float[] generateRawPlaceholderTextureCoordinates() {
        return new float[] {
                0,0,
                0,0,
                0,0,
                0,0,

                0,0,
                0,0,
                0,0,
                0,0
        };
    }
}
