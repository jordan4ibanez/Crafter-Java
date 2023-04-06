package org.crafter.engine.gui.components;

import org.crafter.engine.camera.Camera;
import org.crafter.engine.gui.GUI;
import org.crafter.engine.gui.enumerators.Alignment;
import org.crafter.engine.gui.factories.ColorRectangleFactory;
import org.crafter.engine.gui.factories.FramedMeshFactory;
import org.crafter.engine.gui.font.Font;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.window.Window;
import org.joml.*;

/**
 * Drop boxes are a bit complex. A bit hard to explain in a comment as well.
 */
public class DropMenu extends GUIElement {

    private static final float padding = 16.0f;
    private static final float pixelEdge = 1.0f;
    private static final float borderScale = 2.0f;

    private static final Vector4fc selectionBoxColor = new Vector4f(0.5f,0.5f,0.5f,1);


    private boolean collapsed = true;

    // We don't want the size to change basically
    private final float boxWidth;

    private final float textHeight;

    private final float fontSize;

    private int selectedOption = 0;

    // Holds the actual options string values
    private final String[] options;

    // A quick skip list that automatically allows rendering out the not selected options
    private final int[] renders;

    private String selectionBoxUUID = null;
    private String dropDownOpenUUID = null;
    private String dropDownCollapsedUUID = null;
    private String buttonUUID = null;
    private String buttonTextUUID = null;
    private String collapsedOptionUUID = null;

    // These are the options in full size
    private final String[] optionsUUID;

    private final float heightClosed;
    private final float heightOpen;


    public DropMenu(float boxWidth, String[] options, float fontSize, Alignment alignment, Vector2f offset) {
        super(alignment, offset);

        if (options.length < 2) {
            throw new RuntimeException("DropMenu: You must have more than one option in your drop menu!");
        }
        this.boxWidth = boxWidth;
        this.options = options;
        this.fontSize = fontSize;

        this.textHeight = Font.getTextSize(fontSize, " ").y();
        this.renders = new int[options.length - 1];
        this.optionsUUID = new String[options.length];

        heightClosed = textHeight + (padding * 2);
        heightOpen = (textHeight + (padding * 2)) * options.length;

        this._collide = true;

        recalculateMesh();
    }

    public void setCurrentOption() {
        //FIXME todo

    }


    @Override
    public void render() {
//        Camera.setGuiObjectMatrix(_position.x, _position.y);
//        MeshStorage.render(dropDownCollapsedUUID);

        // If collapsed

        // Main panel
        Camera.setGuiObjectMatrix(_position.x + getPadding(), _position.y + getPadding());
        MeshStorage.render(collapsedOptionUUID);
        Camera.setGuiObjectMatrix(_position.x, _position.y);
        MeshStorage.render(dropDownCollapsedUUID);

        // Drop down button
        Camera.setGuiObjectMatrix(_position.x + getButtonTextOffset(), _position.y + getPadding());
        MeshStorage.render(buttonTextUUID);
        Camera.setGuiObjectMatrix(_position.x + getCollapsedBoxWidth(), _position.y);
        MeshStorage.render(buttonUUID);
    }

    @Override
    public boolean collisionDetect(Vector2fc mousePosition) {
        if (collapsed) {
            return pointCollisionDetect(mousePosition.x(), mousePosition.y(), _position.x(), _position.y(), getFullWidth(), getCollapsedHeight());
        }
        return false;
    }

    @Override
    protected void recalculateMesh() {
//        if (_meshUUID != null) {
//            MeshStorage.destroy(_meshUUID);
//        }

        // If collapsed
        if (collapsed) {
            recalculateCollapsed();
            recalculateCollapsedText();
            recalculateButton();

            setSize(new Vector2f(getBoxWidth() + doublePadding(), (textHeight * getGuiScale()) + doublePadding()));
        }

        recalculateSelectionBox();



//        _meshUUID = ColorRectangleFactory.createColorRectangleMesh(512 * getGuiScale(),512 * getGuiScale(), 0,0,0,1);
//        System.out.println(_meshUUID);

        //FIXME needs to check if opened or closed with number of selections

//        setSize(new Vector2f(512 * getGuiScale(),512 * getGuiScale()));

        recalculatePosition();
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
        //FIXME todo

    }

