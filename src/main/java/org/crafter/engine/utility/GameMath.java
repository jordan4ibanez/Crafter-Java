package org.crafter.engine.utility;

import org.joml.Math;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3ic;

public final class GameMath {

    private static final Vector3f workerVector = new Vector3f();

    // For some reason, JOML does not expose this variable
    private static final float PIHalf_f = (float) (Math.PI * 0.5);

    private GameMath(){}

    /**
     * Utilized for calculation of 2d movement from yaw
     */
    public static Vector3fc getHorizontalDirection(final float yaw) {
        startWork();

        workerVector.x = Math.sin(-yaw);
        workerVector.z = Math.cos(yaw);

        return getWork();
    }

    public static float yawToLeft(final float yaw) {
        return yaw - PIHalf_f;
    }


    /**
     * This is simply to neaten up the plain english readability of this.
     * I also don't want to forget to clear out the old data
     */
    private static void startWork() {
        workerVector.zero();
    }
    /**
     * this is because I don't feel like casting the worker vector into 3ic every time
     */
    private static Vector3fc getWork() {
        return workerVector;
    }
}
