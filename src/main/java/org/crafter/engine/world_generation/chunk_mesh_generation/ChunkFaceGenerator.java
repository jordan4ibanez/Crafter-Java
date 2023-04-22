package org.crafter.engine.world_generation.chunk_mesh_generation;

import org.crafter.engine.world.block.BlockDefinition;
import org.crafter.engine.world.block.BlockDefinitionContainer;

import java.util.ArrayList;
import java.util.HashMap;

public class ChunkFaceGenerator {

    private final BlockDefinitionContainer definitionContainer;
    private final HashMap<String, float[]> faces;

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

    }

}
