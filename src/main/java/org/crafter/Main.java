package org.crafter;

import org.crafter.engine.camera.Camera;
import org.crafter.engine.gui.razorfont.Font;
import org.crafter.engine.gui.razorfont.FontLoadingCalls;
import org.crafter.engine.gui.razorfont.RawData;
import org.crafter.engine.gui.razorfont.RenderCall;
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

        ShaderStorage.createShader("2d", "shaders/2d_vertex.vert", "shaders/3d_fragment.frag");
        ShaderStorage.createUniform("2d", new String[]{"cameraMatrix", "objectMatrix"});

        TextureStorage.createTexture("textures/debug.png");

        Font.setFontStringCall(TextureStorage::createTexture);
        /**
         * Idea: put this into the mesh package, static class, reuse the poop out of tempObject
         */
        Font.setRenderCall(rawData -> {
            Mesh tempObject = new Mesh(
                    null,
                    rawData.vertexPositions,
                    rawData.textureCoordinates,
                    rawData.indices,
                    null,
                    null,
                    Font.getCurrentFontTextureFileLocation()
            );
            tempObject.render();
            tempObject.destroy();
        });
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

            Window.clearDepth();

            Font.updateCanvasSize();

            ShaderStorage.start("2d");

            Font.renderToCanvas(0.0f, 0.0f, 20.0f, "hi there");

            Font.render();



            Window.swapBuffers();

        }

        TextureStorage.destroyAll();
        MeshStorage.destroyAll();
        ShaderStorage.destroyAll();

        Window.destroy();
    }
}