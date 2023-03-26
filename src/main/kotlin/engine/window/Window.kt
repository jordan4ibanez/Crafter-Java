package engine.window

import org.joml.Vector3f
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GLUtil
import org.lwjgl.opengl.KHRDebug
import org.lwjgl.system.Callback
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil

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
        if (!GLFW.glfwInit()) {
            throw RuntimeException("Window: Failed to initialize GLFW!")
        }

        // Let's just make sure the defaults are there
        GLFW.glfwDefaultWindowHints()

        // Enable OpenGL debugging
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE)
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE)
        window = GLFW.glfwCreateWindow(300, 300, "Hello dere!", MemoryUtil.NULL, MemoryUtil.NULL)

        // Uh oh
        if (window == MemoryUtil.NULL) {
            throw RuntimeException("Window: Failed to create the GLFW window!")
        }

        // Fancy key callback
        @Suppress("unused")
        GLFW.glfwSetKeyCallback(window!!) { window: Long, key: Int, scancode: Int, action: Int, mods: Int ->
            println(
                "Ohai dere"
            )
        }
        MemoryStack.stackPush().use { stack ->

            // These are C pointers
            val windowWidth = stack.mallocInt(1)
            val windowHeight = stack.mallocInt(1)

            // Intake refs to these pointers (&windowWidth, etc)
            GLFW.glfwGetWindowSize(window!!, windowWidth, windowHeight)

            // Get the resolution of the primary monitor
            val videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())

            // Now we can finally center it! Woo
            GLFW.glfwSetWindowPos(
                window!!,
                (videoMode!!.width() - windowWidth[0]) / 2,
                (videoMode.height() - windowHeight[0]) / 2
            )
        }
        GLFW.glfwMakeContextCurrent(window!!)
        GLFW.glfwSwapInterval(1)
        GLFW.glfwShowWindow(window!!)
        GL.createCapabilities()
        GL11.glClearColor(clearColor.x, clearColor.y, clearColor.z, 1.0f)

        // Thanks, TheChubu!
        val callback = GLUtil.setupDebugMessageCallback()
        GL11.glEnable(KHRDebug.GL_DEBUG_OUTPUT_SYNCHRONOUS)
        debugCallback = GLUtil.setupDebugMessageCallback()
    }

    // Destroy the window
    fun destroy() {

        // Now clean the C memory up
        Callbacks.glfwFreeCallbacks(window!!)
        GLFW.glfwDestroyWindow(window!!)
        GLFW.glfwTerminate()
        GLFW.glfwSetErrorCallback(null)!!.free()
    }

    // I think this one is pretty obvious
    fun shouldClose(): Boolean {
        return GLFW.glfwWindowShouldClose(window!!)
    }

    fun swapBuffers() {
        GLFW.glfwSwapBuffers(window!!)
    }

    fun pollEvents() {
        GLFW.glfwPollEvents()

        // Integrate the clearing of the frame buffer in here because otherwise it gets messy
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
        /*
     The rest is D code:

     pollMouse();
     calculateFPS();
    */
    }
}