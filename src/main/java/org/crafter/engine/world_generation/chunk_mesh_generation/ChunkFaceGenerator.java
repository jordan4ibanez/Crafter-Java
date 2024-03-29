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
package org.crafter.engine.world_generation.chunk_mesh_generation;

import org.crafter.engine.world.block.BlockDefinition;
import org.crafter.engine.world.block.BlockDefinitionContainer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Performance debugging note: There is literally no reason for this object to exist.
 * This object exists within ChunkMeshWorker.
 */
public class ChunkFaceGenerator {

    private final BlockDefinitionContainer definitionContainer;
    private final HashMap<String, float[]> faces;
    private final int[] indicesOrder = new int[]{0,1,2,2,3,0};

    // light level 15
    private final float[] classicBrightTop = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
    private final float[] classicBrightSideX = new float[]{0.925f, 0.925f, 0.925f, 1.0f};
    private final float[] classicBrightSideZ = new float[]{0.905f, 0.905f, 0.905f, 1.0f};

    // light level 12 or something
    private final float[] classicDarkTop = new float[]{0.8f, 0.8f, 0.8f, 1.0f};
    // light level 6 or something
    private final float[] classicDarkSide = new float[]{0.75f, 0.75f, 0.75f, 1.0f};

    public ChunkFaceGenerator(BlockDefinitionContainer definitionContainer) {

//        final boolean rewriteThis = true;
//
//        if (rewriteThis) {
//            throw new RuntimeException("This should not be assembling in objects like this, it's an absolute mess");
//        }

        this.definitionContainer = definitionContainer;

        faces = new HashMap<>();

        // Blocks are rooted at 0,0,0 x,y,z floating so negative positions are zeroed

        // Note: Right handed coordinate system

        // Todo: Probably need to check these and make sure nothing is upside down! (textures)

        float OVER_PROVISION = 0.00001f;

        //-Z
        // fixme X and Y need over-provisioning
        faces.put("front", new float[]{
                //x,y,z
                // top right
                1 + OVER_PROVISION, 1 + OVER_PROVISION, 0,
                // bottom right
                1 + OVER_PROVISION, 0 - OVER_PROVISION, 0,
                // bottom left
                0 - OVER_PROVISION, 0 - OVER_PROVISION, 0,
                // top left
                0 - OVER_PROVISION, 1 + OVER_PROVISION, 0
        });

        //+Z
        // fixme X and Y need over-provisioning
        faces.put("back", new float[]{
                //x,y,z
                // top left
                0 - OVER_PROVISION, 1 + OVER_PROVISION, 1,
                // bottom left
                0 - OVER_PROVISION, 0 - OVER_PROVISION, 1,
                // bottom right
                1 + OVER_PROVISION, 0 - OVER_PROVISION, 1,
                // top right
                1 + OVER_PROVISION, 1 + OVER_PROVISION, 1
        });



        //-X
        // fixme Y and Z need over-provisioning
        faces.put("left", new float[]{
                //x,y,z
                // top left
                0, 1 + OVER_PROVISION, 0 - OVER_PROVISION,
                // bottom left
                0, 0 - OVER_PROVISION, 0 - OVER_PROVISION,
                // bottom right
                0, 0 - OVER_PROVISION, 1 + OVER_PROVISION,
                // top right
                0, 1 + OVER_PROVISION, 1 + OVER_PROVISION
        });
        //+X
        // fixme Y and Z need over-provisioning
        faces.put("right", new float[]{
                //x,y,z
                // top right
                1, 1 + OVER_PROVISION, 1 + OVER_PROVISION,
                // bottom right
                1, 0 - OVER_PROVISION, 1 + OVER_PROVISION,
                // bottom left
                1, 0 - OVER_PROVISION, 0 - OVER_PROVISION,
                // top left
                1, 1 + OVER_PROVISION, 0 - OVER_PROVISION
        });

        //-Y
        // fixme X and Z need over-provisioning
        faces.put("bottom", new float[]{
                //x,y,z
                // top right
                1 + OVER_PROVISION, 0, 1 + OVER_PROVISION,
                // bottom right
                0 - OVER_PROVISION, 0, 1 + OVER_PROVISION,
                // bottom left
                0 - OVER_PROVISION, 0, 0 - OVER_PROVISION,
                // top left
                1 + OVER_PROVISION, 0, 0 - OVER_PROVISION
        });

        //+Y
        // fixme X and Z need over-provisioning
        faces.put("top", new float[]{
                //x,y,z
                // top left
                1 + OVER_PROVISION, 1, 0 - OVER_PROVISION,
                // bottom left
                0 - OVER_PROVISION, 1, 0 - OVER_PROVISION,
                // bottom right
                0 - OVER_PROVISION, 1, 1 + OVER_PROVISION,
                // top right
                1 + OVER_PROVISION, 1, 1 + OVER_PROVISION
        });
    }

