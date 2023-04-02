package org.crafter.engine.controls;

import org.crafter.engine.window.Window;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public final class Mouse {
    private static final Vector2f position = new Vector2f(-1,-1);

    private static boolean leftClick = false;
    private static boolean rightClick = false;

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

    public static void capture() {
        glfwSetInputMode(Window.getWindowPointer(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    public static void release() {
        glfwSetInputMode(Window.getWindowPointer(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    }
}
