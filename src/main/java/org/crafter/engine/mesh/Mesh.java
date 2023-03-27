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

    Mesh() {

    }



}