    public void attachBack(
            final int ID,
            final int x,
            final int y,
            final int z,
            final ArrayList<Float> positions,
            final ArrayList<Float> textureCoordinates,
            final ArrayList<Integer> indices,
            final ArrayList<Float> colors
    ) {
        dispatch("back", ID, x, y, z, positions, textureCoordinates, indices, colors);
    }
    public void attachFront(
            final int ID,
            final int x,
            final int y,
            final int z,
            final ArrayList<Float> positions,
            final ArrayList<Float> textureCoordinates,
            final ArrayList<Integer> indices,
            final ArrayList<Float> colors
    ) {
        dispatch("front", ID, x, y, z, positions, textureCoordinates, indices, colors);
    }

    public void attachLeft(
            final int ID,
            final int x,
            final int y,
            final int z,
            final ArrayList<Float> positions,
            final ArrayList<Float> textureCoordinates,
            final ArrayList<Integer> indices,
            final ArrayList<Float> colors
    ) {
        dispatch("left", ID, x, y, z, positions, textureCoordinates, indices, colors);
    }
    public void attachRight(
            final int ID,
            final int x,
            final int y,
            final int z,
            final ArrayList<Float> positions,
            final ArrayList<Float> textureCoordinates,
            final ArrayList<Integer> indices,
            final ArrayList<Float> colors
    ) {
        dispatch("right", ID, x, y, z, positions, textureCoordinates, indices, colors);
    }

    public void attachBottom(
            final int ID,
            final int x,
            final int y,
            final int z,
            final ArrayList<Float> positions,
            final ArrayList<Float> textureCoordinates,
            final ArrayList<Integer> indices,
            final ArrayList<Float> colors
    ) {
        dispatch("bottom", ID, x, y, z, positions, textureCoordinates, indices, colors);
    }
    public void attachTop(
            final int ID,
            final int x,
            final int y,
            final int z,
            final ArrayList<Float> positions,
            final ArrayList<Float> textureCoordinates,
            final ArrayList<Integer> indices,
            final ArrayList<Float> colors
    ) {
        dispatch("top", ID, x, y, z, positions, textureCoordinates, indices, colors);
    }

    private void dispatch(
            final String face,
            final int ID,
            final int x,
            final int y,
            final int z,
            final ArrayList<Float> positions,
            final ArrayList<Float> textureCoordinates,
            final ArrayList<Integer> indices,
            final ArrayList<Float> colors
    ) {
        // Texture coordinates
        BlockDefinition thisBlockDef = definitionContainer.getDefinition(ID);

        // It's a blank face, ignore it - Note: This SHOULD NOT be reached, EVER!
        if (!thisBlockDef.containsTextureCoordinate(face)) {
            throwSevereWarning(face);
            return;
        }
        // Vertex positions
        final float[] hardCodedPos = faces.get(face);
        for (int i = 0; i < hardCodedPos.length; i++) {
            final int xyz = i % 3;
            final float floatingPos = hardCodedPos[i];
            switch (xyz) {
                case 0 -> positions.add(floatingPos + x);
                case 1 -> positions.add(floatingPos + y);
                case 2 -> positions.add(floatingPos + z);
                default -> throw new RuntimeException("ChunkFaceGenerator: Got error in modulo calculation! Expected: (0-2) | Got: " + xyz + "!");
            }
        }
        switch(face) {
            case "back","front" -> classicDispatchColors(colors, classicBrightSideZ);
            case "left","right" -> classicDispatchColors(colors, classicBrightSideX);
            case "bottom" -> classicDispatchColors(colors, classicDarkTop);
            case "top" -> classicDispatchColors(colors, classicBrightTop);
        }
        // Texture coordinates
        // This is separated here in case this ever decides to poop out with an error
        float[] defTextureCoordinates = thisBlockDef.getTextureCoordinate(face);
        for (float defTextureCoordinate : defTextureCoordinates) {
            textureCoordinates.add(defTextureCoordinate);
        }
        // Indices
        seedIndices(indices);
    }


    private void classicDispatchColors(final ArrayList<Float> colors, final float[] classicColorLevel) {
        for (int i = 0; i < 4; i++) {
            for (float colorValue : classicColorLevel) {
                colors.add(colorValue);
            }
        }
    }


    private void seedIndices(final ArrayList<Integer> indices) {
        final int length = (indices.size() / 6) * 4;
        for (int i : indicesOrder) {
            indices.add(i + length);
        }
    }

    private void throwSevereWarning(String face) {
        System.out.println("ChunkFaceGenerator: WARNING! A BLOCK DEFINITION HAS A BLANK FACE SOMEHOW! Face: (" + face + ")!");
    }

}
