package engine.window

import org.joml.Vector3f
import org.lwjgl.glfw.Callbacks.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GLUtil
import org.lwjgl.opengl.KHRDebug.*
import org.lwjgl.system.Callback
import org.lwjgl.system.MemoryStack.*
import org.lwjgl.system.MemoryUtil.*

object Window {

    // Pointer to the window memory address
    private var window: Long? = null;

    // The debugging process callbacks
    private var debugCallback: Callback? = null

    private val clearColor = Vector3f(0f, 0f, 0f)


    // Create the window
    fun initialize() {
        // Using default callback
        GLFWErrorCallback.createPrint(System.err).set()

        // Now initialize glfw
        if (!glfwInit()) {
            throw RuntimeException("Window: Failed to initialize GLFW!")
        }

        // Let's just make sure the defaults are there
        glfwDefaultWindowHints()

        // Enable OpenGL debugging
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE)
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        window = glfwCreateWindow(300, 300, "Hello dere!", NULL, NULL)

        // Uh oh
        if (window == NULL) {
            throw RuntimeException("Window: Failed to create the GLFW window!")
        }

        // Fancy key callback
        @Suppress("unused")
        glfwSetKeyCallback(window!!, fun(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
            if (key == GLFW_KEY_ESCAPE) {
                print(key)
                this.close()
            }
        })



        stackPush().use { stack ->

            // These are C pointers
            val windowWidth = stack.mallocInt(1)
            val windowHeight = stack.mallocInt(1)

            // Intake refs to these pointers (&windowWidth, etc)
            glfwGetWindowSize(window!!, windowWidth, windowHeight)

            // Get the resolution of the primary monitor
            val videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor())

            // Now we can finally center it! Woo
            glfwSetWindowPos(
                window!!,
                (videoMode!!.width() - windowWidth[0]) / 2,
                (videoMode.height() - windowHeight[0]) / 2
            )
        }
        glfwMakeContextCurrent(window!!)
        glfwSwapInterval(1)
        glfwShowWindow(window!!)
        createCapabilities()
        glClearColor(clearColor.x, clearColor.y, clearColor.z, 1.0f)

        // Thanks, TheChubu!
        val callback = GLUtil.setupDebugMessageCallback()
        glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS)
        debugCallback = GLUtil.setupDebugMessageCallback()
    }

    // Destroy the window
    fun destroy() {

        // Now clean the C memory up
        glfwFreeCallbacks(window!!)
        glfwDestroyWindow(window!!)
        glfwTerminate()
        glfwSetErrorCallback(null)!!.free()
    }

    // I think this one is pretty obvious
    fun shouldClose(): Boolean {
        return glfwWindowShouldClose(window!!)
    }

    fun close() {
        glfwSetWindowShouldClose(window!!, true)
    }

    fun swapBuffers() {
        glfwSwapBuffers(window!!)
    }

    fun pollEvents() {
        glfwPollEvents()

        // Integrate the clearing of the frame buffer in here because otherwise it gets messy
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
        /*
     The rest is D code:

     pollMouse();
     calculateFPS();
    */
    }
}