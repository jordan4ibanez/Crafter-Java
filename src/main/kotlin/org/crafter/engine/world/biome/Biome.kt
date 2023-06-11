package org.crafter.engine.world.biome

import org.joml.Vector2f

/**
 * Adjustable parameters for a biome.
 * All biomes use simplex noise generation with a base height of X (not currently decided).
 * Can be used for interesting things.
 */
class Biome {
    private val frequency = 0.01f
    private val octaves = 3

    // Just basic names for these, nothing set in stone for them
    private val grassLayer: String? = null
    private val soilLayer: String? = null
    private val stoneLayer: String? = null

    // These are possible future implementations
    private val baseHeight = 60

    // Replace this with OreDefinition or something
    private val ores: Array<String>
        get() {
            TODO()
        }
    private val caveMinMaxNoise: Vector2f? = null
}
