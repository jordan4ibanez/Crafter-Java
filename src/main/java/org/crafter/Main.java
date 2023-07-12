/*
 Crafter - A blocky game (engine) written in Java with LWJGL.
 Copyright (C) 2023  jordan4ibanez

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.crafter;

import org.crafter.engine.api.API;
import org.crafter.engine.camera.Camera;
import org.crafter.engine.controls.Keyboard;
import org.crafter.engine.controls.Mouse;
import org.crafter.engine.gui.font.Font;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.shader.ShaderStorage;
import org.crafter.engine.texture.TextureStorage;
import org.crafter.engine.window.Window;
import org.crafter.engine.world.chunk.ChunkStorage;
import org.crafter.engine.world_generation.ChunkThreadDirector;
import org.crafter.engine.world_generation.chunk_generation.ChunkGenerator;
import org.crafter.engine.world_generation.chunk_mesh_generation.ChunkMeshGenerator;
import org.joml.Vector2i;

import static org.crafter.game.entity.player.PlayerStorage.addNewPlayer;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F11;

public class Main {

    private static final String DEVELOPMENT_CYCLE = "Pre-Classic (Prototyping)";
    private static final String VERSION = "v0.0.4";
    private static final String VERSION_INFO = "Crafter " + DEVELOPMENT_CYCLE + " " + VERSION;
    private static final boolean PROTOTYPE_BUILD = true;

    private static String getVersionInfo() {
        return VERSION_INFO + (PROTOTYPE_BUILD ? " (Prototype Build)" : "");
    }

    // Fixme: This is only for debugging and prototyping, remove this eventually

    // private static final Random random = new Random(new Date().getTime()/1000);

    // Fixme: this is for Classic only :D (V This thing V)
    private static final int classicMapSize = 18;

    public static void main(String[] args) {

        initialize();

        try {
            while(!Window.shouldClose()) {
                mainLoop();
            }
        } catch (Exception e) {
            // Game must shut down external threads or it WILL hang
            ChunkMeshGenerator.stop();
            ChunkGenerator.stop();
            throw new RuntimeException(e);
        }

        destroy();

    }

    private static void initialize() {

        // This is all in a very specific order, if you re-arrange it, expect problems. :T

        Window.initialize();
        Window.setTitle(getVersionInfo(), true);

        API.initialize();

        ChunkGenerator.start();
        ChunkMeshGenerator.start();

        ShaderStorage.createShader("3d", "shaders/3d_vertex.vert", "shaders/3d_fragment.frag");
        ShaderStorage.createUniform("3d", new String[]{"cameraMatrix", "objectMatrix"});

        ShaderStorage.createShader("2d", "shaders/2d_vertex.vert", "shaders/2d_fragment.frag");
        ShaderStorage.createUniform("2d", new String[]{"cameraMatrix", "objectMatrix"});

        Font.createFont("fonts/totally_original", "mc", true);
        Font.setShadowOffset(0.75f,0.75f);

        Window.setClearColor(0.75f);
        Window.setVsync(false);
//        if (false) {
//            Window.maximize();
//        }
        Mouse.capture();

        classicChunkPayload();

        addNewPlayer("singleplayer", true);
    }

    private static void mainLoop() {



        Window.pollEvents();

        if (Keyboard.keyPressed(GLFW_KEY_F11)) {
            Window.toggleMaximize();
        }

        Window.clearAll();

        ShaderStorage.start("3d");


        //todo This is temp remove me plz
        Camera.freeCam();

        ChunkThreadDirector.runLogic();

        for (int x = -classicMapSize; x <= classicMapSize; x++) {
            for (int z = -classicMapSize; z <= classicMapSize; z++) {
                final Vector2i requestingPosition = new Vector2i(x,z);
                if (ChunkStorage.hasPosition(requestingPosition)) {
                    ChunkStorage.getChunk(requestingPosition).render();
                }
            }
        }

        Window.swapBuffers();
    }

    /**
     * Classic payload, the INITIAL chunk generation queue. This generates a fixes square around the center of the map.
     * It's called classic for a reason! :D
     * In subsequent versions, this probably shouldn't be used and should use an initial circular generation or something.
     */
    private static void classicChunkPayload() {
        for (int x = -classicMapSize; x <= classicMapSize; x++) {
            for (int z = -classicMapSize; z <= classicMapSize; z++) {
                ChunkGenerator.pushRequest(new Vector2i(x, z));
            }
        }
    }

    private static void destroy() {
        ChunkMeshGenerator.stop();
        ChunkGenerator.stop();
        TextureStorage.destroyAll();
        MeshStorage.destroyAll();
        ShaderStorage.destroyAll();
        Window.destroy();
    }
}
