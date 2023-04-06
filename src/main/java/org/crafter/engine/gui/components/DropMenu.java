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
    private int hoverSelection = -1;

    // Holds the actual options string values
    private final String[] options;

    private String selectionBoxUUID = null;
    private String fullSizeBackgroundUUID = null;
    private String dropDownCollapsedUUID = null;
    private String buttonUUID = null;
    private String buttonTextUUID = null;
    private String collapsedOptionUUID = null;

    // These are the options in full size
    private final String[] optionsUUIDs;

    // This is so elements can be optimized, can poll if there's a new option
    private boolean newOption = false;

    public DropMenu(float boxWidth, String[] options, float fontSize, Alignment alignment, Vector2f offset, Integer defaultSelection) {
        super(alignment, offset);

        if (options.length < 2) {
            throw new RuntimeException("DropMenu: You must have more than one option in your drop menu!");
        }
        this.boxWidth = boxWidth;
        this.options = options;
        this.fontSize = fontSize;

        this.textHeight = Font.getTextSize(fontSize, " ").y();
        this.optionsUUIDs = new String[options.length];

        if (defaultSelection != null) {
            // Safety check
            if (defaultSelection == -1 || defaultSelection >= options.length) {
                throw new RuntimeException("DropMenu: Default selection is out of bounds!");
            }
            this.selectedOption = defaultSelection;
        }

        this._collide = true;

        recalculateMesh();
    }

    // External usage, for creating neat callbacks!
    public boolean newOption() {
        return newOption;
    }

    public String getSelectedOption() {
        return options[selectedOption];
    }
    // End neat callbacks, so sad

    public void setCurrentOption() {
        // Recreates the selection option text
        // Safety check
        if (hoverSelection == -1 || hoverSelection >= options.length) {
            return;
        }
        selectedOption = hoverSelection;
        recalculateCollapsedText();
    }


    @Override
    public void render() {

        if (collapsed) {
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
        } else {

            // Text options
            for (int i = 0; i < options.length; i++) {
                Camera.setGuiObjectMatrix(_position.x + getPadding(), _position.y + getPadding() + (i * textHeight * getGuiScale()));
                MeshStorage.render(optionsUUIDs[i]);
            }

            // Selection box
            if (hoverSelection != -1) {
                Camera.setGuiObjectMatrix(_position.x + getPadding(), _position.y + getPadding() + (hoverSelection * textHeight * getGuiScale()));
                MeshStorage.render(selectionBoxUUID);
            }

            // Background
            Camera.setGuiObjectMatrix(_position.x, _position.y);
            MeshStorage.render(fullSizeBackgroundUUID);
        }
    }

    @Override
    public boolean collisionDetect(Vector2fc mousePosition) {
        boolean collided = pointCollisionDetect(mousePosition.x(), mousePosition.y(), _position.x(), _position.y(), _size.x(), _size.y());
        if (!collided) {
            // This is cheap, simplistic logic prevents bugs
            hoverSelection = -1;
        }
        return collided;
    }

    @Override
    protected void recalculateMesh() {

        if (collapsed) {
            recalculateCollapsed();
            recalculateCollapsedText();
            recalculateButton();

            setSize(new Vector2f(getBoxWidth() + doublePadding(), (textHeight * getGuiScale()) + doublePadding()));
        } else {
            recalculateFullSizeBackground();
            recalculateOptions();

            setSize(new Vector2f(getBoxWidth() + doublePadding(), ((textHeight * getGuiScale()) * options.length) + doublePadding()));
        }

        // The window can get resized when the drop box is collapsed, making this outdated
        recalculateSelectionBox();

        recalculatePosition();
    }

    @Override
    public void internalOnStep(GUI gui) {
        if (Window.wasResized()) {
            recalculateMesh();
        }
        if (!collapsed && !gui.getCurrentlyFocused().equals(this.name())) {
            collapsed = true;
            recalculateMesh();
        }
    }

    @Override
    protected void recalculatePosition() {
        this._position.set(_alignment.value().mul(Window.getWindowSize()).sub(getSize().mul(_alignment.value())).add(offset()));
    }

    @Override
    public void internalOnHover(Vector2fc mousePosition) {

        newOption = false;

        if(collapsed) {
            return;
        }
        // Collide with elements
        for (int i = 0; i < options.length; i++) {
            if (pointCollisionDetect(mousePosition.x(), mousePosition.y(), _position.x() + getPadding(), (_position.y() + getPadding()) + (textHeight * getGuiScale() * (float)i), getBoxWidth(), textHeight * getGuiScale())) {
                hoverSelection = i;
                return;
            }
        }
        hoverSelection = -1;
    }

    @Override
    public void internalOnClick(Vector2fc mousePosition) {

        newOption = false;

        // Open up the selection box

        if (collapsed) {
            this.collapsed = false;
            recalculateMesh();
            return;
        }

        // Not collapsed but no selection

        if (hoverSelection == -1) {
            collapsed = true;
            recalculateMesh();
            return;
        }

        // Not collapsed and selected, update

        setCurrentOption();
        collapsed = true;
        recalculateMesh();

        newOption = true;

    }

    private void recalculateFullSizeBackground() {
        if (fullSizeBackgroundUUID != null) {
            MeshStorage.destroy(fullSizeBackgroundUUID);
        }

        final float width = (boxWidth * getGuiScale());
        final float height = (textHeight * getGuiScale()) * options.length;

        // (1.0f + ((float)options.length * 0.3f)) is an extreme hardcode, works pretty well, for now
        fullSizeBackgroundUUID = FramedMeshFactory.generateMesh(new Vector2f(width, height), getPadding(), getPixelEdge(), getBorderScale() / (1.0f + ((float)options.length * 0.3f)) ,"textures/button.png");
    }

    private void recalculateCollapsedText() {
        if (collapsedOptionUUID != null) {
            MeshStorage.destroy(collapsedOptionUUID);
        }


        Font.switchColor(1,1,1);
        Font.switchShadowColor(0,0,0);

        final String finalText = makeTextFit(options[selectedOption], getCollapsedTextBoxWidth());

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

    private void recalculateOptions() {
        for (String optionUUID : optionsUUIDs) {
            if (optionUUID != null) {
                MeshStorage.destroy(optionUUID);
            }
        }
        final float boxWidth = getSelectionBoxWidth();

        for (int i = 0; i < options.length; i++) {
            final String option = options[i];

            final String finalText = makeTextFit(option, boxWidth);

            Font.switchColor(1,1,1);
            Font.switchShadowColor(0,0,0);
            optionsUUIDs[i] = Font.grabText(this.fontSize * getGuiScale(), finalText);
        }
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

    private float getSelectionBoxWidth() {
        return boxWidth * getGuiScale();
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

    private String makeTextFit(final String inputString, final float requiredWidth) {
        boolean fits = false;
        String outputText = inputString;
        final int textLength = inputString.length();
        int currentTrim = 0;

        while(!fits) {

            float gottenWidth = Font.getTextSize(fontSize * getGuiScale(), outputText).x();

            if (gottenWidth <= requiredWidth) {
                fits = true;
            } else {
                currentTrim++;
                outputText = outputText.substring(0, textLength - currentTrim)  + "...";
            }
        }
        return outputText;
    }
}
