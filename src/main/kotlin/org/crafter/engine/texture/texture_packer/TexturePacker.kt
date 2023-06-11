@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package org.crafter.engine.texture.texture_packer

import org.joml.*
import org.lwjgl.stb.STBImageWrite
import java.nio.ByteBuffer
import java.util.*

/**
 * This is translated from a D project.
 * [Original project.](https://github.com/jordan4ibanez/fast_pack/blob/main/source/fast_pack.d)
 * Now works as a singleton.
 */
class TexturePacker {
    // Ignore intellij, these are extremely useful to modify up top!
    private val padding = 1

    // These were from the D project, but hey, maybe one day they'll be reimplemented
    private val edgeColor = Vector4i(0, 0, 0, 255)
    private val blankSpaceColor = Vector4i(0, 0, 0, 0)
    private val showDebugEdge = false
    private val expansionAmount = 16
    private val width = 16
    private val height = 16
    private var canvasMaxWidth = 0
    private var canvasMaxHeight = 0
    private val textures: HashMap<String, TexturePackerObject> = HashMap()
    private val canvas: Canvas = Canvas(width, height)
    private val availableX: SortedSet<Int>
    private val availableY: SortedSet<Int>

    /**
     * Buffer automatically cleans up it's data upon flush().
     * But it's still useful to have it as it contains coordinates for textures!
     * So we must lock it out.
     */
    private var lockedOut = false

    init {
        availableX = TreeSet()
        availableY = TreeSet()

        // Needs defaults (top left) or turns into infinite loop
        availableX.add(padding)
        availableY.add(padding)
    }

    val isEmpty: Boolean
        get() = textures.isEmpty()

    /**
     * "dirt.png", "water.png", etc
     * @param fileName The name which you gave the file.
     * @return if it exists.
     */
    fun fileNameExists(fileName: String): Boolean {
        enforceLockout("fileNameExists")
        return textures.containsKey(fileName)
    }

    /**
     * Returns a literal location and size in texture atlas, not adjusted to OpenGL!
     * Specifically implemented to make making quads easier.
     * @param fileName the name of the texture in the texture atlas.
     * @return a Vector4ic containing (position X, position Y, width, height)
     */
    fun getIntegralPositions(fileName: String): Vector4ic {
        existenceCheck(fileName)
        return textures[fileName]!!.positionAndSize
    }

    /**
     * Returns an OpenGL adjusted location and size in texture atlas.
     * @param fileName the name of the texture in the texture atlas.
     * @return a Vector4fc containing OpenGL Scaled (position X, position Y, width, height)
     */
    fun getOpenGLPositions(fileName: String): Vector4fc {
        enforceLockout("getOpenGLPositions")
        val gottenIntegralPositionAndSize = getIntegralPositions(fileName)

//        System.out.println(gottenIntegralPositionAndSize.x() + "," + gottenIntegralPositionAndSize.y() + "," + gottenIntegralPositionAndSize.z() + "," + gottenIntegralPositionAndSize.w());
        return Vector4f( // Position X
            gottenIntegralPositionAndSize.x().toFloat() / canvasMaxWidth.toFloat(),  // Position Y
            gottenIntegralPositionAndSize.y().toFloat() / canvasMaxHeight.toFloat(),  // Width
            gottenIntegralPositionAndSize.z().toFloat() / canvasMaxWidth.toFloat(),  // Height
            gottenIntegralPositionAndSize.w().toFloat() / canvasMaxHeight.toFloat()
        )
    }

    /**
     * Returns a float[] of quad points.
     * @param fileName the name of the texture in the texture atlas.
     * @return a float[] containing exactly OpenGL Positions. xy[top left, bottom left, bottom right, top right]
     */
    fun getQuadOf(fileName: String): FloatArray {
        enforceLockout("getQuadOf")

        // this var was originally called: gottenOpenGLPositionAndSize. You can probably see why I changed it
        val p = getOpenGLPositions(fileName)
        //        System.out.println(p.x() + "," + p.y() + "," + p.z() + "," + p.w());
        // Z = width
        // W = height
        return floatArrayOf( // Top left
            p.x(), p.y(),  // Bottom left
            p.x(), p.y() + p.w(),  // Bottom right
            p.x() + p.z(), p.y() + p.w(),  // Top right
            p.x() + p.z(), p.y()
        )
    }

    /**
     *
     * @param fileName location of the file!
     * @param xLeftTrim How far to trim into the left side towards the right (0.0f, 1.0f)
     * @param xRightTrim How far to trim into the right towards the left (0.0f, 1.0f)
     * @param yTopTrim How far to trim into the top towards the bottom (0.0f, 1.0f)
     * @param yBottomTrim How far to trim into the bottom towards the top (0.0f, 1.0f)
     * @return a float[] containing exactly OpenGL Positions. xy[top left, bottom left, bottom right, top right]
     */
    fun getQuadOf(
        fileName: String,
        xLeftTrim: Float,
        xRightTrim: Float,
        yTopTrim: Float,
        yBottomTrim: Float
    ): FloatArray {
        enforceLockout("getQuadOf")

        // o stands for original position
        val names = arrayOf("xLeftTrim", "xRightTrim", "yTopTrim", "yBottomTrim")
        val values = floatArrayOf(xLeftTrim, xRightTrim, yTopTrim, yBottomTrim)
        for (i in values.indices) {
            val gottenValue = values[i]
            if (gottenValue > 1 || gottenValue < 0) {
                throw RuntimeException("TexturePacker: Trimming value for (" + names[i] + ") is out of bounds! Min 0.0f | max 1.0f")
            }
        }
        val o = getOpenGLPositions(fileName)

        // Z = width
        // W = height
        val adjustedXLeftTrim = xLeftTrim * o.z() + o.x()
        val adjustedXRightTrim = xRightTrim * o.z() + o.x() + o.z()
        val adjustedYTopTrim = yTopTrim * o.w() + o.y()
        val adjustedYBottomTrim = yBottomTrim * o.w() + o.y() + o.w()

        // p stands for position
        return floatArrayOf( // Top left
            adjustedXLeftTrim, adjustedYTopTrim,  // Bottom left
            adjustedXLeftTrim, adjustedYBottomTrim,  // Bottom right
            adjustedXRightTrim, adjustedYBottomTrim,  // Top right
            adjustedXRightTrim, adjustedYTopTrim
        )
    }

