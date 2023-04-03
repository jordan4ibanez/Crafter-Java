package org.crafter.engine.delta;

public final class Delta {
    private static float deltaTime = 0;

    private static long oldTime = System.nanoTime();

    private Delta(){}

    public static void calculateDelta() {
        long time = System.nanoTime();
        deltaTime = time - oldTime;
        oldTime = time;
    }

    public static float getDelta() {
        return deltaTime;
    }
}
