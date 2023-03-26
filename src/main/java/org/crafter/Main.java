package org.crafter;

import org.crafter.Engine.Window;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        Window.initialize();

        while(!Window.shouldClose()) {
            Window.pollEvents();

            System.out.println("hi");

            Window.swapBuffers();
        }

        Window.destroy();

    }
}