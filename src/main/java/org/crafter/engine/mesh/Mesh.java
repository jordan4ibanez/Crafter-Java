package org.crafter.engine.mesh;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;

/**
 * The actual Mesh object.
 * To interface into this you must talk to MeshStorage!
 */
public class Mesh {

    // Reserved invalidation token
    private static final int INVALID = Integer.MAX_VALUE;

    // Required VAO, VBO, & vertex count
    private final int vaoID;
    private final int textureCoordinatesVboID;

    private final int indicesVboID;

    private final int vertexCount;

    // Optional Vertex Buffer Objects
    private int bonesVboID = INVALID;

    private int colorsVboID = INVALID;

    // Not using builder pattern in Java because I'm trying out a new structure implementation
    Mesh(float[] positions, float[] textureCoordinates, int[] indices, int[] bones, float[] colors) {

        checkRequired(positions, textureCoordinates, indices);

        vertexCount = positions.length / 3;

        vaoID = glGenVertexArrays();

        // Bind into the Vertex Array Object context
        glBindVertexArray(vaoID);

        // Vertex Array Object - Buffer Objects go here




        // Now unbind the Vertex Array Object context
        glBindVertexArray(0);
    }

    // Float[] automator method
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


    // This method is specialized, uploads the indices from an int[]
    private int uploadIndices(int[] indicesArray) {

        // Starts off as: int* var = nullptr;
        IntBuffer buffer = null;

        final int returningID;

        try {

            returningID = glGenBuffers();

            buffer = MemoryUtil.memAllocInt(indicesArray.length);
            buffer.put(indicesArray).flip();

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, returningID);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        } finally {
            if (buffer != null) {
                MemoryUtil.memFree(buffer);
            }
        }
        return returningID;
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
