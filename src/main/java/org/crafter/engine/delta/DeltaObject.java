package org.crafter.engine.delta;

/**
 * Useful for testing thread performance!
 */
public class DeltaObject {
    private float deltaTime = 0;

    private long oldTime;

    public DeltaObject(){
        oldTime = System.nanoTime();
    }

    public void calculateDelta() {
        long time = System.nanoTime();
        deltaTime = (float)(time - oldTime) / 1_000_000_000.0f;
        oldTime = time;
    }

    public float getDelta() {
        return deltaTime;
    }
}
