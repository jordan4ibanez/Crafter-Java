package org.crafter.engine.texture.texture_packer;

import org.crafter.engine.texture.TextureStorage;
import org.joml.Vector2ic;

import java.io.File;
import java.nio.ByteBuffer;

/**
 * This is just a hidden away call to make the main function cleaner.
 */
public final class TexturePackerInitializer {

    private TexturePackerInitializer() {}

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
                packer.add("textures/blocks/" + file.getName());
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
