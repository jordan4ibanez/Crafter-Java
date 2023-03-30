package org.crafter.engine.gui.razorfont;



public interface FontLoadingCalls {

    interface StringCall {
        /**
         * Allows automatic render target (OpenGL, Vulkan, Metal, DX) passthrough instantiation.
         * This can basically pass a file location off to your rendering engine and autoload it into memory.
         */
        void fontLoadCallString(String fileLocation);
    }

    interface RawCall {
        /**
         * Allows automatic render target (OpenGL, Vulkan, Metal, DX) DIRECT instantiation.
         * This allows the render engine to AUTOMATICALLY upload the image as RAW data.
         * byte[] = raw data. int = width. int = height.
         */
        void fontLoadCallRaw(byte[] raw, int width, int height);
    }
}
