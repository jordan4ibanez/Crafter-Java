package org.crafter.engine.postprocessing;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class PostProcessing implements AutoCloseable {
    private final List<PipelineItem> pipeline;

    public PostProcessing() {
        this.pipeline = new ArrayList<>();

        this.addToPipeline(
                new PipelineItem("2d_do_nothing")
        );
    }

    /**
     * Adds a new pipeline item to the end of the pipeline.
     * @param item The pipeline item to add.
     */
    public void addToPipeline(PipelineItem item) {
        pipeline.add(item);
    }

    /**
     * WARNING: the pipeline must have at least one item to render the screen properly.
     * @return The list of pipeline items. You may modify this, but make sure that there is at least one pipeline item at all times
     */
    public List<PipelineItem> getPipeline() {
        return this.pipeline;
    }

    /**
     * Run this before rendering anything, including clearing the screen.
     * @throws IllegalStateException If the pipeline has 0 items.
     */
    public void start() throws IllegalStateException {
        if (this.pipeline.size() == 0) {
            throw new IllegalStateException("The pipeline must have at least one item");
        }

        this.pipeline.get(0).bind();
    }

    /**
     * Run this at the end of the frame.
     * @throws IllegalStateException If the pipeline has 0 items.
     */
    public void end() throws IllegalStateException {
        glDisable(GL_DEPTH_TEST);
        // glDisable(GL_CULL_FACE);

        if (this.pipeline.size() == 0) {
            throw new IllegalStateException("The pipeline must have at least one item");
        }

        for (int i = 1; i < this.pipeline.size(); i++) {
            PipelineItem item = this.pipeline.get(i);
            item.bind();
            this.pipeline.get(i - 1).draw();
        }

        this.pipeline.get(0).unbind();  // it doesn't matter which one to call to unbind
        this.pipeline.get(this.pipeline.size() - 1).draw();

        // glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
    }

    /**
     * Updates all pipeline items. Changes the texture size if the window is resized.
     */
    public void update() {
        for (PipelineItem item : this.getPipeline()) {
            item.update();
        }
    }

    /**
     * Closes all pipeline items.
     */
    @Override
    public void close() {
        for (PipelineItem pipelineItem : this.getPipeline()) {
            pipelineItem.close();
        }
    }
}
