package org.crafter.engine.world_generation;

import org.crafter.engine.utility.FastNoise;
import org.crafter.engine.world.block.BlockDefinitionContainer;

import java.util.Arrays;

public class ChunkGenerator implements Runnable {

    private static final Thread thread = new Thread(new ChunkGenerator());

    private final FastNoise noise;

    private final BlockDefinitionContainer blockDefinitionContainer;

    public ChunkGenerator() {
        noise = new FastNoise();
        blockDefinitionContainer = BlockDefinitionContainer.getThreadSafeDuplicate();
    }


    @Override
    public void run() {
        System.out.println("ChunkGenerator: Started!");
        System.out.println("ChunkGenerator: gotten blocks (" + Arrays.toString(blockDefinitionContainer.getAllBlockNames()) + ")!");
    }

    public static void start() {
        thread.start();
    }
}
