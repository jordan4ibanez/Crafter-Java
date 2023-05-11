package org.crafter.engine

import org.crafter.engine.api.API
import org.crafter.engine.camera.Camera
import org.crafter.engine.controls.Keyboard
import org.crafter.engine.controls.Mouse
import org.crafter.engine.delta.Delta
import org.crafter.engine.gui.font.Font
import org.crafter.engine.mesh.MeshStorage
import org.crafter.engine.shader.ShaderStorage
import org.crafter.engine.texture.TextureStorage
import org.crafter.engine.utility.GameMath
import org.crafter.engine.window.Window
import org.crafter.engine.world.chunk.ChunkStorage
import org.crafter.engine.world_generation.ChunkGenerator
import org.crafter.engine.world_generation.chunk_mesh_generation.ChunkMeshGenerator
import org.joml.Vector2i
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW


private const val DEVELOPMENT_CYCLE = "Pre-Alpha"
private const val VERSION = "v0.0.2"
private const val VERSION_INFO = "Crafter $DEVELOPMENT_CYCLE $VERSION"
private const val PROTOTYPE_BUILD = true
private val versionInfo: String

get() = VERSION_INFO + if (PROTOTYPE_BUILD) " (Prototype Build)" else ""

// Fixme: This is only for debugging and prototyping, remove this eventually
//    private val random = Random(Date().time / 1000)

// Fixme: these are only for debugging and prototyping, move this into another class eventually
private val cameraMovementX = Vector3f()
private val cameraMovementY = Vector3f()
private val cameraMovementZ = Vector3f()
private val finalCameraMovement = Vector3f()
private val newCameraPosition = Vector3f()
private val cameraDelta = Vector3f()
private val newCameraRotation = Vector3f()
private const val debugChunkSizeRememberToRemoveThisGarbage = 1
private const val debugON = true

fun main(args: Array<String>) {

    initialize()
    for (x in -debugChunkSizeRememberToRemoveThisGarbage..debugChunkSizeRememberToRemoveThisGarbage) {
        for (z in -debugChunkSizeRememberToRemoveThisGarbage..debugChunkSizeRememberToRemoveThisGarbage) {
            ChunkGenerator.pushRequest(Vector2i(x, z))
        }
    }
    try {
        while (!Window.shouldClose()) {
            mainLoop()
        }
    } catch (e: Exception) {
        // Game must shut down external threads or it WILL hang
        ChunkMeshGenerator.stop()
        ChunkGenerator.stop()
        throw RuntimeException(e)
    }
    destroy()
}

private fun initialize() {
    Window.initialize()
    Window.setTitle(versionInfo, true)
    API.initialize()
    ChunkGenerator.start()
    ChunkMeshGenerator.start()
    ShaderStorage.createShader("3d", "shaders/3d_vertex.vert", "shaders/3d_fragment.frag")
    ShaderStorage.createUniform("3d", arrayOf("cameraMatrix", "objectMatrix"))
    ShaderStorage.createShader("2d", "shaders/2d_vertex.vert", "shaders/2d_fragment.frag")
    ShaderStorage.createUniform("2d", arrayOf("cameraMatrix", "objectMatrix"))
    Font.createFont("fonts/totally_original", "mc", true)
    Font.setShadowOffset(0.75f, 0.75f)
    Window.setClearColor(0.75f)
    Window.setVsync(false)
    Window.maximize()
    Mouse.capture()
}

