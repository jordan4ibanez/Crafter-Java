package org.crafter.engine.gui.records

/**
 * Left, Right, Top, Bottom
 * @param startX
 * @param endX
 * @param startY
 * @param endY
 */
@JvmRecord
data class ImageTrim(
    val width: Float,
    val height: Float,
    val startX: Float,
    val endX: Float,
    val startY: Float,
    val endY: Float
)
