package org.crafter.engine.world_generation.chunk_mesh_generation

import org.crafter.engine.world.block.BlockDefinitionContainer
import org.crafter.engine.world.block.DrawType
import org.crafter.engine.world.chunk.Chunk
import org.crafter.engine.world.chunk.ChunkStorage.getThreadSafeChunkClone
import org.crafter.engine.world.chunk.ChunkStorage.hasPosition
import org.joml.Vector2i
import org.joml.Vector2ic

class ChunkMeshWorker(private val definitionContainer: BlockDefinitionContainer?) {
    private val faceGenerator: ChunkFaceGenerator

    // These chunk neighbors are reused over & over so that the methods do not have huge amounts of parameters
    // Note: These are chunk deep clones
    private var neighborFront: Chunk? = null
    private var neighborBack: Chunk? = null
    private var neighborLeft: Chunk? = null
    private var neighborRight: Chunk? = null

    // Same reason for chunk neighbors!
    var blockNeighborFrontIsBlock = false
    var blockNeighborBackIsBlock = false
    var blockNeighborLeftIsBlock = false
    var blockNeighborRightIsBlock = false
    var blockNeighborBottomIsBlock = false
    var blockNeighborTopIsBlock = false

    init {
        faceGenerator = ChunkFaceGenerator(definitionContainer)
    }

    /**
     * @param chunk A Thread-safe clone passed in from the ChunkMeshGenerator.
     * @param positions Mutable reference ArrayList of vertices.
     * @param textureCoordinates Mutable reference ArrayList of texture coordinates.
     * @param indices Mutable reference ArrayList of indices.
     */
    fun process(
        stackPosition: Int,
        chunk: Chunk?,
        positions: ArrayList<Float>,
        textureCoordinates: ArrayList<Float>,
        indices: ArrayList<Int>
    ) {
        val STACK_HEIGHT = chunk!!.stackHeight
        val WIDTH = chunk.width
        val DEPTH = chunk.depth
        val chunkPosition = chunk.position

        // Right-handed coordinate system - Scoped for clarification & so not reused on accident
        run {
            val front: Vector2ic = Vector2i(chunkPosition.x(), chunkPosition.y() - 1)
            val back: Vector2ic = Vector2i(chunkPosition.x(), chunkPosition.y() + 1)
            val left: Vector2ic = Vector2i(chunkPosition.x() - 1, chunkPosition.y())
            val right: Vector2ic = Vector2i(chunkPosition.x() + 1, chunkPosition.y())
            neighborFront = if (hasPosition(front)) getThreadSafeChunkClone(front) else null
            neighborBack = if (hasPosition(back)) getThreadSafeChunkClone(back) else null
            neighborLeft = if (hasPosition(left)) getThreadSafeChunkClone(left) else null
            neighborRight = if (hasPosition(right)) getThreadSafeChunkClone(right) else null
        }


        /*
        It works its way:
        Left to right (0-15 x)
        -> Front to back (0-15 z)
        --> Bottom to top (0-15 [differs depending on stack] y)
         */for (y in STACK_HEIGHT * stackPosition until STACK_HEIGHT * (stackPosition + 1)) {
            for (z in 0 until DEPTH) {
                for (x in 0 until WIDTH) {
                    branchPathOfGeneration(chunk, x, y, z, positions, textureCoordinates, indices)
                }
            }
        }
    }

