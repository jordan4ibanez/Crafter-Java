package org.crafter.engine.gui.font;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.crafter.engine.mesh.Mesh;
import org.crafter.engine.mesh.MeshStorage;
import org.crafter.engine.texture.TextureStorage;
import org.crafter.engine.utility.RawTextureObject;
import org.joml.*;

import java.io.File;
import java.lang.Math;
import java.util.*;

import static org.crafter.engine.utility.FileReader.getFileString;

/**
 * This is my RazorFont library translated from D.
 * Various things have changed in this, simplification is a main goal.
 * We seriously do NOT want to create new objects every frame.
 * This NEEDS to be only one object, reused every frame.
 * See original here: <a href="https://github.com/jordan4ibanez/RazorFont/blob/main/source/razor_font.d">Fancy Link</a>
 * You can see this is built like a static D module.
 * No, I will not make this a singleton.
 */
public final class Font {

    // The current character limit (letters in string)
    private static final int CHARACTER_LIMIT = 4096;

    // 4 vec2 (so 8 per char) vertex positions
    private static final float[] vertexCache = new float[CHARACTER_LIMIT * 8];

    // 4 vec4 (so 16 per char) colors - defaults to 0,0,0,1 rgba
    private static final float[] colorCache = new float[CHARACTER_LIMIT * 16];

    /// 4 vec2 (so 8 per char) texture coordinate positions
    private static final float[] textureCoordinateCache = new float[CHARACTER_LIMIT * 8];

    // 2 tris (so 6 per char) indices
    private static final int[] indicesCache = new int[CHARACTER_LIMIT * 6];

    /// The count of each of these so we can grab a slice of data fresh out of the oven, delicious!
    private static int vertexCount            = 0;
    private static int textureCoordinateCount = 0;
    private static int indicesCount           = 0;
    private static int colorCount             = 0;
    private static int chars                  = 0;

    private static final float[] RAW_VERTEX  = new float[]{
            0,0,
            0,1,
            1,1,
            1,0
    };
    private static final int[] RAW_INDICES = new int[]{
            0,1,2,
            2,3,0
    };

    private static float shadowOffsetX = 0.05f;
    private static float shadowOffsetY = 0.05f;

    private static final float[] shadowColor = new float[]{0,0,0,1};

    private static boolean shadowsEnabled = false;

    private static boolean shadowColoringEnabled = true;

    private static FontData currentFont = null;

    private static boolean fontLock = false;

    private static final HashMap<String, FontData> fonts = new HashMap<>();


    /**
     * This is a very simple fix for static memory arrays being filled with no.
     * A simple on switch for initialization.
     * To use RazorFont, you must create a font, so it runs this in there.
     */
    private static boolean initializedColorArray = false;
    private static void initColorArray() {
        if (initializedColorArray) {
            return;
        }
        initializedColorArray = true;
        for (int i = 0; i < 16 * CHARACTER_LIMIT; i += 4) {
            colorCache[i]     = 0;
            colorCache[i + 1] = 0;
            colorCache[i + 2] = 0;
            colorCache[i + 3] = 1;
        }
    }


    private static void uploadFontTexture(String fileLocation) {
        TextureStorage.createTexture(fileLocation);
    }


    public static void createFont(String fileLocation, String name, boolean trimming) {
        createFont(fileLocation, name, trimming, 1.0f, 4.0f);
    }
    public static void createFont(String fileLocation, String name, boolean trimming, float spacing) {
        createFont(fileLocation, name, trimming, spacing, 4.0f);
    }
    public static void createFont(String fileLocation, String name, boolean trimming, float spacing, float spaceCharacterSize) {

        // This fills the color buffer's initial values to black
        initColorArray();

        final String pngLocation = fileLocation + ".png";
        final String jsonLocation = fileLocation + ".json";

        // Make sure the files exist
        checkFilesExist(pngLocation, jsonLocation);

        // Automate existing engine integration
        uploadFontTexture(pngLocation);

        // Create the Font object
        FontData fontObject = new FontData();

        // Store the file location in the object
        fontObject.fileLocation = pngLocation;

        // Now parse the json, and pass it into object
        parseJson(fontObject, jsonLocation);

        // Now encode the linear string as a keymap of raw graphics positions
        encodeGraphics(fontObject, trimming, spacing, spaceCharacterSize);

        // Finally add it into the library
        fonts.put(name, fontObject);
    }

