package org.crafter.engine.gui.font

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeType
import org.crafter.engine.mesh.MeshStorage.newMesh
import org.crafter.engine.texture.RawTextureObject
import org.crafter.engine.texture.TextureStorage.createTexture
import org.crafter.engine.utility.FileReader.getFileString
import org.joml.Vector2f
import org.joml.Vector3f
import java.io.File
import java.util.*

/**
 * This is my RazorFont library translated from D.
 * Various things have changed in this, simplification is a main goal.
 * We seriously do NOT want to create new objects every frame.
 * This NEEDS to be only one object, reused every frame.
 * See original here: [Fancy Link](https://github.com/jordan4ibanez/RazorFont/blob/main/source/razor_font.d)
 * You can see this is built like a static D module.
 * No, I will not make this a singleton.
 */
object Font {
    // The current character limit (letters in string)
    const val maxChars = 4096

    // 4 vec2 (so 8 per char) vertex positions
    private val vertexCache = FloatArray(maxChars * 8)

    // 4 vec4 (so 16 per char) colors - defaults to 0,0,0,1 rgba
    private val colorCache = FloatArray(maxChars * 16)

    /// 4 vec2 (so 8 per char) texture coordinate positions
    private val textureCoordinateCache = FloatArray(maxChars * 8)

    // 2 tris (so 6 per char) indices
    private val indicesCache = IntArray(maxChars * 6)

    /// The count of each of these so we can grab a slice of data fresh out of the oven, delicious!
    private var vertexCount = 0
    private var textureCoordinateCount = 0
    private var indicesCount = 0
    private var colorCount = 0
    var currentCharacterIndex = 0
        private set
    private val RAW_VERTEX = floatArrayOf(
        0f, 0f,
        0f, 1f,
        1f, 1f,
        1f, 0f
    )
    private val RAW_INDICES = intArrayOf(
        0, 1, 2,
        2, 3, 0
    )
    private var firstFont = true
    private var shadowOffsetX = 0.05f
    private var shadowOffsetY = 0.05f
    private val shadowColor = floatArrayOf(0f, 0f, 0f, 1f)
    private var currentFont: FontData? = null
    private var fontLock = false
    private val fonts = HashMap<String, FontData>()

    /**
     * This is a very simple fix for static memory arrays being filled with no.
     * A simple on switch for initialization.
     * To use RazorFont, you must create a font, so it runs this in there.
     */
    private var initializedColorArray = false
    private fun initColorArray() {
        if (initializedColorArray) {
            return
        }
        initializedColorArray = true
        var i = 0
        while (i < 16 * maxChars) {
            colorCache[i] = 0f
            colorCache[i + 1] = 0f
            colorCache[i + 2] = 0f
            colorCache[i + 3] = 1f
            i += 4
        }
    }

    private fun uploadFontTexture(fileLocation: String) {
        createTexture(fileLocation)
    }

    @JvmOverloads
    fun createFont(
        fileLocation: String,
        name: String,
        trimming: Boolean,
        spacing: Float = 1.0f,
        spaceCharacterSize: Float = 4.0f
    ) {

        // This fills the color buffer's initial values to black
        initColorArray()
        val pngLocation = "$fileLocation.png"
        val jsonLocation = "$fileLocation.json"

        // Make sure the files exist
        checkFilesExist(pngLocation, jsonLocation)

        // Automate existing engine integration
        uploadFontTexture(pngLocation)

        // Create the Font object
        val fontObject = FontData()

        // Store the file location in the object
        fontObject.fileLocation = pngLocation

        // Now parse the json, and pass it into object
        parseJson(fontObject, jsonLocation)

        // Now encode the linear string as a keymap of raw graphics positions
        encodeGraphics(fontObject, trimming, spacing, spaceCharacterSize)

        // Finally add it into the library
        fonts[name] = fontObject

        // If it's the first font, automatically select it
        if (firstFont) {
            selectFont(name)
            firstFont = false
        }
    }

