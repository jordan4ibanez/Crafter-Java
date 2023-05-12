package org.crafter.engine.gui.components

import org.crafter.engine.camera.Camera.setGuiObjectMatrix
import org.crafter.engine.gui.GUI
import org.crafter.engine.gui.enumerators.Alignment
import org.crafter.engine.gui.factories.ImageMeshFactory
import org.crafter.engine.mesh.MeshStorage.destroy
import org.crafter.engine.mesh.MeshStorage.render
import org.crafter.engine.texture.TextureStorage.getFloatingSize
import org.crafter.engine.window.Window.getWindowSize
import org.crafter.engine.window.Window.wasResized
import org.joml.Vector2f
import org.joml.Vector2fc

class Image @JvmOverloads constructor(
    private val fileLocation: String,
    private var scale: Float,
    alignment: Alignment,
    offset: Vector2f,
    trimmingEnabled: Boolean = false
) : GUIElement(alignment, offset) {
    // Used to keep aspect ratio of raw image
    private val originalImageSize: Vector2fc

    // Used to keep aspect ratio of trimmed image
    private val trimmedImageSize = Vector2f(0f)
    private var trimmingEnabled = false

    init {
        this.trimmingEnabled = trimmingEnabled
        originalImageSize = getFloatingSize(fileLocation)
        recalculateMesh()
    }

    fun enableTrimming() {
        if (trimmingEnabled) {
            throw RuntimeException("Image: You tried to enable trimming more than once!")
        }
        trimmingEnabled = true
        recalculateMesh()
    }

    fun scale(newScale: Float) {
        scale = newScale
        recalculateMesh()
    }

    override fun render() {
        setGuiObjectMatrix(position.x, position.y)
        render(meshUUID)
    }

    override fun collisionDetect(mousePosition: Vector2fc): Boolean {
        // Does nothing
        return false
    }

    override fun recalculateMesh() {
        if (meshUUID != "") {
            destroy(meshUUID)
        }
        if (trimmingEnabled) {
            meshUUID =
                ImageMeshFactory.createTrimmedImageMesh(scale * guiScale, fileLocation)
            size.set(ImageMeshFactory.getSizeOfTrimmed(scale * guiScale, fileLocation))
        } else {
            meshUUID = ImageMeshFactory.createImageMesh(scale * guiScale, fileLocation)
            size.set(
                originalImageSize.x() * scale * guiScale,
                originalImageSize.y() * scale * guiScale
            )
        }
        recalculatePosition()
    }

    override fun internalOnStep(gui: GUI) {
        if (wasResized()) {
            recalculateMesh()
        }
    }

    override fun recalculatePosition() {
        position.set(alignment.value().mul(getWindowSize()).sub(size.mul(alignment.value())).add(offset()))
        //        System.out.println("Image (" + this.name() + ") POSITION: " + _position.x + ", " + _position.y);
    }

    override fun internalOnHover(mousePosition: Vector2fc) {}
    override fun internalOnClick(mousePosition: Vector2fc) {}
}