    public static void selectFont(String font) {

        if (fontLock) {
            throw new RuntimeException("Font: You must render text to clear out the cache before selecting a new font!");
        }

        // Can't render if that font doesn't exist
        if (!fonts.containsKey(font)) {
            throw new RuntimeException("Font: Error! " + font + " is not a registered font!");
        }

        // Now store and lock
        currentFont = fonts.get(font);
        fontLock = true;
    }

    public static void disableShadowColoring() {
        shadowColoringEnabled = false;
    }
    public static void enableShadows() {
        shadowsEnabled = true;
    }

    public static String getCurrentFontTextureFileLocation() {
        if (currentFont == null) {
            throw new RuntimeException("Font: Can't get a font file location! You didn't select one!");
        }
        return currentFont.fileLocation;
    }

    public static int getCurrentCharacterIndex() {
        return chars;
    }

    public static int getMaxChars() {
        return CHARACTER_LIMIT;
    }

    public static void switchShadowColor(float r, float g, float b) {
        if (shadowsEnabled) {

        }
        switchShadowColor(r,g,b,1);
    }
    public static void switchShadowColor(float r, float g, float b, float a) {
        shadowColor[0] = r;
        shadowColor[1] = g;
        shadowColor[2] = b;
        shadowColor[3] = a;
    }

    /**
     * Allows you to blanket set the color for the entire canvas.
     * Be careful though, this overwrites the entire color cache
     * after the currently rendered character position in memory!
     */
    public static void switchColor(float r, float g, float b) {
        switchColor(r,g,b,1);
    }
    public static void switchColor(float r, float g, float b, float a) {
        for (int i = colorCount; i < colorCache.length; i += 4) {
            colorCache[i]     = r;
            colorCache[i + 1] = g;
            colorCache[i + 2] = b;
            colorCache[i + 3] = a;
        }
    }




    /**
     * Allows you to set the offet of the text shadowing.
     * This is RELATIVE via the font size, so it will remain consistent
     * across any font size!
     * Remember: Offset will become reset to default when you call renderToCanvas()
     */

    public static void setShadowOffset(float x, float y) {
        shadowOffsetX = x / 10.0f;
        shadowOffsetY = y / 10.0f;
    }


    /**
     * Allows you to blanket a range of characters in the canvas with a color.
     * So if you have: abcdefg
     * And run setColorRange(0.5,0.5,0.5, 1, 3, 5)
     * Now e and f are gray. Alpha 1.0
     */
    public static void setColorRange(int start, int end, float r, float g, float b) {
        setColorRange(start, end, r,g,b,1);
    }
    public static void setColorRange(int start, int end, float r, float g, float b, float a) {
        for (int i = start * 16; i < end * 16; i += 4) {
            colorCache[i]     = r;
            colorCache[i + 1] = g;
            colorCache[i + 2] = b;
            colorCache[i + 3] = a;
        }
    }


    public static Vector2f getTextCenter(float fontSize, String text) {
        Vector2f textSize = getTextSize(fontSize, text);
        textSize.x /= 2.0f;
        textSize.y /= 2.0f;
        return textSize;
    }

    public static Vector2f getTextSize(float fontSize, String text) {
        float maxWidth = 0;
        float currentWidth = 0;
        float currentHeight = 0.0f;

        // Can't get the size if there's no font!
        if (currentFont == null) {
            throw new RuntimeException("Razor Font: Tried to get text size without selecting a font! You must select a font before getting the size of text with it!");
        }

        // Cache spacing
        final float spacing = currentFont.spacing * fontSize;
        // Cache space (' ') character
        final float spaceCharacterSize = currentFont.spaceCharacterSize * fontSize;

        for (char character : text.toCharArray()) {

            String currentStringChar = String.valueOf(character);

            // Skip space
            if (character == ' ') {
                currentWidth += spaceCharacterSize;
                continue;
            }
            // Move down 1 space Y
            if (character == '\n') {
                if (currentWidth > maxWidth) {
                    maxWidth = currentWidth;
                }
                currentWidth = 0;
                currentHeight += fontSize;
                continue;
            }

            // Skip unknown character
            if (!currentFont.map.containsKey(currentStringChar)) {
                continue;
            }

            // Font stores character width in index 9 (8 [0 count])
            final float characterWidth = currentFont.map.get(currentStringChar)[8];
            currentWidth += (characterWidth * fontSize) + spacing;
        }

        // Now we need the check one more time, because most strings don't end with a carriage return
        if (currentWidth > maxWidth) {
            maxWidth = currentWidth;
        }


        // Add a last bit of the height offset
        currentHeight += fontSize;
        // Remove the last bit of spacing
        maxWidth -= spacing;

        // Finally, if shadowing is enabled, add in shadowing offset
        if (shadowsEnabled) {
            maxWidth += (shadowOffsetX * fontSize);
            currentHeight += (shadowOffsetY * fontSize);
        }

        return new Vector2f(maxWidth, currentHeight);
    }

