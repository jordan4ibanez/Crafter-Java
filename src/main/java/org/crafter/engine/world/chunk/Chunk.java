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
package org.crafter.engine.world.chunk;

import org.crafter.engine.camera.Camera;
import org.crafter.engine.delta.Delta;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.shader.ShaderStorage;
import org.joml.*;
import org.joml.Math;

import java.io.Serializable;

import static org.crafter.engine.collision_detection.FrustumCulling.insideFrustumChunkStack;
import static org.crafter.engine.utility.UtilityPrinter.println;

/**
 * The final instance of chunk in the inheritance chain.
 * Java auto calls super() down the chain.
 */
public class Chunk extends ChunkMeshHandling {

    //Todo: idea: metadata hashmap

    private final Vector2ic position;

    private static final Vector3f positionWorker = new Vector3f(0,0,0);
    private static final Vector3fc rotation = new Vector3f(0,0,0);
    private static final Vector3fc scale = new Vector3f(1,1,1);

    public Chunk(int x, int y) {
        this(new Vector2i(x,y));
    }
    public Chunk(Vector2ic position) {
        this.position = position;
    }

    public Vector2ic getPosition() {
        return position;
    }

    public String getPositionString() {
        return "x = " + position.x() + ", y = " + position.y();
    }

    public int getX() {
        return position.x();
    }

    public int getZ() {
        return position.y();
    }

    /**
     * Render resides in the final implementation of the Chunk inheritance chain because:
     * It requires the position of the chunk!
     */
    public void render() {

        final float positionX = position.x() * getWidth();
        final float positionZ = position.y() * getDepth();

        Camera.setObjectMatrix(positionWorker.set(positionX,0, positionZ), rotation, scale);

        for (int i = 0; i < getStacks(); i++) {

            final float positionY = i * getStackHeight();

            String gottenMeshUUID = getMesh(i);

            if (gottenMeshUUID != null && insideFrustumChunkStack(positionX, positionY, positionZ)) {
                
                    MeshStorage.render(gottenMeshUUID);
                }
            }
        }
    }

    /**
     * Create a deep copy of a chunk.
     * @return Deep copy of chunk.
     */
    public Chunk deepCopy() {
        Chunk copy = new Chunk(new Vector2i(this.position));
        copy.setData(getDataDIRECT());
        return copy;
    }


}
