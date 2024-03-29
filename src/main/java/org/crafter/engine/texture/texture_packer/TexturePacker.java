/*
 Crafter - A blocky game (engine) written in Java with LWJGL.
 Copyright (C) 2023  jordan4ibanez

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.crafter.engine.texture.texture_packer;

import org.joml.*;

import java.nio.ByteBuffer;
import java.util.*;

import static java.util.Collections.reverseOrder;
import static org.joml.Math.floor;
import static org.lwjgl.stb.STBImageWrite.*;

/**
 * This is translated from a D project.
 * <a href="https://github.com/jordan4ibanez/fast_pack/blob/main/source/fast_pack.d">Original project.</a>
 * Now works as a singleton.
 */
public class TexturePacker {

    // Ignore intellij, these are extremely useful to modify up top!
    private final int padding = 1;

    // These were from the D project, but hey, maybe one day they'll be reimplemented
    private final Vector4i edgeColor = new Vector4i(0,0,0,255);
    private final Vector4i blankSpaceColor = new Vector4i(0,0,0,0);
    private final boolean showDebugEdge = false;

    private final int expansionAmount = 16;

    private final int width = 16;
    private final int height = 16;

    private int CANVAS_MAX_WIDTH = 0;
    private int CANVAS_MAX_HEIGHT = 0;
    private final HashMap<String, TexturePackerObject> textures;
    private final Canvas canvas;
    private final SortedSet<Integer> availableX;
    private final SortedSet<Integer> availableY;

    /**
     * Buffer automatically cleans up it's data upon flush().
     * But it's still useful to have it as it contains coordinates for textures!
     * So we must lock it out.
     */
    private boolean lockedOut = false;


    public TexturePacker() {
        textures = new HashMap<>();
        canvas = new Canvas(width, height);
        availableX = new TreeSet<>();
        availableY = new TreeSet<>();

        // Needs defaults (top left) or turns into infinite loop
        availableX.add(padding);
        availableY.add(padding);
    }

    public boolean isEmpty() {
        return textures.isEmpty();
    }

    /**
     * "dirt.png", "water.png", etc
     * @param fileName The name which you gave the file.
     * @return if it exists.
     */
    public boolean fileNameExists(String fileName) {
        enforceLockout("fileNameExists");
        return textures.containsKey(fileName);
    }

    /**
     * Returns a literal location and size in texture atlas, not adjusted to OpenGL!
     * Specifically implemented to make making quads easier.
     * @param fileName the name of the texture in the texture atlas.
     * @return a Vector4ic containing (position X, position Y, width, height)
     */
    public Vector4ic getIntegralPositions(String fileName) {
        existenceCheck(fileName);
        return textures.get(fileName).getPositionAndSize();
    }

    /**
     * Returns an OpenGL adjusted location and size in texture atlas.
     * @param fileName the name of the texture in the texture atlas.
     * @return a Vector4fc containing OpenGL Scaled (position X, position Y, width, height)
     */
    public Vector4fc getOpenGLPositions(String fileName) {

        enforceLockout("getOpenGLPositions");

        Vector4ic gottenIntegralPositionAndSize = getIntegralPositions(fileName);

//        System.out.println(gottenIntegralPositionAndSize.x() + "," + gottenIntegralPositionAndSize.y() + "," + gottenIntegralPositionAndSize.z() + "," + gottenIntegralPositionAndSize.w());

        //!!!WARNING!!! If this is removed, there will be shimmering stitch lines all over the map!
        final float UNDER_PROVISION = 0.00001f;

        return new Vector4f(
                // Position X
                ((float)gottenIntegralPositionAndSize.x() / (float) CANVAS_MAX_WIDTH) + UNDER_PROVISION,
                // Position Y
                ((float)gottenIntegralPositionAndSize.y() / (float) CANVAS_MAX_HEIGHT) + UNDER_PROVISION,
                // Width
                ((float)gottenIntegralPositionAndSize.z() / (float) CANVAS_MAX_WIDTH) - (UNDER_PROVISION * 2.0f),
                // Height
                ((float)gottenIntegralPositionAndSize.w() / (float) CANVAS_MAX_HEIGHT) - (UNDER_PROVISION * 2.0f)
        );
    }

