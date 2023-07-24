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

import org.joml.Vector3i;
import org.junit.jupiter.api.Test;

import static org.crafter.engine.world.chunk.ChunkStorage.setBlockManipulatorPositions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class BlockManipulatorTest {

    @Test
    public void testInvalidCubicSize() {
        // Identical positions at 0,0,0
        try {
            setBlockManipulatorPositions(new Vector3i(0,0,0), new Vector3i(0,0,0));
            fail("Block Manipulator allowed invalid cubic size 1");
        } catch (Exception e) {
            assertEquals("java.lang.RuntimeException: ChunkStorage: Attempted to set the Block Manipulator to 1 block cubic area! Use the single block getters/setters instead!", e.toString());
            // This passes
//            System.out.println("Block Manipulator passed cubic size test 1");
        }

        // Identical positions at -X,0,0
        try {
            setBlockManipulatorPositions(new Vector3i(-64,0,0), new Vector3i(-64,0,0));
            fail("Block Manipulator allowed invalid cubic size 2");
        } catch (Exception e) {
            assertEquals("java.lang.RuntimeException: ChunkStorage: Attempted to set the Block Manipulator to 1 block cubic area! Use the single block getters/setters instead!", e.toString());
            // This passes
//            System.out.println("Block Manipulator passed cubic size test 2");
        }

        // Identical positions at X,0,0
        try {
            setBlockManipulatorPositions(new Vector3i(64,0,0), new Vector3i(64,0,0));
            fail("Block Manipulator allowed invalid cubic size 3");
        } catch (Exception e) {
            assertEquals("java.lang.RuntimeException: ChunkStorage: Attempted to set the Block Manipulator to 1 block cubic area! Use the single block getters/setters instead!", e.toString());
            // This passes
//            System.out.println("Block Manipulator passed cubic size test 3");
        }

        // Identical positions at 0,0,-Z
        try {
            setBlockManipulatorPositions(new Vector3i(0,0,-64), new Vector3i(0,0,-64));
            fail("Block Manipulator allowed invalid cubic size 4");
        } catch (Exception e) {
            assertEquals("java.lang.RuntimeException: ChunkStorage: Attempted to set the Block Manipulator to 1 block cubic area! Use the single block getters/setters instead!", e.toString());
            // This passes
//            System.out.println("Block Manipulator passed cubic size test 4");
        }

        // Identical positions at 0,0,Z
        try {
            setBlockManipulatorPositions(new Vector3i(0,0,64), new Vector3i(0,0,64));
            fail("Block Manipulator allowed invalid cubic size 5");
        } catch (Exception e) {
            assertEquals("java.lang.RuntimeException: ChunkStorage: Attempted to set the Block Manipulator to 1 block cubic area! Use the single block getters/setters instead!", e.toString());
            // This passes
//            System.out.println("Block Manipulator passed cubic size test 5");
        }
    }

    @Test
    public void testInvalidPositions() {

        // X
        try {
            setBlockManipulatorPositions(new Vector3i(-32,0,0), new Vector3i(-34,1,1));
            fail("Block Manipulator allowed invalid positions 1");
        } catch (Exception e) {
            assertEquals("java.lang.RuntimeException: ChunkStorage: Attempted to set Block Manipulator to invalid min/max! (X axis) min is greater than or equal to max!", e.toString());
            // This passes
//            System.out.println("Block Manipulator passed position test 1");
        }

        // X
        try {
            setBlockManipulatorPositions(new Vector3i(34,0,0), new Vector3i(32,1,1));
            fail("Block Manipulator allowed invalid positions 2");
        } catch (Exception e) {
            assertEquals("java.lang.RuntimeException: ChunkStorage: Attempted to set Block Manipulator to invalid min/max! (X axis) min is greater than or equal to max!", e.toString());
            // This passes
//            System.out.println("Block Manipulator passed position test 2");
        }

        // Z
        try {
            setBlockManipulatorPositions(new Vector3i(0,0,-32), new Vector3i(1,1,-34));
            fail("Block Manipulator allowed invalid positions 3");
        } catch (Exception e) {
            assertEquals("java.lang.RuntimeException: ChunkStorage: Attempted to set Block Manipulator to invalid min/max! (Z axis) min is greater than or equal to max!", e.toString());
            // This passes
//            System.out.println("Block Manipulator passed position test 3");
        }

        // Z
        try {
            setBlockManipulatorPositions(new Vector3i(0,0,34), new Vector3i(1,1,32));
            fail("Block Manipulator allowed invalid positions 4");
        } catch (Exception e) {
            assertEquals("java.lang.RuntimeException: ChunkStorage: Attempted to set Block Manipulator to invalid min/max! (Z axis) min is greater than or equal to max!", e.toString());
            // This passes
//            System.out.println("Block Manipulator passed position test 4");
        }

        // Inverted positions
        try {
            setBlockManipulatorPositions(new Vector3i(1,1,1), new Vector3i(-1,0,-1));
            fail("Block Manipulator allowed invalid positions 5");
        } catch (Exception e) {
            assertEquals("java.lang.RuntimeException: ChunkStorage: Attempted to set Block Manipulator to invalid min/max! (X axis) min is greater than or equal to max!", e.toString());
            // This passes
//            System.out.println("Block Manipulator passed position test 5");
        }
    }

    @Test
    public void testInvalidSizes() {

        // X
        try {
            setBlockManipulatorPositions(-64,0,-1, 0,127,0);
            fail("Block Manipulator allowed invalid size 1");
        } catch (Exception e) {
            assertEquals("java.lang.RuntimeException: ChunkStorage: Attempted to set Block Manipulator past size limit! (X axis) limit is 64!", e.toString());
            // This passes
//            System.out.println("Block Manipulator passed test 6");
        }

        // Z
        try {
            setBlockManipulatorPositions(-1,0,-64, 0,127,0);
            fail("Block Manipulator allowed invalid size 2");
        } catch (Exception e) {
            assertEquals("java.lang.RuntimeException: ChunkStorage: Attempted to set Block Manipulator past size limit! (Z axis) limit is 64!", e.toString());
            // This passes
//            System.out.println("Block Manipulator passed test 7");
        }

        // X
        try {
            setBlockManipulatorPositions(-32,0,-1, 32,127,0);
            fail("Block Manipulator allowed invalid size 3");
        } catch (Exception e) {
            assertEquals(e.toString(), "java.lang.RuntimeException: ChunkStorage: Attempted to set Block Manipulator past size limit! (X axis) limit is 64!");
            // This passes
//            System.out.println("Block Manipulator passed test 8");
        }

        // Z
        try {
            setBlockManipulatorPositions(-1,0,-32, 0,127,32);
            fail("Block Manipulator allowed invalid size 4");
        } catch (Exception e) {
            assertEquals(e.toString(), "java.lang.RuntimeException: ChunkStorage: Attempted to set Block Manipulator past size limit! (Z axis) limit is 64!");
            // This passes
//            System.out.println("Block Manipulator passed test 9");
        }

        // X
        try {
            setBlockManipulatorPositions(0,0,0, 64,127,1);
            fail("Block Manipulator allowed invalid size 5");
        } catch (Exception e) {
            assertEquals(e.toString(), "java.lang.RuntimeException: ChunkStorage: Attempted to set Block Manipulator past size limit! (X axis) limit is 64!");
            // This passes
//            System.out.println("Block Manipulator passed test 10");
        }

        // Z
        try {
            setBlockManipulatorPositions(0,0,0, 1,127,64);
            fail("Block Manipulator allowed invalid size 6");
        } catch (Exception e) {
            assertEquals(e.toString(), "java.lang.RuntimeException: ChunkStorage: Attempted to set Block Manipulator past size limit! (Z axis) limit is 64!");
            // This passes
//            System.out.println("Block Manipulator passed test 11");
        }
    }

    @Test
    public void testInvalidYPositions() {

        // Y min ABOVE Y max - This is a 1D check, so it only needs 1 test.
        try {
            setBlockManipulatorPositions(0,127,0, 1,0,1);
            fail("Block Manipulator allowed invalid Y position 1");
        } catch (Exception e) {
            assertEquals("java.lang.RuntimeException: ChunkStorage: Attempted to set Block Manipulator to invalid min/max! (Y axis) min is greater than or equal to max!", e.toString());
            // This passes
            System.out.println("Block Manipulator passed Y position test 1");
        }

        // Y min out of bounds
        try {
            setBlockManipulatorPositions(0,-1,0, 1,64,1);
            fail("Block Manipulator allowed invalid Y position 2");
        } catch (Exception e) {
            assertEquals("java.lang.RuntimeException: ChunkStorage: Attempted to set (min) BlockManipulator outside of map (Y axis) boundaries. Limit: 0 - 127 | Attempt: -1", e.toString());
            // This passes
            System.out.println("Block Manipulator passed Y position test 2");
        }

        // Y max out of bounds
        try {
            setBlockManipulatorPositions(0,64,0, 1,128,1);
            fail("Block Manipulator allowed invalid Y position 2");
        } catch (Exception e) {
            assertEquals("java.lang.RuntimeException: ChunkStorage: Attempted to set (max) BlockManipulator outside of map (Y axis) boundaries. Limit: 0 - 127 | Attempt: 128", e.toString());
            // This passes
            System.out.println("Block Manipulator passed Y position test 2");
        }
    }

    @Test
    public void testBlockManipulatorArrayManipulation() {

    }
}
