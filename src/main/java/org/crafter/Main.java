package org.crafter;

import org.crafter.engine.texture.texture_packer.TexturePacker;
import org.crafter.engine.world.chunk.Chunk;
import org.crafter.engine.gui.font.Font;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.shader.ShaderStorage;
import org.crafter.engine.texture.TextureStorage;
import org.crafter.engine.window.Window;
import org.joml.Vector4fc;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

public class Main {

    private static final String DEVELOPMENT_CYCLE = "Pre-Alpha";
    private static final String VERSION = "v0.0.2";
    private static final String VERSION_INFO = "Crafter " + DEVELOPMENT_CYCLE + " " + VERSION;
    private static final boolean PROTOTYPE_BUILD = true;

    private static String getVersionInfo() {
        return VERSION_INFO + (PROTOTYPE_BUILD ? " (Prototype Build)" : "");
    }

    public static void main(String[] args) {

        Window.initialize();
        Window.setTitle(getVersionInfo(), true);


//        ShaderStorage.createShader("3d", "shaders/3d_vertex.vert", "shaders/3d_fragment.frag");
//        ShaderStorage.createUniform("3d", new String[]{"cameraMatrix", "objectMatrix"});
//
//        ShaderStorage.createShader("2d", "shaders/2d_vertex.vert", "shaders/2d_fragment.frag");
//        ShaderStorage.createUniform("2d", new String[]{"cameraMatrix", "objectMatrix"});

        Font.createFont("fonts/totally_original", "mc", true);
        Font.setShadowOffset(0.75f,0.75f);

        Window.setClearColor(0.75f);

        TexturePacker packer = TexturePacker.getInstance();

        File[] dir = new File("textures/blocks").listFiles();

        if (dir == null) {
            throw new RuntimeException("You done goofed boi, check the other folders too!");
        }

        for (File file : dir) {
            if (!file.isDirectory() && file.getName().contains(".png")) {
                System.out.println(file.getName());
                packer.add("textures/blocks/" + file.getName());
            }
        }

        packer.debugPrintCanvas();
//        System.out.println(Arrays.toString(packer.getQuadOf("textures/button.png")));



        while(Window.shouldClose()) {
            Window.pollEvents();

            Window.clearAll();

            Window.swapBuffers();

        }

        TextureStorage.destroyAll();
        MeshStorage.destroyAll();
        ShaderStorage.destroyAll();

        Window.destroy();
    }
}