package org.crafter.engine.gui;

import org.joml.Vector2f;

import java.util.HashMap;

public final class GUIStorage {

    private final static HashMap<String, GUI> container = new HashMap<>();

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

    public static void selectGUI(String guiName) {
        existenceCheck(guiName);
        selectedGUI = container.get(guiName);
    }

    public static void setOffset(String elementName, Vector2f offset) {
        selectedGUINullCheck("setOffset");
        selectedGUI.setOffset(elementName, offset);
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