    fun selectFont(font: String) {
        if (fontLock) {
            throw RuntimeException("Font: You must render text to clear out the cache before selecting a new font!")
        }

        // Can't render if that font doesn't exist
        if (!fonts.containsKey(font)) {
            throw RuntimeException("Font: Error! $font is not a registered font!")
        }

        // Now store and lock
        currentFont = fonts[font]
        fontLock = true
    }

    val currentFontTextureFileLocation: String?
        get() {
            if (currentFont == null) {
                throw RuntimeException("Font: Can't get a font file location! You didn't select one!")
            }
            return currentFont!!.fileLocation
        }

    fun switchShadowColor(c: Vector3f) {
        switchShadowColor(c.x, c.y, c.z)
    }

    @JvmOverloads
    fun switchShadowColor(r: Float, g: Float, b: Float, a: Float = 1f) {
        shadowColor[0] = r
        shadowColor[1] = g
        shadowColor[2] = b
        shadowColor[3] = a
    }

    /**
     * Allows you to blanket set the color for the entire canvas.
     * Be careful though, this overwrites the entire color cache
     * after the currently rendered character position in memory!
     */
    fun switchColor(c: Vector3f) {
        switchColor(c.x, c.y, c.z)
    }

    @JvmOverloads
    fun switchColor(r: Float, g: Float, b: Float, a: Float = 1f) {
        var i = colorCount
        while (i < colorCache.size) {
            colorCache[i] = r
            colorCache[i + 1] = g
            colorCache[i + 2] = b
            colorCache[i + 3] = a
            i += 4
        }
    }

    /**
     * Allows you to set the offet of the text shadowing.
     * This is RELATIVE via the font size, so it will remain consistent
     * across any font size!
     * Remember: Offset will become reset to default when you call renderToCanvas()
     */
    fun setShadowOffset(x: Float, y: Float) {
        shadowOffsetX = x / 10.0f
        shadowOffsetY = y / 10.0f
    }

    /**
     * Allows you to blanket a range of characters in the canvas with a color.
     * So if you have: abcdefg
     * And run setColorRange(0.5,0.5,0.5, 1, 3, 5)
     * Now e and f are gray. Alpha 1.0
     */
    fun setColorRange(start: Int, end: Int, r: Float, g: Float, b: Float) {
        setColorRange(start, end, r, g, b, 1f)
    }

    fun setColorRange(start: Int, end: Int, r: Float, g: Float, b: Float, a: Float) {
        var i = start * 16
        while (i < end * 16) {
            colorCache[i] = r
            colorCache[i + 1] = g
            colorCache[i + 2] = b
            colorCache[i + 3] = a
            i += 4
        }
    }

    fun getTextCenter(fontSize: Float, text: String?): Vector2f {
        val textSize = getTextSize(fontSize, text)
        textSize.x /= 2.0f
        textSize.y /= 2.0f
        return textSize
    }

    fun getTextSize(fontSize: Float, text: String?): Vector2f {
        var maxWidth = 0f
        var currentWidth = 0f
        var currentHeight = 0.0f

        // Can't get the size if there's no font!
        if (currentFont == null) {
            throw RuntimeException("Razor Font: Tried to get text size without selecting a font! You must select a font before getting the size of text with it!")
        }

        // Cache spacing
        val spacing = currentFont!!.spacing * fontSize
        // Cache space (' ') character
        val spaceCharacterSize = currentFont!!.spaceCharacterSize * fontSize
        for (character in text!!.toCharArray()) {
            val currentStringChar = character.toString()

            // Skip space
            if (character == ' ') {
                currentWidth += spaceCharacterSize
                continue
            }
            // Move down 1 space Y
            if (character == '\n') {
                if (currentWidth > maxWidth) {
                    maxWidth = currentWidth
                }
                currentWidth = 0f
                currentHeight += fontSize
                continue
            }

            // Skip unknown character
            if (!currentFont!!.map.containsKey(currentStringChar)) {
                continue
            }

            // Font stores character width in index 9 (8 [0 count])
            val characterWidth = currentFont!!.map[currentStringChar]!![8]
            currentWidth += characterWidth * fontSize + spacing
        }

        // Now we need the check one more time, because most strings don't end with a carriage return
        if (currentWidth > maxWidth) {
            maxWidth = currentWidth
        }


        // Add a last bit of the height offset
        currentHeight += fontSize
        // Remove the last bit of spacing
        maxWidth -= spacing
        maxWidth += shadowOffsetX * fontSize
        currentHeight += shadowOffsetY * fontSize
        return Vector2f(maxWidth, currentHeight)
    }

