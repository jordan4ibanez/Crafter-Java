package org.crafter.engine.gui.components;

import org.crafter.engine.gui.enumerators.Alignment;
import org.crafter.engine.gui.implementations.Text;

public class Button extends GUIElement implements Text {


    private String textData = "";

    private float fontSize = 24.0f;

    public Button(String name, Alignment alignment) {
        super(name, alignment);

        recalculateTexture();
    }


    @Override
    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
        recalculateTexture();
    }

    @Override
    public void setText(String textData) {
        this.textData = textData;
        recalculateTexture();
    }



    @Override
    public void render() {

    }

    @Override
    public boolean collisionDetect() {

        // This needs a return
        return false;
    }

    @Override
    protected void recalculateTexture() {
        System.out.println("Button: generating a new mesh");
    }

}
