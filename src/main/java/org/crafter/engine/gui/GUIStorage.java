package org.crafter.engine.gui;

import java.util.HashMap;

public final class GUIStorage {

    private final static HashMap<String, GUI> container = new HashMap<>();

    private static GUI selectedGUI;

    private GUIStorage(){};



    public static void addGUI(String guiName, GUI newGUI) {
        checkDuplicates(guiName);
        container.put(guiName, newGUI);
    }

    public static void selectGUI(String guiName) {
        existenceCheck(guiName);
        selectedGUI = container.get(guiName);
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
