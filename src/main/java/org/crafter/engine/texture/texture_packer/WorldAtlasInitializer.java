package org.crafter.engine.texture.texture_packer;

import org.crafter.engine.texture.TextureStorage;
import org.joml.Vector2ic;

import java.io.File;
import java.nio.ByteBuffer;

/**
 * This is just a hidden away call to make the main function cleaner.
 * In the future this will be quite useful when lua is added in.
 */
public final class WorldAtlasInitializer {

    private WorldAtlasInitializer() {}

    //TODO: utilize lua definitions
    //TODO: Scrape lua mod directory folders

    private static final String layout = "textures/blocks/";
    private static final String[] directoryChain = new String[]{"textures", "blocks"};
    public static void initializeWorldBlockTextures() {

        checkDirectories();

        TexturePacker packer = TexturePacker.getInstance();

        File[] dir = new File(layout).listFiles();

        // TODO: interop with lua to automate this further
        if (dir == null) {
            throw new RuntimeException("Failed to access folder: (" + layout + ")!");
        }

        for (File file : dir) {
            if (!file.isDirectory() && file.getName().contains(".png")) {
//                System.out.println(file.getName());
                final String name = file.getName();
                packer.add(name, "textures/blocks/" + name);
            }
        }

        // Now ship it off to the TextureStorage factory

        ByteBuffer buffer = packer.flush();
        Vector2ic size = packer.getCanvasSize();

        TextureStorage.createTexture("worldAtlas",buffer, size);
    }

    private static void checkDirectories() {
        StringBuilder directoryBuilder = new StringBuilder();
        for (String location : directoryChain) {
            directoryBuilder.append(location).append("/");
            if (!(new File(directoryBuilder.toString()).exists())) {
                throw new RuntimeException("TexturePackerInitializer: Failed to access folder (" + directoryBuilder.toString() +")! layout should be: " + layout);
            }
        }
    }
}
