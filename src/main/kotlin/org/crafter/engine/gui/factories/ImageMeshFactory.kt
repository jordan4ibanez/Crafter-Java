package org.crafter.engine.gui.factories

import org.crafter.engine.gui.records.ImageTrim
import org.crafter.engine.mesh.MeshStorage.newMesh
import org.crafter.engine.texture.RawTextureObject
import org.crafter.engine.texture.TextureStorage.getFloatingSize
import org.joml.Vector2f
import org.joml.Vector2fc
import java.util.*

/**
 * This generates a rectangle mesh image. That's it.
 */
object ImageMeshFactory {
    private val textureSizes = HashMap<String, Vector2fc?>()
    private val textureTrims = HashMap<String, ImageTrim?>()
    fun createImageMesh(scale: Float, fileLocation: String): String {
        var imageTextureSize = textureSizes[fileLocation]
        if (imageTextureSize == null) {
            imageTextureSize = getFloatingSize(fileLocation)
            textureSizes[fileLocation] = imageTextureSize
        }
        val width = imageTextureSize!!.x() * scale
        val height = imageTextureSize.y() * scale
        val vertices = floatArrayOf(
            0.0f, 0.0f,
            0.0f, height,
            width, height,
            width, 0.0f
        )
        val textureCoordinates = floatArrayOf(
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f
        )
        val indices = intArrayOf(
            0, 1, 2, 2, 3, 0
        )

        // Fully blank, the shader takes care of blank color space
        val colors = FloatArray(16)
        val uuid = UUID.randomUUID().toString()
        newMesh(
            uuid,
            vertices,
            textureCoordinates,
            indices,
            null,
            colors,
            fileLocation,
            true
        )

//        System.out.println("ImageMeshFactory: Shipping out UUID (" + uuid + ")!");
        return uuid
    }

    /**
     * I could have made the above function do a combo of this.
     * But I think it's easier to understand if it's more explicit.
     */
    fun createTrimmedImageMesh(scale: Float, fileLocation: String): String {
        var imageTrim = textureTrims[fileLocation]
        if (imageTrim == null) {
            imageTrim = trimImage(fileLocation)
            textureTrims[fileLocation] = imageTrim
        }
        val width = imageTrim.width * scale
        val height = imageTrim.height * scale
        val vertices = floatArrayOf(
            0.0f, 0.0f,
            0.0f, height,
            width, height,
            width, 0.0f
        )
        val textureCoordinates = floatArrayOf(
            imageTrim.startX, imageTrim.startY,
            imageTrim.startX, imageTrim.endY,
            imageTrim.endX, imageTrim.endY,
            imageTrim.endX, imageTrim.startY
        )
        val indices = intArrayOf(
            0, 1, 2, 2, 3, 0
        )

        // Fully blank, the shader takes care of blank color space
        val colors = FloatArray(12)
        val uuid = UUID.randomUUID().toString()
        newMesh(
            uuid,
            vertices,
            textureCoordinates,
            indices,
            null,
            colors,
            fileLocation,
            true
        )

//        System.out.println("ImageMeshFactory: Shipping out UUID (" + uuid + ")!");
        return uuid
    }

    fun getSizeOfTrimmed(scale: Float, fileLocation: String): Vector2f {
        if (!textureTrims.containsKey(fileLocation)) {
            throw RuntimeException("ImageMeshFactory: attempted to access size of null trimmed texture! ($fileLocation)")
        }
        val thisTrim = textureTrims[fileLocation]
        return Vector2f(thisTrim!!.width * scale, thisTrim.height * scale)
    }

    private fun trimImage(fileLocation: String): ImageTrim {
        val width: Float
        val height: Float
        var startX = 0f
        var endX = 0f
        var startY = 0f
        var endY = 0f
        val tempImageObject = RawTextureObject(fileLocation)
        val tempWidth = tempImageObject.width.toFloat()
        val tempHeight = tempImageObject.height.toFloat()

        // This check only has to run once
        var blank = true
        var found = false

        // StartX
        run {
            var x = 0
            while (x < tempWidth) {
                var y = 0
                while (y < tempHeight) {
                    if (tempImageObject.getPixel(x, y).w > 0) {
                        blank = false
                        found = true
                        break
                    }
                    y++
                }
                if (found) {
                    startX = x.toFloat()
                    break
                }
                x++
            }
        }
        if (blank) {
            throw RuntimeException("ImageMeshFactory: Tried to trim a blank image!")
        }
        found = false

        // EndX
        for (x in tempWidth.toInt() - 1 downTo 0) {
            var y = 0
            while (y < tempHeight) {
                if (tempImageObject.getPixel(x, y).w > 0) {
                    found = true
                    break
                }
                y++
            }
            if (found) {
                endX = (x + 1).toFloat()
                break
            }
        }
        found = false

        // StartY
        run {
            var y = 0
            while (y < tempHeight) {
                var x = 0
                while (x < tempWidth) {
                    if (tempImageObject.getPixel(x, y).w > 0) {
                        found = true
                        break
                    }
                    x++
                }
                if (found) {
                    startY = y.toFloat()
                    break
                }
                y++
            }
        }
        found = false

        // EndY
        for (y in tempHeight.toInt() - 1 downTo 0) {
            var x = 0
            while (x < tempWidth) {
                if (tempImageObject.getPixel(x, y).w > 0) {
                    found = true
                    break
                }
                x++
            }
            if (found) {
                endY = (y + 1).toFloat()
                break
            }
        }

        // Now finish calculations
        width = endX - startX
        height = endY - startY
        startX /= tempWidth
        endX /= tempWidth
        startY /= tempHeight
        endY /= tempHeight

        // Delete that C memory
        tempImageObject.destroy()
        return ImageTrim(width, height, startX, endX, startY, endY)
    }
}
