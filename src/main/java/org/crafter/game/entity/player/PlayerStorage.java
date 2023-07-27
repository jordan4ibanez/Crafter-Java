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

import org.crafter.engine.api.ActionStorage;

import java.util.HashMap;

/**
 * The player storage is not only the container for player, but how you talk to players.
 * Think of it as a factory for players, kind of.
 * If this is a server, there will be no client player!
 */
public final class PlayerStorage {

    private static boolean clientLockout = false;
    private static String clientPlayerName;
    private static final HashMap<String, Player> container = new HashMap<>();

    private PlayerStorage(){}

    public static void addNewPlayer(String name, boolean clientPlayer) {
        if (clientLockout && clientPlayer) {
            throw new RuntimeException("PlayerStorage: Error! Tried to add more than one Client player into the world!");
        }
        Player player = new Player(name, clientPlayer);
        container.put(name, player);

        if (clientPlayer) {
            clientPlayerName = name;
            clientLockout = true;
        }

        final String playerType = clientPlayer ? "Client" : "External";

        System.out.println("PlayerStorage: Added new (" + playerType + ") player into world. Name: (" + name + ")");

        // Execute all onJoin functions defined by the ECMAScript API.
        ActionStorage.executeOnJoin(player);
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

    public static boolean clientPlayerExists() {
        if (clientPlayerName == null) {
            return false;
        }
        return container.containsKey(clientPlayerName);
    }
    public static Player getClientPlayer() {
        return container.get(clientPlayerName);
    }

}
