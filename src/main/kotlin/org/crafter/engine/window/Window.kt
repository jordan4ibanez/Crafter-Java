@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package org.crafter.engine.window

import org.crafter.engine.controls.Keyboard
import org.crafter.engine.controls.Keyboard.pollMemory
import org.crafter.engine.controls.Keyboard.pollQuitHack
import org.crafter.engine.controls.Mouse
import org.crafter.engine.controls.Mouse.poll
import org.crafter.engine.delta.Delta.calculateDelta
import org.crafter.engine.delta.Delta.delta
import org.crafter.engine.gui.components.GUIElement
import org.joml.Vector2f
import org.joml.Vector2i
import org.joml.Vector3f
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.*
import org.lwjgl.system.Callback
import org.lwjgl.system.MemoryUtil

/**
 * Window interops with OpenGL 4.1 and GLFW 3.3.
 * There can be only 1 Window in this game. So it's a final class with static methods
 */
object Window {
    // This is for internal usage only, to stop this class from becoming a MEGA class
    // Pointer to the window memory address
    var windowPointer: Long = 0
        private set

    // The debugging process callbacks
    private var debugCallback: Callback? = null
    private var _wasResized = false
    private val clearColor = Vector3f()
    private val monitorSize = Vector2i()
    private val windowSize = Vector2i()
    private var title: String? = null
    var framesPerSecond = 0
        private set
    private var framesPerSecondAccumulator = 0
    private var fpsTimeAccumulator = 1.0f
    private var framesPerSecondUpdate = false

    // Create the window
    fun initialize() {
        // Using default callback
        GLFWErrorCallback.createPrint(System.err).set()

        // Now initialize glfw
        if (!GLFW.glfwInit()) {
            throw RuntimeException("Window: Failed to initialize GLFW!")
        }

        // Let's just make sure the defaults are there
        GLFW.glfwDefaultWindowHints()

        // Enable OpenGL debugging
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE)
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE)
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4)
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 1)

        // FORCE core 4.1 minimum, but allow driver optimizations
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE)
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE)

        // Passes the monitor size data into itself
        getMonitorSize()

        // Now we can automatically use that to set the initial window size before any C call is called
        windowSize.x = monitorSize.x / 2
        windowSize.y = monitorSize.y / 2
        windowPointer = GLFW.glfwCreateWindow(windowSize.x, windowSize.y, "", MemoryUtil.NULL, MemoryUtil.NULL)

        // Uh oh
        if (windowPointer == MemoryUtil.NULL) {
            throw RuntimeException("Window: Failed to create the GLFW window!")
        }

        // Enable Mouse's static class
        Mouse.initialize()

        // Enable Keyboard's static class
        Keyboard.initialize()

        // Fancy frame buffer callback - called when window is resized
        GLFW.glfwSetFramebufferSizeCallback(windowPointer) { window: Long, width: Int, height: Int ->
            windowSize.x = width
            windowSize.y = height
            _wasResized = true
            GL11.glViewport(0, 0, width, height)
        }

        // Now center the window
        GLFW.glfwSetWindowPos(
            windowPointer,
            (monitorSize.x - windowSize.x) / 2,
            (monitorSize.y - windowSize.y) / 2
        )
        GLFW.glfwMakeContextCurrent(windowPointer)
        GLFW.glfwSwapInterval(1)
        GLFW.glfwShowWindow(windowPointer)
        startOpenGL()
        GUIElement.recalculateGUIScale()
    }

    private fun startOpenGL() {
        GL.createCapabilities()
        println(GL11.glGetString(GL11.GL_VERSION))
        GL11.glClearColor(clearColor.x, clearColor.y, clearColor.z, 1.0f)

        // Thanks, TheChubu!
        val callback = GLUtil.setupDebugMessageCallback()
        GL11.glEnable(KHRDebug.GL_DEBUG_OUTPUT_SYNCHRONOUS)
        debugCallback = GLUtil.setupDebugMessageCallback()

        // Alpha color blending
        GL11.glEnable(GL11.GL_BLEND)
        GL14.glBlendEquation(GL14.GL_FUNC_ADD)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE)

        // Wireframe mode for debugging polygons