private fun mainLoop() {
    Window.pollEvents()
    Window.clearAll()
    ShaderStorage.start("3d")


    doCameraDebug()


    //Todo: This needs to be wrappered in some type of utility class, this is basically an inter-thread communicator!
    while (ChunkGenerator.hasUpdate()) {
        val generatedChunk = ChunkGenerator.getUpdate()

        ChunkStorage.addOrUpdate(generatedChunk)
        val position = generatedChunk.position
        //fixme: needs to iterate 0-7
        // Render stack 0 (y coordinate 0 to 15)
        for (i in 0 until generatedChunk.stacks) {
            println(i)
            ChunkMeshGenerator.pushRequest(position.x(), i, position.y())
        }
    }
    while (ChunkMeshGenerator.hasUpdate()) {
        val generatedMesh = ChunkMeshGenerator.getUpdate()

        // Fixme: This is a debug for one simple chunk, make sure this is removed so it doesn't cause a random red herring
        // TODO: Make sure this is done within the main thread!
        val destinationPosition = generatedMesh.destinationChunkPosition
        if (ChunkStorage.hasPosition(destinationPosition)) {
            ChunkStorage.getChunk(destinationPosition).setMesh(generatedMesh.stack, generatedMesh)
        } // Else nothing happens to it and it's GCed
    }
    for (x in -debugChunkSizeRememberToRemoveThisGarbage..debugChunkSizeRememberToRemoveThisGarbage) {
        for (z in -debugChunkSizeRememberToRemoveThisGarbage..debugChunkSizeRememberToRemoveThisGarbage) {
            val requestingPosition = Vector2i(x, z)
            if (ChunkStorage.hasPosition(requestingPosition)) {
                ChunkStorage.getChunk(requestingPosition).render()
            }
        }
    }
    Window.swapBuffers()
}

private fun doCameraDebug() {

    // Rotation
    val mouseDelta = Mouse.getDelta()
    // Very, very important note: Notice that x & y are swapped. Because the window 2d matrix is 90 degrees rotated from the 3d matrix!
    cameraDelta.set(mouseDelta.y(), mouseDelta.x(), 0f).mul(Camera.sensitivity);
    Camera.getRotation().add(cameraDelta, newCameraRotation)
    Camera.setRotation(newCameraRotation)

    // newCameraRotation is now used below

    // Movement


    //fixme: this should probably be a vector
    var movementX = 0f
    var movementY = 0f
    var movementZ = 0f
    if (Keyboard.keyDown(GLFW.GLFW_KEY_W)) {
        movementZ += -1f
    }
    if (Keyboard.keyDown(GLFW.GLFW_KEY_S)) {
        movementZ += 1f
    }
    if (Keyboard.keyDown(GLFW.GLFW_KEY_A)) {
        movementX += -1f
    }
    if (Keyboard.keyDown(GLFW.GLFW_KEY_D)) {
        movementX += 1f
    }
    if (Keyboard.keyDown(GLFW.GLFW_KEY_SPACE)) {
        movementY += 1f
    }
    if (Keyboard.keyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || Keyboard.keyDown(GLFW.GLFW_KEY_RIGHT_SHIFT)) {
        movementY -= 1f
    }
    val yaw = newCameraRotation.y()
    val movementDelta = Delta.delta * 10

    // Layered
    cameraMovementX.zero()
    cameraMovementY.zero()
    cameraMovementZ.zero()
    finalCameraMovement.zero()
    cameraMovementX.set(GameMath.getHorizontalDirection(GameMath.yawToLeft(yaw))).mul(movementX)
    cameraMovementY[0f, movementY] = 0f
    cameraMovementZ.set(GameMath.getHorizontalDirection(yaw)).mul(movementZ)

    // Layer in, and then make it usable with delta
    finalCameraMovement.set(cameraMovementX.add(cameraMovementY).add(cameraMovementZ)).mul(movementDelta)
    val cameraPosition = Camera.getPosition()
    cameraPosition.add(finalCameraMovement, newCameraPosition)
    Camera.setPosition(newCameraPosition)
    Camera.updateCameraMatrix()

}

private fun destroy() {
    ChunkMeshGenerator.stop()
    ChunkGenerator.stop()
    TextureStorage.destroyAll()
    MeshStorage.destroyAll()
    ShaderStorage.destroyAll()
    Window.destroy()
}


