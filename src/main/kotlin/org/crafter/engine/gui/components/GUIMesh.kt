package org.crafter.engine.gui.components

import org.crafter.engine.gui.GUI
import org.crafter.engine.gui.enumerators.Alignment
import org.joml.Vector2f
import org.joml.Vector2fc

/**
 * A GUI Mesh is a mesh which exists in 3d, yet in 2d at the same time.
 * Optionally, exists without perspective.
 */
class GUIMesh(uuid: String, alignment: Alignment, offset: Vector2f) : GUIElement(alignment, offset) {
    init {
        meshUUID = uuid
    }

    override fun render() {}
    override fun collisionDetect(mousePosition: Vector2fc): Boolean {
        return false
    }

    override fun recalculateMesh() {}
    override fun internalOnStep(gui: GUI) {}
    override fun recalculatePosition() {}
    override fun internalOnHover(mousePosition: Vector2fc) {}
    override fun internalOnClick(mousePosition: Vector2fc) {}
}
