package org.crafter.engine.world_generation.chunk_mesh_generation

import org.crafter.engine.world.block.BlockDefinitionContainer

class ChunkFaceGenerator(private val definitionContainer: BlockDefinitionContainer?) {
    private val faces: HashMap<String, FloatArray> = HashMap()
    private val indicesOrder = intArrayOf(0, 1, 2, 2, 3, 0)

    init {

        // Blocks are rooted at 0,0,0 x,y,z floating so negative positions are zeroed

        // Note: Right handed coordinate system

        // Todo: Probably need to check these and make sure nothing is upside down! (textures)

        //-Z
        faces["front"] = floatArrayOf( //x,y,z
            // top right
            1f, 1f, 0f,  // bottom right
            1f, 0f, 0f,  // bottom left
            0f, 0f, 0f,  // top left
            0f, 1f, 0f
        )

        //+Z
        faces["back"] = floatArrayOf( //x,y,z
            // top left
            0f, 1f, 1f,  // bottom left
            0f, 0f, 1f,  // bottom right
            1f, 0f, 1f,  // top right
            1f, 1f, 1f
        )


        //-X
        faces["left"] = floatArrayOf( //x,y,z
            // top left
            0f, 1f, 0f,  // bottom left
            0f, 0f, 0f,  // bottom right
            0f, 0f, 1f,  // top right
            0f, 1f, 1f
        )
        //+X
        faces["right"] = floatArrayOf( //x,y,z
            // top right
            1f, 1f, 1f,  // bottom right
            1f, 0f, 1f,  // bottom left
            1f, 0f, 0f,  // top left
            1f, 1f, 0f
        )

        //-Y
        faces["bottom"] = floatArrayOf( //x,y,z
            // top right
            1f, 0f, 1f,  // bottom right
            0f, 0f, 1f,  // bottom left
            0f, 0f, 0f,  // top left
            1f, 0f, 0f
        )

        //+Y
        faces["top"] = floatArrayOf( //x,y,z
            // top left
            1f, 1f, 0f,  // bottom left
            0f, 1f, 0f,  // bottom right
            0f, 1f, 1f,  // top right
            1f, 1f, 1f
        )
    }

    fun attachBack(
        ID: Int,
        x: Int,
        y: Int,
        z: Int,
        positions: ArrayList<Float>,
        textureCoordinates: ArrayList<Float>,
        indices: ArrayList<Int>
    ) {
        dispatch("back", ID, x, y, z, positions, textureCoordinates, indices)
    }

    fun attachFront(
        ID: Int,
        x: Int,
        y: Int,
        z: Int,
        positions: ArrayList<Float>,
        textureCoordinates: ArrayList<Float>,
        indices: ArrayList<Int>
    ) {
        dispatch("front", ID, x, y, z, positions, textureCoordinates, indices)
    }

    fun attachLeft(
        ID: Int,
        x: Int,
        y: Int,
        z: Int,
        positions: ArrayList<Float>,
        textureCoordinates: ArrayList<Float>,
        indices: ArrayList<Int>
    ) {
        dispatch("left", ID, x, y, z, positions, textureCoordinates, indices)
    }

    fun attachRight(
        ID: Int,
        x: Int,
        y: Int,
        z: Int,
        positions: ArrayList<Float>,
        textureCoordinates: ArrayList<Float>,
        indices: ArrayList<Int>
    ) {
        dispatch("right", ID, x, y, z, positions, textureCoordinates, indices)
    }

    fun attachBottom(
        ID: Int,
        x: Int,
        y: Int,
        z: Int,
        positions: ArrayList<Float>,
        textureCoordinates: ArrayList<Float>,
        indices: ArrayList<Int>
    ) {
        dispatch("bottom", ID, x, y, z, positions, textureCoordinates, indices)
    }

    fun attachTop(
        ID: Int,
        x: Int,
        y: Int,
        z: Int,
        positions: ArrayList<Float>,
        textureCoordinates: ArrayList<Float>,
        indices: ArrayList<Int>
    ) {
        dispatch("top", ID, x, y, z, positions, textureCoordinates, indices)
    }

    private fun dispatch(
        face: String,
        ID: Int,
        x: Int,
        y: Int,
        z: Int,
        positions: ArrayList<Float>,
        textureCoordinates: ArrayList<Float>,
        indices: ArrayList<Int>
    ) {
        // Texture coordinates
        val thisBlockDef = definitionContainer!!.getDefinition(ID)

        // It's a blank face, ignore it - Note: This SHOULD NOT be reached, EVER!
        if (!thisBlockDef!!.containsTextureCoordinate(face)) {
            throwSevereWarning(face)
            return
        }
        // Vertex positions
        val hardCodedPos = faces[face]
        for (i in hardCodedPos!!.indices) {
            val xyz = i % 3
            val floatingPos = hardCodedPos[i]
            when (xyz) {
                0 -> positions.add(floatingPos + x)
                1 -> positions.add(floatingPos + y)
                2 -> positions.add(floatingPos + z)
                else -> throw RuntimeException("ChunkFaceGenerator: Got error in modulo calculation! Expected: (0-2) | Got: $xyz!")
            }
        }
        // Texture coordinates
        // This is separated here in case this ever decides to poop out with an error
        val defTextureCoordinates = thisBlockDef.getTextureCoordinate(face)
        for (defTextureCoordinate in defTextureCoordinates!!) {
            textureCoordinates.add(defTextureCoordinate)
        }
        // Indices
        seedIndices(indices)
    }

    private fun seedIndices(indices: ArrayList<Int>) {
        val length = indices.size / 6 * 4
        for (i in indicesOrder) {
            indices.add(i + length)
        }
    }

    private fun throwSevereWarning(face: String) {
        println("ChunkFaceGenerator: WARNING! A BLOCK DEFINITION HAS A BLANK FACE SOMEHOW! Face: ($face)!")
    }
}
