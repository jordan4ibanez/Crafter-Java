package org.crafter.engine.controls;

import org.crafter.engine.window.Window;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public final class Mouse {
    private static final Vector2f position = new Vector2f(-1,-1);

    private static boolean leftClick = false;
    private static boolean leftHeld = false;
    private static boolean leftWasHeld = false;

    private static boolean rightClick = false;
    private static boolean rightHeld = false;
    private static boolean rightWasHeld = false;

    private Mouse(){}

    public static void initialize() {

        if (glfwRawMouseMotionSupported()) {
            glfwSetInputMode(Window.getWindowPointer(), GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);
        }

        glfwSetCursorPosCallback(Window.getWindowPointer(),(windowPointer, xPos, yPos) -> {
            position.set(xPos, yPos);
        });

        glfwSetCursorEnterCallback(Window.getWindowPointer(), (windowPointer, entered) -> {
            // Only resetting to -1 when the mouse leaves
            if (!entered) {
                position.set(-1, -1);
            }
        });
    }

    // This needs to be polled like this to simultaneously poll mouse button down & held
    public static void poll() {
        int leftButtonState = glfwGetMouseButton(Window.getWindowPointer(), GLFW_MOUSE_BUTTON_LEFT);
        int rightButtonState = glfwGetMouseButton(Window.getWindowPointer(), GLFW_MOUSE_BUTTON_RIGHT);

        if (leftButtonState == GLFW_PRESS) {
            leftHeld = true;
            leftClick = !leftWasHeld;
            leftWasHeld = true;
        } else if (leftButtonState == GLFW_RELEASE) {
            leftClick = false;
            leftHeld = false;
            leftWasHeld = false;
        }

        if (rightButtonState == GLFW_PRESS) {
            rightHeld = true;
            rightClick = !rightWasHeld;
            rightWasHeld = true;
        } else if (rightButtonState == GLFW_RELEASE) {
            rightClick = false;
            rightHeld = false;
            rightWasHeld = false;
        }
    }

    public static boolean leftClick() {
        return leftClick;
    }
    public static boolean rightClick() {
        return rightClick;
    }

    public static boolean leftHeld() {
        return leftHeld;
    }
    public static boolean rightHeld() {
        return rightHeld;
    }


    public static void capture() {
        glfwSetInputMode(Window.getWindowPointer(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    public static void release() {
        glfwSetInputMode(Window.getWindowPointer(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    }
}
