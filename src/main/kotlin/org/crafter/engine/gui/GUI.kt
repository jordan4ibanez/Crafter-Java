package org.crafter.engine.gui

import org.crafter.engine.camera.Camera.setGuiObjectMatrix
import org.crafter.engine.camera.Camera.updateGuiCameraMatrix
import org.crafter.engine.controls.Mouse.getPosition
import org.crafter.engine.controls.Mouse.leftClick
import org.crafter.engine.gui.components.*
import org.crafter.engine.gui.enumerators.Alignment
import org.crafter.engine.shader.ShaderStorage.start
import org.crafter.engine.window.Window.clearDepthBuffer
import org.crafter.engine.window.Window.wasResized
import org.joml.Vector2f
import java.util.concurrent.ConcurrentHashMap

/**
 * An instance of a GUI object.
 * So just remember: This takes the Love2D approach.
 * The top left is (0, 0)
 */
class GUI(private val name: String) {
    private val container = ConcurrentHashMap<String?, GUIElement>()
    var currentlyFocused: String? = ""
        private set

    fun addGUIElement(elementName: String, element: GUIElement): GUI {
        checkDuplicates(elementName)
        element.name = elementName
        container[elementName] = element
        return this
    }

    fun doLogic() {
        // GUI Elements automatically recalculate the size. They utilise the member guiScale to keep things nice.
        // So it must have this run first.
        if (wasResized()) {
            GUIElement.Companion.recalculateGUIScale()
        }
        internalOnStep()
        onStep()
        collisionDetect()
        keyInput()
    }

    // This is what the class does on step, it's hardcoded
    private fun internalOnStep() {
        for (element in container.values) {
            element.internalOnStep(this)
        }
    }

    // What the element does with no input
    private fun onStep() {
        for (element in container.values) {
            if (element.onStepable()) {
                element.onStep(this)
            }
        }
    }

    // What the element does with mouse input
    private fun collisionDetect() {
        val mouseClicked = leftClick()
        val mousePosition = getPosition()
        var failedToCollide = true
        for (element in container.values) {
            if (element.collideable()) {
                if (element.collisionDetect(mousePosition)) {

                    // We want certain elements to be able to collision detect reset, so simply continue colliding
                    if (!failedToCollide) {
                        continue
                    }
                    if (mouseClicked) {
                        element.internalOnClick(mousePosition)
                        element.onClick(this)

                        // Prevent any weird behavior with this simple check
                        val newFocus = element.name()
                        existenceCheck(newFocus)
                        currentlyFocused = newFocus
                        //                        System.out.println("new focused element is: " + currentlyFocused);
                    } else {
                        element.internalOnHover(mousePosition)
                        element.onHover(this)
                    }
                    failedToCollide = false
                }
            }
        }
        if (mouseClicked && failedToCollide) {
            currentlyFocused = ""
            //            System.out.println("new focused element is: " + currentlyFocused);
        }
    }

    fun render() {

        // First we render out the standard 2d GUI
        clearDepthBuffer()
        start("2d")
        updateGuiCameraMatrix()
        for (element in container.values) {
            setGuiObjectMatrix(0f, 0f)
            element.render()
        }

        // Finally we render out the GUI Meshes
        clearDepthBuffer()
        // We let the Render function handle shader and matrices. They're completely custom
        for (element in container.values) {
            element.onRender(this)
        }
    }

    fun setOffset(elementName: String?, offset: Vector2f) {
        existenceCheck(elementName)
        container[elementName]!!.setOffset(offset)
    }

    fun setAlignment(elementName: String?, alignment: Alignment) {
        existenceCheck(elementName)
        container[elementName]!!.alignment = alignment
    }

    fun setFontSize(elementName: String?, fontSize: Float) {
        existenceCheck(elementName)
        val gottenElement = container[elementName]
        if (gottenElement is Label) {
            gottenElement.fontSize = fontSize
            return
        } else if (gottenElement is Button) {
            gottenElement.fontSize = fontSize
            return
        }
        incompatibleThrow(gottenElement, "setFontSize")
    }

    fun setText(elementName: String?, textData: String?) {
        existenceCheck(elementName)
        val gottenElement = container[elementName]
        if (gottenElement is Label) {
            gottenElement.setText(textData!!)
            return
        } else if (gottenElement is Button) {
            gottenElement.setText(textData!!)
            return
        }
        incompatibleThrow(gottenElement, "setText")
    }

    private fun keyInput() {
//        System.out.println("GUI: Still needs the static keyboard class to pass in it's typing data!");
        //FIXME: USE THE KEYBOARD CLASS HERE
        val keyboardKey = 1
        for (element in container.values) {
            if (element.keyInputable()) {
                element.onKeyInput(this, keyboardKey)
            }
        }
    }

    private fun incompatibleThrow(element: GUIElement?, methodName: String) {
        throw RuntimeException("GUI: Error! Element (" + element!!.name() + ") does not implement method (" + methodName + ")! It is type (" + element.javaClass.toString() + ")!")
    }

    private fun existenceCheck(elementName: String?) {
        if (!container.containsKey(elementName)) {
            throw RuntimeException("GUI: ERROR! Tried to select nonexistent element ($elementName) in GUI ($name)!")
        }
    }

    private fun checkDuplicates(elementName: String) {
        if (container.containsKey(elementName)) {
            throw RuntimeException("GUI: ERROR! Tried to add in element ($elementName) more than once in GUI ($name)!")
        }
    }
}
