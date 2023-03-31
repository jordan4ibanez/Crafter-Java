package org.crafter.engine.gui;

import org.crafter.engine.gui.components.GUIElement;

import java.util.HashMap;

/**
 * An instance of a GUI object.
 * So just remember: This takes the Love2D approach.
 * The top left is (0, 0)
 */
public class GUI {

    private HashMap<String, GUIElement> container;

    private String currentlyFocused;

    GUI addGUIElement(String elementName, GUIElement element) {
        checkDuplicates(elementName);

        return this;
    }

    private void existenceCheck(String elementName) {
        if (!container.containsKey(elementName)) {
            throw new RuntimeException("GUI: ERROR! Tried to select nonexistent element (" + elementName + ")!");
        }
    }
    private void checkDuplicates(String elementName) {
        if (container.containsKey(elementName)) {
            throw new RuntimeException("GUI: ERROR! Tried to add in element (" + elementName + ") more than once!");
        }
    }

}
