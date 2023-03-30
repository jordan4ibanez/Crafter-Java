package org.crafter.engine.gui.razorfont;

public class RawData {
    /// A simple struct to get the font data for the shader
    float[] vertexPositions;
    float[] textureCoordinates;
    int[]    indices;
    float[] colors;
    RawData(float[] vertexPositions, float[] textureCoordinates, int[] indices, float[] colors) {
        this.vertexPositions = vertexPositions;
        this.textureCoordinates = textureCoordinates;
        this.indices = indices;
        this.colors = colors;
    }
}
