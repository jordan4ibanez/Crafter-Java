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
package org.crafter.engine.api.actions.on_timer;

public class OnTimerShell {

    private float internalTimer = 0.0f;
    private boolean repeat = false;
    private final OnTimer onTimer;

    public OnTimerShell(float timer, boolean repeat, OnTimer onTimer) {
        internalTimer = timer;
        this.repeat = repeat;
        this.onTimer = onTimer;
    }

    public boolean tickDown(float delta) {
        internalTimer -= delta;
        return internalTimer <= 0;
    }

    public void execute() {
        onTimer.execute();
    }

    public boolean doesRepeat() {
        return repeat;
    }
}