    @Override
    public void internalOnHover(Vector2fc mousePosition) {
        System.out.println("DropMenu: hover function!");
    }

    @Override
    public void internalOnClick(Vector2fc mousePosition) {
        System.out.println("DropMenu: click function!");
    }

    private void recalculateCollapsedText() {
        if (collapsedOptionUUID != null) {
            MeshStorage.destroy(collapsedOptionUUID);
        }

        // FIXME: this should be selectable
        Font.switchColor(1,1,1);
        Font.switchShadowColor(0,0,0);

        boolean fits = false;
        String finalText = options[selectedOption];
        final int textLength = finalText.length();
        int currentTrim = 0;
        final float goalWidth = getCollapsedTextBoxWidth();

        while(!fits) {

            float gottenWidth = Font.getTextSize(fontSize * getGuiScale(), finalText).x();

            if (gottenWidth <= goalWidth) {
                fits = true;
            } else {
                currentTrim++;
                finalText = finalText.substring(0, textLength - currentTrim)  + "...";
            }
        }

        collapsedOptionUUID = Font.grabText(this.fontSize * getGuiScale(), finalText);

    }

    private void recalculateCollapsed() {
        if (dropDownCollapsedUUID != null) {
            MeshStorage.destroy(dropDownCollapsedUUID);
        }
        Vector2f boxSize = getSelectionBoxSize();
        boxSize.x -= getButtonWidth() + doublePadding();
        dropDownCollapsedUUID = FramedMeshFactory.generateMesh(boxSize, getPadding(), getPixelEdge(), getBorderScale(), "textures/button.png");
    }

    private void recalculateButton() {
        // The button works as a single unit
        if (buttonUUID != null) {
            MeshStorage.destroy(buttonUUID);
        }
        if (buttonTextUUID != null) {
            MeshStorage.destroy(buttonTextUUID);
        }

        buttonUUID = FramedMeshFactory.generateMesh(new Vector2f(getButtonWidth()), getPadding(), getPixelEdge(), getBorderScale(), "textures/button.png");

        // FIXME: this should be selectable
        Font.switchColor(1,1,0);
        Font.switchShadowColor(0,0,0);

        buttonTextUUID = Font.grabText(this.fontSize * getGuiScale(), "V");


    }

    private void recalculateSelectionBox() {
        if (selectionBoxUUID != null) {
            MeshStorage.destroy(selectionBoxUUID);
        }
        Vector2fc boxSize = getSelectionBoxSize();
        selectionBoxUUID = ColorRectangleFactory.createColorRectangleMesh(boxSize.x(), boxSize.y(), selectionBoxColor.x(), selectionBoxColor.y(), selectionBoxColor.z(), selectionBoxColor.w());
    }


    // Gets it with scaling!
    private Vector2f getSelectionBoxSize() {
        return new Vector2f(boxWidth * getGuiScale(), textHeight * getGuiScale());
    }

    private float doublePadding() {
        return getPadding() * 2.0f;
    }

    public static float getPadding() {
        return padding * getGuiScale();
    }

    private float getCollapsedBoxWidth() {
        return (boxWidth * getGuiScale()) - getButtonWidth();
    }

    private float getCollapsedTextBoxWidth() {
        return (boxWidth * getGuiScale()) - (getButtonWidth() + doublePadding());
    }

    private float getFullWidth() {
        return (boxWidth * getGuiScale()) + doublePadding();
    }

    private float getFullHeight() {
        //FIXME: this is very wrong
        return (textHeight * getGuiScale()) + doublePadding();
    }

    private float getCollapsedHeight() {
        return (textHeight * getGuiScale()) + doublePadding();
    }



    private float getButtonWidth() {
        return textHeight * getGuiScale();
    }

    private float getBoxWidth() {
        return boxWidth * getGuiScale();
    }

    private float getButtonTextOffset() {
        // This is a weird hardcode, but it works
        // Centers the button text on the drop button
        return getCollapsedBoxWidth() + getPadding() + ((fontSize * 0.25f) * getGuiScale());
    }

    public static float getPixelEdge() {
        return pixelEdge;
    }

    public static float getBorderScale() {
        return borderScale;
    }
}
