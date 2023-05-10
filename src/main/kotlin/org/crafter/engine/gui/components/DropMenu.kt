@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package org.crafter.engine.gui.components

import org.crafter.engine.camera.Camera.setGuiObjectMatrix
import org.crafter.engine.gui.GUI
import org.crafter.engine.gui.enumerators.Alignment
import org.crafter.engine.gui.factories.ColorRectangleFactory
import org.crafter.engine.gui.factories.FramedMeshFactory
import org.crafter.engine.gui.font.Font
import org.crafter.engine.gui.font.Font.switchColor
import org.crafter.engine.gui.font.Font.switchShadowColor
import org.crafter.engine.mesh.MeshStorage.destroy
import org.crafter.engine.mesh.MeshStorage.render
import org.crafter.engine.window.Window.getWindowSize
import org.crafter.engine.window.Window.wasResized
import org.joml.Vector2f
import org.joml.Vector2fc
import org.joml.Vector4f
import org.joml.Vector4fc

/**
 * Drop boxes are a bit complex. A bit hard to explain in a comment as well.
 */
class DropMenu(
    boxWidth: Float,
    options: Array<String>,
    fontSize: Float,
    alignment: Alignment,
    offset: Vector2f,
    defaultSelection: Int
) : GUIElement(alignment, offset) {
    private var collapsed = true

    // We don't want the size to change basically
    private val boxWidth: Float
    private val textHeight: Float
    private val fontSize: Float
    private var selectedOption = 0
    private var hoverSelection = -1

    // Holds the actual options string values
    private val options: Array<String>
    private var selectionBoxUUID: String = ""
    private var fullSizeBackgroundUUID: String = ""
    private var dropDownCollapsedUUID: String = ""
    private var buttonUUID: String = ""
    private var buttonTextUUID: String = ""
    private var collapsedOptionUUID: String = ""

    // These are the options in full size
    private val optionsUUIDs: Array<String>

    // This is so elements can be optimized, can poll if there's a new option
    private var newOption = false

    init {
        if (options.size < 2) {
            throw RuntimeException("DropMenu: You must have more than one option in your drop menu!")
        }
        this.boxWidth = boxWidth
        this.options = options
        this.fontSize = fontSize
        textHeight = Font.getTextSize(fontSize, " ").y()
        optionsUUIDs = Array(options.size){""}
        // Safety check
        if (defaultSelection < 0 || defaultSelection >= options.size) {
            throw RuntimeException("DropMenu: Default selection is out of bounds!")
        }
        selectedOption = defaultSelection
        collide = true
        recalculateMesh()
    }

    // External usage, for creating neat callbacks!
    fun newOption(): Boolean {
        return newOption
    }

    fun getSelectedOption(): String {
        return options[selectedOption]
    }

    // End neat callbacks, so sad
    fun setCurrentOption() {
        // Recreates the selection option text
        // Safety check
        if (hoverSelection == -1 || hoverSelection >= options.size) {
            return
        }
        selectedOption = hoverSelection
        recalculateCollapsedText()
    }

    override fun render() {
        if (collapsed) {
            // Main panel
            setGuiObjectMatrix(position.x + padding, position.y + padding)
            render(collapsedOptionUUID)
            setGuiObjectMatrix(position.x, position.y)
            render(dropDownCollapsedUUID)

            // Drop down button
            setGuiObjectMatrix(position.x + buttonTextOffset, position.y + padding)
            render(buttonTextUUID)
            setGuiObjectMatrix(position.x + collapsedBoxWidth, position.y)
            render(buttonUUID)
        } else {

            // Text options
            for (i in options.indices) {
                setGuiObjectMatrix(
                    position.x + padding,
                    position.y + padding + i * textHeight * guiScale
                )
                render(optionsUUIDs[i])
            }

            // Selection box
            if (hoverSelection != -1) {
                setGuiObjectMatrix(
                    position.x + padding,
                    position.y + padding + hoverSelection * textHeight * guiScale
                )
                render(selectionBoxUUID)
            }

            // Background
            setGuiObjectMatrix(position.x, position.y)
            render(fullSizeBackgroundUUID)
        }
    }

    override fun collisionDetect(mousePosition: Vector2fc): Boolean {
        val collided: Boolean = pointCollisionDetect(
            mousePosition.x(),
            mousePosition.y(),
            position.x(),
            position.y(),
            size.x(),
            size.y()
        )
        if (!collided) {
            // This is cheap, simplistic logic prevents bugs
            hoverSelection = -1
        }
        return collided
    }

    override fun recalculateMesh() {
        size = if (collapsed) {
            recalculateCollapsed()
            recalculateCollapsedText()
            recalculateButton()
            Vector2f(getBoxWidth() + doublePadding(), textHeight * guiScale + doublePadding())
        } else {
            recalculateFullSizeBackground()
            recalculateOptions()
            Vector2f(
                getBoxWidth() + doublePadding(),
                textHeight * guiScale * options.size + doublePadding()
            )
        }

        // The window can get resized when the drop box is collapsed, making this outdated
        recalculateSelectionBox()
        recalculatePosition()
    }

    override fun internalOnStep(gui: GUI) {
        if (wasResized()) {
            recalculateMesh()
        }
        if (!collapsed && gui.currentlyFocused != name()) {
            collapsed = true
            recalculateMesh()
        }
    }

    override fun recalculatePosition() {
        position.set(alignment.value().mul(getWindowSize()).sub(size.mul(alignment.value())).add(offset()))
    }

    override fun internalOnHover(mousePosition: Vector2fc) {
        newOption = false
        if (collapsed) {
            return
        }
        // Collide with elements
        for (i in options.indices) {
            if (pointCollisionDetect(
                    mousePosition.x(),
                    mousePosition.y(),
                    position.x() + padding,
                    position.y() + padding + textHeight * guiScale * i.toFloat(),
                    getBoxWidth(),
                    textHeight * guiScale
                )
            ) {
                hoverSelection = i
                return
            }
        }
        hoverSelection = -1
    }

    override fun internalOnClick(mousePosition: Vector2fc) {
        newOption = false

        // Open up the selection box
        if (collapsed) {
            collapsed = false
            recalculateMesh()
            return
        }

        // Not collapsed but no selection
        if (hoverSelection == -1) {
            collapsed = true
            recalculateMesh()
            return
        }

        // Not collapsed and selected, update
        setCurrentOption()
        collapsed = true
        recalculateMesh()
        newOption = true
    }

    private fun recalculateFullSizeBackground() {
        if (fullSizeBackgroundUUID != "") {
            destroy(fullSizeBackgroundUUID)
        }
        val width: Float = boxWidth * guiScale
        val height: Float = textHeight * guiScale * options.size

        // (1.0f + ((float)options.length * 0.3f)) is an extreme hardcode, works pretty well, for now
        fullSizeBackgroundUUID = FramedMeshFactory.generateMesh(
            Vector2f(width, height),
            padding,
            pixelEdge,
            borderScale / (1.0f + options.size.toFloat() * 0.3f),
            "textures/button.png"
        )
    }

    private fun recalculateCollapsedText() {
        if (collapsedOptionUUID != "") {
            destroy(collapsedOptionUUID)
        }
        switchColor(1f, 1f, 1f)
        switchShadowColor(0f, 0f, 0f)
        val finalText = makeTextFit(options[selectedOption], collapsedTextBoxWidth)
        collapsedOptionUUID = Font.grabText(fontSize * guiScale, finalText)
    }

    private fun recalculateCollapsed() {
        if (dropDownCollapsedUUID != "") {
            destroy(dropDownCollapsedUUID)
        }
        val boxSize = selectionBoxSize
        boxSize.x -= buttonWidth + doublePadding()
        dropDownCollapsedUUID =
            FramedMeshFactory.generateMesh(boxSize, padding, pixelEdge, borderScale, "textures/button.png")
    }

    private fun recalculateOptions() {
        for (optionUUID in optionsUUIDs) {
            if (optionUUID != "") {
                destroy(optionUUID)
            }
        }
        val boxWidth = selectionBoxWidth
        for (i in options.indices) {
            val option = options[i]
            val finalText = makeTextFit(option, boxWidth)
            switchColor(1f, 1f, 1f)
            switchShadowColor(0f, 0f, 0f)
            optionsUUIDs[i] = Font.grabText(fontSize * guiScale, finalText)
        }
    }

    private fun recalculateButton() {
        // The button works as a single unit
        if (buttonUUID != "") {
            destroy(buttonUUID)
        }
        if (buttonTextUUID != "") {
            destroy(buttonTextUUID)
        }
        buttonUUID = FramedMeshFactory.generateMesh(
            Vector2f(buttonWidth),
            padding,
            pixelEdge,
            borderScale,
            "textures/button.png"
        )
        switchColor(1f, 1f, 0f)
        switchShadowColor(0f, 0f, 0f)
        buttonTextUUID = Font.grabText(fontSize * guiScale, "V")
    }

    private fun recalculateSelectionBox() {
        if (selectionBoxUUID != "") {
            destroy(selectionBoxUUID)
        }
        val boxSize: Vector2fc = selectionBoxSize
        selectionBoxUUID = ColorRectangleFactory.createColorRectangleMesh(
            boxSize.x(),
            boxSize.y(),
            selectionBoxColor.x(),
            selectionBoxColor.y(),
            selectionBoxColor.z(),
            selectionBoxColor.w()
        )
    }

    private val selectionBoxSize: Vector2f
        // Gets it with scaling!
        get() = Vector2f(
            boxWidth * guiScale,
            textHeight * guiScale
        )
    private val selectionBoxWidth: Float
        get() = boxWidth * guiScale

    private fun doublePadding(): Float {
        return padding * 2.0f
    }

    private val collapsedBoxWidth: Float
        get() = boxWidth * guiScale - buttonWidth
    private val collapsedTextBoxWidth: Float
        get() = boxWidth * guiScale - (buttonWidth + doublePadding())
    private val buttonWidth: Float
        get() = textHeight * guiScale

    private fun getBoxWidth(): Float {
        return boxWidth * guiScale
    }

    private val buttonTextOffset: Float
        get() =// This is a weird hardcode, but it works
            // Centers the button text on the drop button
            collapsedBoxWidth + padding + fontSize * 0.25f * guiScale

    private fun makeTextFit(inputString: String, requiredWidth: Float): String {
        var fits = false
        var outputText = inputString
        val textLength = inputString.length
        var currentTrim = 0
        while (!fits) {
            val gottenWidth = Font.getTextSize(fontSize * guiScale, outputText).x()
            if (gottenWidth <= requiredWidth) {
                fits = true
            } else {
                currentTrim++
                outputText = outputText.substring(0, textLength - currentTrim) + "..."
            }
        }
        return outputText
    }

    companion object {
        val padding = 16.0f
            get() = field * guiScale
        const val pixelEdge = 1.0f
        const val borderScale = 2.0f
        private val selectionBoxColor: Vector4fc = Vector4f(0.5f, 0.5f, 0.5f, 1f)
    }
}
