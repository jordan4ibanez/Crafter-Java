package org.crafter.engine.camera

import org.crafter.engine.shader.ShaderStorage
import org.crafter.engine.utility.GameMath
import org.crafter.engine.window.Window
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector3fc

/**
 * For now - There can only be one camera.
 * In the future - Perhaps this will be a generic object
 * with a CameraStorage static class to accompany it.
 * Camera notes:
 * camera pitch is limited to -90 to 90 degrees pitch, -180 to 180 yaw. This is to keep precision!
 */
object Camera {
    // Important note: -Z is facing forwards
    // Important note: Only expose internals as readonly
    // All fields utilize RADIANS
    private val PIHalf_f = GameMath.getPIHalf_f()
    private val PI2 = GameMath.getPi2()

    // This is a simple calculation to make the sensitivity number applicable to rotating the camera
    val sensitivity = 500.0f
        get() = 1.0f / field

    private var FOV = Math.toRadians(60.0).toFloat()
    private const val zNear = 0.1f
    private const val zFar = 1000.0f
    private val cameraMatrix = Matrix4f()
    private val guiCameraMatrix = Matrix4f()
    private val objectMatrix = Matrix4f()
    private val guiObjectMatrix = Matrix4f()
    private val position = Vector3f()
    private val rotation = Vector3f()

    // This automatically updates the cameraMatrix uniform in the currently selected shader
    fun updateCameraMatrix() {
        cameraMatrix
            .identity()
            .perspective(FOV, Window.getAspectRatio(), zNear, zFar)
            .rotateX(rotation.x)
            .rotateY(rotation.y)
        ShaderStorage.setUniform("cameraMatrix", cameraMatrix)
    }

    // This automatically updates the 2dCameraMatrix
    @JvmStatic
    fun updateGuiCameraMatrix() {
        val windowWidth = Window.getWindowWidth().toFloat()
        val windowHeight = Window.getWindowHeight().toFloat()
        guiCameraMatrix
            .identity() // Top left is the base position, like Love2D
            .setOrtho2D(0f, windowWidth, windowHeight, 0f)
        ShaderStorage.setUniform("cameraMatrix", guiCameraMatrix)
    }

    // This updates the objectMatrix exactly as you tell it to
    @JvmStatic
    fun setObjectMatrix(objectPosition: Vector3f, objectRotation: Vector3f, objectScale: Vector3f?) {
        objectMatrix
            .identity()
            .translate(
                objectPosition.x - position.x,
                objectPosition.y - position.y,
                objectPosition.z - position.z
            )
            .rotateY(-objectRotation.y)
            .rotateX(-objectRotation.x)
            .rotateZ(-objectRotation.z)
            .scale(objectScale)
        ShaderStorage.setUniform("objectMatrix", objectMatrix)
    }

    @JvmStatic
    fun setGuiObjectMatrix(posX: Float, posY: Float) {
        setGuiObjectMatrix(posX, posY, 1f, 1f)
    }

    @JvmStatic
    fun setGuiObjectMatrix(posX: Float, posY: Float, scaleX: Float, scaleY: Float) {
        guiObjectMatrix
            .identity()
            .translate(posX, posY, 0f)
            .scale(scaleX, scaleY, 1f)
        ShaderStorage.setUniform("objectMatrix", guiObjectMatrix)
    }

    fun setPosition(x: Float, y: Float, z: Float) {
        position.x = x
        position.y = y
        position.z = z
    }

    fun setPosition(newPosition: Vector3fc?) {
        position.set(newPosition)
        pitchLock()
    }

    fun setRotation(x: Float, y: Float, z: Float) {
        rotation.x = x
        rotation.y = y
        rotation.z = z
        axesLock()
    }

    fun setRotation(newRotation: Vector3fc?) {
        rotation.set(newRotation)
        axesLock()
    }

    private fun axesLock() {
        pitchLock()
        yawLock()
    }

    private fun pitchLock() {
        if (rotation.x > PIHalf_f) {
            rotation.x = PIHalf_f
        } else if (rotation.x < -PIHalf_f) {
            rotation.x = -PIHalf_f
        }
    }

    private fun yawLock() {
        if (rotation.y > org.joml.Math.PI) {
            rotation.y -= PI2
            //            System.out.println("overflow" + Math.random());
        } else if (rotation.y < -org.joml.Math.PI) {
            rotation.y += PI2
            //            System.out.println("underflow" + Math.random());
        }
    }

    fun setFOV(newFOV: Float) {
        FOV = newFOV
    }

    fun getPosition(): Vector3fc {
        return position
    }

    fun getRotation(): Vector3fc {
        return rotation
    }

    val yaw: Float
        get() = rotation.y()
    val pitch: Float
        get() = rotation.x()
    val roll: Float
        get() = rotation.z()
}
