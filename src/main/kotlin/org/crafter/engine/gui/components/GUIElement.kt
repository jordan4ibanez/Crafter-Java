package org.crafter.engine.gui.components

import org.crafter.engine.gui.enumerators.Alignment
import org.crafter.engine.window.Window
import org.gui.actions.*
import java.util.*

abstract class GUIElement protected constructor(alignment: Alignment, offset: Vector2f?) {
    protected var _name: String? = null
    var meshUUID: String? = null
        protected set
    protected var _onStep: OnStep? = null
    protected var _click: Click? = null
    protected var _hover: Hover? = null
    protected var _keyInput: KeyInput? = null
    protected var _enterInput: EnterInput? = null
    protected var _onRender: OnRender? = null
    protected var _collide = false
    protected var _alignment: Alignment

    // Size is the width and height of the element
    protected val _size: Vector2f = Vector2f(0f, 0f)
    protected val _offset: Vector2f = Vector2f(0f, 0f)
    protected val _position: Vector2f = Vector2f(0f, 0f)

    init {
        _alignment = Objects.requireNonNullElse(alignment, Alignment.DEFAULT)
        /*
         * Offset input is how far off it is from the root alignment.
         */if (offset != null) {
            _offset.set(offset)
        }
    }

    fun setName(name: String?) {
        if (_name != null) {
            throw RuntimeException("GUIElement : ERROR! Tried to set name for element (" + _name + ") more than once!")
        }
        _name = name
    }

    protected fun alignment(): Vector2f {
        return Vector2f(_alignment.value())
    }

    protected fun offset(): Vector2f {
        // I like to have +y be up when setting offset, so I made it like this
        return Vector2f(_offset.x, -_offset.y).mul(guiScale)
    }

    protected var size: Vector2f
        protected get() = Vector2f(_size)
        protected set(newVector) {
            _size.set(newVector)
        }

    fun onStepable(): Boolean {
        return _onStep != null
    }

    fun hoverable(): Boolean {
        return _hover != null
    }

    fun clickable(): Boolean {
        return _click != null
    }

    fun keyInputable(): Boolean {
        return _keyInput != null
    }

    fun enterInputable(): Boolean {
        return _enterInput != null
    }

    fun onRenderable(): Boolean {
        return _onRender != null
    }

    fun collideable(): Boolean {
        return _collide
    }

    // Callbacks are only available on element creation
    fun addOnStepCallback(onStep: OnStep?): GUIElement {
        if (onStepable()) {
            throw RuntimeException("GUIElement: ERROR! Attempted to add (onStep) more than once in element (" + _name + ")!")
        }
        _onStep = onStep
        return this
    }

    fun addHoverCallback(hover: Hover?): GUIElement {
        if (hoverable()) {
            throw RuntimeException("GUIElement: ERROR! Attempted to add (hover) more than once in element (" + _name + ")!")
        }
        _hover = hover
        return this
    }

    fun addClickCallback(click: Click?): GUIElement {
        if (clickable()) {
            throw RuntimeException("GUIElement: ERROR! Attempted to add (click) more than once in element (" + _name + ")!")
        }
        _click = click
        return this
    }

    fun addKeyInputCallback(keyInput: KeyInput?): GUIElement {
        if (keyInputable()) {
            throw RuntimeException("GUIElement: ERROR! Attempted to add (keyInput) more than once in element (" + _name + ")!")
        }
        _keyInput = keyInput
        return this
    }

    fun addEnterInputCallback(enterInput: EnterInput?): GUIElement {
        if (enterInputable()) {
            throw RuntimeException("GUIElement: ERROR! Attempted to add (enterInput) more than once in element (" + _name + ")!")
        }
        _enterInput = enterInput
        return this
    }

    fun addOnRenderCallback(onRender: OnRender?): GUIElement {
        if (onRenderable()) {
            throw RuntimeException("GUIElement: ERROR! Attempted to add (onRender) more than once in element (" + _name + ")!")
        }
        _onRender = onRender
        return this
    }

    fun setOffset(offset: Vector2f) {
        _offset.x = offset.x
        _offset.y = offset.y
        recalculatePosition()
    }

    fun setAlignment(alignment: Alignment) {
        _alignment = alignment
        recalculatePosition()
    }

    fun name(): String? {
        return _name
    }

    // What the GUI element can do when nothing is happening, cool effect, etc
    fun onStep(gui: GUI?) {
        if (onStepable()) {
            _onStep.action(gui, this)
        }
    }

    fun onHover(gui: GUI?) {
        if (hoverable()) {
            _hover.action(gui, this)
        }
    }

    fun onClick(gui: GUI?) {
        if (clickable()) {
            _click.action(gui, this)
        }
    }

    fun onKeyInput(gui: GUI?, keyboardKey: Int /*Replace with real input*/) {
        if (keyInputable()) {
            _keyInput.action(gui, this, keyboardKey)
        }
    }

    /**
     * This is only used for GUIMesh. The fact that this is inherited, well, could be useful down the road maybe.
     * It has a guarantee that it is a GUIMesh, casting to GUIMesh is safe.
     */
    fun onRender(gui: GUI?) {
        if (this !is GUIMesh) {
            return
        }
        /*
        GUIMesh is a special case, it is far, FAR too variable to hardcode anything that relates to rendering
        So it MUST contain a render delegate.
        This also is a runtime error, so it exists with a name now.
        */if (!onRenderable()) {
            throw RuntimeException("GUIMesh: (" + name() + ") MUST have an OnRender function!")
        }
        _onRender.action(gui, this)
    }

    abstract fun render()
    abstract fun collisionDetect(mousePosition: Vector2fc): Boolean
    protected abstract fun recalculateMesh()
    abstract fun internalOnStep(gui: GUI)

    // Enforce recalculation, it's very important to keep gui elements in correct position
    protected abstract fun recalculatePosition()
    abstract fun internalOnHover(mousePosition: Vector2fc)
    abstract fun internalOnClick(mousePosition: Vector2fc?)

    companion object {
        var guiScale = 1f
            private set

        fun recalculateGUIScale() {
            val test: Vector2fc = Window.getWindowSize()

            // 1080p is the gold standard resolution
            val xCompare: Float = test.x() / 1920.0f
            val yCompare: Float = test.y() / 1080.0f
            guiScale = Math.min(xCompare, yCompare)
        }

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
    }
}