    fun add(fileName: String, fileLocation: String) {
        lockoutCheck("add")
        duplicateCheck(fileName)
        textures[fileName] = TexturePackerObject(fileLocation)
    }

    fun debugPrintCanvas() {
        lockoutCheck("debugPrintCanvas")
        pack()
        STBImageWrite.stbi_write_png(
            "test.png",
            canvas.getSize().x(),
            canvas.getSize().y(),
            4,
            canvas.data,
            canvas.getSize().x() * 4
        )
    }

    fun flush(): ByteBuffer {
        lockoutCheck("flush")
        pack()
        return canvas.data
    }

    private fun pack() {
        for (`object` in textures.values) {
            while (!tetrisPack(`object`)) {
                val gottenCanvasSize = canvas.getSize()
                canvas.resize(gottenCanvasSize.x() + expansionAmount, gottenCanvasSize.y() + expansionAmount)
            }
        }
        flushCanvas()
    }

    private fun flushCanvas() {
        canvas.resize(canvasMaxWidth, canvasMaxHeight)
        canvas.allocate()
        for (`object` in textures.values) {
            val posX = `object`.position.x()
            val posY = `object`.position.y()
            for (x in 0 until `object`.size.x()) {
                for (y in 0 until `object`.size.y()) {
                    canvas.setPixel(`object`.getPixel(x, y), x + posX, y + posY)
                }
            }
        }
        lockedOut = true
        for (`object` in textures.values) {
            `object`.destroy()
        }
    }

    private fun tetrisPack(`object`: TexturePackerObject): Boolean {
        var found = false

//        int score = Integer.MAX_VALUE;
        val maxX = canvas.getSize().x()
        val maxY = canvas.getSize().y()
        val thisWidth = `object`.size.x()
        val thisHeight = `object`.size.y()
        var bestX = padding
        var bestY = padding
        for (y in availableY) {
            if (found) {
                break
            }
            for (x in availableX) {

//                int newScore = x + y;
//
//                System.out.println(newScore);
//
////                if (newScore > score) {
////                    continue;
////                }
                if (x + thisWidth + padding >= maxX || y + thisHeight + padding >= maxY) {
                    continue
                }
                var failed = false
                for (otherObject in textures.values) {
                    if (otherObject.uuid == `object`.uuid) {
                        continue
                    }
                    val otherX = otherObject.position.x()
                    val otherY = otherObject.position.y()
                    val otherWidth = otherObject.size.x()
                    val otherHeight = otherObject.size.y()
                    if (otherObject.packed && otherX + otherWidth + padding > x && otherX <= x + thisWidth + padding && otherY + otherHeight + padding > y && otherY <= y + thisHeight + padding) {
                        failed = true
                        break
                    }
                }
                if (!failed) {
                    found = true
                    bestX = x
                    bestY = y
                    //                    score = newScore;
                    break
                }
            }
        }

        // Found has mutated
        if (!found) {
            return false
        }
        `object`.setPosition(bestX, bestY)
        `object`.setPacked()
        val spotRight = bestX + thisWidth + padding
        val spotBelow = bestY + thisHeight + padding
        availableX.add(spotRight)
        availableY.add(spotBelow)
        if (spotRight > canvasMaxWidth) {
            canvasMaxWidth = spotRight
        }
        if (spotBelow > canvasMaxHeight) {
            canvasMaxHeight = spotBelow
        }
        return true
    }

    val canvasSize: Vector2ic
        get() {
            enforceLockout("getCanvasSize")
            return canvas.getSize()
        }

    private fun duplicateCheck(fileLocation: String) {
        if (textures.containsKey(fileLocation)) {
            throw RuntimeException("TexturePacker: Attempted to put duplicate of ($fileLocation)!")
        }
    }

    private fun existenceCheck(fileLocation: String) {
        if (!textures.containsKey(fileLocation)) {
            throw RuntimeException("TexturePacker: Attempted to access ($fileLocation) which is a nonexistent texture!")
        }
    }

    private fun lockoutCheck(methodName: String) {
        if (lockedOut) {
            throw RuntimeException("TexturePacker: Attempted to run method ($methodName) after flushing the buffer!")
        }
    }

    private fun enforceLockout(methodName: String) {
        if (!lockedOut) {
            throw RuntimeException("TexturePacker: Attempted to run method ($methodName) before flushing the buffer!")
        }
    }
}
