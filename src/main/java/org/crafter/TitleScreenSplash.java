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
package org.crafter;

import org.joml.Random;

import java.util.Date;

/**
 * Turn this into a json file eventually.
 */
public final class TitleScreenSplash {

    private static final Random random = new Random((int) (new Date().getTime()/1000));

    private static final String[] texts = new String[]{
            "Error 418: I'm a teapot!",
            "Feeling pretty classic!",
            "Open source!"
    };

    private TitleScreenSplash() {}

    public static String getSplash() {
        return texts[getRandomIndex()];
    }

    private static int getRandomIndex() {
        return random.nextInt(texts.length);
    }
}
