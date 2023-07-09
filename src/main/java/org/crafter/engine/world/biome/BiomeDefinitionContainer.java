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

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.HashMap;

import static org.crafter.engine.utility.UtilityPrinter.println;

public class BiomeDefinitionContainer implements Serializable {

    private static BiomeDefinitionContainer instance = null;

    private final HashMap<String, BiomeDefinition> container;

    private boolean isClone = false;

    private BiomeDefinitionContainer(){
         container = new HashMap<>();
    }

    // Object instance methods

    public void registerBiome(BiomeDefinition definition) {
        if (isClone) {
            throw new RuntimeException("BiomeDefinitionContainer: Tried to manipulate a clone of the master object!");
        }
        // TODO: maybe overrides aren't a good idea? I dunno. See if checking or clearing is a more concise way to do this maybe.
        println("BiomeDefinitionContainer: Registered biome: (" + definition.getName() + ")");
        container.put(definition.getName(), definition);
    }

    public BiomeDefinition getBiome(String name) {
        return container.get(name);
    }

    private void checkDuplicate(BiomeDefinition definition) {
        final String name = definition.getName();
        if (checkName(name)) {
            throw new RuntimeException("BlockDefinitionContainer: Attempted to overwrite biome (" + name +")!");
        }
    }

    private boolean checkName(String name) {
        return container.containsKey(name);
    }

    private boolean isEmpty() {
        return container.isEmpty();
    }

    private void doubleCheckData() {
        if (isEmpty()) {
            throw new RuntimeException("BiomeDefinitionContainer: Tried to create a clone of an empty container!");
        }
    }

    private void setClone() {
        isClone = true;
    }

    // Class methods

    /**
     * Only call this on the main thread when loading the game!
     * @return The master instance of the Biome Definition Container.
     */
    public static BiomeDefinitionContainer getMainInstance() {
        autoDispatch();
        return instance;
    }

    public static synchronized BiomeDefinitionContainer getThreadSafeDuplicate() {
        if (instance == null) {
            throw new RuntimeException("BiomeDefinitionContainer: Attempted to get duplicate of master object before it was created!");
        }
        instance.doubleCheckData();

        BiomeDefinitionContainer clone = SerializationUtils.clone(getMainInstance());
        clone.setClone();
        return clone;
    }

    private static void autoDispatch() {
        if (instance == null) {
            instance = new BiomeDefinitionContainer();
        }
    }
}
