package org.crafter.engine.controls

import org.crafter.engine.window.Window
import org.joml.Vector2f
import org.joml.Vector2fc
import org.lwjgl.glfw.GLFW

object Mouse {
    private val position = Vector2f(-1f, -1f)
    private val oldPosition = Vector2f(-1f, -1f)
    private val delta = Vector2f(0f, 0f)
    private var leftClick = false
    private var leftHeld = false
    private var leftWasHeld = false
    private var rightClick = false
    private var rightHeld = false
    private var rightWasHeld = false
    private var needsDeltaReset = true
    @JvmStatic
    fun initialize() {
        if (GLFW.glfwRawMouseMotionSupported()) {
            GLFW.glfwSetInputMode(Window.pointer, GLFW.GLFW_RAW_MOUSE_MOTION, GLFW.GLFW_TRUE)
        }

        // This causes problems on X11 and Wayland - Todo: Test on Windows - Mouse jolt!
        GLFW.glfwSetCursorPosCallback(Window.pointer) { windowPointer: Long, xPos: Double, yPos: Double ->
            position[xPos] = yPos
        }
        GLFW.glfwSetCursorEnterCallback(Window.pointer) { windowPointer: Long, entered: Boolean ->
            // Only resetting to -1 when the mouse leaves
            if (!entered) {
                println("Mouse: RESETTING TO -1 -1!")
                position[-1f] = -1f
            }
        }
    }

    /**
     * This needs to be polled like this to simultaneously poll mouse button down & held.
     * This is automatically called in Window.pollEvents()
     */
    @JvmStatic
    fun poll() {
        calculateDeltaWhenCaptured()
        val leftButtonState = GLFW.glfwGetMouseButton(Window.pointer, GLFW.GLFW_MOUSE_BUTTON_LEFT)
        val rightButtonState = GLFW.glfwGetMouseButton(Window.pointer, GLFW.GLFW_MOUSE_BUTTON_RIGHT)
        if (leftButtonState == GLFW.GLFW_PRESS) {
            leftHeld = true
            leftClick = !leftWasHeld
            leftWasHeld = true
        } else if (leftButtonState == GLFW.GLFW_RELEASE) {
            leftClick = false
            leftHeld = false
            leftWasHeld = false
        }
        if (rightButtonState == GLFW.GLFW_PRESS) {
            rightHeld = true
            rightClick = !rightWasHeld
            rightWasHeld = true
        } else if (rightButtonState == GLFW.GLFW_RELEASE) {
            rightClick = false
            rightHeld = false
            rightWasHeld = false
        }
    }

    private fun calculateDeltaWhenCaptured() {
        // If it needs a reset, this will automatically ignore the delta calculation and zero it out
        if (isCaptured) {
            // Only calculated if mouse is captured
            if (!needsDeltaReset) {
                getPosition().sub(oldPosition, delta)
            } else {
                doReset()
            }
            oldPosition.set(position)
        }
    }

    @JvmStatic
    fun leftClick(): Boolean {
        return leftClick
    }

    fun rightClick(): Boolean {
        return rightClick
    }

    fun leftHeld(): Boolean {
        return leftHeld
    }

    fun rightHeld(): Boolean {
        return rightHeld
    }

    @JvmStatic
    fun getPosition(): Vector2fc {
        return position
    }

    fun getDelta(): Vector2fc {
        return delta
    }

    fun capture() {
        GLFW.glfwSetInputMode(Window.pointer, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED)
        enableReset()
    }

    fun release() {
        GLFW.glfwSetInputMode(Window.pointer, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL)
        enableReset()
    }

    val isCaptured: Boolean
        get() = GLFW.glfwGetInputMode(Window.pointer, GLFW.GLFW_CURSOR) == GLFW.GLFW_CURSOR_DISABLED

    /**
     * These two are very basic gates to allow this to be more readable in english.
     */
    private fun enableReset() {
        needsDeltaReset = true
    }

    private fun doReset() {
        delta.zero()
        GLFW.glfwSetCursorPos(Window.pointer, 0.0, 0.0)
        needsDeltaReset = false
    }
}
