package org.crafter;

import org.crafter.engine.camera.Camera;
import org.crafter.engine.gui.razorfont.Font;
import org.crafter.engine.gui.razorfont.FontLoadingCalls;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.texture.TextureStorage;
import org.crafter.engine.window.Window;
import org.crafter.engine.shader.ShaderStorage;
import org.joml.Vector3f;

public class Main {
    public static void main(String[] args) {

        Window.initialize();


        ShaderStorage.createShader("3d", "shaders/3d_vertex.vert", "shaders/3d_fragment.frag");
        ShaderStorage.createUniform("3d", new String[]{"cameraMatrix", "objectMatrix", "textureSampler"});

        TextureStorage.createTexture("textures/debug.png");

        Font.setFontStringCall(TextureStorage::createTexture);
        Font.createFont("fonts/totally_original", "mc", true);

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

        float rotation = 0.0f;

        while(!Window.shouldClose()) {

            rotation += 1;

            if (rotation >= 360) {
                rotation = 0;
            }

            Window.pollEvents();

            Window.clearAll();

            // Now we're moving into OpenGL shader implementation

            ShaderStorage.start("3d");

            Camera.updateCameraMatrix();

            Camera.setObjectMatrix(
                    new Vector3f(0.0f,0,-3),
                    new Vector3f(0, (float)Math.toRadians(rotation), 0),
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