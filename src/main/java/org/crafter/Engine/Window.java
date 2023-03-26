package org.crafter.Engine;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Window interops with OpenGL 4.1 and GLFW 3.3.
 * There can be only 1 Window in this game. So it's a final class with static methods
 */
public final class Window {

    // Pointer to the window memory address
    private static long window;

    // Disallow instantiation of this class
    private Window() {}

    private static Vector3f clearColor = new Vector3f(0,0,0);


    // Create the window
    public static void initialize() {
        // Using default callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Now initialize glfw
        if (!glfwInit()) {
            throw new RuntimeException("Window: Failed to initialize GLFW!");
        }

        // Let's just make sure the defaults are there
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(300, 300, "Hello dere!", NULL, NULL);

        // Uh oh
        if (window == NULL) {
            throw new RuntimeException("Window: Failed to create the GLFW window!");
        }

        // Fancy key callback
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            System.out.println("Ohai dere");
        });

        // This song and dance is to center the window
        try (MemoryStack stack = stackPush()) {

            // These are C pointers
            IntBuffer windowWidth  = stack.mallocInt(1);
            IntBuffer windowHeight = stack.mallocInt(1);

            // Intake refs to these pointers (&windowWidth, etc)
            glfwGetWindowSize(window, windowWidth, windowHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Now we can finally center it! Woo
            glfwSetWindowPos(
                    window,
                    (videoMode.width() - windowWidth.get(0)) / 2,
                    (videoMode.height() - windowHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(window);

        glfwSwapInterval(1);

        glfwShowWindow(window);

        createCapabilities();

        glClearColor(clearColor.x, clearColor.y, clearColor.z, 1.0f);

    }

    // Destroy the window
    public static void destroy() {

        // Now clean the C memory up
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();

    }

    // I think this one is pretty obvious
    public static boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    public static void swapBuffers() {
        glfwSwapBuffers(window);
    }

    public static void pollEvents() {
        glfwPollEvents();

        // Integrate the clearing of the frame buffer in here because otherwise it gets messy
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        /*
         The rest is D code:

         pollMouse();
         calculateFPS();
        */
    }


}
