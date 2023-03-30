package org.crafter.engine.gui.razorfont;

import java.util.HashMap;

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

    /**
     * This allows batch rendering to a "canvas" ala vertex positionining
     * With this you can shovel one giant lump of data into a vao or whatever you're using.
     * This is optional though, you can do whatever you want!
     */
    private static double canvasWidth  = -1;
    private static double canvasHeight = -1;

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
    private static float[] shadowColor = new float[]{0,0,0,1};

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
    private static RazorFont currentFont = null;

    // This stores the current font name as a string
    private static String currentFontName;

    // This is the lock described in the comment above;
    private static boolean fontLock = false;

    // Stores all fonts
    private static final HashMap<String, RazorFont> razorFonts = new HashMap<>();

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
    }




    private Font(){}


    

}
