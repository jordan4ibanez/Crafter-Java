package org.crafter.engine.gui.components;

import org.crafter.engine.gui.enumerators.Alignment;
import org.crafter.engine.gui.implementations.Text;

public class Button extends GUIElement implements Text {


    private String textData;

    private float fontSize;

    public Button(String name, Alignment alignment) {
        super(name, alignment);
    }


    @Override
    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    @Override
    public void setText(String textData) {
        this.textData = textData;
    }


    @Override
    public void render() {

    }

    @Override
    public boolean collisionDetect() {

        // This needs a return
        return false;
    }

}
