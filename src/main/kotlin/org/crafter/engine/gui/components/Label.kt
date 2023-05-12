package org.crafter.engine.gui.components

import org.crafter.engine.camera.Camera.setGuiObjectMatrix
import org.crafter.engine.gui.GUI
import org.crafter.engine.gui.enumerators.Alignment
import org.crafter.engine.gui.font.Font
import org.crafter.engine.gui.implementations.Text
import org.crafter.engine.mesh.MeshStorage.destroy
import org.crafter.engine.mesh.MeshStorage.render
import org.crafter.engine.window.Window.getWindowSize
import org.crafter.engine.window.Window.wasResized
import org.joml.Vector2f
import org.joml.Vector2fc

/**
 * Holds text data in memory.
 */
class Label(textData: String, fontSize: Float, alignment: Alignment, offset: Vector2f) :
    Text(textData, fontSize, alignment, offset) {
    init {
        recalculateMesh()
    }

    override fun render() {
        setGuiObjectMatrix(position.x, position.y)
        render(meshUUID)
    }

    override fun collisionDetect(mousePosition: Vector2fc): Boolean {
        // Does nothing
        return false
    }

    override fun recalculateMesh() {
//        System.out.println("Label: generating a new mesh");
        if (meshUUID != "") {
            destroy(meshUUID)
        }
        Font.switchColor(foreGroundColor)
        Font.switchShadowColor(shadowColor)
        this.meshUUID =
            Font.grabText(fontSize * guiScale, textData)
        this.size =
            Font.getTextSize(fontSize * guiScale, textData)
        recalculatePosition()
    }

    override fun internalOnClick(mousePosition: Vector2fc) {
        TODO("Not yet implemented")
    }

    override fun internalOnStep(gui: GUI) {
        if (wasResized()) {
            recalculateMesh()
        }
    }

    override fun recalculatePosition() {
        position.set(alignment.value().mul(getWindowSize()).sub(size.mul(alignment.value())).add(offset()))
        //        System.out.println("Label (" + this.name() + ") POSITION: " + _position.x + ", " + _position.y);
    }
}
