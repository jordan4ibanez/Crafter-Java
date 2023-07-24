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
    public void testSetterFailureStates() {

        // Invalid positions

        // Identical positions
        try {
            setBlockManipulatorPositions(new Vector3i(0,0,0), new Vector3i(0,0,0));
            fail("Block Manipulator allowed invalid state 0");
        } catch (Exception e) {
            assertEquals(e.toString(), "java.lang.RuntimeException: ChunkStorage: Attempted to set Block Manipulator to invalid min/max! (X axis) min is greater than or equal to max!");
            // This passes
            System.out.println("Block Manipulator passed test 0");
        }

        // X
        try {
            setBlockManipulatorPositions(new Vector3i(1,0,0), new Vector3i(1,1,1));
            fail("Block Manipulator allowed invalid state 1");
        } catch (Exception e) {
            assertEquals(e.toString(), "java.lang.RuntimeException: ChunkStorage: Attempted to set Block Manipulator to invalid min/max! (X axis) min is greater than or equal to max!");
            // This passes
            System.out.println("Block Manipulator passed test 1");
        }

        // X
        try {
            setBlockManipulatorPositions(new Vector3i(1,2,-1), new Vector3i(1,1,1));
            fail("Block Manipulator allowed invalid state 2");
        } catch (Exception e) {
            assertEquals(e.toString(), "java.lang.RuntimeException: ChunkStorage: Attempted to set Block Manipulator to invalid min/max! (X axis) min is greater than or equal to max!");
            // This passes
            System.out.println("Block Manipulator passed test 2");
        }

        // Z
        try {
            setBlockManipulatorPositions(new Vector3i(-1,0,2), new Vector3i(1,1,1));
            fail("Block Manipulator allowed invalid state 3");
        } catch (Exception e) {
            assertEquals(e.toString(), "java.lang.RuntimeException: ChunkStorage: Attempted to set Block Manipulator to invalid min/max! (Z axis) min is greater than or equal to max!");
            // This passes
            System.out.println("Block Manipulator passed test 3");
        }

        // Z
        try {
            setBlockManipulatorPositions(new Vector3i(-1,-1,1), new Vector3i(1,1,0));
            fail("Block Manipulator allowed invalid state 4");
        } catch (Exception e) {
            assertEquals(e.toString(), "java.lang.RuntimeException: ChunkStorage: Attempted to set Block Manipulator to invalid min/max! (Z axis) min is greater than or equal to max!");
            // This passes
            System.out.println("Block Manipulator passed test 4");
        }

        // Inverted positions
        try {
            setBlockManipulatorPositions(new Vector3i(1,1,1), new Vector3i(-1,-1,-1));
            fail("Block Manipulator allowed invalid state 5");
        } catch (Exception e) {
            assertEquals(e.toString(), "java.lang.RuntimeException: ChunkStorage: Attempted to set Block Manipulator to invalid min/max! (X axis) min is greater than or equal to max!");
            // This passes
            System.out.println("Block Manipulator passed test 5");
        }

        // Invalid sizes

        // X
        try {
            setBlockManipulatorPositions(-64,0,-1, 0,127,0);
            fail("Block Manipulator allowed invalid size 1");
        } catch (Exception e) {
            assertEquals(e.toString(), "java.lang.RuntimeException: ChunkStorage: Attempted to set Block Manipulator past size limit! (X axis) limit is 64!");
            // This passes
            System.out.println("Block Manipulator passed test 6");
        }

        // Z
        try {
            setBlockManipulatorPositions(-1,0,-64, 0,127,0);
            fail("Block Manipulator allowed invalid size 2");
        } catch (Exception e) {
            assertEquals(e.toString(), "java.lang.RuntimeException: ChunkStorage: Attempted to set Block Manipulator past size limit! (Z axis) limit is 64!");
            // This passes
            System.out.println("Block Manipulator passed test 7");
        }

        // X
        try {
            setBlockManipulatorPositions(-32,0,-1, 32,127,0);
            fail("Block Manipulator allowed invalid size 3");
        } catch (Exception e) {
            assertEquals(e.toString(), "java.lang.RuntimeException: ChunkStorage: Attempted to set Block Manipulator past size limit! (X axis) limit is 64!");
            // This passes
            System.out.println("Block Manipulator passed test 8");
        }

        // Z
        try {
            setBlockManipulatorPositions(-1,0,-32, 0,127,32);
            fail("Block Manipulator allowed invalid size 4");
        } catch (Exception e) {
            assertEquals(e.toString(), "java.lang.RuntimeException: ChunkStorage: Attempted to set Block Manipulator past size limit! (Z axis) limit is 64!");
            // This passes
            System.out.println("Block Manipulator passed test 9");
        }

        // X
        try {
            setBlockManipulatorPositions(0,0,0, 64,127,1);
            fail("Block Manipulator allowed invalid size 5");
        } catch (Exception e) {
            assertEquals(e.toString(), "java.lang.RuntimeException: ChunkStorage: Attempted to set Block Manipulator past size limit! (X axis) limit is 64!");
            // This passes
            System.out.println("Block Manipulator passed test 10");
        }

        // Z
        try {
            setBlockManipulatorPositions(0,0,0, 1,127,64);
            fail("Block Manipulator allowed invalid size 6");
        } catch (Exception e) {
            assertEquals(e.toString(), "java.lang.RuntimeException: ChunkStorage: Attempted to set Block Manipulator past size limit! (Z axis) limit is 64!");
            // This passes
            System.out.println("Block Manipulator passed test 11");
        }
    }
}
