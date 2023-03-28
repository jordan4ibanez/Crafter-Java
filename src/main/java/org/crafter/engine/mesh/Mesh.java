package org.crafter.engine.mesh;

import org.crafter.engine.texture.TextureStorage;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;

/**
 * The actual Mesh object.
 * To interface into this you must talk to MeshStorage!
 */
public class Mesh {

    // Reserved invalidation token
    private static final int INVALID = Integer.MAX_VALUE;

    // Reserved mesh name for internal debugging
    private final String name;


    // Required VAO, VBO, vertex count, & texture ID
    private final int vaoID;

    private final int positionsVboID;

    private final int textureCoordinatesVboID;

    private final int indicesVboID;

    // Used for render method
    private final int vertexCount;

    private final int textureID;


    // Optional Vertex Buffer Objects
    private int bonesVboID = INVALID;

    private int colorsVboID = INVALID;

    // Not using builder pattern in Java because I'm trying out a new structure implementation
    Mesh(String name, float[] positions, float[] textureCoordinates, int[] indices, int[] bones, float[] colors, String textureFileLocation) {

        // Before anything is sent to the GPU, let's check that texture
        try {
            textureID = TextureStorage.getID(textureFileLocation);
        } catch (RuntimeException e) {
            // We're going to throw a different, more specific error
            throw new RuntimeException("Mesh: Tried to use a nonexistent texture for a mesh! (" + textureFileLocation + ") does not exist! Did you add it to the TextureStorage?");
        }

        // Assign it the name for debugging
        this.name = name;

        // Begin OpenGL contextual creation

        checkRequired(positions, textureCoordinates, indices);

        vertexCount = positions.length / 3;

        vaoID = glGenVertexArrays();

        // Bind into the Vertex Array Object context
        glBindVertexArray(vaoID);


        // Assign REQUIRED Vertex buffer Objects
        positionsVboID = uploadFloatArray(positions, 0, 3);
        textureCoordinatesVboID = uploadFloatArray(textureCoordinates, 1, 2);
        indicesVboID = uploadIndices(indices);

        // Assign OPTIONAL Vertex Buffer Objects
        if (bones != null) {
            bonesVboID = uploadIntArray(bones, 2, 1);
        }

        if (colors != null) {
            colorsVboID = uploadFloatArray(colors, 2, 4);
        }

        // Now unbind the Vertex Array Object context
        glBindVertexArray(0);
    }

    // float[] automator method
    private int uploadFloatArray(float[] floatArray, int glslPosition, int componentsInStructure) {
        // Starts off as: float* var = nullptr;
        FloatBuffer buffer = null;

        final int returningID;

        try {

            buffer = MemoryUtil.memAllocFloat(floatArray.length);
            buffer.put(floatArray).flip();

            returningID = glGenBuffers();

            // Bind into the Vertex Buffer Object context
            glBindBuffer(GL_ARRAY_BUFFER, returningID);

            glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
            // Not normalized (false), no stride (0), array starts at index 0 (0)
            glVertexAttribPointer(glslPosition, componentsInStructure, GL_FLOAT, false, 0, 0);

            // Now unbind the Vertex Buffer Object context
            glBindBuffer(GL_ARRAY_BUFFER, 0);

        } finally {
            // Free the C float*
            if (buffer != null) {
                MemoryUtil.memFree(buffer);
            }
        }
        return returningID;
    }

    // int[] automator method
    private int uploadIntArray(int[] intArray, int glslPosition, int componentsInStructure) {
        // Starts off as: int* var = nullptr;
        IntBuffer buffer = null;

        final int returningID;

        try {

            buffer = MemoryUtil.memAllocInt(intArray.length);
            buffer.put(intArray).flip();

            returningID = glGenBuffers();

            // Bind into the Vertex Buffer Object context
            glBindBuffer(GL_ARRAY_BUFFER, returningID);

            glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
            // Not normalized (false), no stride (0), array starts at index 0 (0)
            glVertexAttribIPointer(glslPosition, componentsInStructure, GL_INT, 0, 0);

            // Now unbind the Vertex Buffer Object context
            glBindBuffer(GL_ARRAY_BUFFER, 0);

        } finally {
            // Free the C int*
            if (buffer != null) {
                MemoryUtil.memFree(buffer);
            }
        }
        return returningID;
    }


    // This method is specialized, uploads the indices from an int[]
    private int uploadIndices(int[] indicesArray) {

        // Starts off as: int* var = nullptr;
        IntBuffer buffer = null;

        final int returningID;

        try {

            returningID = glGenBuffers();

            buffer = MemoryUtil.memAllocInt(indicesArray.length);
            buffer.put(indicesArray).flip();

            // Bind into the Vertex Buffer Object context
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, returningID);

            glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

            // Now unbind the Vertex Buffer Object context
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        } finally {
            if (buffer != null) {
                MemoryUtil.memFree(buffer);
            }
        }
        return returningID;
    }

    // Completely obliterates this VAO and all VBOs associated with it
    void destroy() {

        // Bind into Vertex Array Object context
        glBindVertexArray(vaoID);

        // Destroy REQUIRED Vertex Buffer Objects
        destroyVBO(positionsVboID, 0, "positions");
        destroyVBO(textureCoordinatesVboID, 1, "texture coordinates");
        destroyVBO(indicesVboID, -1, "indices");

        // Destroy OPTIONAL Vertex Buffer Objects
        if (bonesVboID != INVALID) {
            destroyVBO(bonesVboID, 2, "bones");
        }

        if (bonesVboID != INVALID) {
            destroyVBO(colorsVboID, 2, "colors");
        }

        // Unbind Vertex Array Object context
        glBindVertexArray(0);

        // Now destroy Vertex Array Object
        glDeleteVertexArrays(vaoID);
        if (glIsVertexArray(vaoID)) {
            throw new RuntimeException("Mesh: Failed to delete VAO (" + name + ")!");
        }

        // Complete
    }

    // Contextual Vertex Buffer Object deletion - GENERIC
    private void destroyVBO(int vboID, int glslPosition, String vboName) {

        // Indices are assigned to -1, skip
        if (vboID >= 0) {
            glDisableVertexAttribArray(glslPosition);
        }
        glDeleteBuffers(vboID);
        if (glIsBuffer(vboID)) {
            throw new RuntimeException("Mesh (" + name + "): Failed to delete VBO (" + vboName + ")!");
        }
    }

    // This is a separate method to improve the constructor readability
    private void checkRequired(float[] positions, float[] textureCoordinates, int[] indices) {
        if (positions == null) {
            throw new RuntimeException("Mesh: Positions parameter CANNOT be null!");
        } else if (textureCoordinates == null) {
            throw new RuntimeException("Mesh: Texture coordinates parameter CANNOT be null!");
        } else if (indices == null) {
            throw new RuntimeException("Mesh: Indices parameter CANNOT be null!");
        }
        // Required data is all there, nice
    }

}
