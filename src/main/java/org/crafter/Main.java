package org.crafter;

import org.crafter.engine.window.Window;
import org.crafter.engine.shader.ShaderStorage;

import static org.lwjgl.opengl.GL11.*;

public class Main {
    public static void main(String[] args) {

        Window.initialize();


        ShaderStorage.createShader("basic", "shaders/vertex.vert", "shaders/fragment.frag");

        ShaderStorage.start("basic");




        while(!Window.shouldClose()) {
            Window.pollEvents();

            glDrawArrays(GL_TRIANGLES, 0, 9);
//            glDrawElements();

            Window.swapBuffers();
        }

        ShaderStorage.destroy();

        Window.destroy();
    }
}