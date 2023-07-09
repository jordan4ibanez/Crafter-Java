/*
 Crafter - A blocky game (engine) written in Java with LWJGL.
 Copyright (C) 2023  jordan4ibanez

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.crafter.engine.window;

import org.crafter.engine.controls.Keyboard;
import org.crafter.engine.controls.Mouse;
import org.crafter.engine.delta.Delta;
import org.crafter.engine.gui.components.GUIElement;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.KHRDebug;
import org.lwjgl.system.Callback;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GLUtil.setupDebugMessageCallback;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Window interops with OpenGL 4.1 and GLFW 3.3.
 * There can be only 1 Window in this game. So it's a final class with static methods
 */
public final class Window {

    // Pointer to the window memory address
    private static long window;

    // The debugging process callbacks
    private static Callback debugCallback;

    private static boolean _wasResized = false;


    private static final Vector3f clearColor = new Vector3f();

    private static final Vector2i monitorSize = new Vector2i();

    private static final Vector2i windowSize = new Vector2i();

    private static String title;

    private static int framesPerSecond = 0;
    private static int framesPerSecondAccumulator = 0;
    private static float fpsTimeAccumulator = 1.0f;
    private static boolean framesPerSecondUpdate = false;


    // Disallow instantiation of this class
    private Window() {}

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

        // Enable OpenGL debugging
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);

        // FORCE core 4.1 minimum, but allow driver optimizations
        glfwWindowHint( GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE );
        glfwWindowHint( GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE );

        // Passes the monitor size data into itself
        getMonitorSize();

        // Now we can automatically use that to set the initial window size before any C call is called
        windowSize.x = monitorSize.x / 2;
        windowSize.y = monitorSize.y / 2;

        window = glfwCreateWindow(windowSize.x, windowSize.y, "", NULL, NULL);

        // Uh oh
        if (window == NULL) {
            throw new RuntimeException("Window: Failed to create the GLFW window!");
        }

        // Enable Mouse's static class
        Mouse.initialize();

        // Enable Keyboard's static class
        Keyboard.initialize();

        // Fancy frame buffer callback - called when window is resized
        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            windowSize.x = width;
            windowSize.y = height;
            _wasResized = true;
            glViewport(0,0, width, height);
        });

        // Now center the window
        glfwSetWindowPos(
            window,
            (monitorSize.x - windowSize.x) / 2,
            (monitorSize.y - windowSize.y) / 2
        );

        glfwMakeContextCurrent(window);

        glfwSwapInterval(1);

        glfwShowWindow(window);

        startOpenGL();

        GUIElement.recalculateGUIScale();
    }

    private static void startOpenGL() {
        createCapabilities();

        System.out.println(glGetString(GL_VERSION));

        glClearColor(clearColor.x, clearColor.y, clearColor.z, 1.0f);

        // Thanks, TheChubu!
        Callback callback = setupDebugMessageCallback();
        glEnable(KHRDebug.GL_DEBUG_OUTPUT_SYNCHRONOUS);
        debugCallback = setupDebugMessageCallback();

        // Alpha color blending
        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE);

        // Wireframe mode for debugging polygons
