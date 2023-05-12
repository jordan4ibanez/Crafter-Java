package org.crafter.engine.gui

import org.crafter.engine.gui.components.GUIElement
import org.crafter.engine.gui.enumerators.Alignment
import org.joml.Vector2f
import java.util.concurrent.ConcurrentHashMap

object GUIStorage {
    private val container = ConcurrentHashMap<String, GUI>()
    private var selectedGUI: GUI? = null

    // Process runs all methods of all elements of the selected GUI
    fun process() {
        selectedGUINullCheck("process")
        selectedGUI!!.doLogic()
    }

    // Renders all methods of all elements of the selected GUI
    fun render() {
        selectedGUINullCheck("render")
        selectedGUI!!.render()
    }

    fun addGUI(guiName: String, newGUI: GUI) {
        checkDuplicates(guiName)
        container[guiName] = newGUI
    }

    fun addElement(guiName: String, elementName: String, newElement: GUIElement) {
        existenceCheck(guiName)
        container[guiName]!!.addGUIElement(elementName, newElement)
    }

    fun selectGUI(guiName: String) {
        existenceCheck(guiName)
        selectedGUI = container[guiName]
    }

    val currentlyFocused: String?
        get() {
            selectedGUINullCheck("getCurrentlyFocused")
            return selectedGUI?.currentlyFocused
        }

    fun setOffset(elementName: String?, offset: Vector2f) {
        selectedGUINullCheck("setOffset")
        selectedGUI!!.setOffset(elementName, offset)
    }

    fun setAlignment(elementName: String?, alignment: Alignment) {
        selectedGUINullCheck("setAlignment")
        selectedGUI!!.setAlignment(elementName, alignment)
    }

    fun setFontSize(elementName: String?, fontSize: Float) {
        selectedGUINullCheck("setFontSize")
        selectedGUI!!.setFontSize(elementName, fontSize)
    }

    fun setText(elementName: String?, textData: String?) {
        selectedGUINullCheck("setFontSize")
        selectedGUI!!.setText(elementName, textData)
    }

    private fun selectedGUINullCheck(inputFunction: String) {
        if (selectedGUI == null) {
            throw RuntimeException("GUIStorage: ERROR! You must select a GUI before you attempt to run ($inputFunction)!")
        }
    }

    private fun existenceCheck(guiName: String) {
        if (!container.containsKey(guiName)) {
            throw RuntimeException("GUIStorage: ERROR! Tried to select nonexistent GUI ($guiName)!")
        }
    }

    private fun checkDuplicates(guiName: String) {
        if (container.containsKey(guiName)) {
            throw RuntimeException("GUIStorage: ERROR! Tried to add in GUI ($guiName) more than once!")
        }
    }
}
