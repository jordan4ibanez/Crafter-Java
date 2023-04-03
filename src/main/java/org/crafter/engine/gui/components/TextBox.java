package org.crafter.engine.gui.components;

import org.crafter.engine.camera.Camera;
import org.crafter.engine.controls.Keyboard;
import org.crafter.engine.delta.Delta;
import org.crafter.engine.gui.GUI;
import org.crafter.engine.gui.enumerators.Alignment;
import org.crafter.engine.gui.factories.FramedMeshFactory;
import org.crafter.engine.gui.font.Font;
import org.crafter.engine.gui.implementations.Text;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.window.Window;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.security.Key;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;

public class TextBox extends Text {

    // We want these to be constant throughout the entire game, class members only
    private static final float padding = 16.0f;
    private static final float pixelEdge = 1.0f;
    private static final float borderScale = 2.0f;

    private String buttonBackGroundMeshUUID = null;

    private final String placeHolderText;

    private final float boxWidth;

    private static final Vector3f placeHolderColor = new Vector3f(0.5f);

    private float repeatTimer = 0.0f;
    private boolean repeating = false;

    // How far into the sentence the output of the textData is in the box
    private int entryCursorPosition = 0;

    private boolean cursorBlink = true;
    private float cursorTimer = 0.0f;

    private boolean clearOnSend = true;

    private boolean enterPressed = false;
    private boolean wasEnterPressed = false;

    private boolean focused = false;
    private boolean wasFocused = false;


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
    public void internalOnStep(GUI gui) {
        if (Window.wasResized()) {
            recalculateMesh();
        }
        if (!gui.getCurrentlyFocused().equals(name())) {
            cursorBlink = false;
            focused = false;
            if (wasFocused) {
                recalculateText();
                wasFocused = false;
                System.out.println("recalculating");
            }
            return;
        }
        focused = true;
        wasFocused = true;

        // FIXME: TURN THIS INTO AN INTERNAL IN THE KEYBOARD STATIC CLASS!
        boolean enterKeyPushed =  Keyboard.isKeyDown(GLFW_KEY_ENTER);

        if (!enterPressed && enterKeyPushed) {
            // One way lock
            enterPressed = true;
            if (!wasEnterPressed && enterInputable()) {
                _enterInput.action(gui, this, textData);
                if (clearOnSend) {
                    this.textData = "";
                    this.entryCursorPosition = 0;
                }
                wasEnterPressed = true;
            }
            return;
        } else if (enterPressed && !enterKeyPushed) {
            System.out.println("clearing");
            enterPressed = false;
            wasEnterPressed = false;
        }

        cursorTimer += Delta.getDelta();
        if (cursorTimer >= 0.25) {
            cursorTimer = 0.0f;
            cursorBlink = !cursorBlink;
            recalculateText();
        }

        if (Keyboard.hasTyped()) {

            textData += Keyboard.getLastInput();

            float textWidth = Font.getTextSize(this.fontSize * getGuiScale(), getTextWithCursorPos()).x;
            while (textWidth > (boxWidth * getGuiScale())) {
                entryCursorPosition++;
                textWidth = Font.getTextSize(this.fontSize * getGuiScale(), getTextWithCursorPos()).x;
            }

        } else if (Keyboard.isKeyDown(GLFW_KEY_BACKSPACE)) {

            int textLength = textData.length();
            if (textLength == 0) {
                return;
            }

            if (repeating && repeatTimer >= 0.05f) {
                backspaceTrim();
                repeatTimer = 0.0f;

            } else if (repeatTimer == 0.0f) {
                backspaceTrim();
            }
            repeatTimer += Delta.getDelta();

            if (repeatTimer >= 0.5f) {
                repeating = true;
                repeatTimer = 0.0f;
            }

        } else {
            repeating = false;
            repeatTimer = 0.0f;
        }

        recalculateText();
    }

    private void backspaceTrim() {
        textData = textData.substring(0, textData.length() - 1);

        boolean hit = false;
        float textWidth = Font.getTextSize(this.fontSize * getGuiScale(), getTextWithCursorPos()).x;
        while (textWidth > (boxWidth * getGuiScale())) {
            if (entryCursorPosition <= 0) {
                break;
            }
            hit = true;
            entryCursorPosition--;
            textWidth = Font.getTextSize(this.fontSize * getGuiScale(), getTextWithCursorPos()).x;
        }
        if (entryCursorPosition <= 0) {
            return;
        }
        if (!hit) {
            entryCursorPosition--;
        }
    }

    private String getTextWithCursorPos(){
        String manipulationString = textData.substring(entryCursorPosition);
        if (cursorBlink) {
            manipulationString += "_";
        } else {
            manipulationString += " ";
        }
        return manipulationString;
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

        // Only needs the height, so ship it nothing
        Vector2f boxSize = Font.getTextSize(this.fontSize * getGuiScale(), "");
        boxSize.x = getBoxWidth();

        buttonBackGroundMeshUUID = FramedMeshFactory.generateMesh(boxSize, getPadding(), getPixelEdge(), getBorderScale(), "textures/text_box.png");

        recalculateText();

        // Padding times 2 because all edges of the button are padding, doubled on X and Y
        this.setSize(boxSize.add(new Vector2f(getPadding() * 2)));

        this.recalculatePosition();
    }

    private void recalculateText() {
        String shownText;
        if (textData.equals("") && !focused) {
            shownText = placeHolderText;
            Font.switchColor(placeHolderColor);
        } else {
            shownText = getTextWithCursorPos();
            Font.switchColor(foreGroundColor);
        }
        Font.switchShadowColor(shadowColor);
        _meshUUID = Font.grabText(this.fontSize * getGuiScale(), shownText);
    }

    public String getText() {
        return textData;
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

    // This might be useful for something
    public TextBox disableClearOnSend() {
        clearOnSend = false;
        return this;
    }
}
