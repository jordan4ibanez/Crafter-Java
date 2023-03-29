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

    // All fields utilize RADIANS
    private static float FOV = 72.0f;

    private static final float zNear = 0.01f;

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
                .rotateY(rotation.y)
                .rotateZ(rotation.z);

        ShaderStorage.setUniform("cameraMatrix", cameraMatrix);
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
