package org.crafter.engine.camera;

import org.crafter.engine.shader.ShaderStorage;
import org.crafter.engine.window.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3ic;

/**
 * For now - There can only be one camera.
 * In the future - Perhaps this will be a generic object
 * with a CameraStorage static class to accompany it.
 */
public final class Camera {

    // Important note: -Z is facing forwards
    // Important note: Only expose internals as readonly

    // All fields utilize RADIANS
    private static float sensitivity = 100.0f;
    private static float FOV = (float)Math.toRadians(60.0);

    private static final float zNear = 0.1f;

    private static final float zFar = 1000.0f;

    private static final Matrix4f cameraMatrix = new Matrix4f();

    private static final Matrix4f guiCameraMatrix = new Matrix4f();

    private static final Matrix4f objectMatrix = new Matrix4f();

    private static final Matrix4f guiObjectMatrix = new Matrix4f();

    private static final Vector3f position = new Vector3f();

    private static final Vector3f rotation = new Vector3f();


    private Camera(){};

    // This automatically updates the cameraMatrix uniform in the currently selected shader
    public static void updateCameraMatrix() {
        cameraMatrix
                .identity()
                .perspective(FOV, Window.getAspectRatio(), zNear, zFar)
                .rotateX(rotation.x)
                .rotateY(rotation.y);

        ShaderStorage.setUniform("cameraMatrix", cameraMatrix);
    }

    // This automatically updates the 2dCameraMatrix
    public static void updateGuiCameraMatrix() {
        float windowWidth = (float)Window.getWindowWidth();
        float windowHeight = (float)Window.getWindowHeight();

        guiCameraMatrix
                .identity()
                // Top left is the base position, like Love2D
                .setOrtho2D(0, windowWidth, windowHeight, 0);

        ShaderStorage.setUniform("cameraMatrix", guiCameraMatrix);
    }

    // This updates the objectMatrix exactly as you tell it to
    public static void setObjectMatrix(Vector3f objectPosition, Vector3f objectRotation, Vector3f objectScale) {

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
                .scale(objectScale);

        ShaderStorage.setUniform("objectMatrix", objectMatrix);
    }

    public static void setGuiObjectMatrix(float posX, float posY) {
        setGuiObjectMatrix(posX, posY, 1, 1);
    }
    private static void setGuiObjectMatrix(float posX, float posY, float scaleX, float scaleY) {
        guiObjectMatrix
                .identity()
                .translate(posX, posY, 0)
                .scale(scaleX, scaleY, 1);
        ShaderStorage.setUniform("objectMatrix", guiObjectMatrix);
    }

    public static void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }
    public static void setPosition(Vector3fc newPosition) {
        position.set(newPosition);
    }

    public static void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }
    public static void setRotation(Vector3fc newRotation) {
        rotation.set(newRotation);
    }

    public static void setFOV(float newFOV) {
        FOV = newFOV;
    }

    public static Vector3fc getPosition() {
        return position;
    }

    public static Vector3fc getRotation() {
        return rotation;
    }

    public static float getYaw() {
        return rotation.y();
    }

    public static float getPitch() {
        return rotation.x();
    }

    public static float getRoll() {
        return rotation.z();
    }

    public static float getSensitivity() {
        // This is a simple calculation to make the sensitivity number applicable to rotating the camera
        return 1.0f / sensitivity;
    }


}
