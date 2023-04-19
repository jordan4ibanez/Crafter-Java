package org.crafter;

import org.crafter.engine.api.API;
import org.crafter.engine.camera.Camera;
import org.crafter.engine.gui.font.Font;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.shader.ShaderStorage;
import org.crafter.engine.texture.TextureStorage;
import org.crafter.engine.window.Window;
import org.crafter.engine.world.chunk.Chunk;
import org.crafter.engine.world.chunk.ChunkStorage;
import org.crafter.engine.world_generation.ChunkGenerator;
import org.crafter.engine.world_generation.chunk_mesh_generation.ChunkMeshGenerator;
import org.crafter.engine.world_generation.chunk_mesh_generation.ChunkMeshRecord;
import org.joml.Random;
import org.joml.Vector2i;

import java.util.Arrays;
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
        ChunkMeshGenerator.start();

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

            ShaderStorage.start("3d");

            Camera.updateCameraMatrix();


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

            //Todo: This needs to be wrappered in some type of utility class, this is basically an inter-thread communicator!
            while (ChunkGenerator.hasUpdate()) {
                Chunk generatedChunk = ChunkGenerator.getUpdate();
//                System.out.println("Main: Received chunk (" + generatedChunk.getX() + ", " + generatedChunk.getY() + ")!");
                ChunkStorage.addOrUpdate(generatedChunk);
                ChunkMeshGenerator.pushRequest(generatedChunk.getPosition());
            }
            while (ChunkMeshGenerator.hasUpdate()) {
                ChunkMeshRecord generatedMesh = ChunkMeshGenerator.getUpdate();
                System.out.println("------- BEGIN RECORD DEBUGGING --------");
                System.out.println("Got record for: " + generatedMesh.destinationChunkPosition().x() + ", " + generatedMesh.destinationChunkPosition().y());
                System.out.println("Positions: " + Arrays.toString(generatedMesh.positions()));
                System.out.println("Tcoords: " + Arrays.toString(generatedMesh.textureCoordinates()));
                System.out.println("Indices: " + Arrays.toString(generatedMesh.indices()));
                System.out.println("------- END RECORD DEBUGGING --------");

                // Fixme: This is a debug for one simple chunk, make sure this is removed so it doesn't cause a random red herring
                // TODO: Make sure this is done within the main thread!

                if (ChunkStorage.hasPosition(generatedMesh.destinationChunkPosition())) {
                    ChunkStorage.getChunk(generatedMesh.destinationChunkPosition()).setMesh(0, generatedMesh);
                } // Else nothing happens to it and it's GCed
            }


            if (ChunkStorage.hasPosition(new Vector2i(0,0))) {
                ChunkStorage.getChunk(new Vector2i(0, 0)).render();
            }


            Window.swapBuffers();

        }

        ChunkMeshGenerator.stop();
        ChunkGenerator.stop();
        TextureStorage.destroyAll();
        MeshStorage.destroyAll();
        ShaderStorage.destroyAll();
        API.destroy();
        Window.destroy();
    }
}