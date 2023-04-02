package org.crafter.engine.gui.components;

import org.crafter.engine.camera.Camera;
import org.crafter.engine.gui.enumerators.Alignment;
import org.crafter.engine.gui.font.Font;
import org.crafter.engine.gui.implementations.Text;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.window.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Objects;

/**
 * Holds text data in memory.
 */
public class Label extends Text {

    public Label(String name, String textData, float fontSize, Alignment alignment, Vector2f offset, Vector3f foreGroundColor, Vector3f shadowColor) {
        super(name,  textData, fontSize, alignment, offset);

        if (foreGroundColor != null) {
            this.foreGroundColor.set(foreGroundColor);
        }

        if (shadowColor != null) {
            this.shadowColor.set(shadowColor);
        }

        recalculateMesh();
    }

    @Override
    public void render() {
        Camera.setGuiObjectMatrix(_renderPosition.x, _renderPosition.y);
        MeshStorage.render(this._meshUUID);
    }

    @Override
    public boolean collisionDetect() {
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

        String newUUID = Font.grabText(this.fontSize, this.textData);

        setCenteringVector(Font.getTextSize(this.fontSize, this.textData));

        this.setMeshUUID(newUUID);

        this.recalculatePosition();
    }

    @Override
    public void internalOnStep() {
        if (Window.wasResized()) {
            this.recalculatePosition();
        }
    }

    @Override
    protected void recalculatePosition() {
        this._renderPosition.set(_alignment.value().mul(Window.getWindowSize()).sub(getCenteringVector().mul(_alignment.value())).add(offset()));
        System.out.println("Label (" + this.name() + ") RENDER POSITION: " + _renderPosition.x + ", " + _renderPosition.y);
    }
}
