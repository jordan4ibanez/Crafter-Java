package org.crafter.engine.gui.components

import org.crafter.engine.camera.Camera.setGuiObjectMatrix
import org.crafter.engine.controls.Keyboard.hasTyped
import org.crafter.engine.controls.Keyboard.keyDown
import org.crafter.engine.controls.Keyboard.keyPressed
import org.crafter.engine.controls.Keyboard.lastInput
import org.crafter.engine.delta.Delta.delta
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
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW

class TextBox(placeHolderText: String, fontSize: Float, alignment: Alignment, offset: Vector2f?, boxWidth: Float) :
    Text("", fontSize, alignment, offset) {
    private var buttonBackGroundMeshUUID: String? = null
    private val placeHolderText: String
    private val boxWidth: Float
    private var repeatTimer = 0.0f
    private var repeating = false

    // How far into the sentence the output of the textData is in the box
    private var entryCursorPosition = 0
    private var cursorBlink = true
    private var cursorTimer = 0.0f
    private var clearOnSend = true
    private var focused = false
    private var wasFocused = false

    init {
        _collide = true
        this.placeHolderText = placeHolderText
        this.boxWidth = boxWidth
        recalculateMesh()
    }

    override fun render() {
        setGuiObjectMatrix(_position.x + padding, _position.y + padding)
        render(_meshUUID)
        setGuiObjectMatrix(_position.x, _position.y)
        render(buttonBackGroundMeshUUID!!)
    }

    override fun internalOnStep(gui: GUI) {
        if (wasResized()) {
            recalculateMesh()
        }
        if (gui.currentlyFocused != name()) {
            cursorBlink = false
            focused = false
            if (wasFocused) {
                recalculateText()
                wasFocused = false
                //                System.out.println("recalculating");
            }
            return
        }
        focused = true
        wasFocused = true
        val enterKeyPressed = keyPressed(GLFW.GLFW_KEY_ENTER)
        if (enterKeyPressed) {
            if (enterInputable()) {
                enterInput.action(gui, this, textData)
                if (clearOnSend) {
                    textData = ""
                    entryCursorPosition = 0
                }
            }
            return
        }
        cursorTimer += delta
        if (cursorTimer >= 0.25) {
            cursorTimer = 0.0f
            cursorBlink = !cursorBlink
            recalculateText()
        }
        if (hasTyped()) {
            textData += lastInput
            var textWidth = Font.getTextSize(fontSize * GUIElement.Companion.getGuiScale(), textWithCursorPos).x
            while (textWidth > boxWidth * GUIElement.Companion.getGuiScale()) {
                entryCursorPosition++
                textWidth = Font.getTextSize(fontSize * GUIElement.Companion.getGuiScale(), textWithCursorPos).x
            }
        } else if (keyDown(GLFW.GLFW_KEY_BACKSPACE)) {
            val textLength = textData.length
            if (textLength == 0) {
                return
            }
            if (repeating && repeatTimer >= 0.05f) {
                backspaceTrim()
                repeatTimer = 0.0f
            } else if (repeatTimer == 0.0f) {
                backspaceTrim()
            }
            repeatTimer += delta
            if (repeatTimer >= 0.5f) {
                repeating = true
                repeatTimer = 0.0f
            }
        } else {
            repeating = false
            repeatTimer = 0.0f
        }
        recalculateText()
    }

    private fun backspaceTrim() {
        textData = textData.substring(0, textData.length - 1)
        var hit = false
        var textWidth = Font.getTextSize(fontSize * GUIElement.Companion.getGuiScale(), textWithCursorPos).x
        while (textWidth > boxWidth * GUIElement.Companion.getGuiScale()) {
            if (entryCursorPosition <= 0) {
                break
            }
            hit = true
            entryCursorPosition--
            textWidth = Font.getTextSize(fontSize * GUIElement.Companion.getGuiScale(), textWithCursorPos).x
        }
        if (entryCursorPosition <= 0) {
            return
        }
        if (!hit) {
            entryCursorPosition--
        }
    }

    private val textWithCursorPos: String
        private get() {
            var manipulationString = textData.substring(entryCursorPosition)
            manipulationString += if (cursorBlink) {
                "_"
            } else {
                " "
            }
            return manipulationString
        }

    override fun recalculatePosition() {
        _position.set(_alignment.value().mul(getWindowSize()).sub(size.mul(_alignment.value())).add(offset()))
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

        // Only needs the height, so ship it nothing
        val boxSize = Font.getTextSize(fontSize * GUIElement.Companion.getGuiScale(), "")
        boxSize!!.x = getBoxWidth()
        buttonBackGroundMeshUUID =
            FramedMeshFactory.generateMesh(boxSize, padding, pixelEdge, borderScale, "textures/text_box.png")
        recalculateText()

        // Padding times 2 because all edges of the button are padding, doubled on X and Y
        this.size = boxSize.add(Vector2f(padding * 2))
        recalculatePosition()
    }

    private fun recalculateText() {
        val shownText: String
        if (textData == "" && !focused) {
            shownText = placeHolderText
            Font.switchColor(placeHolderColor)
        } else {
            shownText = textWithCursorPos
            Font.switchColor(foreGroundColor)
        }
        Font.switchShadowColor(shadowColor)
        _meshUUID = Font.grabText(fontSize * GUIElement.Companion.getGuiScale(), shownText)
    }

    val text: String
        get() = textData

    fun getBoxWidth(): Float {
        return boxWidth * GUIElement.Companion.getGuiScale()
    }

    // This might be useful for something
    fun disableClearOnSend(): TextBox {
        clearOnSend = false
        return this
    }

    companion object {
        // We want these to be constant throughout the entire game, class members only
        val padding = 16.0f
            get() = field * GUIElement.Companion.getGuiScale()
        const val pixelEdge = 1.0f
        const val borderScale = 2.0f
        private val placeHolderColor = Vector3f(0.5f)
    }
}
