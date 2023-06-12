package org.crafter.engine.controls;

import org.crafter.engine.window.Window;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.nio.DoubleBuffer;

import static org.crafter.engine.utility.GameMath.printVector;
import static org.lwjgl.glfw.GLFW.*;

public final class Mouse {
    private static final Vector2f position = new Vector2f(-1,-1);
    private static final Vector2f oldPosition = new Vector2f(-1, -1);
    private static final Vector2f delta = new Vector2f(0,0);

    private static boolean leftClick = false;
    private static boolean leftHeld = false;
    private static boolean leftWasHeld = false;

    private static boolean rightClick = false;
    private static boolean rightHeld = false;
    private static boolean rightWasHeld = false;

    private static boolean needsDeltaReset = true;

    private Mouse(){}

    public static void initialize() {

        if (glfwRawMouseMotionSupported()) {
            glfwSetInputMode(Window.getWindowPointer(), GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);
        }

        // This causes problems on X11 and Wayland - Todo: Test on Windows - Mouse jolt!
        glfwSetCursorPosCallback(Window.getWindowPointer(),(windowPointer, xPos, yPos) -> {
            position.set(xPos, yPos);
        });

        glfwSetCursorEnterCallback(Window.getWindowPointer(), (windowPointer, entered) -> {
            // Only resetting to -1 when the mouse leaves
            if (!entered) {
                System.out.println("Mouse: RESETTING TO -1 -1!");
                position.set(-1, -1);
            }
        });
    }

    /**
     * This needs to be polled like this to simultaneously poll mouse button down & held.
     * This is automatically called in Window.pollEvents()
     */
    public static void poll() {

        calculateDeltaWhenCaptured();

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
    private static void calculateDeltaWhenCaptured() {
        // If it needs a reset, this will automatically ignore the delta calculation and zero it out
        if (isCaptured()) {
            // Only calculated if mouse is captured
            if (!needsDeltaReset) {
                getPosition().sub(oldPosition, delta);
            } else {
                doReset();
            }
            oldPosition.set(position);
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

    public static Vector2fc getPosition() {
        return position;
    }

    public static Vector2fc getDelta() {
        return delta;
    }


    public static void capture() {
        glfwSetInputMode(Window.getWindowPointer(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        enableReset();
    }

    public static void release() {
        glfwSetInputMode(Window.getWindowPointer(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        enableReset();
    }

    public static boolean isCaptured() {
        return glfwGetInputMode(Window.getWindowPointer(), GLFW_CURSOR) == GLFW_CURSOR_DISABLED;
    }


    /**
     * These two are very basic gates to allow this to be more readable in english.
     */
    private static void enableReset() {
        needsDeltaReset = true;
    }
    private static void doReset() {
        delta.zero();
        glfwSetCursorPos(Window.getWindowPointer(), 0, 0);
        needsDeltaReset = false;
    }
}
