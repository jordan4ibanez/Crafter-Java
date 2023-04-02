package org.crafter.engine.gui.components;

import org.crafter.engine.gui.enumerators.Alignment;
import org.crafter.engine.gui.factories.ButtonMeshFactory;
import org.crafter.engine.gui.implementations.Text;
import org.crafter.engine.window.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Button extends Text {

    private float padding;

    private final Vector2f size = new Vector2f(0,0);

    public Button(String name,  String textData, float fontSize, Alignment alignment, Vector2f offset, Vector3f foreGroundColor, Vector3f shadowColor) {
        super(name, textData, fontSize, alignment, offset);

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
