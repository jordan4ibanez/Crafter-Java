package org.crafter.engine.gui.factories

import org.crafter.engine.mesh.MeshStorage.newMesh
import org.crafter.engine.texture.TextureStorage.getFloatingSize
import org.joml.Vector2f
import org.joml.Vector2fc
import java.util.*

object FramedMeshFactory {
    // This is a reuser field
    private val size = Vector2f(0f, 0f)

    // This gets auto initialized
    private val textureSizes = HashMap<String, Vector2fc?>()

    /**
     * Button Mesh Factory does exactly what it says on the tin.
     * It's sole existence is to generate the mesh for the Button component.
     * This keeps the Button class clean as a whistle.
     * Note: The comments are from the original D project.
     */
    fun generateMesh(
        textSize: Vector2fc?,
        padding: Float,
        pixelEdge: Float,
        borderScale: Float,
        fileLocation: String
    ): String {
        // Pixel padding between the edge of the button texture, and the text texture
//        final float padding = Button.getPadding();

        // The guide edges for buttons, keeps texture edges from stretching
        // So think of this of like: How many pixels does your button texture use before getting to the text part.
//        final float pixelEdge = Button.getPixelEdge();

        // Border scalar just makes the button border more pronounced/visible
//        final float borderScale = Button.getBorderScale();
        size[textSize!!.x() + padding * 2] = textSize.y() + padding * 2
        var buttonTextureSize = textureSizes[fileLocation]
        // Auto initialize
        if (buttonTextureSize == null) {
            buttonTextureSize = getFloatingSize(fileLocation)
            textureSizes[fileLocation] = buttonTextureSize
        }

        // We're going to use the height to create the consistent layout
        val centerBorder = size.y / buttonTextureSize!!.y() * pixelEdge * borderScale

        /*
         This is each point on the horizontal 1d array of the button background.

         0  1                                 2  3
          _______________________________________
         |  ___________________________________  |
         | |                                   | |
         */
        //                                           0  1             2                      3
        val horizontalVertex = floatArrayOf(0f, centerBorder, size.x - centerBorder, size.x)

        /*
         This is each point on the vertical 1d array of button background.
         0  ________
           |
         1 |    ____
           |   |
           |   |
           |   |
         2 |   |_____
           |
         3 |_________
         */

        //                                         0  1             2                      3
        val verticalVertex = floatArrayOf(0f, centerBorder, size.y - centerBorder, size.y)
        val vertices = floatArrayOf( // Top left
            horizontalVertex[0], verticalVertex[0],
            horizontalVertex[0], verticalVertex[1],
            horizontalVertex[1], verticalVertex[1],
            horizontalVertex[1], verticalVertex[0],  // Top center
            horizontalVertex[1], verticalVertex[0],
            horizontalVertex[1], verticalVertex[1],
            horizontalVertex[2], verticalVertex[1],
            horizontalVertex[2], verticalVertex[0],  // Top right
            horizontalVertex[2], verticalVertex[0],
            horizontalVertex[2], verticalVertex[1],
            horizontalVertex[3], verticalVertex[1],
            horizontalVertex[3], verticalVertex[0],  // Center left
            horizontalVertex[0], verticalVertex[1],
            horizontalVertex[0], verticalVertex[2],
            horizontalVertex[1], verticalVertex[2],
            horizontalVertex[1], verticalVertex[1],  // Center center
            horizontalVertex[1], verticalVertex[1],
            horizontalVertex[1], verticalVertex[2],
            horizontalVertex[2], verticalVertex[2],
            horizontalVertex[2], verticalVertex[1],  // Center right
            horizontalVertex[2], verticalVertex[1],
            horizontalVertex[2], verticalVertex[2],
            horizontalVertex[3], verticalVertex[2],
            horizontalVertex[3], verticalVertex[1],  // Bottom left
            horizontalVertex[0], verticalVertex[2],
            horizontalVertex[0], verticalVertex[3],
            horizontalVertex[1], verticalVertex[3],
            horizontalVertex[1], verticalVertex[2],  // Bottom center
            horizontalVertex[1], verticalVertex[2],
            horizontalVertex[1], verticalVertex[3],
            horizontalVertex[2], verticalVertex[3],
            horizontalVertex[2], verticalVertex[2],  // Bottom right
            horizontalVertex[2], verticalVertex[2],
            horizontalVertex[2], verticalVertex[3],
            horizontalVertex[3], verticalVertex[3],
            horizontalVertex[3], verticalVertex[2]
        )

        /*
         So the texture coordinates work exactly as explained above, only we're mapping to the texture
         instead of generating the vertices.
         */

        //                                            0     1                                2                                                        3
        val horizontalTexture = floatArrayOf(
            0.0f,
            pixelEdge / buttonTextureSize.x(),
            (buttonTextureSize.x() - pixelEdge) / buttonTextureSize.x(),
            1.0f
        )

        //                                          0     1                                2                                                        3
        val verticalTexture = floatArrayOf(
            0.0f,
            pixelEdge / buttonTextureSize.y(),
            (buttonTextureSize.y() - pixelEdge) / buttonTextureSize.y(),
            1.0f
        )
        val textureCoordinates = floatArrayOf( // Top left
            horizontalTexture[0], verticalTexture[0],
            horizontalTexture[0], verticalTexture[1],
            horizontalTexture[1], verticalTexture[1],
            horizontalTexture[1], verticalTexture[0],  // Top center
            horizontalTexture[1], verticalTexture[0],
            horizontalTexture[1], verticalTexture[1],
            horizontalTexture[2], verticalTexture[1],
            horizontalTexture[2], verticalTexture[0],  // Top right
            horizontalTexture[2], verticalTexture[0],
            horizontalTexture[2], verticalTexture[1],
            horizontalTexture[3], verticalTexture[1],
            horizontalTexture[3], verticalTexture[0],  // Center left
            horizontalTexture[0], verticalTexture[1],
            horizontalTexture[0], verticalTexture[2],
            horizontalTexture[1], verticalTexture[2],
            horizontalTexture[1], verticalTexture[1],  // Center center
            horizontalTexture[1], verticalTexture[1],
            horizontalTexture[1], verticalTexture[2],
            horizontalTexture[2], verticalTexture[2],
            horizontalTexture[2], verticalTexture[1],  // Center right
            horizontalTexture[2], verticalTexture[1],
            horizontalTexture[2], verticalTexture[2],
            horizontalTexture[3], verticalTexture[2],
            horizontalTexture[3], verticalTexture[1],  // Bottom left
            horizontalTexture[0], verticalTexture[2],
            horizontalTexture[0], verticalTexture[3],
            horizontalTexture[1], verticalTexture[3],
            horizontalTexture[1], verticalTexture[2],  // Bottom center
            horizontalTexture[1], verticalTexture[2],
            horizontalTexture[1], verticalTexture[3],
            horizontalTexture[2], verticalTexture[3],
            horizontalTexture[2], verticalTexture[2],  // Bottom right
            horizontalTexture[2], verticalTexture[2],
            horizontalTexture[2], verticalTexture[3],
            horizontalTexture[3], verticalTexture[3],
            horizontalTexture[3], verticalTexture[2]
        )
        val indices = intArrayOf( // Top left
            0, 1, 2, 2, 3, 0,  // Top center
            4, 5, 6, 6, 7, 4,  // Top right
            8, 9, 10, 10, 11, 8,  // Center left
            12, 13, 14, 14, 15, 12,  // Center
            16, 17, 18, 18, 19, 16,  // Center right
            20, 21, 22, 22, 23, 20,  // Bottom left
            24, 25, 26, 26, 27, 24,  // Bottom center
            28, 29, 30, 30, 31, 28,  // Bottom right
            32, 33, 34, 34, 35, 32
        )

        // Fully blank, the shader takes care of blank color space
        val colors = FloatArray(144)
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

        // System.out.println("ButtonMeshFactory: Shipping out UUID (" + uuid + ")!");
        return uuid
    }
}
