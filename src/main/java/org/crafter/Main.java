package org.crafter;

import org.crafter.engine.camera.Camera;
import org.crafter.engine.gui.font.Font;
import org.crafter.engine.mesh.Mesh;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.texture.TextureStorage;
import org.crafter.engine.window.Window;
import org.crafter.engine.shader.ShaderStorage;
import org.joml.Vector3f;

public class Main {
    public static void main(String[] args) {

        Window.initialize();


        ShaderStorage.createShader("3d", "shaders/3d_vertex.vert", "shaders/3d_fragment.frag");
        ShaderStorage.createUniform("3d", new String[]{"cameraMatrix", "objectMatrix"});

        ShaderStorage.createShader("2d", "shaders/2d_vertex.vert", "shaders/2d_fragment.frag");
        ShaderStorage.createUniform("2d", new String[]{"cameraMatrix", "objectMatrix"});

        TextureStorage.createTexture("textures/debug.png");

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
                "textures/debug.png",
                false
        );

        Window.setClearColor(0.75f);

        Font.selectFont("mc");

        float rotation = 0.0f;

        while(!Window.shouldClose()) {


            rotation += 1;

            if (rotation >= 360) {
                rotation = 0;
            }

            Window.pollEvents();

            Window.clearAll();

            // Now we're moving into OpenGL shader implementation

            // 3d

            ShaderStorage.start("3d");

            Camera.updateCameraMatrix();

            Camera.setObjectMatrix(
                    new Vector3f(0.0f,0,-3),
                    new Vector3f(0, (float)Math.toRadians(rotation), 0),
                    new Vector3f(1)
            );

            MeshStorage.render("test");

            // 2d

            Window.clearDepthBuffer();

            ShaderStorage.start("2d");

            Camera.updateGuiCameraMatrix();

            Camera.setGuiObjectMatrix(Window.getWindowWidth() / 2.0f,Window.getWindowHeight() / 2.0f, 100, -100);

            MeshStorage.render("test");

            Camera.setGuiObjectMatrix(0,0);

            Font.drawText(0, 0, 42.0f, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ\n0123456789!?%|:;_-*^,.\"'#$&@+=/(){}");

            Window.swapBuffers();

        }

        TextureStorage.destroyAll();
        MeshStorage.destroyAll();
        ShaderStorage.destroyAll();

        Window.destroy();
    }
}