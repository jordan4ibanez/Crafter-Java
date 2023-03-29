package org.crafter.engine.camera;

import org.crafter.engine.shader.ShaderStorage;
import org.crafter.engine.window.Window;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

/**
 * For now - There can only be one camera.
 * In the future - Perhaps this will be a generic object
 * with a CameraStorage static class to accompany it.
 */
public final class Camera {

    // Important note: -Z is facing forwards

    // All fields utilize RADIANS
    private static float FOV = (float)Math.toRadians(60.0);

    private static final float zNear = 0.1f;

    private static final float zFar = 1000.0f;

    private static final Matrix4f cameraMatrix = new Matrix4f();

    private static final Matrix4f objectMatrix = new Matrix4f();

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

    // This updates the objectMatrix exactly as you tell it to
    public static void setObjectMatrix(Vector3f objectPosition, Vector3f objectRotation, Vector3f objectScale) {
        System.out.println(rotation + " | " + position);
        objectMatrix
                .identity()
                .translate(
                        position.x - objectPosition.x,
                        position.y - objectPosition.y,
                        position.z - objectPosition.z
                )
                .rotateY(-objectRotation.y)
                .rotateX(-objectRotation.x)
                .rotateZ(-objectRotation.z)
                .scale(objectScale);

        ShaderStorage.setUniform("objectMatrix", objectMatrix);
    }

    public static void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public static void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    public static void setFOV(float newFOV) {
        FOV = newFOV;
    }






}
