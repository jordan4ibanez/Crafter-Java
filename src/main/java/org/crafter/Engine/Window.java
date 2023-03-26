package org.crafter.Engine;

import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

/**
 * Window interops with OpenGL 4.1 and GLFW 3.3.
 * There can be only 1 Window in this game. So it's a final class with static methods
 */
public final class Window {

    // Pointer to the window memory address
    private static long window;


    // Disallow instantiation of this class
    private Window() {}


    // Create the window
    public static void initialize() {
        // Using default callback
        GLFWErrorCallback.createPrint(System.err).set();


    }

    // Destroy the window
    public static void destroy() {

        // Now clean the C memory up
        glfwTerminate();
        glfwSetErrorCallback(null).free();


    }


}
