package org.crafter.engine.gui;

import org.crafter.engine.gui.components.GUIElement;

import java.util.HashMap;

/**
 * An instance of a GUI object.
 * So just remember: This takes the Love2D approach.
 * The top left is (0, 0)
 */
public class GUI {

    private final HashMap<String, GUIElement> container = new HashMap<>();

    private String currentlyFocused;

    public GUI addGUIElement(String elementName, GUIElement element) {
        checkDuplicates(elementName);
        container.put(elementName, element);
        return this;
    }

    public void doLogic() {
        onStep();
        collisionDetect();
        keyInput();
    }

    // What the element does with no input
    private void onStep() {
        for (GUIElement element : container.values()) {
            if (element.onStepable()) {
                element.onStep(this);
            }
        }
    }

    // What the element does with mouse input
    private void collisionDetect() {

//        System.out.println("GUI: Still needs the static mouse class to pass in it's click data!");
        // FIXME: USE THE MOUSE CLASS HERE
        boolean mouseClicked = false;

        for (GUIElement element : container.values()) {
            if (element.collideable()) {
                if (element.collisionDetect()) {
                    if (mouseClicked) {
                        element.onClick(this);
                    } else {
                        element.onHover(this);
                    }

                    // Prevent any weird behavior with this simple check
                    String newFocus = element.name();
                    existenceCheck(newFocus);
                    currentlyFocused = newFocus;

                    System.out.println("new focused element is: " + newFocus);
                    break;
                }
            }
        }
    }

    private void keyInput() {
//        System.out.println("GUI: Still needs the static keyboard class to pass in it's typing data!");
        //FIXME: USE THE KEYBOARD CLASS HERE
        int keyboardKey = 1;

        for (GUIElement element : container.values()) {
            if (element.keyInputable()) {
                element.onKeyInput(this, keyboardKey);
            }
        }
    }

    public String getCurrentlyFocused() {
        return currentlyFocused;
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
