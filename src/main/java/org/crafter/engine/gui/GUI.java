package org.crafter.engine.gui;

import org.crafter.engine.gui.components.Button;
import org.crafter.engine.gui.components.GUIElement;
import org.crafter.engine.gui.components.Label;
import org.crafter.engine.shader.ShaderStorage;
import org.crafter.engine.window.Window;
import org.joml.Vector2f;

import java.util.Arrays;
import java.util.HashMap;

/**
 * An instance of a GUI object.
 * So just remember: This takes the Love2D approach.
 * The top left is (0, 0)
 */
public class GUI {

    private final String name;

    private final HashMap<String, GUIElement> container = new HashMap<>();

    private String currentlyFocused;

    public GUI(String name) {
        this.name = name;
    }

    public GUI addGUIElement(String elementName, GUIElement element) {
        checkDuplicates(elementName);
        container.put(elementName, element);
        return this;
    }

    public void doLogic() {
        internalOnStep();
        onStep();
        collisionDetect();
        keyInput();
    }

    // This is what the class does on step, it's hardcoded
    private void internalOnStep() {
        for (GUIElement element : container.values()) {
            element.internalOnStep();
        }
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

    public void render() {
        // Fancy way to automate the gui instantiation in OpenGL
        Window.clearDepthBuffer();
        ShaderStorage.start("2d");
        for (GUIElement element : container.values()) {
            element.render();
        }
    }

    public void setOffset(String elementName, Vector2f offset) {
        existenceCheck(elementName);
        container.get(elementName).setOffset(offset);
    }

    public void setFontSize(String elementName, float fontSize) {
        existenceCheck(elementName);
        GUIElement gottenElement = container.get(elementName);

        if (gottenElement instanceof Label) {
            ((Label) gottenElement).setFontSize(fontSize);
            return;
        } else if (gottenElement instanceof Button) {
            ((Button) gottenElement).setFontSize(fontSize);
            return;
        }

        incompatibleThrow(gottenElement, "setFontSize");
    }

    public void setText(String elementName, String textData) {
        existenceCheck(elementName);
        GUIElement gottenElement = container.get(elementName);

        if (gottenElement instanceof Label) {
            ((Label) gottenElement).setText(textData);
            return;
        } else if (gottenElement instanceof Button) {
            ((Button) gottenElement).setText(textData);
            return;
        }

        incompatibleThrow(gottenElement, "setText");
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

    private void incompatibleThrow(GUIElement element, String methodName) {
        throw new RuntimeException("GUI: Error! Element (" + element.name() + ") does not implement method (" + methodName + ")! It is type (" + element.getClass().toString() + ")!");
    }

    private void existenceCheck(String elementName) {
        if (!container.containsKey(elementName)) {
            throw new RuntimeException("GUI: ERROR! Tried to select nonexistent element (" + elementName + ") in GUI (" + name + ")!");
        }
    }
    private void checkDuplicates(String elementName) {
        if (container.containsKey(elementName)) {
            throw new RuntimeException("GUI: ERROR! Tried to add in element (" + elementName + ") more than once in GUI (" + name + "!");
        }
    }

}
