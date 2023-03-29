package org.crafter;

import org.crafter.engine.camera.Camera;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.texture.TextureStorage;
import org.crafter.engine.window.Window;
import org.crafter.engine.shader.ShaderStorage;
import org.joml.Vector3f;
import org.w3c.dom.Text;

public class Main {
    public static void main(String[] args) {

        Window.initialize();


        ShaderStorage.createShader("basic", "shaders/vertex.vert", "shaders/fragment.frag");
        ShaderStorage.createUniform("basic", new String[]{"cameraMatrix", "objectMatrix", "textureSampler"});

        ShaderStorage.start("basic");

        TextureStorage.createTexture("textures/debug.png");

        MeshStorage.newMesh(
            "test",
                new float[]{
                        0.0f,  0.5f, 0.0f,
                        -0.5f, -0.5f, 0.0f,
                        0.5f, -0.5f, 0.0f
                },
                new float[] {
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f
                },
                new int[] {
                        0,1,2
                },
                null,
                null,
                "textures/debug.png"
        );

        Window.setClearColor(0.75f);

        while(!Window.shouldClose()) {

            Window.pollEvents();

            Window.clearAll();

            Camera.updateCameraMatrix();

            Camera.setObjectMatrix(
                    new Vector3f(0,0,-1),
                    new Vector3f(0),
                    new Vector3f(1)
            );

            MeshStorage.render("test");

            Window.swapBuffers();

        }

        TextureStorage.destroyAll();
        MeshStorage.destroyAll();
        ShaderStorage.destroyAll();

        Window.destroy();
    }
}