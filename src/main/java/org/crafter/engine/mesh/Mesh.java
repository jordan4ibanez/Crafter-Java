package org.crafter.engine.mesh;

/**
 * The actual Mesh object.
 * To interface into this you must talk to MeshStorage!
 */
public class Mesh {

    // Reserved invalidation token
    private static final int INVALID = Integer.MAX_VALUE;

    // Required VAO & VBO
    private final int vaoID;
    private final int textureCoordinatesVboID;

    private final int indicesVboID;

    // Optional Vertex Buffer Objects
    private int bonesVboID = INVALID;

    private int colorsVboID = INVALID;

    // Not using builder pattern in Java because I'm trying out a new structure implementation
    Mesh(float[] positions, float[] textureCoordinates, int[] indices, int[] bones, float[] colors) {

        checkRequired(positions, textureCoordinates, indices);


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