    fun getTextLengthWithShadows(input: String?): Int {
        return getTextLength(input) * 2
    }

    fun getTextLength(input: String?): Int {
        return input!!.replace(" ", "").replace("\n", "").length
    }

    // This now gives you back a mesh UUID in the storage container
    fun grabText(fontSize: Float, text: String?): String? {
        return grabText(0f, 0f, fontSize, text, true)
    }

    private fun grabText(posX: Float, posY: Float, fontSize: Float, text: String?, returnMesh: Boolean): String? {

        // Can't render if no font is selected
        if (currentFont == null) {
            throw RuntimeException("Font: Tried to render without selecting a font! You must select a font before rendering to canvas!")
        }

        // Store how far the arm has moved to the right
        var typeWriterArmX = 0.0f
        // Store how far the arm has moved down
        var typeWriterArmY = 0.0f

        // Cache spacing
        val spacing = currentFont!!.spacing * fontSize

        // Cache space (' ') character
        val spaceCharacterSize = currentFont!!.spaceCharacterSize * fontSize
        for (character in text!!.toCharArray()) {

            // Skip space
            if (character == ' ') {
                typeWriterArmX += spaceCharacterSize
                continue
            }
            // Move down 1 space Y and to space 0 X
            if (character == '\n') {
                typeWriterArmY += fontSize
                typeWriterArmX = 0.0f
                continue
            }
            val stringCharacter = character.toString()

            // Skip unknown character
            if (!currentFont!!.map.containsKey(stringCharacter)) {
                continue
            }

            // Font stores character width in index 9 (8 [0 count])
            val textureData = Arrays.copyOfRange(currentFont!!.map[stringCharacter], 0, 9)

            //Now dispatch into the cache
            System.arraycopy(textureData, 0, textureCoordinateCache, textureCoordinateCount, 8)

            // This is the width of the character
            // Keep on the stack
            val characterWidth = textureData[8]

            // Keep this on the stack
            val rawVertex = Arrays.copyOf(RAW_VERTEX, RAW_VERTEX.size)


            // ( 0 x 1 y 2 x 3 y ) <- left side ( 4 x 5 y 6 x 7 y ) <- right side is goal
            // Now apply trimming
            run {
                var i = 4
                while (i < 8) {
                    rawVertex[i] = characterWidth
                    i += 2
                }
            }

            // Now scale
            for (i in rawVertex.indices) {
                rawVertex[i] *= fontSize
            }

            // Shifting
            run {
                var i = 0
                while (i < 8) {

                    // Now shift right
                    rawVertex[i] += typeWriterArmX + posX
                    // Now shift down
                    rawVertex[i + 1] += typeWriterArmY + posY
                    i += 2
                }
            }
            typeWriterArmX += characterWidth * fontSize + spacing

            // vertexData ~= rawVertex;
            // Now dispatch into the cache
            System.arraycopy(rawVertex, 0, vertexCache, vertexCount, 8)
            val rawIndices = Arrays.copyOf(RAW_INDICES, RAW_INDICES.size)
            for (i in rawIndices.indices) {
                rawIndices[i] += vertexCount / 2
            }

            // Now dispatch into the cache
            System.arraycopy(rawIndices, 0, indicesCache, indicesCount, 6)

            // Now hold cursor position (count) in arrays
            vertexCount += 8
            textureCoordinateCount += 8
            indicesCount += 6
            colorCount += 16
            // This one is characters literal
            currentCharacterIndex++
            if (vertexCount >= maxChars || indicesCount >= maxChars) {
                throw RuntimeException("Font: Exceeded character limit! Character limit is: " + maxChars)
            }
        }

        /*
         * Because there is no Z buffer in 2d, OpenGL seems to NOT overwrite pixel data of existing
         * framebuffer pixels. Since this is my testbed, I must assume that this is how
         * Vulkan, Metal, DX, and so-on do this. This is GUARANTEED to not affect software renderers.
         * So we have to do the shadowing AFTER the foreground.
         * We need to poll, THEN disable the shadow variable because without that it would be
         * an infinite recursion, aka a stack overflow.
         */if (returnMesh) {
            val textLength = getTextLength(text)
            val currentIndex = currentCharacterIndex
            setColorRange(
                currentIndex,
                currentIndex + textLength,
                shadowColor[0],
                shadowColor[1],
                shadowColor[2],
                shadowColor[3]
            )
            grabText(shadowOffsetX * fontSize, shadowOffsetY * fontSize, fontSize, text, false)
            switchShadowColor(0f, 0f, 0f)

            // Now render it if told to do so
            return generateMesh()
        }
        return null
    }

