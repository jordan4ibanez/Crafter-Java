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
package org.crafter.engine.api;

import org.crafter.engine.api.actions.on_join.OnJoin;
import org.crafter.engine.api.actions.on_tick.OnTick;
import org.crafter.engine.api.actions.on_timer.OnTimer;
import org.crafter.engine.api.actions.on_timer.OnTimerShell;

import java.util.ArrayList;

public final class ActionStorage {
    private static final ArrayList<OnJoin> onJoinList = new ArrayList<>();
    private static final ArrayList<OnTick> onTickList = new ArrayList<>();
    private static final ArrayList<OnTimerShell> onTimerList = new ArrayList<>();

    private ActionStorage(){}

    /**
     * Register an onJoin function. This will run when a player joins the game.
     * @param onJoin The onJoin function.
     */
    public static void registerOnJoin(OnJoin onJoin) {
        nullCheck(onJoin, "onJoin");
        onJoinList.add(onJoin);
    }

    /**
     * Register an onTick function. This will run every tick of the game logic.
     * @param onTick The onTick function.
     */
    public static void registerOnTick(OnTick onTick) {
        nullCheck(onTick, "onTick");
        onTickList.add(onTick);
    }

    /**
     * Register an onTimer function. This will run every X seconds. Will repeat if repeats is true.
     * @param interval How much time until this function executes.
     * @param onTimer The onTimer function.
     * @param repeats If this function should keep repeating every X seconds.
     */
    public static void registerOnTimer(final float interval, final OnTimer onTimer, final boolean repeats) {
        nullCheck(onTimer, "onTimer");
        OnTimerShell onTimerShell = new OnTimerShell(onTimer, interval, repeats);
        onTimerList.add(onTimerShell);
    }


    /**
     * Add safety to this API.
     * @param action The action functional interface object.
     * @param actionName The action functional interface name.
     */
    private static void nullCheck(final Object action, final String actionName) {
        if (action == null) {
            throw new RuntimeException("ActionStorage: Tried to register a NULL " + actionName + " function!");
        }
    }
}
