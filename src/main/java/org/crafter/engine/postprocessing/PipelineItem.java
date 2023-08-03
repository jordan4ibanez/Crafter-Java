package org.crafter.engine.postprocessing;

import org.crafter.engine.shader.ShaderStorage;
import org.crafter.engine.window.Window;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;

public class PipelineItem implements AutoCloseable {
    private int rectVAO;
    private int rectVBO;

    private final int framebuffer;
    private final int framebufferTexture;
    private final String shaderID;

    /**
     * Creates a pipeline item to be used in post-processing.
     * @param shaderID The shader program to be used for post-processing. Any vertex shaders passed should not have a
     *                 projection matrix, view matrix, model matrix, or any other matrix.
     */
    public PipelineItem(String shaderID) {
        this.shaderID = shaderID;

        this.framebuffer = glGenFramebuffers();
        this.bind();
        this.framebufferTexture = glGenTextures();
        this.refreshFramebufferTexture();

        errorCheck();

        this.unbind();

        this.genRect();
    }

    /**
     * Sets the rectVAO and rectVBO member variables.
     */
    private void genRect() {
        final float[] coords = new float[]{
                // Coords      Tex coords
                -1,  1,        0, 1,
                -1, -1,        0, 0,
                1, -1,         1, 0,

                -1, 1,         0, 1,
                1, -1,         1, 0,
                1,  1,         1, 1,
        };

        this.rectVAO = glGenVertexArrays();
        this.rectVBO = glGenBuffers();

        glBindVertexArray(this.rectVAO);
        glBindBuffer(GL_ARRAY_BUFFER, this.rectVBO);

        int sizeOfFloat = Float.SIZE / 8;

        glBufferData(GL_ARRAY_BUFFER, coords, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * sizeOfFloat, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * sizeOfFloat, 2 * sizeOfFloat);
    }

    private void refreshFramebufferTexture() {
        this.bind();

        glBindTexture(GL_TEXTURE_2D, this.framebufferTexture);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, Window.getWindowWidth(), Window.getWindowHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.framebufferTexture, 0);

        this.unbind();
    }

    private static void errorCheck() {
        int fboStatus = glCheckFramebufferStatus(GL_FRAMEBUFFER);

        if (fboStatus != GL_FRAMEBUFFER_COMPLETE) {
            System.err.println("FBO status has an error. Error code: " + fboStatus);
        }
    }

    public String getShaderID() {
        return this.shaderID;
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, this.framebuffer);
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void draw() {
        glBindTexture(GL_TEXTURE_2D, this.framebufferTexture);
        ShaderStorage.start(this.shaderID);

        glBindVertexArray(this.rectVAO);
        glDrawArrays(GL_TRIANGLES, 0, 6);

        ShaderStorage.stop();
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * Changes the frame buffer texture size to the window's new size if it is rescaled
     */
    public void update() {
        if (!Window.wasResized()) {
            return;
        }

        this.refreshFramebufferTexture();
    }

    /**
     * Warning: this doesn't close the shader program associated with the pipeline item since it is not created
     *          by the PipelineItem itself. Make sure to do this.getShaderProgram().close() before running this
     */
    @Override
    public void close() {
        glDeleteFramebuffers(this.framebuffer);
        glDeleteTextures(this.framebufferTexture);

        glDeleteVertexArrays(this.rectVAO);
        glDeleteBuffers(this.rectVBO);
    }
}