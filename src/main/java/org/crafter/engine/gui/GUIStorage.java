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

import org.crafter.engine.gui.components.GUIElement;
import org.crafter.engine.gui.enumerators.Alignment;
import org.joml.Vector2f;

import java.util.concurrent.ConcurrentHashMap;

public final class GUIStorage {

    private final static ConcurrentHashMap<String, GUI> container = new ConcurrentHashMap<>();

    private static GUI selectedGUI;

    private GUIStorage(){};


    // Process runs all methods of all elements of the selected GUI
    public static void process() {
        selectedGUINullCheck("process");
        selectedGUI.doLogic();
    }

    // Renders all methods of all elements of the selected GUI
    public static void render() {
        selectedGUINullCheck("render");
        selectedGUI.render();
    }

    public static void addGUI(String guiName, GUI newGUI) {
        checkDuplicates(guiName);
        container.put(guiName, newGUI);
    }

    public static void addElement(String guiName, String elementName, GUIElement newElement) {
        existenceCheck(guiName);
        container.get(guiName).addGUIElement(elementName, newElement);
    }

    public static void selectGUI(String guiName) {
        existenceCheck(guiName);
        selectedGUI = container.get(guiName);
    }

    public static String getCurrentlyFocused() {
        selectedGUINullCheck("getCurrentlyFocused");
        return selectedGUI.getCurrentlyFocused();
    }

    public static void setOffset(String elementName, Vector2f offset) {
        selectedGUINullCheck("setOffset");
        selectedGUI.setOffset(elementName, offset);
    }

    public static void setAlignment(String elementName, Alignment alignment) {
        selectedGUINullCheck("setAlignment");
        selectedGUI.setAlignment(elementName, alignment);
    }

    public static void setFontSize(String elementName, float fontSize) {
        selectedGUINullCheck("setFontSize");
        selectedGUI.setFontSize(elementName, fontSize);
    }

    public static void setText(String elementName, String textData) {
        selectedGUINullCheck("setFontSize");
        selectedGUI.setText(elementName, textData);
    }

    private static void selectedGUINullCheck(String inputFunction) {
        if (selectedGUI == null) {
            throw new RuntimeException("GUIStorage: ERROR! You must select a GUI before you attempt to run (" + inputFunction + ")!");
        }
    }

    private static void existenceCheck(String guiName) {
        if (!container.containsKey(guiName)) {
            throw new RuntimeException("GUIStorage: ERROR! Tried to select nonexistent GUI (" + guiName + ")!");
        }
    }
    private static void checkDuplicates(String guiName) {
        if (container.containsKey(guiName)) {
            throw new RuntimeException("GUIStorage: ERROR! Tried to add in GUI (" + guiName + ") more than once!");
        }
    }
}
