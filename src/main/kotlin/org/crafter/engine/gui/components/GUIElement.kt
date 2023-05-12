@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package org.crafter.engine.gui.components

import org.crafter.engine.gui.GUI
import org.crafter.engine.gui.actions.*
import org.crafter.engine.gui.enumerators.Alignment
import org.crafter.engine.window.Window
import org.joml.Vector2f
import org.joml.Vector2fc

abstract class GUIElement protected constructor(alignment: Alignment, offset: Vector2f) {
    protected var name: String = ""
    var meshUUID: String = ""
        protected set
    protected var onStep: OnStep? = null
    protected var click: Click? = null
    protected var hover: Hover? = null
    protected var keyInput: KeyInput? = null
    protected var enterInput: EnterInput? = null
    protected var onRender: OnRender? = null
    protected var collide = false

    protected val offset: Vector2f
    protected var alignment: Alignment

    init {
        this.offset = offset
        this.alignment = alignment
    }

    // Size is the width and height of the element
    protected var size: Vector2f = Vector2f()
        get() = Vector2f(field)
        protected set(newVector) {
            field.set(newVector)
        }
    protected val position: Vector2f = Vector2f(0f, 0f)

    fun setName(name: String) {
        if (name != "") {
            throw RuntimeException("GUIElement : ERROR! Tried to set name for element ($name) more than once!")
        }
        this.name = name
    }

    protected fun alignment(): Vector2f {
        return Vector2f(alignment.value())
    }

    protected fun offset(): Vector2f {
        // I like to have +y be up when setting offset, so I made it like this
        return Vector2f(offset.x, -offset.y).mul(guiScale)
    }

    fun onStepable(): Boolean {
        return onStep != null
    }

    fun hoverable(): Boolean {
        return hover != null
    }

    fun clickable(): Boolean {
        return click != null
    }

    fun keyInputable(): Boolean {
        return keyInput != null
    }

    fun enterInputable(): Boolean {
        return enterInput != null
    }

    fun onRenderable(): Boolean {
        return onRender != null
    }

    fun collideable(): Boolean {
        return collide
    }

    // Callbacks are only available on element creation
    fun addOnStepCallback(onStep: OnStep): GUIElement {
        if (onStepable()) {
            throw RuntimeException("GUIElement: ERROR! Attempted to add (onStep) more than once in element ($name)!")
        }
        this.onStep = onStep
        return this
    }

    fun addHoverCallback(hover: Hover): GUIElement {
        if (hoverable()) {
            throw RuntimeException("GUIElement: ERROR! Attempted to add (hover) more than once in element ($name)!")
        }
        this.hover = hover
        return this
    }

    fun addClickCallback(click: Click): GUIElement {
        if (clickable()) {
            throw RuntimeException("GUIElement: ERROR! Attempted to add (click) more than once in element ($name)!")
        }
        this.click = click
        return this
    }

    fun addKeyInputCallback(keyInput: KeyInput): GUIElement {
        if (keyInputable()) {
            throw RuntimeException("GUIElement: ERROR! Attempted to add (keyInput) more than once in element ($name)!")
        }
        this.keyInput = keyInput
        return this
    }

    fun addEnterInputCallback(enterInput: EnterInput): GUIElement {
        if (enterInputable()) {
            throw RuntimeException("GUIElement: ERROR! Attempted to add (enterInput) more than once in element ($name)!")
        }
        this.enterInput = enterInput
        return this
    }

    fun addOnRenderCallback(onRender: OnRender): GUIElement {
        if (onRenderable()) {
            throw RuntimeException("GUIElement: ERROR! Attempted to add (onRender) more than once in element ($name)!")
        }
        this.onRender = onRender
        return this
    }

    fun setOffset(offset: Vector2f) {
        this.offset.x = offset.x
        this.offset.y = offset.y
        recalculatePosition()
    }

    fun setAlignment(alignment: Alignment) {
        this.alignment = alignment
        recalculatePosition()
    }

    fun name(): String {
        return name
    }

    // What the GUI element can do when nothing is happening, cool effect, etc
    fun onStep(gui: GUI) {
        if (onStepable()) {
            onStep!!.action(gui, this)
        }
    }

    fun onHover(gui: GUI) {
        if (hoverable()) {
            hover!!.action(gui, this)
        }
    }

    fun onClick(gui: GUI) {
        if (clickable()) {
            click!!.action(gui, this)
        }
    }

    fun onKeyInput(gui: GUI, keyboardKey: Int /*Replace with real input*/) {
        if (keyInputable()) {
            keyInput!!.action(gui, this, keyboardKey)
        }
    }

    /**
     * This is only used for GUIMesh. The fact that this is inherited, well, could be useful down the road, maybe.
     * It has a guarantee that it is a GUIMesh, casting to GUIMesh is safe.
     */
    fun onRender(gui: GUI) {
        if (this !is GUIMesh) {
            return
        }
        /*
        GUIMesh is a special case, it is far, FAR too variable to hardcode anything that relates to rendering
        So it MUST contain a render delegate.
        This also is a runtime error, so it exists with a name now.
        */if (!onRenderable()) {
            throw RuntimeException("GUIMesh: ($name) MUST have an OnRender function!")
        }
        onRender!!.action(gui, this)
    }

    abstract fun render()
    abstract fun collisionDetect(mousePosition: Vector2fc): Boolean
    protected abstract fun recalculateMesh()
    abstract fun internalOnStep(gui: GUI)

    // Enforce recalculation, it's very important to keep gui elements in correct position
    protected abstract fun recalculatePosition()
    abstract fun internalOnHover(mousePosition: Vector2fc)
    abstract fun internalOnClick(mousePosition: Vector2fc)

    // Internal point calculation, specifically for mouse. Class member. Utilizes stack.
    protected fun pointCollisionDetect(
        pointX: Float,
        pointY: Float,
        posX: Float,
        posY: Float,
        width: Float,
        height: Float
    ): Boolean {
        return pointX >= posX && pointX <= posX + width && pointY >= posY && pointY <= posY + height
    }

    companion object {
        var guiScale = 1f
            private set

        fun recalculateGUIScale() {
            val test: Vector2fc = Window.getWindowSize()

            // 1080p is the gold standard resolution
            val xCompare: Float = test.x() / 1920.0f
            val yCompare: Float = test.y() / 1080.0f
            guiScale = xCompare.coerceAtMost(yCompare)
        }
    }
}
