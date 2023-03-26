package org.crafter;

import org.crafter.engine.Window;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        Window.initialize();

        while(!Window.shouldClose()) {
            Window.pollEvents();

            Window.swapBuffers();
        }

        Window.destroy();

    }
}