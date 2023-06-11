package org.crafter.engine.gui.implementations

import org.crafter.engine.gui.components.GUIElement
import org.crafter.engine.gui.enumerators.Alignment
import org.joml.Vector2f
import org.joml.Vector2fc
import org.joml.Vector3f

abstract class Text protected constructor(textData: String?, fontSize: Float, alignment: Alignment, offset: Vector2f) :
    GUIElement(alignment, offset) {
    protected var textData = ""
    var fontSize: Float = 0f
        set(value) {
            field = value
            // System.out.println("Fontsize for " + this.name() + " is " + this.fontSize);
            recalculateMesh()
        }
    protected val foreGroundColor = Vector3f(1f, 1f, 1f)
    protected val shadowColor = Vector3f(0f, 0f, 0f)

    init {
        if (textData == null) {
            throw RuntimeException("Text: textData cannot be null!")
        }
        this.textData = textData
        this.fontSize = fontSize
    }

    fun setForeGroundColor(r: Float, g: Float, b: Float) {
        foreGroundColor[r, g] = b
        recalculateMesh()
    }

    fun setShadowColor(r: Float, b: Float, g: Float) {
        shadowColor[r, g] = b
        recalculateMesh()
    }


    fun setText(textData: String) {
        this.textData = textData
        recalculateMesh()
    }

    abstract override fun collisionDetect(mousePosition: Vector2fc): Boolean
    abstract override fun recalculateMesh()
    override fun internalOnHover(mousePosition: Vector2fc) {}
    override fun internalOnClick(mousePosition: Vector2fc) {}
}
