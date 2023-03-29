package org.crafter;

import org.crafter.engine.camera.Camera;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.texture.TextureStorage;
import org.crafter.engine.window.Window;
import org.crafter.engine.shader.ShaderStorage;

public class Main {
    public static void main(String[] args) {

        Window.initialize();


        ShaderStorage.createShader("basic", "shaders/vertex.vert", "shaders/fragment.frag");
        ShaderStorage.createUniform("basic", new String[]{"cameraMatrix", "objectMatrix"});

        ShaderStorage.start("basic");

        Window.setClearColor(0.75f);


        while(!Window.shouldClose()) {

            Window.pollEvents();

            Window.clearAll();

            Camera.updateCameraMatrix();





            Window.swapBuffers();

        }

        TextureStorage.destroyAll();
        MeshStorage.destroyAll();
        ShaderStorage.destroyAll();

        Window.destroy();
    }
}