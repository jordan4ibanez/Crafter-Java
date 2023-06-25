/*
 Crafter - A blocky game (engine) written in Java with LWJGL.
 Copyright (C) 2023  jordan4ibanez

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.crafter.engine.gui;

import org.crafter.engine.camera.Camera;
import org.crafter.engine.controls.Mouse;
import org.crafter.engine.gui.components.Button;
import org.crafter.engine.gui.components.GUIElement;
import org.crafter.engine.gui.components.Label;
import org.crafter.engine.gui.enumerators.Alignment;
import org.crafter.engine.shader.ShaderStorage;
import org.crafter.engine.window.Window;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.util.concurrent.ConcurrentHashMap;

/**
 * An instance of a GUI object.
 * So just remember: This takes the Love2D approach.
 * The top left is (0, 0)
 */
public class GUI {

    private final String name;

    private final ConcurrentHashMap<String, GUIElement> container = new ConcurrentHashMap<>();

    private String currentlyFocused = "";

    public GUI(String name) {
        this.name = name;
    }

    public GUI addGUIElement(String elementName, GUIElement element) {
        checkDuplicates(elementName);
        element.setName(elementName);
        container.put(elementName, element);
        return this;
    }

    public void doLogic() {
        // GUI Elements automatically recalculate the size. They utilise the member guiScale to keep things nice.
        // So it must have this run first.
        if (Window.wasResized()) {
            GUIElement.recalculateGUIScale();
        }
        internalOnStep();
        onStep();
        collisionDetect();
        keyInput();
    }

    // This is what the class does on step, it's hardcoded
    private void internalOnStep() {
        for (GUIElement element : container.values()) {
            element.internalOnStep(this);
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

        boolean mouseClicked = Mouse.leftClick();
        Vector2fc mousePosition = Mouse.getPosition();

        boolean failedToCollide = true;

        for (GUIElement element : container.values()) {
            if (element.collideable()) {
                if (element.collisionDetect(mousePosition)) {

                    // We want certain elements to be able to collision detect reset, so simply continue colliding
                    if (!failedToCollide) {
                        continue;
                    }

                    if (mouseClicked) {

                        element.internalOnClick(mousePosition);

                        element.onClick(this);

                        // Prevent any weird behavior with this simple check
                        String newFocus = element.name();
                        existenceCheck(newFocus);
                        currentlyFocused = newFocus;
//                        System.out.println("new focused element is: " + currentlyFocused);

                    } else {
                        element.internalOnHover(mousePosition);

                        element.onHover(this);
                    }

                    failedToCollide = false;
                }
            }
        }

        if (mouseClicked && failedToCollide) {
            currentlyFocused = "";
//            System.out.println("new focused element is: " + currentlyFocused);
        }
    }

    public void render() {

        // First we render out the standard 2d GUI
        Window.clearDepthBuffer();
        ShaderStorage.start("2d");
        Camera.updateGuiCameraMatrix();
        for (GUIElement element : container.values()) {
            Camera.setGuiObjectMatrix(0,0);
            element.render();
        }

        // Finally we render out the GUI Meshes
        Window.clearDepthBuffer();
        // We let the Render function handle shader and matrices. They're completely custom
        for (GUIElement element : container.values()) {
            element.onRender(this);
        }
    }

    public void setOffset(String elementName, Vector2f offset) {
        existenceCheck(elementName);
        container.get(elementName).setOffset(offset);
    }

    public void setAlignment(String elementName, Alignment alignment) {
        existenceCheck(elementName);
        container.get(elementName).setAlignment(alignment);
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
            throw new RuntimeException("GUI: ERROR! Tried to add in element (" + elementName + ") more than once in GUI (" + name + ")!");
        }
    }

}
