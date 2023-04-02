package org.crafter.engine.gui.components;

import org.crafter.engine.gui.enumerators.Alignment;
import org.crafter.engine.gui.factories.ButtonMeshFactory;
import org.crafter.engine.gui.implementations.Text;
import org.crafter.engine.window.Window;
import org.joml.Vector2f;

public class Button extends GUIElement implements Text {

    private Label text = null;

    private float padding;

    private final Vector2f size = new Vector2f(0,0);

    public Button(String name, Alignment alignment, Label text, int padding) {
        super(name, alignment);
        if (text == null) {
            throw new RuntimeException("Button: ERROR in (" + this.name() + ")! Text cannot be null!");
        }
        this.text = text;
        this.padding = padding;

        recalculateMesh();
    }


    @Override
    public void setFontSize(float fontSize) {
        this.text.setFontSize(fontSize);
        recalculateMesh();
    }

    @Override
    public void setText(String textData) {
        this.text.setText(textData);
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
        ButtonMeshFactory.generateMesh();
    }

    @Override
    public void internalOnStep() {
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
