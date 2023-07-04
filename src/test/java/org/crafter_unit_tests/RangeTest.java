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
package org.crafter_unit_tests;

import org.junit.jupiter.api.Test;

import static org.crafter.engine.utility.Range.range;
import static org.crafter.engine.utility.Range.reverseRange;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RangeTest {

    @Test
    public void increment() {
        int tracker = 0;

        final int[] test1 = new int[]{0,1,2,3,4,5,6,7,8,9};

        for (int i : range(10)) {
            System.out.println(i);
            assertEquals(i, test1[tracker]);
            tracker++;
        }

        tracker = 0;

        final int[] test2 = new int[]{5,6,7,8,9};

        for (int i : range(5,10)) {
            System.out.println(i);
            assertEquals(i, test2[tracker]);
            tracker++;
        }

        tracker = 0;

        final int[] test3 = new int[]{0,2,4,6,8};

        for (int i : range(0,10,2)) {
            System.out.println(i);
            assertEquals(i, test3[tracker]);
            tracker++;
        }
    }

    @Test
    public void decrement() {
        int tracker = 0;

        final int[] test1 = new int[]{10,9,8,7,6,5,4,3,2,1,0};

        for (int i : reverseRange(10)) {
            System.out.println(i);
            assertEquals(i, test1[tracker]);
            tracker++;
        }

        tracker = 0;

        final int[] test2 = new int[]{10,9,8,7,6,5};

        for (int i : reverseRange(10,5)) {
            System.out.println(i);
            assertEquals(i, test2[tracker]);
            tracker++;
        }

        tracker = 0;

        final int[] test3 = new int[]{10,8,6,4,2,0};

        for (int i : reverseRange(10,0, 2)) {
            System.out.println(i);
            assertEquals(i, test3[tracker]);
            tracker++;
        }
    }
}
