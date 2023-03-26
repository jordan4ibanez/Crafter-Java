package org.crafter;

import org.crafter.engine.window.Window;
import org.crafter.engine.shader.ShaderStorage;

public class Main {
    public static void main(String[] args) {

        Window.initialize();


        ShaderStorage.createShader("basic", "shaders/vertex.vert", "shaders/fragment.frag");


        while(!Window.shouldClose()) {
            Window.pollEvents();

            Window.swapBuffers();
        }

        ShaderStorage.destroy();

        Window.destroy();
    }
}