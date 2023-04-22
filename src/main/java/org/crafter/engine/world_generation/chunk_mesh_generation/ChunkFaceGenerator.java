package org.crafter.engine.world_generation.chunk_mesh_generation;

import org.crafter.engine.world.block.BlockDefinition;
import org.crafter.engine.world.block.BlockDefinitionContainer;

import java.util.ArrayList;
import java.util.HashMap;

public class ChunkFaceGenerator {

    private final BlockDefinitionContainer definitionContainer;
    private final HashMap<String, float[]> faces;

    private final int[] indicesOrder = new int[]{0,1,2,2,3,0};

    public ChunkFaceGenerator(BlockDefinitionContainer definitionContainer) {

        this.definitionContainer = definitionContainer;

        faces = new HashMap<>();

        // Blocks are rooted at 0,0,0 x,y,z floating so negative positions are zeroed

        //-Z
        faces.put("back", new float[]{
                //x,y,z
                // top left
                0,1,0,
                // bottom left
                0,0,0,
                // bottom right
                1,0,0,
                // top right
                1,1,0
        });
        //+Z
        faces.put("front", new float[]{

        });

        //-X
        faces.put("left", new float[]{

        });
        //+X
        faces.put("right", new float[]{

        });

        //-Y
        faces.put("bottom", new float[]{

        });
        //+Y
        faces.put("top", new float[]{

        });
    }

    public void attachBack(final int ID, final int x, final int y, final int z, final ArrayList<Float> positions, final ArrayList<Float> textureCoordinates, final ArrayList<Integer> indices) {

        // Texture coordinates
        BlockDefinition thisBlockDef = definitionContainer.getDefinition(ID);

        // It's a blank face, ignore it - Note: This SHOULD NOT be reached, EVER!
        if (!thisBlockDef.containsTextureCoordinate("back")) {
            throwSevereWarning("back");
            return;
        }

        // Vertex positions
        final float[] hardCodedPos = faces.get("back");

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

        // This is separated here in case this ever decides to shit out with an error
        float[] defTextureCoordinates = thisBlockDef.getTextureCoordinate("back");

        for (float defTextureCoordinate : defTextureCoordinates) {
            textureCoordinates.add(defTextureCoordinate);
        }

        // Indices
        seedIndices(indices);
    }





    private void seedIndices(final ArrayList<Integer> indices) {
        final int length = indices.size();
        for (int i : indicesOrder) {
            indices.add(i + length);
        }
    }

    private void throwSevereWarning(String face) {
        System.out.println("ChunkFaceGenerator: WARNING! A BLOCK DEFINITION HAS A BLANK FACE SOMEHOW! Face: (" + face + ")!");
    }

}
