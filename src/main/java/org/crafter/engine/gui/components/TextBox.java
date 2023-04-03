package org.crafter.engine.gui.components;

import org.crafter.engine.camera.Camera;
import org.crafter.engine.gui.enumerators.Alignment;
import org.crafter.engine.gui.factories.FramedMeshFactory;
import org.crafter.engine.gui.font.Font;
import org.crafter.engine.gui.implementations.Text;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.window.Window;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class TextBox extends Text {

    // We want these to be constant throughout the entire game, class members only
    private static final float padding = 16.0f;
    private static final float pixelEdge = 1.0f;
    private static final float borderScale = 2.0f;

    private String buttonBackGroundMeshUUID = null;

    private final String placeHolderText;

    private final float boxWidth;


    public TextBox(String name, String placeHolderText, float fontSize, Alignment alignment, Vector2f offset, float boxWidth) {
        super(name, "", fontSize, alignment, offset);
        this._collide = true;
        this.placeHolderText = placeHolderText;
        this.boxWidth = boxWidth;
        recalculateMesh();
    }

    @Override
    public void render() {
        Camera.setGuiObjectMatrix(_position.x + getPadding(), _position.y + getPadding());
        MeshStorage.render(this._meshUUID);
        Camera.setGuiObjectMatrix(_position.x, _position.y);
        MeshStorage.render(this.buttonBackGroundMeshUUID);
    }

    @Override
    public void internalOnStep() {
        if (Window.wasResized()) {
            recalculateMesh();
        }
    }

    @Override
    protected void recalculatePosition() {
        this._position.set(_alignment.value().mul(Window.getWindowSize()).sub(getSize().mul(_alignment.value())).add(offset()));
    }

    @Override
    public boolean collisionDetect(Vector2fc mousePosition) {
        return pointCollisionDetect(mousePosition.x(), mousePosition.y(), _position.x(), _position.y(), _size.x(), _size.y());
    }

    @Override
    protected void recalculateMesh() {
        if (_meshUUID != null) {
            MeshStorage.destroy(_meshUUID);
        }
        if (buttonBackGroundMeshUUID != null) {
            MeshStorage.destroy(buttonBackGroundMeshUUID);
        }

        Vector2f boxSize = Font.getTextSize(this.fontSize * getGuiScale(), this.textData);
        boxSize.x = getBoxWidth();

        buttonBackGroundMeshUUID = FramedMeshFactory.generateMesh(boxSize, getPadding(), getPixelEdge(), getBorderScale(), "textures/text_box.png");

        Font.switchColor(foreGroundColor);
        Font.switchShadowColor(shadowColor);
        _meshUUID = Font.grabText(this.fontSize * getGuiScale(), this.textData);

        // Padding times 2 because all edges of the button are padding, doubled on X and Y
        this.setSize(boxSize.add(new Vector2f(getPadding() * 2)));

        this.recalculatePosition();
    }

    public float getBoxWidth() {
        return boxWidth * getGuiScale();
    }

    public static float getPadding() {
        return padding * getGuiScale();
    }

    public static float getPixelEdge() {
        return pixelEdge;
    }

    public static float getBorderScale() {
        return borderScale;
    }
}
