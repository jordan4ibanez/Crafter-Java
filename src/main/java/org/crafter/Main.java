package org.crafter;

import org.crafter.engine.gui.GUI;
import org.crafter.engine.gui.GUIStorage;
import org.crafter.engine.gui.components.Button;
import org.crafter.engine.gui.enumerators.Alignment;
import org.crafter.engine.gui.components.Label;
import org.crafter.engine.gui.font.Font;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.texture.TextureStorage;
import org.crafter.engine.window.Window;
import org.crafter.engine.shader.ShaderStorage;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Main {

    private static final String DEVELOPMENT_CYCLE = "Pre-Alpha";

    private static final String VERSION = "v0.0.0";

    private static final String VERSION_INFO = "Crafter " + DEVELOPMENT_CYCLE + " " + VERSION;
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

        Font.setShadowOffset(0.9f,0.9f);

        float rotation = 0.0f;


        GUIStorage.addGUI("inGame",
             new GUI("inGame")
                    .addGUIElement(
                            "versionInfo",
                            new Label("versionInfo", VERSION_INFO, 40, Alignment.TOP_LEFT, null, null, null)
                    )
                    .addGUIElement(
                            "buttonTest",
                            new Button("buttonTest", Alignment.CENTER, null, null, null),  5)
                    )
        );

        GUIStorage.selectGUI("inGame");


        while(!Window.shouldClose()) {

            rotation += 1;

            if (rotation >= 360) {
                rotation = 0;
            }

            Window.pollEvents();

            GUIStorage.process();

            Window.clearAll();

            GUIStorage.render();

//
//            // Now we're moving into OpenGL shader implementation
//
//            // 3d
//
//            ShaderStorage.start("3d");
//
//            Camera.updateCameraMatrix();
//
//            Camera.setObjectMatrix(
//                    new Vector3f(0.0f,0,-3),
//                    new Vector3f(0, (float)Math.toRadians(rotation), 0),
//                    new Vector3f(1)
//            );
//
//            MeshStorage.render("test");
//
//            // 2d
//
//            Window.clearDepthBuffer();
//
//            ShaderStorage.start("2d");
//
//            Camera.updateGuiCameraMatrix();
//
//
//            Font.enableShadows();
//            String text = "hello\nthere";
//
//            Vector2f textCenterOnWindow = Window.getWindowCenter().sub(Font.getTextCenter(42.0f, text));
//
//            Camera.setGuiObjectMatrix(0,0);
//
//
//            Font.setShadowOffset(0.5f, 0.5f);
//            Font.switchColor(1f,1f,1f);
//            Font.switchShadowColor(0,0,0);
//
//            Font.drawText(textCenterOnWindow.x, textCenterOnWindow.y, 42.0f, text);

            Window.swapBuffers();

        }

        TextureStorage.destroyAll();
        MeshStorage.destroyAll();
        ShaderStorage.destroyAll();

        Window.destroy();
    }
}