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
package org.crafter.engine.utility;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Represents a range iterator in a functional manner.
 * Basically, a faster way to type out a numeric for loop.
 * Works as a self range factory.
 */
public class Range implements Iterable<Integer> {
    private final boolean _reverse;
    private final int _min;
    private final int _max;

    // Increment/decrement amount, defaults to 1.
    private final int _amount;

    private Range(final int min, final int max, final int amount, final boolean reverse) {
        _min = min;
        _max = max;
        _amount = amount;
        _reverse = reverse;
    }
    @Override
    public Iterator<Integer> iterator() {

        if (_reverse) {
            return new Iterator<>() {

                private final int max = _max;
                private int counter = _min;
                private final int amount = _amount;

                @Override
                public boolean hasNext() {
                    // Inclusive because reverse ranges can become very confusing
                    return counter >= max;
                }

                @Override
                public Integer next() {
                    if (hasNext()) {
                        final int temp = counter;
                        counter -= amount;
                        return temp;
                    } else {
                        throw new NoSuchElementException("Range: Reached end of range.");
                    }
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("Range: No elements to remove.");
                }
            };
        } else {
            return new Iterator<>() {

                private final int max = _max;
                private int counter = _min;
                private final int amount = _amount;

                @Override
                public boolean hasNext() {
                    return counter < max;
                }

                @Override
                public Integer next() {
                    if (hasNext()) {
                        final int temp = counter;
                        counter += amount;
                        return temp;
                    } else {
                        throw new NoSuchElementException("Range: Reached end of range.");
                    }
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("Range: No elements to remove.");
                }
            };
        }
    }

    // Begin methods to utilize Range.

    /**
     * Get an iterable numeric range. Begins at 0, ends at max (exclusive), incremented by 1.
     * @param max Exclusive max value.
     * @return A range from 0 to max.
     */
    public static Range range(final int max) {
        return new Range(0, max, 1, false);
    }

    /**
     * Get an iterable numeric range. Begins at min (inclusive), ends at max (exclusive), incremented by 1.
     * @param min Inclusive min value.
     * @param max Exclusive max value.
     * @return A Range from min to max.
     */
    public static Range range(final int min, final int max) {
        return new Range(min, max, 1, false);
    }

    /**
     * Get an iterable numeric range. Begins at min (inclusive), ends at max (exclusive), incremented by increment.
     * @param min Inclusive min value.
     * @param max Exclusive max value.
     * @param increment Amount to increment by.
     * @return A Range from min to max, incremented by specified amount.
     */
    public static Range range(final int min, final int max, final int increment) {
        return new Range(min, max, increment, false);
    }

    /**
     * Get an iterable reverse numeric range. Begins at max (inclusive), ends at 0 (inclusive), decremented by 1.
     * @param max Inclusive max value.
     * @return A reverse Range from max to 0.
     */
    public static Range reverseRange(final int max) {
        return new Range(max, 0, 1, true);
    }

    /**
     * Get an iterable reverse numeric range. Begins at max (inclusive), ends at 0 (inclusive), decremented by 1.
     * @param max Inclusive max value.
     * @param min Inclusive min value.
     * @return A reverse Range from max to min.
     */
    public static Range reverseRange(final int max, final int min) {
        return new Range(max, min, 1, true);
    }

    /**
     * Get an iterable reverse numeric range. Begins at max (inclusive), ends at 0 (inclusive), decremented by decrement.
     * @param max Inclusive max value.
     * @param min Inclusive min value.
     * @param decrement Amount to decrement by.
     * @return A reverse Range from max to min, decremented by specified amount.
     */
    public static Range reverseRange(final int max, final int min, final int decrement) {
        return new Range(max, min, decrement, true);
    }
}
