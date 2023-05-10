package org.crafter.engine.gui.actions

import org.crafter.engine.gui.GUI
import org.crafter.engine.gui.components.GUIElement

interface Hover {
    fun action(gui: GUI?, element: GUIElement?)
}
