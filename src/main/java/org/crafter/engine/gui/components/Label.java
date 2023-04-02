package org.crafter.engine.gui.components;

import org.crafter.engine.camera.Camera;
import org.crafter.engine.gui.enumerators.Alignment;
import org.crafter.engine.gui.font.Font;
import org.crafter.engine.gui.implementations.Text;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.shader.ShaderStorage;
import org.crafter.engine.window.Window;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;

import java.util.Objects;

/**
 * Holds text data in memory.
 */
public class Label extends Text {



    public Label(String name, String textData, float fontSize, Alignment alignment, Vector2f offset, Vector3f foreGroundColor, Vector3f shadowColor) {
        super(name,  textData, fontSize, alignment);

        this.textData = textData;
        this.fontSize = fontSize;

        /**
         * Offset input is how far off it is from the root
         * Then required offset is an internal function that calls into the font library to find the text size so it stays locked to it's position
         */

        this._offset.set(Objects.requireNonNullElseGet(offset, () -> new Vector2f(0, 0)));

        if (foreGroundColor != null) {
            this.foreGroundColor.set(foreGroundColor);
        }

        if (shadowColor != null) {
            this.shadowColor.set(shadowColor);
        }

        recalculateMesh();
    }

    public void setForeGroundColor(float r, float g, float b) {
        this.foreGroundColor.set(r,g,b);
    }

    public void setShadowColor(float r, float b, float g) {
        this.shadowColor.set(r,g,b);
    }

    @Override
    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
         System.out.println("Fontsize for " + this.name() + " is " + this.fontSize);
        recalculateMesh();
    }

    @Override
    public void setText(String textData) {
        this.textData = textData;
        recalculateMesh();
    }



    @Override
    public void render() {
        Camera.setGuiObjectMatrix(_renderPosition.x, _renderPosition.y);
        MeshStorage.render(this._meshUUID);
    }

    @Override
    public boolean collisionDetect() {
//        System.out.println("collision detection");
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
