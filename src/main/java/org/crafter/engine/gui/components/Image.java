package org.crafter.engine.gui.components;

import org.crafter.engine.gui.GUI;
import org.crafter.engine.gui.enumerators.Alignment;
import org.crafter.engine.gui.factories.ImageMeshFactory;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.texture.TextureStorage;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class Image extends GUIElement{

    private final String fileLocation;

    // Used to keep aspect ratio of raw image
    private final Vector2f originalImageSize = new Vector2f(0);

    // Used to keep aspect ratio of trimmed image
    private final Vector2f trimmedImageSize = new Vector2f(0);

    private float scale;

    private boolean trimmingEnabled = false;

    protected Image(String fileLocation, float scale, Alignment alignment, Vector2f offset) {
        super(alignment, offset);

        this.fileLocation = fileLocation;
        this.scale = scale;

        originalImageSize.set(TextureStorage.getFloatingSize(fileLocation));

        recalculateMesh();
    }

    public void enableTrimming() {
        if (trimmingEnabled) {
            throw new RuntimeException("Image: You tried to enable trimming more than once!");
        }
        trimmingEnabled = true;
        recalculateMesh();
    }

    @Override
    public void render() {

    }

    @Override
    public boolean collisionDetect(Vector2fc mousePosition) {
        return false;
    }

    @Override
    protected void recalculateMesh() {
        if (_meshUUID != null) {
            MeshStorage.destroy(_meshUUID);
        }

        _meshUUID = ImageMeshFactory.createImageMesh(scale, fileLocation);
    }

    @Override
    public void internalOnStep(GUI gui) {

    }

    @Override
    protected void recalculatePosition() {

    }
}
