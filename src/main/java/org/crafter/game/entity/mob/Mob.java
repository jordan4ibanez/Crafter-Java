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
import org.crafter.game.entity.mob.mob_functions.OnDie;
import org.crafter.game.entity.mob.mob_functions.OnPunch;
import org.crafter.game.entity.mob.mob_functions.OnSpawn;
import org.crafter.game.entity.mob.mob_functions.OnStep;

public class Mob extends Entity {

    private boolean locked = false;

    private OnSpawn onSpawn;
    private OnPunch onPunch;
    private OnStep onStep;
    private OnDie onDie;

    public Mob() {}

    /**
     * Set the mob onSpawn function. Chainable for ease of use.
     * @param onSpawn onSpawn function.
     * @return Mob definition.
     */
    public Mob setOnSpawn(OnSpawn onSpawn) {
        checkLock("setOnSpawn");
        this.onSpawn = onSpawn;
        return this;
    }


    /**
     * Set the mob onStep function. Chainable for ease of use.
     * @param onStep onStep function.
     * @return Mob definition.
     */
    public Mob setOnStep(OnStep onStep) {
        checkLock("setOnStep");
        this.onStep = onStep;
        return this;
    }

    /**
     * Set the mob onDie function. Chainable for ease of use.
     * @param onDie onDie function.
     * @return Mob Definition.
     */
    public Mob setOnDie(OnDie onDie) {
        checkLock("setOnDie");
        this.onDie = onDie;
        return this;
    }

    public void lockOut() {
        checkLock("lockOut");

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
    private void checkLock(String methodName) {
        if (locked) {
            throw new RuntimeException("Mob: ERROR! Attempted to assign mob behavior in " + methodName + " after mob has been locked!");
        }
    }

}
