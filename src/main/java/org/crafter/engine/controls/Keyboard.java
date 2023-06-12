package org.crafter.engine.controls;

import org.crafter.engine.window.Window;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import static org.lwjgl.glfw.GLFW.*;

public final class Keyboard {

    // Starts of as a C line terminator
    // Might need to make this a linked list, then reconstruct into array when polled, then clear
    private static char lastKey = '\0';

    // This will need to warm up, but it will quickly build itself down to O(1) notation
    private final static HashMap<Integer, Boolean> currentMap = new HashMap<>();
    private final static HashMap<Integer, Boolean> memoryMap = new HashMap<>();

    // This is needed to utilize memory, it needs to poll right after initial value set because GLFW_PRESS delays before GLFW_REPEAT
    private final static Queue<Integer> memoryFlush = new LinkedList<>();

    private Keyboard(){}

    public static void initialize() {
        glfwSetCharCallback(Window.getWindowPointer(), (window, codePoint) ->{
            lastKey = (char)codePoint;
        });

        glfwSetKeyCallback(Window.getWindowPointer(), (window, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                setMemory(key);
                setCurrent(key, true);
                memoryFlush.add(key);
            } else if (action == GLFW_RELEASE) {
                setCurrent(key, false);
                setMemory(key);
            }
        });
    }

    private static void setCurrent(int key, boolean action) {
        currentMap.put(key, action);
    }
    private static void setMemory(int key) {
        if (!currentMap.containsKey(key)) {
            memoryMap.put(key, false);
            return;
        }
        memoryMap.put(key, currentMap.get(key));
    }

    public static void pollMemory() {
        while (!memoryFlush.isEmpty()) {
            setMemory(memoryFlush.remove());
        }
    }

    /**
     * (FIXME) Remember: Remove this
     */
    public static void pollQuitHack() {
        if (keyPressed(GLFW_KEY_ESCAPE)) {
            Window.close();
        }
    }

    public static boolean hasTyped() {
        return lastKey != '\0';
    }

    public static char getLastInput() {
        if (!hasTyped()) {
            throw new RuntimeException("Keyboard: You MUST check that the player has typed! Use hasTyped() before calling this!");
        }
        char gottenChar = lastKey;
        lastKey = '\0';
        return gottenChar;
    }

    // IsDown doesn't need memory, just if the key is held down
    public static boolean keyDown(int key) {
        return getCurrent(key);
    }

    // KeyPressed needs memory, only true in state on initial state change
    public static boolean keyPressed(int key) {
        return getCurrent(key) && !getMemory(key);
    }

    private static boolean getCurrent(int key) {
        Boolean keyValue = currentMap.get(key);
        if (keyValue != null) {
            return keyValue;
        }
        // Default: put in a false value
        currentMap.put(key, false);
        return false;
    }
    private static boolean getMemory(int key) {
        Boolean keyValue = memoryMap.get(key);
        if (keyValue != null) {
            return keyValue;
        }
        // Default: put in a false value
        memoryMap.put(key, false);
        return false;
    }

}
