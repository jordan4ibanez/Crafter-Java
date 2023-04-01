package org.crafter.engine.gui.components;

import org.crafter.engine.camera.Camera;
import org.crafter.engine.gui.enumerators.Alignment;
import org.crafter.engine.gui.font.Font;
import org.crafter.engine.gui.implementations.Text;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.shader.ShaderStorage;
import org.crafter.engine.window.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Objects;

/**
 * Holds text data in memory.
 */
public class Label extends GUIElement implements Text {

    private String textData = "";

    private float fontSize = 24.0f;

    private Vector3f color;

    public Label(String name, String textData, float fontSize, Alignment alignment, Vector2f offset) {
        super(name, alignment);

        this.textData = textData;
        this.fontSize = fontSize;

        /**
         * Offset input is how far off it is from the root
         * Then required offset is an internal function that calls into the font library to find the text size so it stays locked to it's position
         */

        this._offset.set(Objects.requireNonNullElseGet(offset, () -> new Vector2f(0, 0)));

        recalculateMesh();
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

        Font.enableShadows();
        Font.switchColor(1,0,0);

        String newUUID = Font.grabText(this.fontSize, this.textData);

        this._centeringVector.set(Font.getTextCenter(this.fontSize, this.textData));

        System.out.println(_centeringVector.x + ", " + _centeringVector.y);

        this.setMeshUUID(newUUID);
    }

    @Override
    public void internalOnStep() {
        if (Window.wasResized()) {
            this.recalculatePosition();
        }
    }

    @Override
    protected void recalculatePosition() {
        System.out.println("Recalculation!");
    }
}