//        glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );

        // Enable depth testing
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glDepthFunc(GL11.GL_LESS)
        GL11.glEnable(GL11.GL_BLEND)
        val cull = true
        if (cull) {
            GL11.glEnable(GL11.GL_CULL_FACE)
        } else {
            GL11.glDisable(GL11.GL_CULL_FACE)
        }
    }

    // Destroy the window
    fun destroy() {

        // Now clean the C memory up
        Callbacks.glfwFreeCallbacks(windowPointer)
        GLFW.glfwDestroyWindow(windowPointer)
        GLFW.glfwTerminate()
        GLFW.glfwSetErrorCallback(null)!!.free()
    }

    // I think this one is pretty obvious
    fun shouldClose(): Boolean {
        return GLFW.glfwWindowShouldClose(windowPointer)
    }

    @JvmStatic
    fun wasResized(): Boolean {
        return _wasResized
    }

    fun swapBuffers() {
        GLFW.glfwSwapBuffers(windowPointer)
        _wasResized = false
    }

    fun pollEvents() {
        // Delta and PollMemory are special cases from what is said below
        calculateDelta()
        calculateFPS()

        // Fixme: If having the FPS in the window title is annoying, remove this
        autoInjectFPSIntoWindowTitle()
        pollMemory()

        // Remember: glfwPollEvents must go first before other calls, or everything else Mouse and Keyboard WILL break (order of operations)
        GLFW.glfwPollEvents()
        poll()
        pollQuitHack()

        // Integrate the clearing of the frame buffer in here because otherwise it gets messy
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
        /*
         The rest is D code:

         pollMouse();
         calculateFPS();
        */
    }

    private fun calculateFPS() {
        framesPerSecondUpdate = false
        fpsTimeAccumulator += delta
        if (fpsTimeAccumulator >= 1.0f) {
            fpsTimeAccumulator -= 1.0f
            framesPerSecond = framesPerSecondAccumulator
            framesPerSecondUpdate = true
            framesPerSecondAccumulator = 0
        }
        framesPerSecondAccumulator++
    }

    fun framePerSecondUpdate(): Boolean {
        return framesPerSecondUpdate
    }

    /**
     * This is a bolt on for setting the FPS in the window title automatically
     */
    private fun autoInjectFPSIntoWindowTitle() {
        if (framePerSecondUpdate()) {
            setTitle(getTitle() + " | FPS: " + framesPerSecond)
        }
    }

    @JvmStatic
    fun getWindowSize(): Vector2f {
        return Vector2f(windowSize)
    }

    val windowCenter: Vector2f
        /**
         * This is a float because it's a literal window center. Floaty.
         */
        get() = Vector2f(windowWidth / 2.0f, windowHeight / 2.0f)
    val windowWidth: Int
        get() = windowSize.x
    val windowHeight: Int
        get() = windowSize.y
    val windowCenterX: Float
        get() = windowSize.x / 2.0f
    val windowCenterY: Float
        get() = windowSize.y / 2.0f

    // RGB version of setting clear color
    fun setClearColor(r: Float, g: Float, b: Float) {
        clearColor.x = r
        clearColor.y = g
        clearColor.z = b
        GL11.glClearColor(r, g, b, 1.0f)
    }

    // 1D (black to white) setting clear color
    fun setClearColor(intensity: Float) {
        clearColor.x = intensity
        clearColor.y = intensity
        clearColor.z = intensity
        GL11.glClearColor(intensity, intensity, intensity, 1.0f)
    }

    /**
     * Simple setter for updating the window title components
     */
    fun setTitle(newTitle: String?) {
        setTitle(newTitle, false)
    }

    fun setTitle(newTitle: String?, storeNewTitle: Boolean) {
        if (storeNewTitle) {
            title = newTitle
        }
        GLFW.glfwSetWindowTitle(windowPointer, newTitle)
    }

    fun getTitle(): String? {
        return title
    }

    fun clearAll() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
    }

    @JvmStatic
    fun clearDepthBuffer() {
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
    }

    val aspectRatio: Float
        get() = windowSize.x.toFloat() / windowSize.y.toFloat()

    private fun getMonitorSize() {
        val videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())
            ?: throw RuntimeException("Window: Error, your monitor returned a NULL video mode!")
        monitorSize.x = videoMode.width()
        monitorSize.y = videoMode.height()
    }

    fun maximize() {
        GLFW.glfwMaximizeWindow(windowPointer)
    }

    val isFocused: Boolean
        get() = GLFW.glfwGetWindowAttrib(windowPointer, GLFW.GLFW_FOCUSED) == 1

    fun setVsync(onOrOff: Boolean) {
        GLFW.glfwSwapInterval(if (onOrOff) 1 else 0)
    }

    fun close() {
        GLFW.glfwSetWindowShouldClose(windowPointer, true)
    }
}
