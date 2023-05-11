package org.crafter.engine.gui.enumerators

import org.joml.Vector2f

enum class Alignment(val value: Vector2f) {
    TOP_LEFT(Vector2f(0.0f, 0.0f)),
    TOP_CENTER(Vector2f(0.5f, 0.0f)),
    TOP_RIGHT(Vector2f(1.0f, 0.0f)),
    CENTER_LEFT(Vector2f(0.0f, 0.5f)),
    CENTER(Vector2f(0.5f, 0.5f)),
    CENTER_RIGHT(Vector2f(1.0f, 0.5f)),
    BOTTOM_LEFT(Vector2f(0.0f, 1.0f)),
    BOTTOM_CENTER(Vector2f(0.5f, 1.0f)),
    BOTTOM_RIGHT(Vector2f(1.0f, 1.0f)),
    DEFAULT(TOP_LEFT.value());

    fun value(): Vector2f {
        return Vector2f(value)
    }

    val x: Float
        get() = value.x
    val y: Float
        get() = value.y

    companion object {
        fun asArray(): Array<Alignment> {
            return arrayOf(
                TOP_LEFT,
                TOP_CENTER,
                TOP_RIGHT,
                CENTER_LEFT,
                CENTER,
                CENTER_RIGHT,
                BOTTOM_LEFT,
                BOTTOM_CENTER,
                BOTTOM_RIGHT
            )
        }
    }
}
