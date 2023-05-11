package org.crafter.engine.mesh

import org.crafter.engine.texture.TextureStorage
import org.lwjgl.opengl.*
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer
import java.nio.IntBuffer

/**
 * The actual Mesh object.
 * To interface into this you must talk to MeshStorage!
 */
class Mesh(
    name: String,
    positions: FloatArray,
    textureCoordinates: FloatArray,
    indices: IntArray,
    bones: IntArray?,
    colors: FloatArray?,
    textureFileLocation: String,
    is2d: Boolean
) {
    // Reserved mesh name for internal debugging
    private val name: String

    // Required VAO, VBO, vertex count, & texture ID
    private val vaoID: Int
    private val positionsVboID: Int
    private val textureCoordinatesVboID: Int
    private val indicesVboID: Int

    // Used for render method
    private val indicesCount: Int
    private var textureID = 0

    // Optional Vertex Buffer Objects
    private var bonesVboID = INVALID
    private var colorsVboID = INVALID

    // Not using builder pattern in Java because I'm trying out a new structure implementation
    init {

        // Before anything is sent to the GPU, let's check that texture
        textureID = try {
            TextureStorage.getID(textureFileLocation)
        } catch (e: RuntimeException) {
            // We're going to throw a different, more specific error
            throw RuntimeException("Mesh: Tried to use a nonexistent texture for a mesh! ($textureFileLocation) does not exist! Did you add it to the TextureStorage?")
        }

        // Assign it the name for debugging
        this.name = name

        // Begin OpenGL contextual creation
        checkRequired(positions, textureCoordinates, indices)
        indicesCount = indices.size
        vaoID = GL30C.glGenVertexArrays()

        // Bind into the Vertex Array Object context
        GL30C.glBindVertexArray(vaoID)


        // Assign REQUIRED Vertex buffer Objects
        positionsVboID = uploadFloatArray(positions, 0, if (is2d) 2 else 3)
        textureCoordinatesVboID = uploadFloatArray(textureCoordinates, 1, 2)
        indicesVboID = uploadIndices(indices)

        // Assign OPTIONAL Vertex Buffer Objects
        if (bones != null) {
            bonesVboID = uploadIntArray(bones, 2, 1)
        }
        if (colors != null) {
            colorsVboID = uploadFloatArray(colors, 2, 4)
        }

        // Now unbind the Vertex Array Object context
        GL30C.glBindVertexArray(0)
    }

    // Allows hot swapping texture for Mesh
    fun swapTexture(newTextureLocation: String) {
        val newTextureID: Int = try {
            TextureStorage.getID(newTextureLocation)
        } catch (e: RuntimeException) {
            // Throwing a more contextual error here
            throw RuntimeException("Mesh: Tried to hotswap to nonexistent texture ($newTextureLocation) on mesh ($name)!")
        }
        textureID = newTextureID
    }

    // Automated internal render method
    fun render() {
        // All shaders are REQUIRED to have a texture sampler
        // FIXME: But why are we setting this to 0 over and over?
        // TEST: Remove this and see if it works on Mesa

        // This is always on array column 0
//        ShaderStorage.setUniform("textureSampler", 0);


        // Activate Bank 0
        // This is always on bank 0
//        glActiveTexture(GL_TEXTURE0);

        // Now bind the context of the texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID)

        // Bind to Vertex Array Object context
        GL30C.glBindVertexArray(vaoID)

        // TODO: Test this (this is line mode)
        // glDrawArrays(GL_LINES, 0, this.indexCount)

        // Draw it - Indices Array starts at 0 (tightly packed)
        GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCount, GL11.GL_UNSIGNED_INT, 0)

        // Optional safety precaution
        // Unbind Vertex Array Object context
        GL30C.glBindVertexArray(0)
    }

    // float[] automator method
    private fun uploadFloatArray(floatArray: FloatArray, glslPosition: Int, componentsInStructure: Int): Int {
        // Starts off as: float* var = nullptr;
        var buffer: FloatBuffer? = null
        val returningID: Int
        try {
            buffer = MemoryUtil.memAllocFloat(floatArray.size)
            buffer.put(floatArray).flip()
            returningID = GL15.glGenBuffers()

            // Bind into the Vertex Buffer Object context
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, returningID)
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)
            // Not normalized (false), no stride (0), array starts at index 0 (0)
            GL20.glVertexAttribPointer(glslPosition, componentsInStructure, GL11.GL_FLOAT, false, 0, 0)

            // Now enable memory address pointer
            GL20.glEnableVertexAttribArray(glslPosition)

            // Now unbind the Vertex Buffer Object context
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        } finally {
            // Free the C float*
            if (buffer != null) {
                MemoryUtil.memFree(buffer)
            }
        }
        return returningID
    }

    // int[] automator method
    private fun uploadIntArray(intArray: IntArray, glslPosition: Int, componentsInStructure: Int): Int {
        // Starts off as: int* var = nullptr;
        var buffer: IntBuffer? = null
        val returningID: Int
        try {
            buffer = MemoryUtil.memAllocInt(intArray.size)
            buffer.put(intArray).flip()
            returningID = GL15.glGenBuffers()

            // Bind into the Vertex Buffer Object context
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, returningID)
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)
            // Not normalized (false), no stride (0), array starts at index 0 (0)
            GL30.glVertexAttribIPointer(glslPosition, componentsInStructure, GL11.GL_INT, 0, 0)

            // Now enable memory address pointer
            GL20.glEnableVertexAttribArray(glslPosition)

            // Now unbind the Vertex Buffer Object context
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        } finally {
            // Free the C int*
            if (buffer != null) {
                MemoryUtil.memFree(buffer)
            }
        }
        return returningID
    }

    // This method is specialized, uploads the indices from an int[]
    private fun uploadIndices(indicesArray: IntArray): Int {

        // Starts off as: int* var = nullptr;
        var buffer: IntBuffer? = null
        val returningID: Int
        try {
            returningID = GL15.glGenBuffers()
            buffer = MemoryUtil.memAllocInt(indicesArray.size)
            buffer.put(indicesArray).flip()

            // Bind into the Vertex Buffer Object context
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, returningID)
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)

            // Note: Do note unbind GL_ELEMENT_ARRAY_BUFFER
        } finally {
            if (buffer != null) {
                MemoryUtil.memFree(buffer)
            }
        }
        return returningID
    }

    // Completely obliterates this VAO and all VBOs associated with it
    fun destroy() {

        // Bind into Vertex Array Object context
        GL30C.glBindVertexArray(vaoID)

        // Destroy REQUIRED Vertex Buffer Objects
        destroyVBO(positionsVboID, 0, "positions")
        destroyVBO(textureCoordinatesVboID, 1, "texture coordinates")
        destroyVBO(indicesVboID, -1, "indices")

        // Destroy OPTIONAL Vertex Buffer Objects
        if (bonesVboID != INVALID) {
            destroyVBO(bonesVboID, 2, "bones")
        }
        if (colorsVboID != INVALID) {
            destroyVBO(colorsVboID, 2, "colors")
        }

        // Unbind Vertex Array Object context
        GL30C.glBindVertexArray(0)

        // Now destroy Vertex Array Object
        GL30.glDeleteVertexArrays(vaoID)
        if (GL30.glIsVertexArray(vaoID)) {
            throw RuntimeException("Mesh: Failed to delete VAO ($name)!")
        }

        // Complete
    }

    // Contextual Vertex Buffer Object deletion - GENERIC
    private fun destroyVBO(vboID: Int, glslPosition: Int, vboName: String) {

        // Indices are assigned to -1, skip
        if (glslPosition >= 0) {
            GL20.glDisableVertexAttribArray(glslPosition)
        }
        GL15.glDeleteBuffers(vboID)
        if (GL15.glIsBuffer(vboID)) {
            throw RuntimeException("Mesh ($name): Failed to delete VBO ($vboName)!")
        }
    }

    // This is a separate method to improve the constructor readability
    private fun checkRequired(positions: FloatArray?, textureCoordinates: FloatArray?, indices: IntArray?) {
        // Null check
        if (positions == null) {
            throw RuntimeException("Mesh: Positions parameter CANNOT be null!")
        } else if (textureCoordinates == null) {
            throw RuntimeException("Mesh: Texture coordinates parameter CANNOT be null!")
        } else if (indices == null) {
            throw RuntimeException("Mesh: Indices parameter CANNOT be null!")
            // Empty array check - This can cause issues with blank chunk stacks
        } /*else if (positions.length == 0) {
            throw new RuntimeException("Mesh: Positions parameter CANNOT be an empty array!");
        } else if (textureCoordinates.length == 0) {
            throw new RuntimeException("Mesh: Texture coordinates parameter CANNOT be  an empty array!");
        } else if (indices.length == 0) {
            throw new RuntimeException("Mesh: Indices parameter CANNOT be  an empty array!");

        }*/
        // Required data is all there, nice
    }

    companion object {
        // Reserved invalidation token
        private const val INVALID = Int.MAX_VALUE
    }
}
