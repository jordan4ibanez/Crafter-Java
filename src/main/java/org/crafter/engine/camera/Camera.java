package org.crafter.engine.camera;

import org.crafter.engine.controls.Keyboard;
import org.crafter.engine.controls.Mouse;
import org.crafter.engine.delta.Delta;
import org.crafter.engine.shader.ShaderStorage;
import org.crafter.engine.window.Window;
import org.joml.*;

import java.lang.Math;

import static org.crafter.engine.utility.GameMath.*;
import static org.joml.Math.PI;
import static org.lwjgl.glfw.GLFW.*;

/**
 * For now - There can only be one camera.
 * In the future - Perhaps this will be a generic object
 * with a CameraStorage static class to accompany it.
 * Camera notes:
 * camera pitch is limited to -90 to 90 degrees pitch, -180 to 180 yaw. This is to keep precision!
 */
public final class Camera {

    // Important note: -Z is facing forwards
    // Important note: Only expose internals as readonly

    // All fields utilize RADIANS

    private static final float PIHalf_f = getPIHalf_f();
    private static final float PI2 = getPi2();
    private static float sensitivity = 500.0f;
    private static float FOV = (float)Math.toRadians(60.0);

    private static final float zNear = 0.1f;

    private static final float zFar = 1000.0f;

    private static final Matrix4f cameraMatrix = new Matrix4f();

    private static final Matrix4f guiCameraMatrix = new Matrix4f();

    private static final Matrix4f objectMatrix = new Matrix4f();

    private static final Matrix4f guiObjectMatrix = new Matrix4f();

    private static final Vector3f position = new Vector3f(0,70,0);

    private static final Vector3f rotation = new Vector3f();

    private static final Vector3i inputMovement = new Vector3i();
    private static final Vector3f cameraMovementX = new Vector3f();
    private static final Vector3f cameraMovementY = new Vector3f();
    private static final Vector3f cameraMovementZ = new Vector3f();
    private static final Vector3f finalCameraMovement = new Vector3f();
    private static final Vector3f newCameraPosition = new Vector3f();
    private static final Vector3f cameraDelta = new Vector3f();
    private static final Vector3f newCameraRotation = new Vector3f();



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
        pitchLock();
    }

    public static void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
        axesLock();
    }
    public static void setRotation(Vector3fc newRotation) {
        rotation.set(newRotation);
        axesLock();
    }
    private static void axesLock() {
        pitchLock();
        yawLock();
    }
    private static void pitchLock() {
        if (rotation.x > PIHalf_f) {
            rotation.x = PIHalf_f;
        } else if (rotation.x < -PIHalf_f) {
            rotation.x = -PIHalf_f;
        }
    }
    private static void yawLock() {
        if (rotation.y > PI) {
            rotation.y -= PI2;
//            System.out.println("overflow" + Math.random());
        } else if (rotation.y < -PI) {
            rotation.y += PI2;
//            System.out.println("underflow" + Math.random());
        }
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

    //todo: This is an ultra hack which should definitely be a state of the camera if it has a first person control system.
    // make it so that it's controlled with a boolean or number or some poop
    // ALSO make this pure functional where camera has one state call and it will automatically go between methods based on the state

    public static void freeCam() {
        // FIXME: BEGIN CAMERA INPUT DEBUGGING

        // Rotation
        Vector2fc mouseDelta = Mouse.getDelta();
        // Very, very important note: Notice that x & y are swapped. Because the window 2d matrix is 90 degrees rotated from the 3d matrix!
        cameraDelta.set(mouseDelta.y(), mouseDelta.x(), 0).mul(Camera.getSensitivity());
        Camera.getRotation().add(cameraDelta, newCameraRotation);
        Camera.setRotation(newCameraRotation);

        // newCameraRotation is now used below

        // Movement

        inputMovement.zero();

        if (Keyboard.keyDown(GLFW_KEY_W)) {
            inputMovement.z += -1;
        }
        if (Keyboard.keyDown(GLFW_KEY_S)) {
            inputMovement.z += 1;
        }

        if (Keyboard.keyDown(GLFW_KEY_A)) {
            inputMovement.x += -1;
        }
        if (Keyboard.keyDown(GLFW_KEY_D)) {
            inputMovement.x += 1;
        }

        if (Keyboard.keyDown(GLFW_KEY_SPACE)) {
            inputMovement.y += 1;
        }
        if (Keyboard.keyDown(GLFW_KEY_LEFT_SHIFT) || Keyboard.keyDown(GLFW_KEY_RIGHT_SHIFT)) {
            inputMovement.y -= 1;
        }


        final float yaw = newCameraRotation.y();
        final float movementDelta = Delta.getDelta() * 50;

        // Layered
        cameraMovementX.zero();
        cameraMovementY.zero();
        cameraMovementZ.zero();
        finalCameraMovement.zero();

        cameraMovementX.set(getHorizontalDirection(yawToLeft(yaw))).mul(inputMovement.x());
        cameraMovementY.set(0,inputMovement.y(), 0);
        cameraMovementZ.set(getHorizontalDirection(yaw)).mul(inputMovement.z());

        // Layer in, and then make it usable with delta
        finalCameraMovement.set(cameraMovementX.add(cameraMovementY).add(cameraMovementZ)).mul(movementDelta);

        Vector3fc cameraPosition = Camera.getPosition();
        cameraPosition.add(finalCameraMovement, newCameraPosition);
        Camera.setPosition(newCameraPosition);

        Camera.updateCameraMatrix();
    }


}
