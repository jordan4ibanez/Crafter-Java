package org.crafter.engine.camera;

import org.joml.Matrix4f;

/**
 * For now - There can only be one camera.
 * In the future - Perhaps this will be a generic object
 * with a CameraStorage static class to accompany it.
 */
public final class Camera {


    Matrix4f cameraMatrix = new Matrix4f();
    Matrix4f objectMatrix = new Matrix4f();

    private Camera(){};

    


}
