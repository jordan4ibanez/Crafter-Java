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
package org.crafter.engine.world.biome;

import org.joml.Vector2f;

/**
 * Adjustable parameters for a biome.
 * All biomes use simplex noise generation with a base height of X (not currently decided).
 * Can be used for interesting things.
 */
public class BiomeDefinition {

    private final String name;

    private float frequency = 0.01f;

    private int octaves = 3;

    // Just basic names for these, nothing set in stone for them
    private String grassLayer;
    private String soilLayer;
    private String stoneLayer;

    // These are possible future implementations

    private final static int BASE_HEIGHT = 60;

    // Replace this with OreDefinition or something
    private String[] ores;

    private Vector2f caveMinMaxNoise;

    public BiomeDefinition(String name) {
        this.name = name;
    }

    public int getBaseHeight() {
        return BASE_HEIGHT;
    }

    public String getName() {
        return name;
    }

    public float getFrequency() {
        return frequency;
    }

    public BiomeDefinition setFrequency(float frequency) {
        this.frequency = frequency;
        return this;
    }

    public int getOctaves() {
        return octaves;
    }

    public BiomeDefinition setOctaves(int octaves) {
        this.octaves = octaves;
        return this;
    }

    public String getGrassLayer() {
        return grassLayer;
    }

    public BiomeDefinition setGrassLayer(String grassLayer) {
        this.grassLayer = grassLayer;
        return this;
    }

    public String getSoilLayer() {
        return soilLayer;
    }

    public BiomeDefinition setSoilLayer(String soilLayer) {
        this.soilLayer = soilLayer;
        return this;
    }

    public String getStoneLayer() {
        return stoneLayer;
    }

    public BiomeDefinition setStoneLayer(String stoneLayer) {
        this.stoneLayer = stoneLayer;
        return this;
    }

    public String[] getOres() {
        return ores;
    }

    public BiomeDefinition setOres(String[] ores) {
        this.ores = ores;
        return this;
    }

    public Vector2f getCaveMinMaxNoise() {
        return caveMinMaxNoise;
    }

    public BiomeDefinition setCaveMinMaxNoise(Vector2f caveMinMaxNoise) {
        this.caveMinMaxNoise = caveMinMaxNoise;
        return this;
    }
}
