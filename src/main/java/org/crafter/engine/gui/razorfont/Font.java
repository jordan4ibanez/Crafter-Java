package org.crafter.engine.gui.razorfont;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.crafter.engine.utility.RawTextureObject;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

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

    /*
     * This allows batch rendering to a "canvas" ala vertex positioning
     * With this you can shovel one giant lump of data into a vao or whatever you're using.
     * This is optional though, you can do whatever you want!
     */
//    private static float canvasWidth  = -1;
//    private static float canvasHeight = -1;

    /**
     * These store constant data that is highly repetitive
     */
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

    /**
     * The offset of the text shadowing.
     * Note: Since offset is only proportional to the font size when rendering,
     * the offset is completely detached from the font spec!
     * The font spec has no bearing on how the offset is calculated. Only font size.
     * 0.05 by default because I think it looks nice. :)
     */
    private static float shadowOffsetX = 0.05f;
    private static float shadowOffsetY = 0.05f;

    /**
     * The RGBA components of the shadow
     */
    private static final float[] shadowColor = new float[]{0,0,0,1};

    /**
     Are shadows enabled?
     They get disabled everytime you run renderToCanvas().
     This is so there basically isn't a "shadow memory leak".
     As in: Oops I forgot to disable shadows now everything after has a
     shadow for some reason!
     */
    private static boolean shadowsEnabled = false;

    /**
     Allows turning off the shadowing color fill for performance.
     Say you want a rainbow shadow, you can use this for that.
     */
    private static boolean shadowColoringEnabled = true;

    /**
     This is a very simple fix for static memory arrays being filled with no.
     A simple on switch for initialization.
     To use RazorFont, you must create a font, so it runs this in there.
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

    /**
     Caches the current font in use.
     Think of this like the golfball on an IBM Selectric.
     You can use one ball, type out in one font. Then flush to your render target.
     Then you can swap to another ball and type in another font.
     Just remember, you must flush or this is going to throw an error because
     it would create garbage text data without a lock when swapping golfballs, aka fonts.
     */
    private static FontData currentFont = null;

    // This stores the current font name as a string
    private static String currentFontName;

    // This is the lock described in the comment above;
    private static boolean fontLock = false;

    // Stores all fonts
    private static final HashMap<String, FontData> fonts = new HashMap<>();

    // Allows an automatic upload into whatever render target (OpenGL, Vulkan, Metal, DX) as a string file location
    private static FontLoadingCalls.StringCall stringUpload;

    // Allows DIRECT automatic upload into whatever render target (OpenGL, Vulkan, Metal, DX) as RAW data
    private static FontLoadingCalls.RawCall rawUpload;

    // Allows an automate render into whatever render target (OpenGL, Vulkan, Metal, DX) simply by calling render()
    private static RenderCall renderCall;

    /**
     * Allows automatic render target (OpenGL, Vulkan, Metal, DX) passthrough instantiation.
     * This can basically pass a file location off to your rendering engine and autoload it into memory.
     */
    public static void setFontStringCall(FontLoadingCalls.StringCall stringCall) {
        if (stringUpload != null) {
            throw new RuntimeException("Font: Tried to set the string api integration function more than once!");
        }
        stringUpload = stringCall;
    }

    /**
     * Allows automatic render target (OpenGL, Vulkan, Metal, DX) DIRECT instantiation.
     * This allows the render engine to AUTOMATICALLY upload the image as RAW data.
     * byte[] = raw data. int = width. int = height.
     */
    public static void setFontRawCall(FontLoadingCalls.RawCall rawCall) {
        if (rawUpload != null) {
            throw new RuntimeException("Font: Tried to set the raw api integration function more than once!");
        }
        rawUpload = rawCall;
    }

    /**
     * Allows automatic render target (OpenGL, Vulkan, Metal, DX) DIRECT rendering via RazorFont.
     * You can simply call render() on the library, and it will automatically do whatever you
     * tell it to with this delegate function. This will also automatically run flush().
     */
    public static void setRenderCall(RenderCall newRenderCall) {
        if (renderCall != null) {
            throw new RuntimeException("Font: Tried to set the render api call more than once!");
        }
        renderCall = newRenderCall;
    }

    /**
     * Create a font from your PNG JSON pairing in the directory.
     * You do not specify an extension.
     * So if you have: cool.png and cool.json
     * You would call this as: createFont("fonts/cool")
     * Name is an optional. You will call into Razor Font by this name.
     * If you do not specify a name, you must call into Razor Font by the fileLocation, literal.
     * If you turn on trimming, your font will go from monospace to proportional.
     * Spacing is how far the letters are from each other. Default: 1.0 pixel
     * spaceCharacterSize is how big the ' ' (space) character is. By default, it's 4 pixels wide.
     */
    public static void createFont(String fileLocation, String name, boolean trimming) {
        createFont(fileLocation, name, true, 1.0f, 4.0f);
    }
    public static void createFont(String fileLocation, String name, boolean trimming, float spacing) {
        createFont(fileLocation, name, true, spacing, 4.0f);
    }
    public static void createFont(String fileLocation, String name, boolean trimming, float spacing, float spaceCharacterSize) {

        // This is the fix explained above
        initColorArray();

        // Are we using the fileLocation as the key, or did they specify a name?
//        final String key = Objects.equals(name, "") ? fileLocation : name;

        final String pngLocation = fileLocation + ".png";
        final String jsonLocation = fileLocation + ".json";

        // Make sure the files exist
        checkFilesExist(pngLocation, jsonLocation);

        // Automate existing engine integration
        tryCallingRAWApi(pngLocation);
        tryCallingStringApi(pngLocation);

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

    // ============================ BEGIN GRAPHICS DISPATCH ===========================

    /**
     Allows you to blanket set the color for the entire canvas.
     Be careful though, this overwrites the entire color cache
     after the currently rendered character position in memory!
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
     Allows you to set the offet of the text shadowing.
     This is RELATIVE via the font size so it will remain consistent
     across any font size!
     Remember: Offset will become reset to default when you call renderToCanvas()
     */
    public static void setShadowOffset(float x, float y) {
        shadowOffsetX = x / 10.0f;
        shadowOffsetY = y / 10.0f;
    }

    /**
     Allows you to blanket set the shadow color for the entire canvas after the current character.
     Remember: When you renderToCanvas() shadow colors will default back to black.
     */
    public static void switchShadowColor(float r, float g, float b) {
        switchShadowColor(r,g,b,1);
    }
    public static void switchShadowColor(float r, float g, float b, float a) {
        shadowColor[0] = r;
        shadowColor[1] = g;
        shadowColor[2] = b;
        shadowColor[3] = a;
    }

    /**
     Allows you to blanket a range of characters in the canvas with a color.
     So if you have: abcdefg
     And run setColorRange(0.5,0.5,0.5, 1, 3, 5)
     Now e and f are gray. Alpha 1.0
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

    /**
     Allows you to set individual character colors
     */
    public static void setColorChar(int charIndex, float r, float g, float b) {
        setColorChar(charIndex, r,g,b,1);
    }
    public static void setColorChar(int charIndex, float r, float g, float b, float a) {
        final int startIndex = charIndex * 16;
        for (int i = startIndex; i < startIndex + 16; i += 4) {
            colorCache[i]     = r;
            colorCache[i + 1] = g;
            colorCache[i + 2] = b;
            colorCache[i + 3] = a;
        }
    }

    /**
     Allows you to directly work on vertex position colors in a character.
     Using direct points (verbose)
     */
    public static void setColorPoints(
            int charIndex,

            float topLeftR,
            float topLeftG,
            float topLeftB,
            float topLeftA,

            float bottomLeftR,
            float bottomLeftG,
            float bottomLeftB,
            float bottomLeftA,

            float bottomRightR,
            float bottomRightG,
            float bottomRightB,
            float bottomRightA,

            float topRightR,
            float topRightG,
            float topRightB,
            float topRightA
    ) {
        final int startIndex = charIndex * 16;

        // It's already immensely verbose, let's just add on to this verbosity

        colorCache[startIndex]      = topLeftR;
        colorCache[startIndex + 1]  = topLeftG;
        colorCache[startIndex + 2]  = topLeftB;
        colorCache[startIndex + 3]  = topLeftA;

        colorCache[startIndex + 4]  = bottomLeftR;
        colorCache[startIndex + 5]  = bottomLeftG;
        colorCache[startIndex + 6]  = bottomLeftB;
        colorCache[startIndex + 7]  = bottomLeftA;

        colorCache[startIndex + 8]  = bottomRightR;
        colorCache[startIndex + 9]  = bottomRightG;
        colorCache[startIndex + 10] = bottomRightB;
        colorCache[startIndex + 11] = bottomRightA;

        colorCache[startIndex + 12] = topRightR;
        colorCache[startIndex + 13] = topRightG;
        colorCache[startIndex + 14] = topRightB;
        colorCache[startIndex + 15] = topRightA;
    }

    /**
     Allows you to directly work on vertex position colors in a character.
     Using direct points (tidy).
     float vec is [R,G,B,A]
     */
    public static void setColorPoints(int charIndex, float[] topLeft, float[] bottomLeft, float[] bottomRight, float[] topRight) {
        final int startIndex = charIndex * 16;
        int externalIndex = 0;
        for(float[] vec4 : new float[][]{topLeft, bottomLeft, bottomRight, topRight}) {
            int index = 0;
            for (float value : vec4) {
                colorCache[startIndex + (externalIndex * 4) + index] = value;
                index++;
            }
            externalIndex++;

        }
    }
    // Allows you to get the max amount of characters allowed in canvas
    public static int getMaxChars() {
        return CHARACTER_LIMIT;
    }


    /**
     Allows you to index the current amount of characters on the canvas. This does
     not include spaces and carriage returns. You MUST call renderToCanvas before
     calling this otherwise this will always be 0 when you call it.
     */
    public static int getCurrentCharacterIndex() {
        return chars;
    }

    /**
     Allows you to extract the current font PNG file location automatically
     */
    public static String getCurrentFontTextureFileLocation() {
        if (currentFont == null) {
            throw new RuntimeException("Font: Can't get a font file location! You didn't select one!");
        }
        return currentFont.fileLocation;
    }

    /**
     Turns on shadowing.
     Remember: This creates twice as many characters because
     you have to render a background, then a foreground.
     You can also do some crazy stuff with shadows because the shadow
     colors are stored in the same color cache as regular text.
     Remember: When you renderToCanvas() shadows turn off.
     */
    public static void enableShadows() {
        shadowsEnabled = true;
    }

    // Allows you to render to a canvas using top left as a base position
    // This is now modified to be less UNIXy, calling straight into the Window class
//    public static void updateCanvasSize(/*float width, float height*/) {
        // Dividing by 2.0 because my test environment shader renders to center on pos(0,0) top left
//        Vector2i windowSize =  Window.getWindowSize();

//        canvasWidth  = (float)windowSize.x / 2.0f;
//        canvasHeight = (float)windowSize.y / 2.0f;
//    }

    /**
     Automatically flushes out the cache, handing the data structure off to
     the delegate function you defined via setRenderFunc()
     */
    public static void render() {
        if (renderCall == null) {
            throw new RuntimeException("Font: You did not set a render api call!");
        }
        renderCall.draw(flush());
    }

    /// Flushes out the cache, gives you back a font struct containing the raw data
    public static RawData flush() {

        fontLock = false;

        RawData returningStruct = new RawData(
                Arrays.copyOfRange(vertexCache, 0, vertexCount),
                Arrays.copyOfRange(textureCoordinateCache, 0, textureCoordinateCount),
                Arrays.copyOfRange(indicesCache, 0, indicesCount),
                Arrays.copyOfRange(colorCache, 0, colorCount)
        );

        // Reset the counters
        vertexCount = 0;
        textureCoordinateCount = 0;
        indicesCount = 0;
        colorCount = 0;
        chars = 0;

        return returningStruct;
    }

    /// Allows you to get text size to do interesting things. Returns as RazorTextSize struct
    public static Vector2f getTextSize(float fontSize, String text) {
        float accumulatorX = 0.0f;
        float accumulatorY = 0.0f;
        // Cache spacing
        final float spacing = currentFont.spacing * fontSize;
        // Cache space (' ') character
        final float spaceCharacterSize = currentFont.spaceCharacterSize * fontSize;

        // Can't get the size if there's no font!
        if (currentFont == null) {
            throw new RuntimeException("Razor Font: Tried to get text size without selecting a font! " +
                    "You must select a font before getting the size of text with it!");
        }

        for (char character : text.toCharArray()) {

            String currentStringChar = String.valueOf(character);

            // Skip space
            if (character == ' ') {
                accumulatorX += spaceCharacterSize;
                continue;
            }
            // Move down 1 space Y
            if (character == '\n') {
                accumulatorY += fontSize;
                continue;
            }

            // Skip unknown character
            if (!currentFont.map.containsKey(currentStringChar)) {
                continue;
            }

            // Font stores character width in index 9 (8 [0 count])
            accumulatorX += (currentFont.map.get(currentStringChar)[8] * fontSize) + spacing;
        }

        // Add a last bit of the height offset
        accumulatorY += fontSize;
        // Remove the last bit of spacing
        accumulatorX -= spacing;

        // Finally, if shadowing is enabled, add in shadowing offset
        if (shadowsEnabled) {
            accumulatorX += (shadowOffsetX * fontSize);
            accumulatorY += (shadowOffsetY * fontSize);
        }

        return new Vector2f(accumulatorX, accumulatorY);
    }

    /**
     Selects and caches the font of your choosing.
     Remember: You must flush the cache before choosing a new font.
     This is done because all fonts are different. It would create garbage
     data on screen without this.
     */
    public static void selectFont(String font) {

        if (fontLock) {
            throw new RuntimeException("Font: You must flush() out the cache before selecting a new font!");
        }

        // Can't render if that font doesn't exist
        if (!fonts.containsKey(font)) {
            throw new RuntimeException("Font: Error! " + font + " is not a registered font!");
        }

        // Now store and lock
        currentFont = fonts.get(font);
        currentFontName = font;
        fontLock = true;
    }

    /**
     * Get the name of the currently used font
     */
    public static String getCurrentFontName() {
        return currentFontName;
    }

    /**
     Render to the canvas. Remember: You must run flush() to collect this canvas.
     If rounding is enabled, it will attempt to keep your text aligned with the pixels on screen
     to avoid wavy/blurry/jagged text. This will automatically render shadows for you as well.
     */
    public static void renderToCanvas(float posX, float posY, final float fontSize, String text) {
        renderToCanvas(posX, posY, fontSize, text,true);
    }
    public static void renderToCanvas(float posX, float posY, final float fontSize, String text, boolean rounding) {

        // Keep square pixels
        if (rounding) {
            posX = Math.round(posX);
            posY = Math.round(posY);
        }

        // Can't render if no font is selected
        if (currentFont == null) {
            throw new RuntimeException("Font: Tried to render without selecting a font! " +
                    "You must select a font before rendering to canvas!");
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

//            System.out.println(stringCharacter);
//            if (stringCharacter.equals("h")) {
//                System.out.println(Arrays.toString(currentFont.map.get(stringCharacter)));
//            }

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
            final int textLength = getTextRenderableCharsLength(text);
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
            renderToCanvas(posX + (shadowOffsetX * fontSize), posY + (shadowOffsetY * fontSize), fontSize, text, false);

            shadowOffsetX = 0.05f;
            shadowOffsetY = 0.05f;
        }

        // Turn this back on because it can become a confusing nightmare
        shadowColoringEnabled = true;
        // Switch back to black because this also can become a confusing nightmare
        switchShadowColor(0,0,0);
    }

    /**
     Processes your input string, then sends you how long it would be when rendering.
     Helpful for repositioning your "cursor" in the texture cache!
     Note: This will return cursor position into the beginning index of the background
     of the shadowed text if you're using it for subtraction.
     */
    public static int getTextRenderableCharsLength(String input) {
        return input.replace(" ", "").replace("\n", "").length();
    }

    /**
     Processes your input text string with shadows to see how long it would be when rendering.
     Helpful for positioning your "cursor" in the texture cache!
     Note: This will return cursor position into the beginning index of the foreground
     of the shadowed text if you're using it for subtraction.
     */
    public static int getTextRenderableCharsLengthWithShadows(String input) {
        return getTextRenderableCharsLength(input) * 2;
    }

    /**
     Allows you to disable shadow coloring for a teeny tiny bit of performance
     when you're doing cool custom shadow coloring!
     Important Note: When renderToCanvas() is called, shadow coloring is turned
     back on because it can become a confusing nightmare if not done like this.
     */
    public static void disableShadowColoring() {
        shadowColoringEnabled = false;
    }


    /**
     Allows you to manually move around characters.
     Note: You can manually move around shadows by getting the
     renderable text size before turning on shadows, then offset
     your current index into the string by this size.
     Note: This is in pixel coordinates.
     */
    public static void moveChar(int index, float posX, float posY) {
        // This gets a bit confusing, so I'm going to write it out verbosely to be able to read/maintain it

        // Move to cursor position in vertexCache
        final int baseIndex = index * 8;

        // Top left
        vertexCache[baseIndex    ] += posX; // X
        vertexCache[baseIndex + 1] -= posY; // Y

        // Bottom left
        vertexCache[baseIndex + 2] += posX; // X
        vertexCache[baseIndex + 3] -= posY; // Y

        // Bottom right
        vertexCache[baseIndex + 4] += posX; // X
        vertexCache[baseIndex + 5] -= posY; // Y

        // Top right
        vertexCache[baseIndex + 6] += posX; // X
        vertexCache[baseIndex + 7] -= posY; // Y
    }
    /**
     Rotate a character around the center point of its face.
     Note: This defaults to radians by default.
     Note: If you use moveChar() with this, you MUST do moveChar() first!
     */
    public static void rotateChar(int index, float rotation) {
        rotateChar(index,rotation,false);
    }
    public static void rotateChar(int index, float rotation, boolean isDegrees) {

        // Degrees are annoying
        if (isDegrees) {
            final float radToDegrees = 180.0f / (float)Math.PI;
            rotation *= radToDegrees;
        }

        /*
         This is written out even more verbosely than moveChar()
         so you can see why you must do moveChar() first.
         */

        // Move to cursor position in vertexCache
        final int baseIndex = index * 8;

        // Convert to 3d to supplement to 4x4 matrix
        Vector3f topLeft     = new Vector3f(vertexCache[baseIndex    ], vertexCache[baseIndex + 1], 0);
        Vector3f bottomLeft  = new Vector3f(vertexCache[baseIndex + 2], vertexCache[baseIndex + 3], 0);
        Vector3f bottomRight = new Vector3f(vertexCache[baseIndex + 4], vertexCache[baseIndex + 5], 0);
        Vector3f topRight    = new Vector3f(vertexCache[baseIndex + 6], vertexCache[baseIndex + 7], 0);

        Vector3f centerPoint = new Vector3f((topLeft.x + topRight.x) / 2.0f,  (topLeft.y + bottomLeft.y) / 2.0f, 0);

        Vector3f topLeftDiff      = new Vector3f(topLeft)    .sub(centerPoint);
        Vector3f bottomLeftDiff   = new Vector3f(bottomLeft) .sub(centerPoint);
        Vector3f bottomRightDiff = new Vector3f(bottomRight).sub(centerPoint);
        Vector3f topRightDiff    = new Vector3f(topRight)   .sub(centerPoint);

        // These calculations also store the new data in the variables we created above
        // We must center the coordinates into real coordinates

        new Matrix4f().rotate(rotation, 0,0,1).translate(topLeftDiff)     .getTranslation(topLeft);
        new Matrix4f().rotate(rotation, 0,0,1).translate(bottomLeftDiff)  .getTranslation(bottomLeft);
        new Matrix4f().rotate(rotation, 0,0,1).translate(bottomRightDiff) .getTranslation(bottomRight);
        new Matrix4f().rotate(rotation, 0,0,1).translate(topRightDiff)    .getTranslation(topRight);


        topLeft.x += centerPoint.x;
        topLeft.y += centerPoint.y;

        bottomLeft.x += centerPoint.x;
        bottomLeft.y += centerPoint.y;

        bottomRight.x += centerPoint.x;
        bottomRight.y += centerPoint.y;

        topRight.x += centerPoint.x;
        topRight.y += centerPoint.y;

        vertexCache[baseIndex    ] = topLeft.x;
        vertexCache[baseIndex + 1] = topLeft.y;

        vertexCache[baseIndex + 2] = bottomLeft.x;
        vertexCache[baseIndex + 3] = bottomLeft.y;

        vertexCache[baseIndex + 4] = bottomRight.x;
        vertexCache[baseIndex + 5] = bottomRight.y;

        vertexCache[baseIndex + 6] = topRight.x;
        vertexCache[baseIndex + 7] = topRight.y;
    }


    // ============================ END GRAPHICS DISPATCH =============================

    // ========================= BEGIN GRAPHICS ENCODING ==============================

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

        if (tempImageObject != null) {
            tempImageObject.debugSpam();
        }

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

                // Trim left side
                outer1: for (int x = minX; x < maxX; x++){
                    newMinX = x;
                    for (int y = intPosY; y < maxY; y++) {
                        // This is ubyte (0-255)
                        if (tempImageObject.getPixel(x,y).w > 0) {
                            break outer1;
                        }
                    }
                }

                // Trim right side
                outer2: for (int x = maxX - 1; x >= minX; x--) {
                    // +1 because of the reason stated above assigning minX and maxX
                    newMaxX = x + 1;
                    for (int y = intPosY; y < maxY; y++) {
                        // This is ubyte (0-255)
                        if (tempImageObject.getPixel(x,y).w > 0) {
                            break outer2;
                        }
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


            if (String.valueOf(value).equals("h")) {
                System.out.println(Arrays.toString(glPositions));
                System.out.println("int pos: " + intPosX + " " + intPosY);
            }

            // Now dump it into the dictionary
            fontObject.map.put(String.valueOf(value), glPositions);
        }

        // Now clean up that buffer object wrapper if it exists
        if (tempImageObject != null) {
            tempImageObject.destroy();
        }
    }
    // ========================= END GRAPHICS ENCODING ================================



    // ========================== BEGIN JSON DECODING ==================================
    // Run through the required data to assemble a font object
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
                    fontObject.rawMap = value.toString();
                }
                default -> {
                } // Unknown
            }
        }
    }


    //============================ END JSON DECODING ==================================


    // ========================== BEGIN API AGNOSTIC CALLS ============================
    // Attempts to automate the api RAW call
    private static void tryCallingRAWApi(String fileLocation) {
        if (rawUpload == null) {
            return;
        }

        RawTextureObject tempImageObject = new RawTextureObject(fileLocation);

        final int width = tempImageObject.getWidth();
        final int height = tempImageObject.getHeight();

        rawUpload.fontLoadCallRaw(tempImageObject.getBuffer(), width, height);

        // Garbage collected :P
        tempImageObject.destroy();
    }

    // Attempts to automate the api String call
    private static void tryCallingStringApi(String fileLocation) {
        if (stringUpload == null) {
            return;
        }

        stringUpload.fontLoadCallString(fileLocation);
    }

    // ======================= END API AGNOSTIC CALLS ================================

    // ===================== BEGIN ETC FUNCTIONS ===============================


    // Makes sure there's data where there should be
    private static void checkFilesExist(String pngLocation, String jsonLocation) {
        if (!new File(pngLocation).exists()) {
            throw new RuntimeException("Font: Texture (" + pngLocation + ") does not exist!");
        }
        if (!new File(jsonLocation).exists()) {
            throw new RuntimeException("Font: Data (" + jsonLocation + ") does not exist!");
        }
    }

    // ===================== END ETC FUNCTIONS =====================================


    private Font(){}


    

}