    /**
     * Returns a float[] of quad points.
     * @param fileName the name of the texture in the texture atlas.
     * @return a float[] containing exactly OpenGL Positions. xy[top left, bottom left, bottom right, top right]
     */
    public float[] getQuadOf(String fileName) {

        enforceLockout("getQuadOf");

        // this var was originally called: gottenOpenGLPositionAndSize. You can probably see why I changed it
        Vector4fc p = getOpenGLPositions(fileName);
//        System.out.println(p.x() + "," + p.y() + "," + p.z() + "," + p.w());
        // Z = width
        // W = height
        return new float[] {
                // Top left
                p.x(),         p.y(),
                // Bottom left
                p.x(),         p.y() + p.w(),
                // Bottom right
                p.x() + p.z(), p.y() + p.w(),
                // Top right
                p.x() + p.z(), p.y()
        };
    }

    /**
     *
     * @param fileName location of the file!
     * @param xLeftTrim How far to trim into the left side towards the right (0.0f, 1.0f)
     * @param xRightTrim How far to trim into the right towards the left (0.0f, 1.0f)
     * @param yTopTrim How far to trim into the top towards the bottom (0.0f, 1.0f)
     * @param yBottomTrim How far to trim into the bottom towards the top (0.0f, 1.0f)
     * @return a float[] containing exactly OpenGL Positions. xy[top left, bottom left, bottom right, top right]
     */
    public float[] getQuadOf(String fileName, float xLeftTrim, float xRightTrim, float yTopTrim, float yBottomTrim) {

        enforceLockout("getQuadOf");

        // o stands for original position
        final String[] names = new String[]{"xLeftTrim", "xRightTrim", "yTopTrim", "yBottomTrim"};
        final float[] values = new float[]{xLeftTrim, xRightTrim, yTopTrim, yBottomTrim};

        for (int i = 0; i < values.length; i++) {
            final float gottenValue = values[i];
            if (gottenValue > 1 || gottenValue < 0) {
                throw new RuntimeException("TexturePacker: Trimming value for (" + names[i] + ") is out of bounds! Min 0.0f | max 1.0f");
            }
        }

        Vector4fc o = getOpenGLPositions(fileName);

        // Z = width
        // W = height

        final float adjustedXLeftTrim = (xLeftTrim * o.z()) + o.x();
        final float adjustedXRightTrim = (xRightTrim * o.z()) + o.x() + o.z();
        final float adjustedYTopTrim = (yTopTrim * o.w()) + o.y();
        final float adjustedYBottomTrim = (yBottomTrim * o.w()) + o.y() + o.w();

        // p stands for position
        return new float[]{
                // Top left
                adjustedXLeftTrim,   adjustedYTopTrim,
                // Bottom left
                adjustedXLeftTrim,   adjustedYBottomTrim,
                // Bottom right
                adjustedXRightTrim , adjustedYBottomTrim,
                // Top right
                adjustedXRightTrim,  adjustedYTopTrim
        };
    }

    public void add(String fileName, String fileLocation) {
        lockoutCheck("add");
        nullCheck("name", fileName);
        nullCheck("fileLocation", fileLocation);
        duplicateCheck(fileName);
        textures.put(fileName, new TexturePackerObject(fileLocation));
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
            while(!tetrisPack(object)) {
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

//        int score = Integer.MAX_VALUE;

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

//                int newScore = x + y;
//
//                System.out.println(newScore);
//
////                if (newScore > score) {
////                    continue;
////                }

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
//                    score = newScore;
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

    public Vector2ic getCanvasSize() {
        enforceLockout("getCanvasSize");
        return canvas.getSize();
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

    private void existenceCheck(String fileLocation) {
        if (!textures.containsKey(fileLocation)) {
            throw new RuntimeException("TexturePacker: Attempted to access (" + fileLocation + ") which is a nonexistent texture!");
        }
    }

    private void lockoutCheck(String methodName) {
        if (lockedOut) {
            throw new RuntimeException("TexturePacker: Attempted to run method (" + methodName + ") after flushing the buffer!");
        }
    }

    private void enforceLockout(String methodName) {
        if (!lockedOut) {
            throw new RuntimeException("TexturePacker: Attempted to run method (" + methodName + ") before flushing the buffer!");
        }
    }
}
