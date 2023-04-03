package org.crafter.engine.controls;

import org.crafter.engine.window.Window;

import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.*;

public final class Keyboard {

    // Starts of as a C line terminator
    // Might need to make this a linked list, then reconstruct into array when polled, then clear
    private static char lastKey = '\0';

    // This will need to warm up, but it will quickly build itself down to O(1) notation
    private final static HashMap<Integer, Integer> keyInputMap = new HashMap<>();

    private Keyboard(){}

    public static void initialize() {
        glfwSetCharCallback(Window.getWindowPointer(), (window, codePoint) ->{
            lastKey = (char)codePoint;
        });

        glfwSetKeyCallback(Window.getWindowPointer(), (window, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS || action == GLFW_RELEASE) {
                // We want this to overwrite existing values
                keyInputMap.put(key, action);
            }
        });
    }

    /**
     * (FIXME) Remember: Remove this
     */
    public static void pollQuitHack() {
        if (isKeyDown(GLFW_KEY_ESCAPE)) {
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

    public static boolean isKeyDown(int key) {
        return getKey(key) > 0;
    }

    private static int getKey(int key) {
        Integer keyValue = keyInputMap.get(key);
        if (keyValue != null) {
            return keyValue;
        }
        // Default: put in a false value
        keyInputMap.put(key, 0);
        return 0;
    }

}
