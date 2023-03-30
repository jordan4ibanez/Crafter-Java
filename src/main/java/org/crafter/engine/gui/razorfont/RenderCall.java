package org.crafter.engine.gui.razorfont;

public interface RenderCall {
    /**
     * Allows automatic render target (OpenGL, Vulkan, Metal, DX) DIRECT instantiation.
     * This allows the render engine to AUTOMATICALLY upload the image as RAW data.
     * byte[] = raw data. int = width. int = height.
     */
    void draw(byte[] raw, int width, int height);
}
