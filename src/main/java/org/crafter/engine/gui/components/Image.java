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
package org.crafter.engine.gui.components;

import org.crafter.engine.camera.Camera;
import org.crafter.engine.gui.GUI;
import org.crafter.engine.gui.enumerators.Alignment;
import org.crafter.engine.gui.factories.ImageMeshFactory;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.texture.TextureStorage;
import org.crafter.engine.window.Window;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class Image extends GUIElement{

    private final String fileLocation;

    // Used to keep aspect ratio of raw image
    private final Vector2fc originalImageSize;

    // Used to keep aspect ratio of trimmed image
    private final Vector2f trimmedImageSize = new Vector2f(0);

    private float scale;

    private boolean trimmingEnabled = false;

    public Image(String fileLocation, float scale, Alignment alignment, Vector2f offset) {
        this(fileLocation, scale, alignment, offset, false);
    }

    public Image(String fileLocation, float scale, Alignment alignment, Vector2f offset, boolean trimmingEnabled) {
        super(alignment, offset);

        this.fileLocation = fileLocation;
        this.scale = scale;
        this.trimmingEnabled = trimmingEnabled;

        originalImageSize = TextureStorage.getFloatingSize(fileLocation);

        recalculateMesh();
    }

    public void enableTrimming() {
        if (trimmingEnabled) {
            throw new RuntimeException("Image: You tried to enable trimming more than once!");
        }
        trimmingEnabled = true;
        recalculateMesh();
    }

    public void scale(float newScale) {
        this.scale = newScale;
        recalculateMesh();
    }

    @Override
    public void render() {
        Camera.setGuiObjectMatrix(_position.x, _position.y);
        MeshStorage.render(this._meshUUID);
    }

    @Override
    public boolean collisionDetect(Vector2fc mousePosition) {
        // Does nothing
        return false;
    }

    @Override
    protected void recalculateMesh() {
        if (_meshUUID != null) {
            MeshStorage.destroy(_meshUUID);
        }

        if (trimmingEnabled) {
            _meshUUID = ImageMeshFactory.createTrimmedImageMesh(scale * getGuiScale(), fileLocation);
            this._size.set(ImageMeshFactory.getSizeOfTrimmed(scale * getGuiScale(), fileLocation));
        } else {
            _meshUUID = ImageMeshFactory.createImageMesh(scale * getGuiScale(), fileLocation);
            this._size.set(originalImageSize.x() * scale * getGuiScale(), originalImageSize.y() * scale * getGuiScale());
        }
        recalculatePosition();
    }

    @Override
    public void internalOnStep(GUI gui) {
        if (Window.wasResized()) {
            recalculateMesh();
        }
    }

    @Override
    protected void recalculatePosition() {
        this._position.set(_alignment.value().mul(Window.getWindowSize()).sub(getSize().mul(_alignment.value())).add(offset()));
//        System.out.println("Image (" + this.name() + ") POSITION: " + _position.x + ", " + _position.y);
    }

    @Override
    public void internalOnHover(Vector2fc mousePosition) {}

    @Override
    public void internalOnClick(Vector2fc mousePosition) {}
}