    // ^ v Keep these two next to each other, easier to understand
    /// Flushes out the cache, gives you back a font struct containing the MeshStorage UUID string
    private fun generateMesh(): String {
        val uuid = UUID.randomUUID().toString()
        newMesh(
            uuid,
            Arrays.copyOfRange(vertexCache, 0, vertexCount),
            Arrays.copyOfRange(textureCoordinateCache, 0, textureCoordinateCount),
            Arrays.copyOfRange(indicesCache, 0, indicesCount),
            null,
            Arrays.copyOfRange(colorCache, 0, colorCount),
            currentFont!!.fileLocation,
            true
        )
        //        tempObject.render();
//        tempObject.destroy();
        vertexCount = 0
        textureCoordinateCount = 0
        indicesCount = 0
        colorCount = 0
        currentCharacterIndex = 0
        return uuid
    }

    private fun encodeGraphics(fontObject: FontData, trimming: Boolean, spacing: Float, spaceCharacterSize: Float) {

        // Store all this on the stack

        // Total image size
        val palletWidth = fontObject.palletWidth.toFloat()
        val palletHeight = fontObject.palletHeight.toFloat()

        // How many characters (width, then height)
        val rows = fontObject.rows

        // How wide and tall are the characters in pixels
        val characterWidth = fontObject.characterWidth
        val characterHeight = fontObject.characterHeight

        // The border between the characters in pixels
        val border = fontObject.border

        // Store font spacing here as it's a one shot operation
        fontObject.spacing = spacing / characterWidth

        // Store space character width as it's a one shot operation
        fontObject.spaceCharacterSize = spaceCharacterSize / characterWidth

        // Cache a raw true color image for trimming if requested
        val tempImageObject = if (!trimming) null else RawTextureObject(fontObject.fileLocation!!)
        var index = -1
        for (value in fontObject.rawMap!!.toCharArray()) {
            index++

            // Starts off as a normal monospace size
            var thisCharacterWidth = characterWidth


            // Now get where the typewriter is
            val currentRow = index % rows
            val currentColum = index / rows

            // Now get literal pixel position (top left)
            val intPosX = (characterWidth + border) * currentRow
            val intPosY = (characterHeight + border) * currentColum

            // left  top,
            // left  bottom,
            // right bottom,
            // right top

            // Now calculate limiters
            // +1 on max because the GL texture stops on the top left of the point in the texture pixel
            var minX = intPosX
            var maxX = intPosX + characterWidth + 1
            val maxY = intPosY + characterHeight + 1

            // Now trim it if requested
            if (trimming) {

                // Create temp workers
                var newMinX = minX
                var newMaxX = maxX
                var found = false
                // Trim left side
                for (x in minX until maxX) {
                    newMinX = x
                    for (y in intPosY until maxY) {

//                        Vector4i temp = tempImageObject.getPixel(x, y);
                        // This is ubyte (0-255)
                        if (tempImageObject!!.getPixel(x, y).w > 0) {
                            found = true
                            break
                        }
                    }
                    if (found) {
                        break
                    }
                }
                found = false

                // Trim right side
                for (x in maxX - 1 downTo minX) {
                    // +1 because of the reason stated above assigning minX and maxX
                    newMaxX = x + 1
                    for (y in intPosY until maxY) {
                        // This is ubyte (0-255)
                        if (tempImageObject!!.getPixel(x, y).w > 0) {
                            found = true
                            break
                        }
                    }
                    if (found) {
                        break
                    }
                }

                // I was going to throw a blank space check, but maybe someone has a reason for that
                minX = newMinX
                maxX = newMaxX
                thisCharacterWidth = maxX - minX
            }

            // Now shovel it into a raw array, so we can easily use it - iPos stands for Integral Positions
            // -1 on maxY because the position was overshot, now we reverse it
            val iPos = intArrayOf(
                minX, intPosY,  // Top left
                minX, maxY - 1,  // Bottom left
                maxX, maxY - 1,  // Bottom right
                maxX, intPosY,  // Top right
                thisCharacterWidth // Width
            )

            // Now calculate REAL graphical texture map
            val glPositions = floatArrayOf(
                iPos[0] / palletWidth,
                iPos[1] / palletHeight,
                iPos[2] / palletWidth,
                iPos[3] / palletHeight,
                iPos[4] / palletWidth,
                iPos[5] / palletHeight,
                iPos[6] / palletWidth,
                iPos[7] / palletHeight,  // Now store char width - Find the new float size by comparing it to original
                // Will simply be 1.0 with monospaced fonts
                iPos[8].toFloat() / characterWidth.toFloat()
            )


//            System.out.println(Arrays.toString(glPositions));
//            System.out.println("Letter: " + value + " int pos: " + intPosX + " " + intPosY);

            // Now dump it into the dictionary
            fontObject.map[value.toString()] = glPositions
        }

        // Now clean up that buffer object wrapper if it exists
        tempImageObject?.destroy()
    }

