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
package org.crafter.game;

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
            "Open source!",
            "Entities are an anomaly!",
            "Is it a class or struct?!",
            "A long time coming!",
            "Also try Minetest!",
            "Music made in LMMS!",
            "Runs on OpenSUSE!",
            "Opens a Github issue!",
            "Made by one guy!",
            "Nostalgic!",
            "Scripted, not stirred!",
            "Let's go again! Let's go again!",
            "Unit tested...TOO MUCH!",
            "Advanced angles, simple geometry!",
            "Also try Mindustry!",
            "Programmed on a Model M!",
            "Delegates objective methods!",
            "Atomic, until error!",
            "Multi threading included!",
            "Also available on Linux!",
            "Libre!",
            "FSF approved, I hope!",
            "SquidRings is cool!",
            "chaottic is cool!",
            "Broken until fixed!",
            "Tuned for speeeed!"
    };

    private TitleScreenSplash() {}

    public static String getSplash() {
        return texts[getRandomIndex()];
    }

    private static int getRandomIndex() {
        return random.nextInt(texts.length);
    }
}
