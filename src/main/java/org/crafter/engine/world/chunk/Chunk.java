package org.crafter.engine.world.chunk;

import org.crafter.engine.camera.Camera;
import org.crafter.engine.delta.Delta;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.shader.ShaderStorage;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3f;

import java.io.Serializable;

/**
 * The final instance of chunk in the inheritance chain.
 * Java auto calls super() down the chain.
 */
public class Chunk extends ChunkMeshHandling implements Serializable {

    //Todo: idea: metadata hashmap

    private final Vector2ic position;

    // fixme: remove! Temp
    private float rotation = 0.0f;

    public Chunk(int x, int y) {
        this(new Vector2i(x,y));
    }
    public Chunk(Vector2ic position) {
        this.position = position;
    }

    public Vector2ic getPosition() {
        return position;
    }

    public int getX() {
        return position.x();
    }

    public int getY() {
        return position.y();
    }

    /**
     * Render resides in the final implementation of the Chunk inheritance chain because:
     * It requires the position of the chunk!
     */
    public void render() {
        rotation += Delta.getDelta();
        Camera.setObjectMatrix(new Vector3f(0,0,-1), new Vector3f(0,0,0), new Vector3f(1,1,1));
        for (int i = 0; i < getStacks(); i++) {
            String gottenMeshUUID = getMesh(i);
            if (gottenMeshUUID != null) {
                System.out.println("rendering: " + gottenMeshUUID);
                MeshStorage.render(gottenMeshUUID);
            }
        }
    }



}