    private fun parseJson(fontObject: FontData, jsonLocation: String) {
        val mapper = ObjectMapper()
        val nodes: JsonNode
        nodes = try {
            mapper.readTree(getFileString(jsonLocation))
        } catch (e: Exception) {
            throw RuntimeException("Font: ERROR loading! $e")
        }

        // Crawl up the JSON tree
        val keys = nodes.fieldNames()
        val it = nodes.elements()
        while (it.hasNext()) {
            val key = keys.next()
            val value = it.next()
            val type = value.nodeType
            when (key) {
                "pallet_width" -> {
                    assert(type == JsonNodeType.NUMBER)
                    fontObject.palletWidth = value.asInt()
                }

                "pallet_height" -> {
                    assert(type == JsonNodeType.NUMBER)
                    fontObject.palletHeight = value.asInt()
                }

                "border" -> {
                    assert(type == JsonNodeType.NUMBER)
                    fontObject.border = value.asInt()
                }

                "rows" -> {
                    assert(type == JsonNodeType.NUMBER)
                    fontObject.rows = value.asInt()
                }

                "character_width" -> {
                    assert(type == JsonNodeType.NUMBER)
                    fontObject.characterWidth = value.asInt()
                }

                "character_height" -> {
                    assert(type == JsonNodeType.NUMBER)
                    fontObject.characterHeight = value.asInt()
                }

                "character_map" -> {
                    assert(type == JsonNodeType.STRING)
                    fontObject.rawMap = value.asText()
                }

                else -> {}
            }
        }
    }

    private fun checkFilesExist(pngLocation: String, jsonLocation: String) {
        if (!File(pngLocation).exists()) {
            throw RuntimeException("Font: Texture ($pngLocation) does not exist!")
        }
        if (!File(jsonLocation).exists()) {
            throw RuntimeException("Font: Data ($jsonLocation) does not exist!")
        }
    }
}
