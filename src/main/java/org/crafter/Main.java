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

    private static final String DEVELOPMENT_CYCLE = "Pre-Alpha";
    private static final String VERSION = "v0.0.2";
    private static final String VERSION_INFO = "Crafter " + DEVELOPMENT_CYCLE + " " + VERSION;
    private static final boolean PROTOTYPE_BUILD = true;

    private static String getVersionInfo() {
        return VERSION_INFO + (PROTOTYPE_BUILD ? " (Prototype Build)" : "");
    }

    // Fixme: This is only for debugging and prototyping, remove this eventually
    private static final Random random = new Random(new Date().getTime()/1000);

    // Fixme: these are only for debugging and prototyping, move this into another class eventually
    private static final Vector3f cameraMovementX = new Vector3f();
    private static final Vector3f cameraMovementY = new Vector3f();
    private static final Vector3f cameraMovementZ = new Vector3f();
    private static final Vector3f finalCameraMovement = new Vector3f();
    private static final Vector3f newCameraPosition = new Vector3f();
    private static final Vector3f cameraDelta = new Vector3f();
    private static final Vector3f newCameraRotation = new Vector3f();

    public static void main(String[] args) {

        initialize();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                ChunkGenerator.pushRequest(new Vector2i(x,z));
            }
        }

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

        Window.pollEvents();

        Window.clearAll();

        ShaderStorage.start("3d");


        // Note: This is an EXTREME test! This is so out of the scope of this game
        // that it's basically the equivalent of a few servers with thousands of people on them all loading in
        // at the same time running on one instance!
//        System.out.println("--------- MAIN THREAD STARTED REQUESTS ----------");
//        for (int i = 0; i < random.nextInt(100); i++) {
//            // -25 to 25
//            ChunkGenerator.pushRequest(new Vector2i(
//                    random.nextInt(100) - 51,
//                    random.nextInt(100) - 51
//            ));
//        }

        // FIXME: BEGIN CAMERA INPUT DEBUGGING

        // Rotation
        Vector2fc mouseDelta = Mouse.getDelta();
        // Very, very important note: Notice that x & y are swapped. Because the window 2d matrix is 90 degrees rotated from the 3d matrix!
        cameraDelta.set(mouseDelta.y(), mouseDelta.x(), 0).mul(Camera.getSensitivity());
        Camera.getRotation().add(cameraDelta, newCameraRotation);
        Camera.setRotation(newCameraRotation);

        // newCameraRotation is now used below

        // Movement


        //fixme: this should probably be a vector
        float movementX = 0;
        float movementY = 0;
        float movementZ = 0;

        if (Keyboard.keyDown(GLFW_KEY_W)) {
            movementZ += -1;
        }
        if (Keyboard.keyDown(GLFW_KEY_S)) {
            movementZ += 1;
        }
        if (Keyboard.keyDown(GLFW_KEY_A)) {
            movementX += -1;
        }
        if (Keyboard.keyDown(GLFW_KEY_D)) {
            movementX += 1;
        }

        if (Keyboard.keyDown(GLFW_KEY_SPACE)) {
            movementY += 1;
        }
        if (Keyboard.keyDown(GLFW_KEY_LEFT_SHIFT) || Keyboard.keyDown(GLFW_KEY_RIGHT_SHIFT)) {
            movementY -= 1;
        }


        final float yaw = newCameraRotation.y();
        final float movementDelta = Delta.getDelta() * 10;

        // Layered
        cameraMovementX.zero();
        cameraMovementY.zero();
        cameraMovementZ.zero();
        finalCameraMovement.zero();

        cameraMovementX.set(getHorizontalDirection(yawToLeft(yaw))).mul(movementX);
        cameraMovementY.set(0,movementY, 0);
        cameraMovementZ.set(getHorizontalDirection(yaw)).mul(movementZ);

        // Layer in, and then make it usable with delta
        finalCameraMovement.set(cameraMovementX.add(cameraMovementY).add(cameraMovementZ)).mul(movementDelta);

        Vector3fc cameraPosition = Camera.getPosition();
        cameraPosition.add(finalCameraMovement, newCameraPosition);
        Camera.setPosition(newCameraPosition);

        Camera.updateCameraMatrix();

        // FIXME: END CAMERA INPUT DEBUGGING

        //Todo: This needs to be wrappered in some type of utility class, this is basically an inter-thread communicator!
        while (ChunkGenerator.hasUpdate()) {
            Chunk generatedChunk = ChunkGenerator.getUpdate();
//                System.out.println("Main: Received chunk (" + generatedChunk.getX() + ", " + generatedChunk.getY() + ")!");
            ChunkStorage.addOrUpdate(generatedChunk);

            Vector2ic position = generatedChunk.getPosition();
            //fixme: needs to iterate 0-7
            // Render stack 0 (y coordinate 0 to 15)
            for (int i = 0; i < generatedChunk.getStacks(); i++) {
                System.out.println(i);
                ChunkMeshGenerator.pushRequest(position.x(), i, position.y());
            }
        }
        while (ChunkMeshGenerator.hasUpdate()) {
            ChunkMeshRecord generatedMesh = ChunkMeshGenerator.getUpdate();
//            System.out.println("------- BEGIN RECORD DEBUGGING --------");
//            System.out.println("Got record for: " + generatedMesh.destinationChunkPosition().x() + ", " + generatedMesh.destinationChunkPosition().y());
//            System.out.println("Positions: " + Arrays.toString(generatedMesh.positions()));
//            System.out.println("Tcoords: " + Arrays.toString(generatedMesh.textureCoordinates()));
//            System.out.println("Indices: " + Arrays.toString(generatedMesh.indices()));
//            System.out.println("------- END RECORD DEBUGGING --------");

            // Fixme: This is a debug for one simple chunk, make sure this is removed so it doesn't cause a random red herring
            // TODO: Make sure this is done within the main thread!

            final Vector2ic destinationPosition = generatedMesh.destinationChunkPosition();

            if (ChunkStorage.hasPosition(destinationPosition)) {
                ChunkStorage.getChunk(destinationPosition).setMesh(generatedMesh.stack(), generatedMesh);
            } // Else nothing happens to it and it's GCed
        }


        if (ChunkStorage.hasPosition(new Vector2i(0,0))) {
            ChunkStorage.getChunk(new Vector2i(0, 0)).render();
        }

        Window.swapBuffers();
    }

    private static void destroy() {
        ChunkMeshGenerator.stop();
        ChunkGenerator.stop();
        TextureStorage.destroyAll();
        MeshStorage.destroyAll();
        ShaderStorage.destroyAll();
        Window.destroy();
    }
}