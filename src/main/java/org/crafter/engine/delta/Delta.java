package org.crafter.engine.delta;

public final class Delta {
    private static float deltaTime = 0;

    private static long oldTime = System.nanoTime();

    private Delta(){}

    public static void calculateDelta() {
        long time = System.nanoTime();
        deltaTime = (float)(time - oldTime) / 1_000_000_000.0f;
        oldTime = time;
    }

    public static float getDelta() {
        return deltaTime;
    }
}
