package org.crafter.engine.gui.actions;

import org.crafter.engine.gui.GUI;
import org.crafter.engine.gui.components.GUIElement;

public interface KeyInput {
    void action(GUI gui, GUIElement element, int keyboardKey);
}
