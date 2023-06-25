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

        //-Z
        faces.put("front", new float[]{
                //x,y,z
                // top right
                1,1,0,
                // bottom right
                1,0,0,
                // bottom left
                0,0,0,
                // top left
                0,1,0
        });

        //+Z
        faces.put("back", new float[]{
                //x,y,z
                // top left
                0,1,1,
                // bottom left
                0,0,1,
                // bottom right
                1,0,1,
                // top right
                1,1,1
        });



        //-X
        faces.put("left", new float[]{
                //x,y,z
                // top left
                0,1,0,
                // bottom left
                0,0,0,
                // bottom right
                0,0,1,
                // top right
                0,1,1
        });
        //+X
        faces.put("right", new float[]{
                //x,y,z
                // top right
                1,1,1,
                // bottom right
                1,0,1,
                // bottom left
                1,0,0,
                // top left
                1,1,0
        });

        //-Y
        faces.put("bottom", new float[]{
                //x,y,z
                // top right
                1,0,1,
                // bottom right
                0,0,1,
                // bottom left
                0,0,0,
                // top left
                1,0,0
        });

        //+Y
        faces.put("top", new float[]{
                //x,y,z
                // top left
                1,1,0,
                // bottom left
                0,1,0,
                // bottom right
                0,1,1,
                // top right
                1,1,1
        });
    }

    public void attachBack(final int ID, final int x, final int y, final int z, final ArrayList<Float> positions, final ArrayList<Float> textureCoordinates, final ArrayList<Integer> indices) {
        dispatch("back", ID, x, y, z, positions, textureCoordinates, indices);
    }
    public void attachFront(final int ID, final int x, final int y, final int z, final ArrayList<Float> positions, final ArrayList<Float> textureCoordinates, final ArrayList<Integer> indices) {
        dispatch("front", ID, x, y, z, positions, textureCoordinates, indices);
    }

    public void attachLeft(final int ID, final int x, final int y, final int z, final ArrayList<Float> positions, final ArrayList<Float> textureCoordinates, final ArrayList<Integer> indices) {
        dispatch("left", ID, x, y, z, positions, textureCoordinates, indices);
    }
    public void attachRight(final int ID, final int x, final int y, final int z, final ArrayList<Float> positions, final ArrayList<Float> textureCoordinates, final ArrayList<Integer> indices) {
        dispatch("right", ID, x, y, z, positions, textureCoordinates, indices);
    }

    public void attachBottom(final int ID, final int x, final int y, final int z, final ArrayList<Float> positions, final ArrayList<Float> textureCoordinates, final ArrayList<Integer> indices) {
        dispatch("bottom", ID, x, y, z, positions, textureCoordinates, indices);
    }
    public void attachTop(final int ID, final int x, final int y, final int z, final ArrayList<Float> positions, final ArrayList<Float> textureCoordinates, final ArrayList<Integer> indices) {
        dispatch("top", ID, x, y, z, positions, textureCoordinates, indices);
    }

    private void dispatch(String face, final int ID, final int x, final int y, final int z, final ArrayList<Float> positions, final ArrayList<Float> textureCoordinates, final ArrayList<Integer> indices) {
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
        // Texture coordinates
        // This is separated here in case this ever decides to poop out with an error
        float[] defTextureCoordinates = thisBlockDef.getTextureCoordinate(face);
        for (float defTextureCoordinate : defTextureCoordinates) {
            textureCoordinates.add(defTextureCoordinate);
        }
        // Indices
        seedIndices(indices);
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
