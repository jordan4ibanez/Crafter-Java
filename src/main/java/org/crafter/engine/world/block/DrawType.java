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
package org.crafter.engine.world.block;

public enum DrawType {
    AIR(0),
    BLOCK(1),
    BLOCK_BOX(2),
    TORCH(3),
    LIQUID_SOURCE(4),
    LIQUID_FLOW(5),
    GLASS(6),
    PLANT(7),
    LEAVES(8);

    private final int value;

    DrawType(int value){
        this.value = value;
    }

    public int value() {
        return value;
    }


    public static DrawType intToDrawType(int input) {
        for (DrawType drawType : getAsArray()) {
            if (drawType.value() == input) {
                return drawType;
            }
        }
        throw new RuntimeException("DrawType: Attempted to convert invalid value! (" + input + ")");
    }

    public static DrawType[] getAsArray() {
        return new DrawType[] {AIR, BLOCK, BLOCK_BOX, TORCH, LIQUID_SOURCE, LIQUID_FLOW, GLASS, PLANT, LEAVES};
    }
}
