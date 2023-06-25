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
package org.crafter.engine.gui.font;

import java.util.HashMap;

/**
 * FontData is simply a data container class for fonts. Amazing.
 */
class FontData {

    // Font base pallet width (in pixels)
    int palletWidth  = 0;
    int palletHeight = 0;

    // Pixel space (literally) between characters in pallet
    int border = 0;

    // Number of characters (horizontal, aka X)
    int rows = 0;

    // How far the letters are from each other
    float spacing = 1.0f;

    // How big the space character is (' ')
    float spaceCharacterSize = 4.0f;

    // Character pallet (individual) in pixels
    int characterWidth   = 0;
    int characterHeight = 0;

    // Readonly specifier if trimming was enabled
    boolean trimmedX = false;
    boolean trimmedY = false;

    // Readonly directory for texture (entire, including the .png)
    String fileLocation;

    // Character map - stored as a linear associative array for O(1) retrieval
    /**
     * Stores as:
     * [
     *      -x -y,
     *      -x +y,
     *      +x +y,
     *      +x -y
     * ]
     * or this, if it's easier to understand:
     * [
     *      top    left,
     *      bottom left,
     *      bottom right,
     *      top    right
     * ]
     * GPU optimized vertex positions!
     * Accessed as:
     * double[] myCoolBlah = map["whatever letter/unicode thing you're getting"];
     * The last value specifies width of the character.
     */

    HashMap<String, float[]> map;

    // Stores the map raw as a linear array before processed
    String rawMap;

    FontData() {
        map = new HashMap<>();
    }

    // Debug util
    public String getAsString(){
        return "Pallet: " + palletHeight + " " + palletWidth + " | Rows" + rows;
    }
}
