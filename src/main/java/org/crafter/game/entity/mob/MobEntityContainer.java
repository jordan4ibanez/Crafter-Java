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
 * Holds all mobs during gameplay. Mobs are assigned a string UUID. That becomes their name.
 */
public final class MobEntityContainer {

    private final static HashMap<String, MobEntity> container = new HashMap<>();

    private MobEntityContainer() {}

    public void spawnMob(String name, int amount) {

    }
}
