

package org.crafter.engine.world.chunk

import org.crafter.engine.camera.Camera.setObjectMatrix
import org.crafter.engine.delta.Delta.delta
import org.crafter.engine.mesh.MeshStorage
import org.crafter.engine.mesh.MeshStorage.render
//Fixme
// import org.crafter.engine.world_generation.chunk_mesh_generation.ChunkMeshRecord
import org.joml.*

//Todo: idea: metadata hashmap

/**
 * The Basis for working with Chunk's internal Array.
 * Chunks are basically fancy arrays of data.
 * This class goes into Chunk, finalizing this snowball of inheritance.
 *
 *
 * A chunk of map data. It is 16 wide, 128 high, and 16 long.
 *
 * Some notes:
 * << shifts to the left X amount, so << 3 of 0001 (1) in a byte now represents 1000 (8)
 * >> shifts to the right X amount
 *
 * Chunk represented as:
 * [16 bit] block | [4 bit lightLevel] | [4 bit blockState] | [ 8 bits left over for additional functionality]
 * This is literal, here is an exact representation:
 * | 0000 0000 0000 0000 | 0000 | 0000 | 0000 0000 |
 */

class Chunk {
    var position: Vector2ic

    // fixme: remove! Temp
    var rotation: Float = 0f

    constructor(x: Int, y: Int) {
        position = Vector2i(x,y);
    }
    constructor(newPos: Vector2ic) {
        position = Vector2i(newPos.x(), newPos.y())
    }

    val x: Int
        get() = position.x()
    val y: Int
        get() = position.y()

    // X
    /**
     * Width of chunk in blocks.
     */
    val width = 16

    // Y
    /**
     * Height of chunk in blocks.
     */
    val height = 128

    // Z
    /**
     * Depth of chunk in blocks.
     */
    val depth: Int = 16

    private val yStride: Int = width * depth

    val arraySize: Int = width * height * depth


    //!TODO: begin chunk mesh handling ---------------------------------------------------

    private val meshes: Array<String?> = arrayOfNulls(8)

    val stackHeight = 16
    /**
     * Stacks, as in, mesh stacks. There are 8 individual meshes which make up a chunk, for speed of processing the chunk.
     * TODO: Give this a better name!
     * @return integral position in array. Literal position is bottom to top 0-7
     */
    val stacks: Int = height / stackHeight

    init {
//        System.out.println("ChunkMeshHandling: Stacks: " + STACKS);
    }

    //Fixme
//    fun setMesh(stack: Int, newMesh: ChunkMeshRecord) {
//        if (meshes[stack] != null) {
//            MeshStorage.destroy(meshes[stack]!!)
//        }
//        MeshStorage.newMesh(
//            newMesh.uuid,
//            newMesh.positions,
//            newMesh.textureCoordinates,
//            newMesh.indices,
//            null,  // Todo: Colors can be an easy way to implement light values!
//            null,
//            "worldAtlas",
//            false
//        )
////        println("ChunkMeshHandling: Chunk (" + newMesh.destinationChunkPosition.x() + ", " + newMesh.destinationChunkPosition.y() + ") stack (" + stack + ") has uuid (" + newMesh.uuid + ")")
//        meshes[stack] = newMesh.uuid
//    }

    fun getMesh(stack: Int): String? {
        return meshes[stack]
    }


    //TODO begin array manipulation --------------------------------------------------------
    // Consists of bit shifted integral values
    private val data: IntArray = IntArray(arraySize)

    /**
     * Set a single block, think of this as minetest.set_node();
     * For bulk setting, it is currently recommended to use the array methods.
     * @param position is the 3D position in the internal array.
     * @param blockData is the constructed bit manipulated integer that represents a block.
     */
    fun setBlockData(position: Vector3ic, blockData: Int) {
        check(position)
        data[positionToIndex(position)] = blockData
    }

    /**
     * Set a single block, think of this as minetest.set_node();
     * For bulk setting, it is currently recommended to use the array methods.
     * @param index is the 1D index in the internal array.
     * @param blockData is the constructed bit manipulated integer that represents a block.
     */
    fun setBlockData(index: Int, blockData: Int) {
        check(index)
//        println("setting block data! new value is $blockData at index $index")
        data[index] = blockData
    }

    /**
     * Get a single block, think of this as minetest.get_node();
     * For bulk getting, it is currently recommended to use the array methods.
     * @param index is the 1D position in the internal array.
     * @return is the bit manipulated data value.
     */
    fun getBlockData(index: Int): Int {
        check(index)
        return data[index]
    }

    /**
     * Get a single block, think of this as minetest.get_node();
     * For bulk getting, it is currently recommended to use the array methods.
     * @param position is the 3D position in the internal array.
     * @return is the bit manipulated data value.
     */
    fun getBlockData(position: Vector3ic): Int {
        check(position)
        return data[positionToIndex(position)]
    }

    /**
     * Allows a chunk to get entirely new block data from a pre bit-shifted array.
     */
    fun streamNewBlockData(newData: IntArray) {
        for (i in 0 until arraySize) {
            data[i] = newData[i]
        }
    }

    /**
     * Returns the raw data of a chunk. This is a cloned data set. It is a completely separate object from
     * the internal data structure inside this chunk.
     * This is done like this as to prevent a horrific headache from object mutability.
     */
    fun getRawData(): IntArray {
        val dataClone = IntArray(arraySize)
        for (i in 0 until arraySize) {
            dataClone[i] = data[i]
        }
        return dataClone
    }

    fun positionToIndex(position: Vector3ic): Int {
        return position.y() * yStride + position.z() * depth + position.x()
    } // One below is for iterator assembly

