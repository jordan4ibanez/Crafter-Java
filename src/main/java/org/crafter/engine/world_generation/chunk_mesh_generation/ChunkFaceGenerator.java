package org.crafter.engine.world_generation.chunk_mesh_generation;

import java.util.HashMap;

public class ChunkFaceGenerator {
    private final HashMap<String, float[]> faces;

    public ChunkFaceGenerator() {
        faces = new HashMap<>();

        // Blocks are rooted at 0,0,0 x,y,z floating so negative positions are zeroed

        //+Z
        faces.put("front", new float[]{

        });
        //-Z
        faces.put("back", new float[]{

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

}
