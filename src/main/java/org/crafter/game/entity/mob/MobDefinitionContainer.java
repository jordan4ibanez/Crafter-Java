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

import java.util.HashMap;

/**
 * Holds mob definitions. For mobs during gameplay look in _insert the created runtime container here_.
 */
public final class MobDefinitionContainer {
    HashMap<String, MobDefinition> container = new HashMap<>();
    private MobDefinitionContainer() {}

    /**
     * Register a mob into the game's container.
     * @param mobDefinition MobDefinition, what it says on the tin.
     */
    public void registerMob(MobDefinition mobDefinition) {
        mobDefinition.lockOut();
        container.put(mobDefinition.getName(), mobDefinition);
    }

    public MobDefinition getMob(String name) {
        return container.get(name);
    }
}
