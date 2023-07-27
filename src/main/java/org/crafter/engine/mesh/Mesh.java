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
package org.crafter.engine.mesh;

import org.crafter.engine.texture.TextureStorage;
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
    private final int indicesCount;

    private int textureID;


    // Optional Vertex Buffer Objects
    private int bonesVboID = INVALID;

    private int colorsVboID = INVALID;

    // Not using builder pattern in Java because I'm trying out a new structure implementation
    public Mesh(String name, float[] positions, float[] textureCoordinates, int[] indices, int[] bones, float[] colors, String textureFileLocation, boolean is2d) {

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

        indicesCount = indices.length;

        vaoID = glGenVertexArrays();

        // Bind into the Vertex Array Object context
        glBindVertexArray(vaoID);


        // Assign REQUIRED Vertex buffer Objects
        positionsVboID = uploadFloatArray(positions, 0, is2d ? 2 : 3);
        textureCoordinatesVboID = uploadFloatArray(textureCoordinates, 1, 2);
        indicesVboID = uploadIndices(indices);

        // Assign OPTIONAL Vertex Buffer Objects
        if (bones != null) {
            bonesVboID = uploadIntArray(bones, 3, 1);
        }

        if (colors != null) {
            colorsVboID = uploadFloatArray(colors, 2, 4);
        }

        // Now unbind the Vertex Array Object context
        glBindVertexArray(0);
    }

    // Allows hot swapping texture for Mesh
    void swapTexture(String newTextureLocation) {
        int newTextureID;
        try {
            newTextureID = TextureStorage.getID(newTextureLocation);
        } catch (RuntimeException e) {
            // Throwing a more contextual error here
            throw new RuntimeException("Mesh: Tried to hotswap to nonexistent texture (" + newTextureLocation + ") on mesh (" + name + ")!");
        }
        textureID = newTextureID;
    }

    // Automated internal render method
    public void render() {
        // All shaders are REQUIRED to have a texture sampler
        // FIXME: But why are we setting this to 0 over and over?
        // TEST: Remove this and see if it works on Mesa

        // This is always on array column 0
//        ShaderStorage.setUniform("textureSampler", 0);


        // Activate Bank 0
        // This is always on bank 0
//        glActiveTexture(GL_TEXTURE0);

        // Now bind the context of the texture
        glBindTexture(GL_TEXTURE_2D, textureID);

        // Bind to Vertex Array Object context
        glBindVertexArray(vaoID);

        // TODO: Test this (this is line mode)
        // glDrawArrays(GL_LINES, 0, this.indexCount)

        // Draw it - Indices Array starts at 0 (tightly packed)
        glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_INT, 0);

        // Optional safety precaution
        // Unbind Vertex Array Object context
        glBindVertexArray(0);
    }

    // Automated internal line mode render method
    public void renderLineMode() {
        glBindTexture(GL_TEXTURE_2D, textureID);
        glBindVertexArray(vaoID);
//        glDrawArrays(GL_TRIANGLES, 0, indicesCount);
         glDrawElements(GL_LINES, indicesCount, GL_UNSIGNED_INT, 0);
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

            // Now enable memory address pointer
            glEnableVertexAttribArray(glslPosition);

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

            // Now enable memory address pointer
            glEnableVertexAttribArray(glslPosition);

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

            // Note: Do note unbind GL_ELEMENT_ARRAY_BUFFER

        } finally {
            if (buffer != null) {
                MemoryUtil.memFree(buffer);
            }
        }
        return returningID;
    }

    // Completely obliterates this VAO and all VBOs associated with it
    public void destroy() {

        // Bind into Vertex Array Object context
        glBindVertexArray(vaoID);

        // Destroy REQUIRED Vertex Buffer Objects
        destroyVBO(positionsVboID, 0, "positions");
        destroyVBO(textureCoordinatesVboID, 1, "texture coordinates");
        destroyVBO(indicesVboID, -1, "indices");

        // Destroy OPTIONAL Vertex Buffer Objects
        if (bonesVboID != INVALID) {
            destroyVBO(bonesVboID, 3, "bones");
        }

        if (colorsVboID != INVALID) {
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
        if (glslPosition >= 0) {
            glDisableVertexAttribArray(glslPosition);
        }
        glDeleteBuffers(vboID);
        if (glIsBuffer(vboID)) {
            throw new RuntimeException("Mesh (" + name + "): Failed to delete VBO (" + vboName + ")!");
        }
    }

    // This is a separate method to improve the constructor readability
    private void checkRequired(float[] positions, float[] textureCoordinates, int[] indices) {
        // Null check
        if (positions == null) {
            throw new RuntimeException("Mesh: Positions parameter CANNOT be null!");
        } else if (textureCoordinates == null) {
            throw new RuntimeException("Mesh: Texture coordinates parameter CANNOT be null!");
        } else if (indices == null) {
            throw new RuntimeException("Mesh: Indices parameter CANNOT be null!");
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
}
