package org.crafter.engine.gui.razorfont;

public class RawData {
    /// A simple struct to get the font data for the shader
    public float[] vertexPositions;
    public float[] textureCoordinates;
    public int[]    indices;
    public float[] colors;
    public RawData(float[] vertexPositions, float[] textureCoordinates, int[] indices, float[] colors) {
        this.vertexPositions = vertexPositions;
        this.textureCoordinates = textureCoordinates;
        this.indices = indices;
        this.colors = colors;
    }
}
