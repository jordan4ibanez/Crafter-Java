package org.crafter.engine.delta

/**
 * Useful for testing thread performance!
 */
class DeltaObject {
    var delta = 0f
        private set
    private var oldTime: Long

    init {
        oldTime = System.nanoTime()
    }

    fun calculateDelta() {
        val time = System.nanoTime()
        delta = (time - oldTime).toFloat() / 1000000000.0f
        oldTime = time
    }
}
