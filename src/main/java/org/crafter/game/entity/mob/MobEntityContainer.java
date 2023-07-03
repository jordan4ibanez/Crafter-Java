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
package org.crafter.game.entity.mob;

import org.joml.Vector3fc;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

/**
 * Holds all mobs during gameplay. Mobs are assigned a string UUID. That becomes their name.
 */
public final class MobEntityContainer {

    private final static HashMap<String, MobEntity> container = new HashMap<>();

    private MobEntityContainer() {}

    /**
     * Spawns a single mob into the game.
     * @param name Name of the mob.
     * @param position Where to spawn this mob.
     */
    public void spawnMob(String name, Vector3fc position) {
        spawnMob(name, 1, position);
    }

    /**
     * Spawns X amount of a mob into the game.
     * @param name Name of the mob.
     * @param amount Number of mobs to spawn.
     * @param position Where to spawn this mob.
     */
    public void spawnMob(String name, int amount, Vector3fc position) {
        final MobDefinition definition = MobDefinitionContainer.getMob(name);
        String uuid = UUID.randomUUID().toString();
        MobEntity newMob = new MobEntity(definition, uuid);
        newMob.setPosition(position);
        container.put(uuid, newMob);
    }

    /**
     * Get an iterable collection of all existing mobs.
     * @return An iterable collection of all existing mobs.
     */
    public Collection<MobEntity> iterateMobs() {
        return container.values();
    }
}
