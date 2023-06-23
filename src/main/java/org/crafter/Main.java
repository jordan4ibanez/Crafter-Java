package org.crafter;

import org.crafter.engine.api.API;
import org.crafter.engine.camera.Camera;
import org.crafter.engine.controls.Keyboard;
import org.crafter.engine.controls.Mouse;
import org.crafter.engine.delta.Delta;
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
import org.joml.*;

import java.util.Date;

import static org.crafter.engine.utility.GameMath.getHorizontalDirection;
import static org.crafter.engine.utility.GameMath.yawToLeft;
import static org.lwjgl.glfw.GLFW.*;

public class Main {

    private static final String DEVELOPMENT_CYCLE = "Pre-Classic (Prototyping)";
    private static final String VERSION = "v0.0.3";
    private static final String VERSION_INFO = "Crafter " + DEVELOPMENT_CYCLE + " " + VERSION;
    private static final boolean PROTOTYPE_BUILD = true;

    private static String getVersionInfo() {
        return VERSION_INFO + (PROTOTYPE_BUILD ? " (Prototype Build)" : "");
    }

    // Fixme: This is only for debugging and prototyping, remove this eventually
    // private static final Random random = new Random(new Date().getTime()/1000);

    // Fixme: these are only for debugging and prototyping, move this into another class eventually
    private static final Vector3f cameraMovementX = new Vector3f();
    private static final Vector3f cameraMovementY = new Vector3f();
    private static final Vector3f cameraMovementZ = new Vector3f();
    private static final Vector3f finalCameraMovement = new Vector3f();
    private static final Vector3f newCameraPosition = new Vector3f();
    private static final Vector3f cameraDelta = new Vector3f();
    private static final Vector3f newCameraRotation = new Vector3f();

    private static final int debugChunkSizeRememberToRemoveThisGarbage = 18;
    private static int currentOffsetX = -debugChunkSizeRememberToRemoveThisGarbage;
    private static int currentOffsetZ = -debugChunkSizeRememberToRemoveThisGarbage;

    private static boolean finishGeneration = false;
    private static int frameCounter = 0;
    private static final int frameSkips = 1000;

    public static void main(String[] args) {

        initialize();

        try {
            while(!Window.shouldClose()) {
                mainLoop();
            }
        } catch (Exception e) {
            // Game must shut down external threads or it WILL hang
            ChunkMeshGenerator.stop();
            ChunkGenerator.stop();
            throw new RuntimeException(e);
        }

        destroy();

    }

    private static void initialize() {
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
        Window.maximize();
        Mouse.capture();

    }

    private static void mainLoop() {

        // This basically is making chunks generate really, REALLY slowly
        if (!finishGeneration) {

            if (frameCounter < frameSkips) {
                frameCounter++;
            } else {

                frameCounter = frameSkips; // 0

                ChunkGenerator.pushRequest(new Vector2i(currentOffsetX, currentOffsetZ));

                currentOffsetX++;

                if (currentOffsetX >= debugChunkSizeRememberToRemoveThisGarbage) {
                    currentOffsetX = -debugChunkSizeRememberToRemoveThisGarbage;
                    currentOffsetZ++;
                    if (currentOffsetZ >= debugChunkSizeRememberToRemoveThisGarbage) {
                        finishGeneration = true;
                    }
                }
            }
        }

        Window.pollEvents();

        Window.clearAll();

        ShaderStorage.start("3d");


        //todo This is temp remove me plz
        doFirstPersonCamera();

        //Todo: This needs to be wrappered in some type of utility class, this is basically an inter-thread communicator!
        while (ChunkGenerator.hasUpdate()) {

            Chunk generatedChunk = ChunkGenerator.getUpdate();

            System.out.println("Main: Received chunk (" + generatedChunk.getPositionString() + ")!");

            ChunkStorage.addOrUpdate(generatedChunk);

            Vector2ic position = generatedChunk.getPosition();

            //fixme: needs to iterate 0-7
            // Render stack 0 (y coordinate 0 to 15)
            generateFullChunkMesh(position.x(), position.y());

            // Now we update neighbors.
            // Right handed coordinate system.
            // Basic if branch because why not.
            // ChunkMeshGenerator automatically !NOW! REJECTS duplicates - this might cause horrible performance.
            // FIXME: this is the cause if performance is brutal.
            // So now we blindly shovel in requests.
            // This is scoped to auto GC if hit fails. It also allows to be more explicit.

            { // Front
                Vector2ic neighborFront = new Vector2i(position.x(), position.y() - 1);
                if (ChunkStorage.hasPosition(neighborFront)) {
                    generateFullChunkMesh(neighborFront.x(), neighborFront.y());
                }
            }
            { // Back
                Vector2ic neighborBack = new Vector2i(position.x(), position.y() + 1);
                if (ChunkStorage.hasPosition(neighborBack)) {
                    generateFullChunkMesh(neighborBack.x(), neighborBack.y());
                }
            }
            { // Left
                Vector2ic neighborLeft = new Vector2i(position.x() - 1, position.y());
                if (ChunkStorage.hasPosition(neighborLeft)) {
                    generateFullChunkMesh(neighborLeft.x(), neighborLeft.y());
                }
            }
            { // Right
                Vector2ic neighborRight = new Vector2i(position.x() + 1, position.y());
                if (ChunkStorage.hasPosition(neighborRight)) {
                    generateFullChunkMesh(neighborRight.x(), neighborRight.y());
                }
            }

        }


        while (ChunkMeshGenerator.hasUpdate()) {
            ChunkMeshRecord generatedMesh = ChunkMeshGenerator.getUpdate();

            // Fixme: This is a debug for one simple chunk, make sure this is removed so it doesn't cause a random red herring
            // TODO: Make sure this is done within the main thread!

            final Vector2ic destinationPosition = generatedMesh.destinationChunkPosition();

            if (ChunkStorage.hasPosition(destinationPosition)) {
                ChunkStorage.getChunk(destinationPosition).setMesh(generatedMesh.stack(), generatedMesh);
            } // Else nothing happens to it and it's GCed
        }


        for (int x = -debugChunkSizeRememberToRemoveThisGarbage; x <= debugChunkSizeRememberToRemoveThisGarbage; x++) {
            for (int z = -debugChunkSizeRememberToRemoveThisGarbage; z <= debugChunkSizeRememberToRemoveThisGarbage; z++) {
                final Vector2i requestingPosition = new Vector2i(x,z);
                if (ChunkStorage.hasPosition(requestingPosition)) {
                    ChunkStorage.getChunk(requestingPosition).render();
                }
            }
        }

        Window.swapBuffers();
    }

    /**
     * Generates chunk mesh stacks (0-7)
     * @param x world position on X axis (literal)
     * @param z world position on Z axis (literal)
     */
    private static void generateFullChunkMesh(int x, int z) {
        for (int i = 0; i < Chunk.getStacks(); i++) {
//                System.out.println(i);
            ChunkMeshGenerator.pushRequest(x, i, z);
        }
    }

    private static void destroy() {
        ChunkMeshGenerator.stop();
        ChunkGenerator.stop();
        TextureStorage.destroyAll();
        MeshStorage.destroyAll();
        ShaderStorage.destroyAll();
        Window.destroy();
    }

    // BLOP

}
