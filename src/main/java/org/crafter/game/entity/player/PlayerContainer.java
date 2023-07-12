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
package org.crafter.game.entity.player;

import java.util.HashMap;

/**
 * The player container is not only the container for player, but how you talk to players.
 * Think of it as a factory for players, kind of.
 */
public final class PlayerContainer {

    private static final HashMap<String, Player> container = new HashMap<>();

    private PlayerContainer(){}

    public static void addNewPlayer(String name, boolean clientPlayer) {
        Player player = new Player(name, clientPlayer);
    }


    public static boolean playerExists(String name) {
        return container.containsKey(name);
    }
    public static Player getPlayer(String name) {
        return container.get(name);
    }

    public static void deletePlayer(String name) {
        container.remove(name);
    }

}
