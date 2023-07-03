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

/**
 * An instance of a mob during gameplay. Gets built by _insert thing here_ using the blueprint from MobDefinitionContainer.
 */
public class MobEntity extends Entity {
    private final String name;
    private final OnSpawn _onSpawn;
    private final OnPunch _onPunch;
    private final OnStep _onStep;
    private final OnDie _onDie;

    public MobEntity(MobDefinition definition) {
        this.name = definition.name;
        this._onSpawn = definition._onSpawn;
        this._onPunch = definition._onPunch;
        this._onStep = definition._onStep;
        this._onDie = definition._onDie;
    }

    public String getName() {
        return name;
    }

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