    public static int getTextLengthWithShadows(String input) {
        return getTextLength(input) * 2;
    }
    public static int getTextLength(String input) {
        return input.replace(" ", "").replace("\n", "").length();
    }

    // This now gives you back a mesh UUID in the storage container
    public static String grabText(float posX, float posY, final float fontSize, String text) {
        return grabText(posX, posY, fontSize, text,true, true);
    }
    public static String grabText(float posX, float posY, final float fontSize, String text, boolean rounding) {
        return grabText(posX, posY, fontSize, text, rounding, true );
    }
    public static String grabText(float posX, float posY, final float fontSize, String text, boolean rounding, boolean instantRender) {

        // Keep square pixels
        if (rounding) {
            posX = Math.round(posX);
            posY = Math.round(posY);
        }

        // Can't render if no font is selected
        if (currentFont == null) {
            throw new RuntimeException("Font: Tried to render without selecting a font! You must select a font before rendering to canvas!");
        }

        // Store how far the arm has moved to the right
        float typeWriterArmX = 0.0f;
        // Store how far the arm has moved down
        float typeWriterArmY = 0.0f;

        // Top left of canvas is root position (X: 0, y: 0)
        final float positionX = posX;
        final float positionY = posY;

        // Cache spacing
        final float spacing = currentFont.spacing * fontSize;

        // Cache space (' ') character
        final float spaceCharacterSize = currentFont.spaceCharacterSize * fontSize;

        for (char character : text.toCharArray()) {

            // Skip space
            if (character == ' ') {
                typeWriterArmX += spaceCharacterSize;
                continue;
            }
            // Move down 1 space Y and to space 0 X
            if (character == '\n') {
                typeWriterArmY += fontSize;
                typeWriterArmX = 0.0f;
                continue;
            }

            String stringCharacter = String.valueOf(character);

            // Skip unknown character
            if (!currentFont.map.containsKey(stringCharacter)) {
                continue;
            }

            // Font stores character width in index 9 (8 [0 count])
            float[] textureData = Arrays.copyOfRange(currentFont.map.get(stringCharacter), 0, 9);

            //Now dispatch into the cache
            System.arraycopy(textureData, 0, textureCoordinateCache, textureCoordinateCount, 8);

            // This is the width of the character
            // Keep on the stack
            float characterWidth = textureData[8];

            // Keep this on the stack
            float[] rawVertex = Arrays.copyOf(RAW_VERTEX, RAW_VERTEX.length);


            // ( 0 x 1 y 2 x 3 y ) <- left side ( 4 x 5 y 6 x 7 y ) <- right side is goal
            // Now apply trimming
            for (int i = 4; i < 8; i += 2) {
                rawVertex[i] = characterWidth;
            }

            // Now scale
            for (int i = 0; i < rawVertex.length; i++) {
                rawVertex[i] *= fontSize;
            }

            // Shifting
            for (int i = 0; i < 8; i += 2) {
                // Now shift right
                rawVertex[i] += typeWriterArmX + positionX;
                // Now shift down
                rawVertex[i + 1] += typeWriterArmY + positionY;
            }

            typeWriterArmX += (characterWidth * fontSize) + spacing;

            // vertexData ~= rawVertex;
            // Now dispatch into the cache
            System.arraycopy(rawVertex, 0, vertexCache, vertexCount, 8);


            int[] rawIndices = Arrays.copyOf(RAW_INDICES, RAW_INDICES.length);

            for (int i = 0; i < rawIndices.length; i++) {
                rawIndices[i] += vertexCount / 2;
            }

            // Now dispatch into the cache
            System.arraycopy(rawIndices, 0, indicesCache, indicesCount, 6);

            // Now hold cursor position (count) in arrays
            vertexCount  += 8;
            textureCoordinateCount += 8;
            indicesCount += 6;
            colorCount += 16;
            // This one is characters literal
            chars++;

            if (vertexCount >= CHARACTER_LIMIT || indicesCount >= CHARACTER_LIMIT) {
                throw new RuntimeException("Font: Exceeded character limit! Character limit is: " + CHARACTER_LIMIT);
            }
        }

        /*
         * Because there is no Z buffer in 2d, OpenGL seems to NOT overwrite pixel data of existing
         * framebuffer pixels. Since this is my testbed, I must assume that this is how
         * Vulkan, Metal, DX, and so-on do this. This is GUARANTEED to not affect software renderers.
         * So we have to do the shadowing AFTER the foreground.
         * We need to poll, THEN disable the shadow variable because without that it would be
         * an infinite recursion, aka a stack overflow.
         */
        final boolean shadowsWereEnabled = shadowsEnabled;
        shadowsEnabled = false;
        if (shadowsWereEnabled) {
            final int textLength = getTextLength(text);
            final int currentIndex = getCurrentCharacterIndex();
            if (shadowColoringEnabled) {
                setColorRange(
                        currentIndex,
                        currentIndex + textLength,
                        shadowColor[0],
                        shadowColor[1],
                        shadowColor[2],
                        shadowColor[3]
                );
            }
            grabText(posX + (shadowOffsetX * fontSize), posY + (shadowOffsetY * fontSize), fontSize, text, false,false);

            shadowOffsetX = 0.05f;
            shadowOffsetY = 0.05f;
        }

        // Turn this back on because it can become a confusing nightmare
        shadowColoringEnabled = true;
        // Switch back to black because this also can become a confusing nightmare
        switchShadowColor(0,0,0);

        // Now render it if told to do so
        if (instantRender) {
            return generateMesh();
        }

        return null;
    }

