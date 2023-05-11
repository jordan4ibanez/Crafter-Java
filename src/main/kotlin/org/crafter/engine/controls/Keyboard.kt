package org.crafter.engine.controls

import org.crafter.engine.window.Window
import org.lwjgl.glfw.GLFW
import java.util.*

object Keyboard {
    // Starts of as a C line terminator
    // Might need to make this a linked list, then reconstruct into array when polled, then clear
    private var lastKey = '\u0000'

    // This will need to warm up, but it will quickly build itself down to O(1) notation
    private val currentMap = HashMap<Int, Boolean>()
    private val memoryMap = HashMap<Int, Boolean?>()

    // This is needed to utilize memory, it needs to poll right after initial value set because GLFW_PRESS delays before GLFW_REPEAT
    private val memoryFlush: Queue<Int> = LinkedList()
    @JvmStatic
    fun initialize() {
        GLFW.glfwSetCharCallback(Window.getWindowPointer()) { window: Long, codePoint: Int ->
            lastKey = codePoint.toChar()
        }
        GLFW.glfwSetKeyCallback(Window.getWindowPointer()) { window: Long, key: Int, scancode: Int, action: Int, mods: Int ->
            if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT) {
                setMemory(key)
                setCurrent(key, true)
                memoryFlush.add(key)
            } else if (action == GLFW.GLFW_RELEASE) {
                setCurrent(key, false)
                setMemory(key)
            }
        }
    }

    private fun setCurrent(key: Int, action: Boolean) {
        currentMap[key] = action
    }

    private fun setMemory(key: Int) {
        if (!currentMap.containsKey(key)) {
            memoryMap[key] = false
            return
        }
        memoryMap[key] = currentMap[key]
    }

    @JvmStatic
    fun pollMemory() {
        while (!memoryFlush.isEmpty()) {
            setMemory(memoryFlush.remove())
        }
    }

    /**
     * (FIXME) Remember: Remove this
     */
    @JvmStatic
    fun pollQuitHack() {
        if (keyPressed(GLFW.GLFW_KEY_ESCAPE)) {
            Window.close()
        }
    }

    @JvmStatic
    fun hasTyped(): Boolean {
        return lastKey != '\u0000'
    }

    @JvmStatic
    val lastInput: Char
        get() {
            if (!hasTyped()) {
                throw RuntimeException("Keyboard: You MUST check that the player has typed! Use hasTyped() before calling this!")
            }
            val gottenChar = lastKey
            lastKey = '\u0000'
            return gottenChar
        }

    // IsDown doesn't need memory, just if the key is held down
    @JvmStatic
    fun keyDown(key: Int): Boolean {
        return getCurrent(key)
    }

    // KeyPressed needs memory, only true in state on initial state change
    @JvmStatic
    fun keyPressed(key: Int): Boolean {
        return getCurrent(key) && !getMemory(key)
    }

    private fun getCurrent(key: Int): Boolean {
        val keyValue = currentMap[key]
        if (keyValue != null) {
            return keyValue
        }
        // Default: put in a false value
        currentMap[key] = false
        return false
    }

    private fun getMemory(key: Int): Boolean {
        val keyValue = memoryMap[key]
        if (keyValue != null) {
            return keyValue
        }
        // Default: put in a false value
        memoryMap[key] = false
        return false
    }
}
