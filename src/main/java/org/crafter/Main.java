package org.crafter;

import org.crafter.engine.api.API;
import org.crafter.engine.gui.font.Font;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.shader.ShaderStorage;
import org.crafter.engine.texture.TextureStorage;
import org.crafter.engine.window.Window;
import org.crafter.engine.world.chunk.Chunk;
import org.crafter.engine.world.chunk.ChunkStorage;
import org.crafter.engine.world_generation.ChunkGenerator;
import org.joml.Random;
import org.joml.Vector2i;

import java.util.Date;

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

        API.initialize();

        ChunkGenerator.start();

        ShaderStorage.createShader("3d", "shaders/3d_vertex.vert", "shaders/3d_fragment.frag");
        ShaderStorage.createUniform("3d", new String[]{"cameraMatrix", "objectMatrix"});

        ShaderStorage.createShader("2d", "shaders/2d_vertex.vert", "shaders/2d_fragment.frag");
        ShaderStorage.createUniform("2d", new String[]{"cameraMatrix", "objectMatrix"});

        Font.createFont("fonts/totally_original", "mc", true);
        Font.setShadowOffset(0.75f,0.75f);

        Window.setClearColor(0.75f);
        Window.setVsync(false);


        Random random = new Random(new Date().getTime()/1000);

        ChunkGenerator.pushRequest(new Vector2i(0,0));

        while(!Window.shouldClose()) {
            Window.pollEvents();

            Window.clearAll();


            // Note: This is an EXTREME test! This is so out of the scope of this game
            // that it's basically the equivalent of a few servers with thousands of people on them all loading in
            // at the same time running on one instance!
//            System.out.println("--------- MAIN THREAD STARTED REQUESTS ----------");
//            for (int i = 0; i < random.nextInt(100); i++) {
//                // -25 to 25
//                ChunkGenerator.pushRequest(new Vector2i(
//                        random.nextInt(100) - 51,
//                        random.nextInt(100) - 51
//                ));
//            }

            while (ChunkGenerator.hasUpdate()) {
                Chunk generatedChunk = ChunkGenerator.getUpdate();
                System.out.println("Main: Received chunk (" + generatedChunk.getX() + ", " + generatedChunk.getY() + ")!");
                ChunkStorage.addOrUpdate(generatedChunk);
            }




            Window.swapBuffers();

        }

        ChunkGenerator.stop();
        TextureStorage.destroyAll();
        MeshStorage.destroyAll();
        ShaderStorage.destroyAll();
        API.destroy();
        Window.destroy();
    }
}