package org.crafter.engine.gui.components;

import org.crafter.engine.camera.Camera;
import org.crafter.engine.gui.GUI;
import org.crafter.engine.gui.enumerators.Alignment;
import org.crafter.engine.gui.font.Font;
import org.crafter.engine.gui.implementations.Text;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.window.Window;
import org.joml.Vector2f;
import org.joml.Vector2fc;

/**
 * Holds text data in memory.
 */
public class Label extends Text {

    public Label(String name, String textData, float fontSize, Alignment alignment, Vector2f offset) {
        super(name,  textData, fontSize, alignment, offset);

        recalculateMesh();
    }

    @Override
    public void render() {
        Camera.setGuiObjectMatrix(_position.x, _position.y);
        MeshStorage.render(this._meshUUID);
    }

    @Override
    public boolean collisionDetect(Vector2fc mousePosition) {
        // Does nothing
        return false;
    }

    @Override
    protected void recalculateMesh() {
//        System.out.println("Label: generating a new mesh");
        if (_meshUUID != null) {
            MeshStorage.destroy(_meshUUID);
        }

        Font.switchColor(foreGroundColor);
        Font.switchShadowColor(shadowColor);

        this.setMeshUUID(Font.grabText(this.fontSize * getGuiScale(), this.textData));

        this.setSize(Font.getTextSize(this.fontSize * getGuiScale(), this.textData));

        this.recalculatePosition();
    }

    @Override
    public void internalOnStep(GUI gui) {
        if (Window.wasResized()) {
            recalculateMesh();
        }
    }

    @Override
    protected void recalculatePosition() {
        this._position.set(_alignment.value().mul(Window.getWindowSize()).sub(getSize().mul(_alignment.value())).add(offset()));
//        System.out.println("Label (" + this.name() + ") POSITION: " + _position.x + ", " + _position.y);
    }
}
