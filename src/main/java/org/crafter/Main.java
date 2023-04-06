package org.crafter;

import org.crafter.engine.controls.Mouse;
import org.crafter.engine.delta.Delta;
import org.crafter.engine.gui.GUI;
import org.crafter.engine.gui.GUIStorage;
import org.crafter.engine.gui.actions.Click;
import org.crafter.engine.gui.actions.OnRender;
import org.crafter.engine.gui.actions.OnStep;
import org.crafter.engine.gui.components.*;
import org.crafter.engine.gui.enumerators.Alignment;
import org.crafter.engine.gui.factories.ImageMeshFactory;
import org.crafter.engine.gui.font.Font;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.shader.ShaderStorage;
import org.crafter.engine.texture.TextureStorage;
import org.crafter.engine.window.Window;
import org.joml.Vector2f;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

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

        Font.createFont("fonts/totally_original", "mc", true);
        Font.setShadowOffset(0.75f,0.75f);

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

        float rotation = 0.0f;

        AtomicInteger index = new AtomicInteger();
        final String[] lastFocused = {""};

        GUIStorage.addGUI("inGame",
             new GUI("inGame")
//                    .addGUIElement(
//                            "versionInfo",
//                            new Label(VERSION_INFO, 40, Alignment.TOP_LEFT, null)
//                    )
//                    .addGUIElement(
//                            "buttonTest",
//                            new Button("I am a button!", 52, Alignment.CENTER, null)
//                                    .addClickCallback((gui, element) -> {
//                                        System.out.println("click clack");
//                                        System.out.println("This is definitely a button, yes");
//                                        gui.setText(element.name(), "NICE!");
//                                    })
//                    )
//                     .addGUIElement(
//                             "textBox",
//                             new TextBox("Your text here...", 52, Alignment.BOTTOM_LEFT, null, 1024)
//                                     .addEnterInputCallback((gui, element, textData) -> {
//                                         if (!textData.equals("")) {
//                                             System.out.println(element.name() + " output: " + textData);
//
//                                             int gotten = index.get();
//
//                                             DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm");
//                                             LocalDateTime timeStampRaw = LocalDateTime.now();
//                                             String timeStamp = formatter.format(timeStampRaw);
//
//                                             String playerText = "(" + timeStamp + ") Player: " + textData;
//
//                                             GUIStorage.addElement("inGame", "index" + gotten, new Label(
//                                                     playerText,
//                                                     32,
//                                                     Alignment.BOTTOM_LEFT,
//                                                     new Vector2f(0, 60)
//                                             ));
//
//                                             for (int i = 0; i <= gotten; i++) {
//                                                 int multiplier = gotten - i;
//                                                 GUIStorage.setOffset("index" + i, new Vector2f(0, (multiplier + 2) * 60));
//                                             }
//
//                                             index.getAndIncrement();
//                                         }
//                                     })
////                                     .addClickCallback((gui, element) -> {
////                                         System.out.println("click clack");
////                                     })
//                     )
//                     .addGUIElement("youtubeButton", new Label("test", 52, Alignment.BOTTOM_RIGHT,null)
//                             .addOnStepCallback((gui, element) -> {
//                                 gui.setText("youtubeButton", "Delta Time: " + Delta.getDelta());
//                             })
//                     )
//                     .addGUIElement("mousey", new Label("", 52, Alignment.TOP_RIGHT,null)
//                             .addOnStepCallback((gui, element) -> {
//                                 gui.setText("mousey", "Mouse Pos: " + Mouse.getPosition().x() + ", " + Mouse.getPosition().y());
//                             })
//                     )
//                     .addGUIElement("fancy", new Label("test", 52, Alignment.TOP_LEFT,new Vector2f(0, -60))
//                             .addOnStepCallback(
//                                     new OnStep() {
//                                    @Override
//                                    public void action(GUI gui, GUIElement element) {
//                                        String focusedElement = gui.getCurrentlyFocused();
//                                        if (!focusedElement.equals(lastFocused[0])) {
//                                            gui.setText("fancy", "Currently Focused: " + focusedElement);
//                                            lastFocused[0] = focusedElement;
//                                        }
//                                    }
//                             })
//                     )
//                     .addGUIElement("imageBoi", new Image("textures/debug.png", 10, Alignment.CENTER_LEFT, null)
//                     )
//                     // -160 because original is 16x16 scaled by 10x, trimmed pixels (1 off top, 1 off bottom) makes it 3x14
//                     .addGUIElement("trimTest", new Image("textures/trim_test.png", 10, Alignment.TOP_CENTER, new Vector2f(0,0), true)
//                     )
                     .addGUIElement("dropMenu", new DropMenu(512, new String[]{"this is a test of a long blah blah blah", "there"}, 32, Alignment.BOTTOM_RIGHT,null, null))
                     .addGUIElement("colorMenu", new DropMenu(256, new String[]{"red", "green", "blue", "gray"}, 24, Alignment.TOP_LEFT,null, 3)
                             .addClickCallback((gui, element) -> {
                                 DropMenu thisMenu = (DropMenu) element;
                                 if (thisMenu.newOption()) {
                                     switch (thisMenu.getSelectedOption()) {
                                         case "red" -> Window.setClearColor(1, 0, 0);
                                         case "green" -> Window.setClearColor(0, 1, 0);
                                         case "blue" -> Window.setClearColor(0, 0, 1);
                                         case "gray" -> Window.setClearColor(0.75f);
                                         default -> {
                                             // oops
                                         }
                                     }
                                 }
                             })
                     )
                     .addGUIElement("myCoolMesh",
                             new GUIMesh(
                                     ImageMeshFactory.createImageMesh(10, "textures/debug.png"),
                                     Alignment.CENTER,
                                     null,
                                     false
                             )
                                     .addOnRenderCallback(new OnRender() {
                                         @Override
                                         public void action(GUI gui, GUIElement element) {

                                             System.out.println("I'm rendering woo");

                                         }
                                     })
                     )
//                     .addGUIElement(
//                             "sassyButton",
//                             new Button("sassyButton","Getcya buttons here!", 52, Alignment.BOTTOM_RIGHT, null)
//                                     .addClickCallback((gui, element) -> {
//                                         String[] sassiness = new String[] {
//                                                 "over here!",
//                                                 "Nah, over here!",
//                                                 "Here I am!",
//                                                 "Nah, jk, here!"
//                                         };
//
//                                         gui.setText(element.name(), sassiness[(int)(Math.random() * 3)]);
//                                         gui.setAlignment(element.name(), Alignment.asArray()[(int)(Math.random() * 8)] );
//
//                                     })
//                                     .addHoverCallback(((gui, element) -> {
//                                         gui.setText(element.name(), String.valueOf(Math.random()));
//                                     }))
//                     )
//                     .addGUIElement("quitButton",
//                             new Button("quitButton", "QUIT", 44, Alignment.BOTTOM_CENTER, null)
//                                     .addClickCallback(((gui, element) -> Window.close()))
//                     )
        );

        GUIStorage.selectGUI("inGame");


        while(!Window.shouldClose()) {
            Window.pollEvents();

//            System.out.println(Delta.getDelta());

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