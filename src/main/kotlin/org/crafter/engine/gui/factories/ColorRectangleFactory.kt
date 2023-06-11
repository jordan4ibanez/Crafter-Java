package org.crafter.engine.gui.factories

import org.crafter.engine.mesh.MeshStorage.newMesh
import java.util.*

object ColorRectangleFactory {
    @JvmOverloads
    fun createColorRectangleMesh(width: Float, height: Float, r: Float, g: Float, b: Float, a: Float = 1f): String {
        val vertices = floatArrayOf(
            0.0f, 0.0f,
            0.0f, height,
            width, height,
            width, 0.0f
        )
        val textureCoordinates = floatArrayOf(
            0f, 0f,
            0f, 1f,
            1f, 1f,
            1f, 0f
        )
        val indices = intArrayOf(
            0, 1, 2, 2, 3, 0
        )
        val colors = floatArrayOf(
            r, g, b, a,
            r, g, b, a,
            r, g, b, a,
            r, g, b, a
        )
        val uuid = UUID.randomUUID().toString()
        newMesh(
            uuid,
            vertices,
            textureCoordinates,
            indices,
            null,
            colors,
            "textures/blank_pixel.png",
            true
        )

//        System.out.println("ColorRectangleFactory: Shipping out UUID (" + uuid + ")!");
        return uuid
    }
}
