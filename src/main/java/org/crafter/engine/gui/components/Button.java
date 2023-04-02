package org.crafter.engine.gui.components;

import org.crafter.engine.camera.Camera;
import org.crafter.engine.gui.enumerators.Alignment;
import org.crafter.engine.gui.factories.ButtonMeshFactory;
import org.crafter.engine.gui.font.Font;
import org.crafter.engine.gui.implementations.Text;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.window.Window;
import org.joml.Vector2f;

public class Button extends Text {

    // We want these to be constant throughout the entire game, class members only

    public static final float padding = 10.0f;
    public static final float pixelEdge = 1.0f;
    public static final float borderScale = 2.0f;

    private String buttonBackGroundMeshUUID = null;



    public Button(String name,  String textData, float fontSize, Alignment alignment, Vector2f offset) {
        super(name, textData, fontSize, alignment, offset);

        recalculateMesh();
    }



    @Override
    public void render() {
        Camera.setGuiObjectMatrix(_position.x, _position.y);
        MeshStorage.render(this.buttonBackGroundMeshUUID);
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
        if (buttonBackGroundMeshUUID != null) {
            MeshStorage.destroy(buttonBackGroundMeshUUID);
        }

        Vector2f textSize = Font.getTextSize(this.fontSize, this.textData);

        buttonBackGroundMeshUUID = ButtonMeshFactory.generateMesh(textSize);
        _meshUUID = Font.grabText(this.fontSize, this.textData);

        // Padding times 2 because all edges of the button are padding, doubled on X and Y
        this.setSize(textSize.add(new Vector2f(padding * 2)));

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
        this._position.set(_alignment.value().mul(Window.getWindowSize()).sub(getSize().mul(_alignment.value())).add(offset()));
        System.out.println("Button (" + this.name() + ") POSITION: " + _position.x + ", " + _position.y);
    }

}
