package org.crafter.engine.gui.font

/**
 * FontData is simply a data container class for fonts. Amazing.
 */
internal class FontData {
    // Font base pallet width (in pixels)
    var palletWidth = 0
    var palletHeight = 0

    // Pixel space (literally) between characters in pallet
    var border = 0

    // Number of characters (horizontal, aka X)
    var rows = 0

    // How far the letters are from each other
    var spacing = 1.0f

    // How big the space character is (' ')
    var spaceCharacterSize = 4.0f

    // Character pallet (individual) in pixels
    var characterWidth = 0
    var characterHeight = 0

    // Readonly specifier if trimming was enabled
    var trimmedX = false
    var trimmedY = false

    // Readonly directory for texture (entire, including the .png)
    var fileLocation: String? = null
    // Character map - stored as a linear associative array for O(1) retrieval
    /**
     * Stores as:
     * [
     * -x -y,
     * -x +y,
     * +x +y,
     * +x -y
     * ]
     * or this, if it's easier to understand:
     * [
     * top    left,
     * bottom left,
     * bottom right,
     * top    right
     * ]
     * GPU optimized vertex positions!
     * Accessed as:
     * double[] myCoolBlah = map["whatever letter/unicode thing you're getting"];
     * The last value specifies width of the character.
     */
    var map: HashMap<String, FloatArray> = HashMap()

    // Stores the map raw as a linear array before processed
    var rawMap: String? = null

    val asString: String
        // Debug util
        get() = "Pallet: $palletHeight $palletWidth | Rows$rows"
}
