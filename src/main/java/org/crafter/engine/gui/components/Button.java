package org.crafter.engine.gui.components;

import org.crafter.engine.gui.enumerators.Alignment;
import org.crafter.engine.gui.implementations.Text;
import org.crafter.engine.window.Window;

public class Button extends GUIElement implements Text {

    private Label text;

    private String textData = "";

    private float fontSize = 24.0f;

    public Button(String name, Alignment alignment) {
        super(name, alignment);

        recalculateMesh();
    }


    @Override
    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
        recalculateMesh();
    }

    @Override
    public void setText(String textData) {
        this.textData = textData;
        recalculateMesh();
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
    protected void recalculateMesh() {
        System.out.println("Button: generating a new mesh");
    }

    @Override
    public void internalOnStep() {
        System.out.println("internal on step");
        if (Window.wasResized()) {
            System.out.println("Button: Window resized!");
            recalculatePosition();
        }
    }

    @Override
    protected void recalculatePosition() {
        System.out.println("recalculating");
    }

}