//        glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );

        // Enable depth testing
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);

        glEnable(GL_BLEND);

        final boolean cull = true;

        if (cull) {
            glEnable(GL_CULL_FACE);
        } else {
            glDisable(GL_CULL_FACE);
        }

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

    public static boolean wasResized() {
        return _wasResized;
    }

    public static void swapBuffers() {
        glfwSwapBuffers(window);
        _wasResized = false;
    }

    public static void pollEvents() {
        // Delta and PollMemory are special cases from what is said below
        Delta.calculateDelta();

        calculateFPS();

        // Fixme: If having the FPS in the window title is annoying, remove this
        autoInjectFPSIntoWindowTitle();

        Keyboard.pollMemory();

        // Remember: glfwPollEvents must go first before other calls, or everything else Mouse and Keyboard WILL break (order of operations)
        glfwPollEvents();

        Mouse.poll();

        Keyboard.pollQuitHack();

        // Integrate the clearing of the frame buffer in here because otherwise it gets messy
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        /*
         The rest is D code:

         pollMouse();
         calculateFPS();
        */
    }

    private static void calculateFPS() {
        framesPerSecondUpdate = false;
        fpsTimeAccumulator += Delta.getDelta();

        if (fpsTimeAccumulator >= 1.0f) {
            fpsTimeAccumulator -= 1.0f;
            framesPerSecond = framesPerSecondAccumulator;
            framesPerSecondUpdate = true;
            framesPerSecondAccumulator = 0;
        }
        framesPerSecondAccumulator++;
    }
    public static int getFramesPerSecond() {
        return framesPerSecond;
    }
    public static boolean framePerSecondUpdate() {
        return framesPerSecondUpdate;
    }

    /**
     * This is a bolt on for setting the FPS in the window title automatically
     */
    private static void autoInjectFPSIntoWindowTitle() {
        if (framePerSecondUpdate()) {
            setTitle(getTitle() + " | FPS: " + getFramesPerSecond());
        }
    }


    public static Vector2f getWindowSize() {
        return new Vector2f(windowSize);
    }

    /**
     * This is a float because it's a literal window center. Floaty.
     */
    public static Vector2f getWindowCenter() {
        return new Vector2f(getWindowWidth() / 2.0f, getWindowHeight() / 2.0f);
    }

    public static int getWindowWidth() {
        return windowSize.x;
    }
    public static int getWindowHeight() {
        return windowSize.y;
    }

    public static float getWindowCenterX() {
        return windowSize.x / 2.0f;
    }
    public static float getWindowCenterY() {
        return windowSize.y / 2.0f;
    }

    // RGB version of setting clear color
    public static void setClearColor(float r, float g, float b) {
        clearColor.x = r;
        clearColor.y = g;
        clearColor.z = b;

        glClearColor(r,g,b,1.0f);
    }

    // 1D (black to white) setting clear color
    public static void setClearColor(float intensity) {
        clearColor.x = intensity;
        clearColor.y = intensity;
        clearColor.z = intensity;

        glClearColor(intensity,intensity,intensity,1.0f);
    }

    /**
     * Simple setter for updating the window title components
     */
    public static void setTitle(String newTitle) {
        setTitle(newTitle, false);
    }
    public static void setTitle(String newTitle, boolean storeNewTitle) {
        if (storeNewTitle) {
            title = newTitle;
        }
        glfwSetWindowTitle(window, newTitle);
    }

    public static String getTitle() {
        return title;
    }

    public static void clearAll() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public static void clearDepthBuffer() {
        glClear(GL_DEPTH_BUFFER_BIT);
    }

    public static float getAspectRatio() {
        return (float)windowSize.x / (float)windowSize.y;
    }

    private static void getMonitorSize() {
        GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (videoMode == null) {
            throw new RuntimeException("Window: Error, your monitor returned a NULL video mode!");
        }
        monitorSize.x = videoMode.width();
        monitorSize.y = videoMode.height();
    }

    public static void maximize() {
        glfwMaximizeWindow(window);
    }

    public static void unMaximize() {
        glfwRestoreWindow(window);
    }

    public static void toggleMaximize() {
        if (isMaximized()) {
            unMaximize();
        } else {
            maximize();
        }
    }

    public static boolean isMaximized() {
        return glfwGetWindowAttrib(window, GLFW_MAXIMIZED) == 1;
    }

    public static boolean isFocused() {
        return glfwGetWindowAttrib(window, GLFW_FOCUSED) == 1;
    }

    public static void setVsync(boolean onOrOff) {
        glfwSwapInterval(onOrOff ? 1 : 0);
    }

    public static void close() {
        glfwSetWindowShouldClose(window, true);
    }

    // This is for internal usage only, to stop this class from becoming a MEGA class
    public static long getWindowPointer() {
        return window;
    }
}
