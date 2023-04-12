package org.crafter.engine.texture.texture_packer;

import org.joml.Vector2ic;
import org.joml.Vector4i;

import java.nio.ByteBuffer;
import java.util.*;

import static org.lwjgl.stb.STBImageWrite.*;

/**
 * This is translated from a D project.
 * <a href="https://github.com/jordan4ibanez/fast_pack/blob/main/source/fast_pack.d">Original project.</a>
 */
public class TexturePacker {

    // Ignore intellij, these are extremely useful to modify up top!
    private final int padding = 1;
    private final Vector4i edgeColor = new Vector4i(0,0,0,255);
    private final Vector4i blankSpaceColor = new Vector4i(0,0,0,0);
    private final int expansionAmount = 16;
    private final boolean showDebugEdge = false;

    private final int width = 16;
    private final int height = 16;

    private int CANVAS_MAX_WIDTH = 0;
    private int CANVAS_MAX_HEIGHT = 0;

    private int currentID = 0;
    private final HashMap<String, TexturePackerObject> textures;
    private final Canvas canvas;
    private final Set<Integer> availableX;
    private final Set<Integer> availableY;

    /**
     * Buffer automatically cleans up it's data upon flush().
     * But it's still useful to have it as it contains coordinates for textures!
     * So we must lock it out.
     */
    private boolean lockedOut = false;


    public TexturePacker() {
        textures = new HashMap<>();
        canvas = new Canvas(width, height);
        availableX = new HashSet<>();
        availableY = new HashSet<>();

        // Needs defaults (top left) or turns into infinite loop
        availableX.add(padding);
        availableY.add(padding);
    }

    public void add(String fileLocation) {
        lockoutCheck("add");
        nullCheck("fileLocation", fileLocation);
        duplicateCheck(fileLocation);
        textures.put(fileLocation, new TexturePackerObject(fileLocation));
    }

    public void debugPrintCanvas() {
        lockoutCheck("debugPrintCanvas");
        pack();
        stbi_write_png("test.png", canvas.getSize().x(), canvas.getSize().y(), 4, canvas.getData(), canvas.getSize().x() * 4);
    }

    public ByteBuffer flush() {
        lockoutCheck("flush");
        pack();
        return canvas.getData();
    }

    private void pack() {
        for (TexturePackerObject object : textures.values()) {
            int counter = 0;
            while(!tetrisPack(object)) {
                System.out.println("run " + counter + " on object (" + object.getUuid().toString() + ")");
                Vector2ic gottenCanvasSize = canvas.getSize();
                canvas.resize(gottenCanvasSize.x() + expansionAmount, gottenCanvasSize.y() + expansionAmount);
            }
        }
        flushCanvas();
    }

    private void flushCanvas() {

        canvas.resize(CANVAS_MAX_WIDTH, CANVAS_MAX_HEIGHT);
        canvas.allocate();

        for (TexturePackerObject object : textures.values()) {

            final int posX = object.getPosition().x();
            final int posY = object.getPosition().y();

            for (int x = 0; x < object.getSize().x(); x++) {
                for (int y = 0; y < object.getSize().y(); y++) {
                    canvas.setPixel(object.getPixel(x,y), x + posX, y + posY);
                }
            }
        }

        lockedOut = true;

        for (TexturePackerObject object : textures.values()) {
            object.destroy();
        }

    }

    private boolean tetrisPack(TexturePackerObject object) {

        boolean found = false;

        int score = Integer.MAX_VALUE;

        final int maxX = canvas.getSize().x();
        final int maxY = canvas.getSize().y();

        final int thisWidth = object.getSize().x();
        final int thisHeight = object.getSize().y();

        int bestX = padding;
        int bestY = padding;

        for (int y : availableY) {

            if (found) {
                break;
            }

            for (int x : availableX) {

                int newScore = x + y;

                if (newScore >= score) {
                    continue;
                }

                if (x + thisWidth + padding >= maxX || y + thisHeight + padding >= maxY) {
                    continue;
                }

                boolean failed = false;

                for (TexturePackerObject otherObject : textures.values()) {


                    if (otherObject.getUuid().equals(object.getUuid())) {
                        continue;
                    }

                    final int otherX = otherObject.getPosition().x();
                    final int otherY = otherObject.getPosition().y();

                    final int otherWidth = otherObject.getSize().x();
                    final int otherHeight = otherObject.getSize().y();

                    if (otherObject.getPacked() &&
                            otherX + otherWidth + padding > x &&
                            otherX <= x + thisWidth + padding &&
                            otherY + otherHeight + padding > y &&
                            otherY <= y + thisHeight + padding) {
                        failed = true;
                        break;
                    }
                }

                if (!failed) {
                    found = true;
                    bestX = x;
                    bestY = y;
                    score = newScore;
                    break;
                }
            }
        }

        // Found has mutated
        if (!found) {
            return false;
        }

        object.setPosition(bestX, bestY);

        object.setPacked();

        final int spotRight = bestX + thisWidth + padding;
        final int spotBelow = bestY + thisHeight + padding;

        availableX.add(spotRight);
        availableY.add(spotBelow);

        if (spotRight > CANVAS_MAX_WIDTH) {
            CANVAS_MAX_WIDTH = spotRight;
        }
        if (spotBelow > CANVAS_MAX_HEIGHT) {
            CANVAS_MAX_HEIGHT = spotBelow;
        }

        return true;
    }


    private int getAndTickID() {
        final int gotten = currentID;
        currentID++;
        return gotten;
    }


    private void duplicateCheck(String fileLocation) {
        if (textures.containsKey(fileLocation)) {
            throw new RuntimeException("TexturePacker: Attempted to put duplicate of (" + fileLocation + ")!");
        }
    }

    private void nullCheck(String name, String data) {
        if (data == null) {
            throw new RuntimeException("TexturePacker: (" + name + ") cannot be null!");
        }
    }

    private void lockoutCheck(String methodName) {
        if (lockedOut) {
            throw new RuntimeException("TexturePacker: Attempted to run method (" + methodName + ") after flushing the buffer!");
        }
    }
}
