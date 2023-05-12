package org.crafter.engine.gui.components

import org.crafter.engine.camera.Camera.setGuiObjectMatrix
import org.crafter.engine.gui.GUI
import org.crafter.engine.gui.enumerators.Alignment
import org.crafter.engine.gui.factories.FramedMeshFactory
import org.crafter.engine.gui.font.Font
import org.crafter.engine.gui.implementations.Text
import org.crafter.engine.mesh.MeshStorage.destroy
import org.crafter.engine.mesh.MeshStorage.render
import org.crafter.engine.window.Window.getWindowSize
import org.crafter.engine.window.Window.wasResized
import org.joml.Vector2f
import org.joml.Vector2fc

class Button(textData: String?, fontSize: Float, alignment: Alignment, offset: Vector2f?) :
    Text(textData, fontSize, alignment, offset) {
    private var buttonBackGroundMeshUUID: String? = null

    init {
        _collide = true
        recalculateMesh()
    }

    override fun render() {
        setGuiObjectMatrix(_position.x + padding, _position.y + padding)
        render(_meshUUID)
        setGuiObjectMatrix(_position.x, _position.y)
        render(buttonBackGroundMeshUUID!!)
    }

    override fun collisionDetect(mousePosition: Vector2fc): Boolean {
        return GUIElement.Companion.pointCollisionDetect(
            mousePosition.x(),
            mousePosition.y(),
            _position.x(),
            _position.y(),
            _size.x(),
            _size.y()
        )
    }

    override fun recalculateMesh() {
        if (_meshUUID != null) {
            destroy(_meshUUID)
        }
        if (buttonBackGroundMeshUUID != null) {
            destroy(buttonBackGroundMeshUUID!!)
        }
        val textSize = Font.getTextSize(fontSize * GUIElement.Companion.getGuiScale(), textData)
        buttonBackGroundMeshUUID =
            FramedMeshFactory.generateMesh(textSize, padding, pixelEdge, borderScale, "textures/button.png")
        Font.switchColor(foreGroundColor)
        Font.switchShadowColor(shadowColor)
        _meshUUID = Font.grabText(fontSize * GUIElement.Companion.getGuiScale(), textData)

        // Padding times 2 because all edges of the button are padding, doubled on X and Y
        this.size = textSize!!.add(Vector2f(padding * 2))
        recalculatePosition()
    }

    override fun internalOnStep(ui: GUI) {
        if (wasResized()) {
            recalculateMesh()
        }
    }

    override fun recalculatePosition() {
        _position.set(_alignment.value().mul(getWindowSize()).sub(size.mul(_alignment.value())).add(offset()))
        //        System.out.println("Button (" + this.name() + ") POSITION: " + _position.x + ", " + _position.y);
    }

    companion object {
        // We want these to be constant throughout the entire game, class members only
        val padding = 16.0f
            get() = field * GUIElement.Companion.getGuiScale()
        const val pixelEdge = 1.0f
        const val borderScale = 2.0f
    }
}
