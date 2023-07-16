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
package org.crafter.game.entity.player;

import org.crafter.engine.camera.Camera;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.game.entity.entity_prototypes.Entity;
import org.joml.Vector3f;

import static org.crafter.engine.collision_detection.world_collision.DebugCollisionBoxMeshFactory.generateCollisionBox;
import static org.crafter.engine.utility.JOMLUtils.printVec;
import static org.crafter.engine.utility.UtilityPrinter.println;

public class Player extends Entity {
    private final String name;
    // Basically, the client player is the player that the client's camera gets glued to
    private final boolean clientPlayer;

    private final String collisionBoxMesh;
    private float eyeHeight = 1.5f;

    public Player(String name, boolean clientPlayer) {
        this.name = name;
        this.clientPlayer = clientPlayer;
        setSize(0.3f, 1.8f);
        collisionBoxMesh = generateCollisionBox(this.getSize());
    }

    public String getName() {
        return name;
    }

    public boolean isClientPlayer() {
        return clientPlayer;
    }

    public String getCollisionBoxMesh() {
        return collisionBoxMesh;
    }

    public void renderCollisionBox() {
//        setPosition(Camera.getPosition());
        Camera.setObjectMatrix(getPosition(), new Vector3f(0), new Vector3f(1));
        MeshStorage.renderLineMode(collisionBoxMesh);

//        printVec(getPosition());
    }

    public float getEyeHeight() {
        return eyeHeight;
    }

    public void setEyeHeight(float eyeHeight) {
        this.eyeHeight = eyeHeight;
    }
}
