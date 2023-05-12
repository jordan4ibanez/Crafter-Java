package org.crafter.engine.delta

object Delta {
    var delta = 0f
        private set

    private var oldTime = System.nanoTime()

    fun calculateDelta() {
        val time = System.nanoTime()
        delta = (time - oldTime).toFloat() / 1000000000.0f
        oldTime = time
    }
}
