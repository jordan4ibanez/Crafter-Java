package org.crafter.engine.world.biome;

import org.joml.Vector2f;

/**
 * Adjustable parameters for a biome.
 * All biomes use simplex noise generation with a base height of X (not currently decided).
 * Can be used for interesting things.
 */
public class Biome {
    private float frequency = 0.01f;

    private int octaves = 3;

    // Just basic names for these, nothing set in stone for them
    private String grassLayer;
    private String soilLayer;
    private String stoneLayer;

    // These are possible future implementations

    private int baseHeight = 60;

    // Replace this with OreDefinition or something
    private String[] ores;

    private Vector2f caveMinMaxNoise;

}
