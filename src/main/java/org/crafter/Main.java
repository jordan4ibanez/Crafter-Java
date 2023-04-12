package org.crafter;

import org.crafter.engine.texture.texture_packer.WorldAtlasInitializer;
import org.crafter.engine.gui.font.Font;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.shader.ShaderStorage;
import org.crafter.engine.texture.TextureStorage;
import org.crafter.engine.window.Window;

public class Main {

    private static final String DEVELOPMENT_CYCLE = "Pre-Alpha";
    private static final String VERSION = "v0.0.2";
    private static final String VERSION_INFO = "Crafter " + DEVELOPMENT_CYCLE + " " + VERSION;
    private static final boolean PROTOTYPE_BUILD = true;

    private static String getVersionInfo() {
        return VERSION_INFO + (PROTOTYPE_BUILD ? " (Prototype Build)" : "");
    }

    public static void main(String[] args) {

        Window.initialize();
        Window.setTitle(getVersionInfo(), true);
        WorldAtlasInitializer.initializeWorldBlockTextures();


//        ShaderStorage.createShader("3d", "shaders/3d_vertex.vert", "shaders/3d_fragment.frag");
//        ShaderStorage.createUniform("3d", new String[]{"cameraMatrix", "objectMatrix"});
//
//        ShaderStorage.createShader("2d", "shaders/2d_vertex.vert", "shaders/2d_fragment.frag");
//        ShaderStorage.createUniform("2d", new String[]{"cameraMatrix", "objectMatrix"});

        Font.createFont("fonts/totally_original", "mc", true);
        Font.setShadowOffset(0.75f,0.75f);

        Window.setClearColor(0.75f);



        while(Window.shouldClose()) {
            Window.pollEvents();

            Window.clearAll();

            Window.swapBuffers();

        }

        TextureStorage.destroyAll();
        MeshStorage.destroyAll();
        ShaderStorage.destroyAll();

        Window.destroy();
    }
}