    private fun branchPathOfGeneration(
        chunk: Chunk?,
        x: Int,
        y: Int,
        z: Int,
        positions: ArrayList<Float>,
        textureCoordinates: ArrayList<Float>,
        indices: ArrayList<Int>
    ) {
        // Fixme: This might be a bit too slow
        val chunkData = chunk!!.data
        val ID = chunk.getBlockID(chunkData[chunk.positionToIndex(x, y, z)])

        // 0 is reserved for air! Also don't process air drawtype blocks!
        if (ID == 0 || definitionContainer!!.getDefinition(ID)!!.drawType == DrawType.AIR) {
            return
        }


        // Note: -Z is facing forwards +X is facing right
        blockNeighborFrontIsBlock = neighborIsBlockDrawType(getNeighbor(chunk, x, y, z - 1))
        blockNeighborBackIsBlock = neighborIsBlockDrawType(getNeighbor(chunk, x, y, z + 1))
        blockNeighborLeftIsBlock = neighborIsBlockDrawType(getNeighbor(chunk, x - 1, y, z))
        blockNeighborRightIsBlock = neighborIsBlockDrawType(getNeighbor(chunk, x + 1, y, z))
        blockNeighborBottomIsBlock = neighborIsBlockDrawType(getNeighbor(chunk, x, y - 1, z))
        blockNeighborTopIsBlock = neighborIsBlockDrawType(getNeighbor(chunk, x, y + 1, z))
        when (definitionContainer!!.getDefinition(ID)!!.drawType) {
            DrawType.BLOCK -> {
                blockDrawType(ID, x, y, z, positions, textureCoordinates, indices)
            }

            DrawType.GLASS, DrawType.PLANT, DrawType.TORCH, DrawType.LEAVES, DrawType.BLOCK_BOX, DrawType.LIQUID_FLOW, DrawType.LIQUID_SOURCE -> {
                //todo;
            }

            DrawType.AIR -> {
                // do nothing
                return
            }

            else -> throw IllegalStateException("Unexpected value: " + definitionContainer.getDefinition(ID)!!.drawType)
        }
    }

    private fun blockDrawType(
        ID: Int,
        x: Int,
        y: Int,
        z: Int,
        positions: ArrayList<Float>,
        textureCoordinates: ArrayList<Float>,
        indices: ArrayList<Int>
    ) {
        //Fixme: This will check neighbors etc when completed

        // Note: Right handed coordinate system - to + all axes
        if (!blockNeighborFrontIsBlock) {
            faceGenerator.attachFront(ID, x, y, z, positions, textureCoordinates, indices)
        }
        if (!blockNeighborBackIsBlock) {
            faceGenerator.attachBack(ID, x, y, z, positions, textureCoordinates, indices)
        }
        if (!blockNeighborLeftIsBlock) {
            faceGenerator.attachLeft(ID, x, y, z, positions, textureCoordinates, indices)
        }
        if (!blockNeighborRightIsBlock) {
            faceGenerator.attachRight(ID, x, y, z, positions, textureCoordinates, indices)
        }
        if (!blockNeighborBottomIsBlock) {
            faceGenerator.attachBottom(ID, x, y, z, positions, textureCoordinates, indices)
        }
        if (!blockNeighborTopIsBlock) {
            faceGenerator.attachTop(ID, x, y, z, positions, textureCoordinates, indices)
        }
    }

    private fun neighborIsBlockDrawType(inputID: Int): Boolean {
        return definitionContainer!!.getDefinition(inputID)!!.drawType == DrawType.BLOCK
    }

    private fun getNeighbor(chunk: Chunk?, x: Int, y: Int, z: Int): Int {

        // todo: Implement neighbor chunk getter
        return if (xyzIsOutOfBoundsCheck(x, y, z, chunk!!.width, chunk.height, chunk.depth)) {
            // Out of bounds within the chunk
            // Zero is reserved for air
            0
        } else chunk.getBlockID(chunk.getBlockData(chunk.positionToIndex(x, y, z)))
    }

    private fun xyzIsOutOfBoundsCheck(x: Int, y: Int, z: Int, width: Int, height: Int, depth: Int): Boolean {
        return x < 0 || x >= width || y < 0 || y >= height || z < 0 || z >= depth
    }

    private fun frontNeighborExists(): Boolean {
        return neighborFront != null
    }

    private fun backNeighborExists(): Boolean {
        return neighborBack != null
    }

    private fun leftNeighborExists(): Boolean {
        return neighborLeft != null
    }

    private fun rightNeighborExists(): Boolean {
        return neighborRight != null
    }
}