    // ^ v Keep these two next to each other, easier to understand

    /// Flushes out the cache, gives you back a font struct containing the MeshStorage UUID string
    private static String generateMesh() {

        String uuid = UUID.randomUUID().toString();

        MeshStorage.newMesh(
                uuid,
                Arrays.copyOfRange(vertexCache, 0, vertexCount),
                Arrays.copyOfRange(textureCoordinateCache, 0, textureCoordinateCount),
                Arrays.copyOfRange(indicesCache, 0, indicesCount),
                null,
                Arrays.copyOfRange(colorCache, 0, colorCount),
                currentFont.fileLocation,
                true

        );
//        tempObject.render();
//        tempObject.destroy();

        vertexCount = 0;
        textureCoordinateCount = 0;
        indicesCount = 0;
        colorCount = 0;
        chars = 0;

        return uuid;
    }



    private static void encodeGraphics(FontData fontObject, boolean trimming, float spacing, float spaceCharacterSize) {

        // Store all this on the stack

        // Total image size
        final float palletWidth = fontObject.palletWidth;
        final float palletHeight = fontObject.palletHeight;

        // How many characters (width, then height)
        final int rows = fontObject.rows;

        // How wide and tall are the characters in pixels
        final int characterWidth = fontObject.characterWidth;
        final int characterHeight = fontObject.characterHeight;

        // The border between the characters in pixels
        final int border = fontObject.border;

        // Store font spacing here as it's a one shot operation
        fontObject.spacing = spacing / characterWidth;

        // Store space character width as it's a one shot operation
        fontObject.spaceCharacterSize = spaceCharacterSize / characterWidth;

        // Cache a raw true color image for trimming if requested
        final RawTextureObject tempImageObject = !trimming ? null : new RawTextureObject(fontObject.fileLocation);

        int index = -1;
        for (char value : fontObject.rawMap.toCharArray()) {

            index++;

            // Starts off as a normal monospace size
            int thisCharacterWidth = characterWidth;


            // Now get where the typewriter is
            final int currentRow = index % rows;
            final int currentColum = index / rows;

            // Now get literal pixel position (top left)
            int intPosX = (characterWidth + border) * currentRow;
            int intPosY = (characterHeight + border) * currentColum;

            // left  top,
            // left  bottom,
            // right bottom,
            // right top

            // Now calculate limiters
            // +1 on max because the GL texture stops on the top left of the point in the texture pixel
            int minX = intPosX;
            int maxX = intPosX + characterWidth + 1;

            final int maxY = intPosY + characterHeight + 1;

            // Now trim it if requested
            if (trimming) {

                // Create temp workers
                int newMinX = minX;
                int newMaxX = maxX;

                boolean found = false;
                // Trim left side
                for (int x = minX; x < maxX; x++) {
                    newMinX = x;
                    for (int y = intPosY; y < maxY; y++) {

                        Vector4i temp = tempImageObject.getPixel(x, y);
                        // This is ubyte (0-255)
                        if (tempImageObject.getPixel(x, y).w > 0) {
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        break;
                    }
                }

                found = false;

                // Trim right side
                for (int x = maxX - 1; x >= minX; x--) {
                    // +1 because of the reason stated above assigning minX and maxX
                    newMaxX = x + 1;
                    for (int y = intPosY; y < maxY; y++) {
                        // This is ubyte (0-255)
                        if (tempImageObject.getPixel(x, y).w > 0) {
                            found = true;
                        }
                    }
                    if (found) {
                        break;
                    }
                }

                // I was going to throw a blank space check, but maybe someone has a reason for that

                minX = newMinX;
                maxX = newMaxX;

                thisCharacterWidth = maxX - minX;

            }

            // Now shovel it into a raw array, so we can easily use it - iPos stands for Integral Positions
            // -1 on maxY because the position was overshot, now we reverse it
            int[] iPos = {
                    minX, intPosY,     // Top left
                    minX, maxY - 1, // Bottom left
                    maxX, maxY - 1, // Bottom right
                    maxX, intPosY,    // Top right

                    thisCharacterWidth // Width
            };

            // Now calculate REAL graphical texture map
            float[] glPositions  = {
                    iPos[0] / palletWidth, iPos[1] / palletHeight,
                    iPos[2] / palletWidth, iPos[3] / palletHeight,
                    iPos[4] / palletWidth, iPos[5] / palletHeight,
                    iPos[6] / palletWidth, iPos[7] / palletHeight,

                    // Now store char width - Find the new float size by comparing it to original
                    // Will simply be 1.0 with monospaced fonts
                    (float)iPos[8] / (float)characterWidth
            };


//            System.out.println(Arrays.toString(glPositions));
//            System.out.println("Letter: " + value + " int pos: " + intPosX + " " + intPosY);

            // Now dump it into the dictionary
            fontObject.map.put(String.valueOf(value), glPositions);
        }

        // Now clean up that buffer object wrapper if it exists
        if (tempImageObject != null) {
            tempImageObject.destroy();
        }
    }

    private static void parseJson(FontData fontObject, final String jsonLocation) {

        ObjectMapper mapper = new ObjectMapper();

        JsonNode nodes;

        try {
            nodes = mapper.readTree(getFileString(jsonLocation));
        } catch (Exception e) {
            throw new RuntimeException("Font: ERROR loading! " + e);
        }

        // Crawl up the JSON tree

        Iterator<String> keys = nodes.fieldNames();

        for (Iterator<JsonNode> it = nodes.elements(); it.hasNext(); ) {

            String key = keys.next();
            JsonNode value = it.next();

            JsonNodeType type = value.getNodeType();

            switch (key) {
                case "pallet_width" -> {
                    assert (type == JsonNodeType.NUMBER);
                    fontObject.palletWidth = value.asInt();
                }
                case "pallet_height" -> {
                    assert (type == JsonNodeType.NUMBER);
                    fontObject.palletHeight = value.asInt();
                }
                case "border" -> {
                    assert (type == JsonNodeType.NUMBER);
                    fontObject.border = value.asInt();
                }
                case "rows" -> {
                    assert (type == JsonNodeType.NUMBER);
                    fontObject.rows = value.asInt();
                }
                case "character_width" -> {
                    assert (type == JsonNodeType.NUMBER);
                    fontObject.characterWidth = value.asInt();
                }
                case "character_height" -> {
                    assert (type == JsonNodeType.NUMBER);
                    fontObject.characterHeight = value.asInt();
                }
                case "character_map" -> {
                    assert (type == JsonNodeType.STRING);
                    fontObject.rawMap = value.asText();
                }
                default -> {
                } // Unknown
            }
        }
    }

    private static void checkFilesExist(String pngLocation, String jsonLocation) {
        if (!new File(pngLocation).exists()) {
            throw new RuntimeException("Font: Texture (" + pngLocation + ") does not exist!");
        }
        if (!new File(jsonLocation).exists()) {
            throw new RuntimeException("Font: Data (" + jsonLocation + ") does not exist!");
        }
    }


    private Font(){}


    

}
