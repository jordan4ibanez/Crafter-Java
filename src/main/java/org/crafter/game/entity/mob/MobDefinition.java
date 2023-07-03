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

public class MobDefinition extends Entity {

    private final String name;
    private boolean locked = false;

    private OnSpawn _onSpawn;
    private OnPunch _onPunch;
    private OnStep _onStep;
    private OnDie _onDie;

    public MobDefinition(String name) {
        this.name = name;
    }

    /**
     * Get the mob's name.
     * @return Mob's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the mob onSpawn function. Chainable for ease of use.
     * @param onSpawn onSpawn function.
     * @return Mob definition.
     */
    public MobDefinition setOnSpawn(OnSpawn onSpawn) {
        checkLock("setOnSpawn");
        this._onSpawn = onSpawn;
        return this;
    }

    public MobDefinition setOnPunch(OnPunch onPunch) {
        checkLock("setOnPunch");
        this._onPunch = onPunch;
        return this;
    }


    /**
     * Set the mob onStep function. Chainable for ease of use.
     * @param onStep onStep function.
     * @return Mob definition.
     */
    public MobDefinition setOnStep(OnStep onStep) {
        checkLock("setOnStep");
        this._onStep = onStep;
        return this;
    }

    /**
     * Set the mob onDie function. Chainable for ease of use.
     * @param onDie onDie function.
     * @return Mob Definition.
     */
    public MobDefinition setOnDie(OnDie onDie) {
        checkLock("setOnDie");
        this._onDie = onDie;
        return this;
    }

    protected void lockOut() {
        checkLock("lockOut");

        if (_onSpawn == null) {
            _onSpawn = mob -> {
                // Placeholder
            };
        }

        if (_onPunch == null) {
            _onPunch = mob -> {
                // Placeholder
            };
        }

        if (_onStep == null) {
            _onStep = mob -> {
                // Placeholder
            };
        }

        if (_onDie == null) {
            _onDie = mob -> {
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

    // Now begins external method callers during gameplay.

    public void onSpawn() {
        _onSpawn.onSpawn(this);
    }

    public void onPunch() {
        _onPunch.onPunch(this);
    }

    public void onStep() {
        _onStep.onStep(this);
    }

    public void onDie() {
        _onDie.onDie(this);
    }

}
