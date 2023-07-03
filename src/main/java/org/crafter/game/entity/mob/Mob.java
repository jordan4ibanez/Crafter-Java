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

import org.crafter.game.entity.Entity;
import org.crafter.game.entity.mob.mob_functions.OnSpawn;
import org.crafter.game.entity.mob.mob_functions.OnStep;

public class Mob extends Entity {

    private boolean locked = false;

    private OnSpawn onSpawn;
    private OnStep onStep;

    public Mob() {}
    

    /**
     * Set the mob onStep function.
     * @param onStep the onStep function.
     * @return The mob definition.
     */
    public Mob setOnStep(OnStep onStep) {
        checkFinalized("setOnStep");
        this.onStep = onStep;
        return this;
    }

    public void finalize() {

        if (onSpawn == null) {
            onSpawn = mob -> {
                // Placeholder
            };
        }

        if (onStep == null) {
            onStep = mob -> {
                // Placeholder
            };
        }

        // Now no methods can be modified during runtime in the modding API.
        locked = true;
    }

    /**
     * To prevent a mod from dynamically changing a mob's state, this check will run on assignment attempt.
     */
    private void checkFinalized(String methodName) {
        if (locked) {
            throw new RuntimeException("Mob: ERROR! Attempted to assign mob behavior in " + methodName + " after mob has been finalized!");
        }
    }

}