    fun positionToIndex(x: Int, y: Int, z: Int): Int {
        return y * yStride + z * depth + x
    }

    fun indexToPosition(index: Int): Vector3ic {
        return Vector3i(
            index % width,
            index / yStride % height,
            index / depth % depth
        )
    }

    private fun check(array: IntArray) {
        if (!boundsCheck(array)) {
            throw RuntimeException("ChunkArrayManipulation: Tried to set internal data to an array length of (" + array.size + ")!")
        }
    }

    private fun check(index: Int) {
        if (!boundsCheck(index)) {
            throw RuntimeException("ChunkArrayManipulation: Index ($index) is out of bounds!")
        }
    }

    private fun check(position: Vector3ic) {
        if (!boundsCheck(position)) {
            throw RuntimeException("ChunkArrayManipulation: Position (" + position.x() + ", " + position.y() + ", " + position.z() + ") is out of bounds!")
        }
    }

    private fun boundsCheck(array: IntArray): Boolean {
        return array.size == arraySize
    }

    private fun boundsCheck(position: Vector3ic): Boolean {
        return position.x() >= 0 && position.x() < width && position.y() >= 0 && position.y() < height && position.z() >= 0 && position.z() < depth
    }

    private fun boundsCheck(index: Int): Boolean {
        return index >= 0 && index < arraySize
    }


    //TODO begin bit manipulation -------------------------------------------------------------------
    private var output: StringBuilder? = null

    fun printBits(input: Int) {
        for (i in 31 downTo 0) {
            if ((i + 1) % 4 == 0) {
                output!!.append("|")
            }
            output!!.append(if (input and (1 shl i) == 0) "0" else "1")
        }
        output!!.append("|")
        println("Literal ($input) | binary: $output")
        output!!.setLength(0)
    }

    /**
     * These are user-friendly direct value getters
     */
    fun getBlockID(input: Int): Int {
        return input ushr 16
    }

    fun getBlockLight(input: Int): Int {
        return input shl 16 ushr 28
    }

    fun getBlockState(input: Int): Int {
        return input shl 20 ushr 28
    }

    /**
     * These are internalized anti boilerplate methods for working with integers that represent a block.
     * Public so they can be used dynamically externally.
     */
    fun setBlockID(input: Int, newID: Int): Int {
        if (newID > 65535 || newID < 0) {
            throw java.lang.RuntimeException("ChunkBitManipulation: Attempted to exceed ushort limit for block ID! Attempted to input value: ($newID)")
        }
        val blockID = shiftBlock(newID)
        val light = parseLightLevel(input)
        val state = parseBlockState(input)
        return combine(blockID, light, state)
    }

    fun setBlockLight(input: Int, newLight: Int): Int {
        if (newLight > 15 || newLight < 0) {
            throw java.lang.RuntimeException("ChunkBitManipulation: Attempted to exceed 4 bit limit for light! Attempted to input value: ($newLight)")
        }
        val blockID = parseBlockID(input)
        val light = shiftLight(newLight)
        val state = parseBlockState(input)
        return combine(blockID, light, state)
    }

    fun setBlockState(input: Int, newState: Int): Int {
        if (newState > 15 || newState < 0) {
            throw java.lang.RuntimeException("ChunkBitManipulation: Attempted to exceed 4 bit limit for light! Attempted to input value: ($newState)")
        }
        val blockID = parseBlockID(input)
        val light = parseLightLevel(input)
        val state = shiftState(newState)
        return combine(blockID, light, state)
    }

    /**
     * Get integral bit data raw.
     * These do not give out the true number, just the data held in that section of the buffer.
     */
    fun parseBlockID(input: Int): Int {
        // Clear out right 16 bits
        return input ushr 16 shl 16
    }

    fun parseLightLevel(input: Int): Int {
        // Clear out left 16 bits
        var input1 = input
        input1 = input1 shl 16 ushr 16
        // Clear out right 12 bits
        input1 = input1 ushr 12 shl 12
        return input1
    }

    fun parseBlockState(input: Int): Int {
        // Clear out left 20 bits
        var input1 = input
        input1 = input1 shl 20 ushr 20
        // Clear out right 8 bits
        input1 = input1 ushr 8 shl 8
        return input1
    }

    /**
     * Set integral bit data raw. Used for chaining. This is at the bottom because it's just boilerplate bit manipulation
     */
    fun shiftBlock(input: Int): Int {
        return input shl 16
    }

    fun shiftLight(input: Int): Int {
        return input shl 12
    }

    fun shiftState(input: Int): Int {
        return input shl 8
    }

    /**
     * Mini boilerplate combination method, makes code easier to read
     */
    fun combine(blockID: Int, light: Int, state: Int): Int {
        return blockID or light or state
    }

    fun clone(): Chunk {
        val clone = Chunk(Vector2i(position.x(), position.y()))
        for (i in 0 until arraySize) {
            clone.data[i] = data[i]
        }
        return clone
    }

    fun render() {

        //Fixme: This is HORRIBLE TO CREATE A NEW OBJECT EVERY FRAME!
//    println(position.x());
        setObjectMatrix(
            Vector3f((position.x() * width).toFloat(), 0f, (position.y() * depth).toFloat()),
            Vector3f(0f, 0f, 0f),
            Vector3f(1f, 1f, 1f)
        )
        var got = false
        for (i in 0 until stacks) {
            val gottenMeshUUID = getMesh(i)
            if (gottenMeshUUID != null) {
                got = true
                //                System.out.println("rendering: " + gottenMeshUUID);
                render(gottenMeshUUID)
            }
        }
        if (got) {
            rotation += delta * 15.0f
        }
    }
}



