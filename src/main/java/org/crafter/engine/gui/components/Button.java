package org.crafter.engine.gui.components;

import org.crafter.engine.gui.enumerators.Alignment;
import org.crafter.engine.gui.factories.ButtonMeshFactory;
import org.crafter.engine.gui.font.Font;
import org.crafter.engine.gui.implementations.Text;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.window.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Button extends Text {

    private float padding;

    private String buttonBackGroundTexture = null;



    public Button(String name,  String textData, float fontSize, Alignment alignment, Vector2f offset, float padding) {
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
        if (_meshUUID != null) {
            MeshStorage.destroy(_meshUUID);
        }
        if (buttonBackGroundTexture != null) {
            MeshStorage.destroy(buttonBackGroundTexture);
        }

        buttonBackGroundTexture = ButtonMeshFactory.generateMesh();
        _meshUUID = Font.grabText(this.fontSize, this.textData);

        // Padding times 2 because all edges of the button are padding, doubled on X and Y
        this.setSize(Font.getTextSize(this.fontSize, this.textData).add(new Vector2f(padding * 2)));

        this.recalculatePosition();
    }

    @Override
    public void internalOnStep() {
        if (Window.wasResized()) {
            System.out.println("Button: Window resized!");
            this.recalculatePosition();
        }
    }

    @Override
    protected void recalculatePosition() {
        this._renderPosition.set(_alignment.value().mul(Window.getWindowSize()).sub(getSize().mul(_alignment.value())).add(offset()));
        System.out.println("Button (" + this.name() + ") RENDER POSITION: " + _renderPosition.x + ", " + _renderPosition.y);
    }

}
