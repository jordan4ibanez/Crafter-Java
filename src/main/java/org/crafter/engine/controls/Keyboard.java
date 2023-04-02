package org.crafter.engine.controls;

import org.crafter.engine.window.Window;

import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;

public final class Keyboard {

    // Starts of as a C line terminator
    private static char lastKey = '\0';

    private Keyboard(){}

    public static void initialize() {
        glfwSetCharCallback(Window.getWindowPointer(), (window, codePoint) ->{
            System.out.println(codePoint);
        });
    }


}
