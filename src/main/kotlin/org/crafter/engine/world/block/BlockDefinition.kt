package org.crafter.engine.world.block

import org.crafter.engine.texture.WorldAtlas.instance
import java.io.Serializable

/**
 * A simple container object for block definitions.
 * Can utilize the builder pattern.
 */
class BlockDefinition(val internalName: String) : Serializable {
    // Required
    var id = -1
        private set

    private var textures: Array<String>? = null

    // Optional
    var readableName: String = ""
        private set
    var drawType = DrawType.BLOCK
        private set
    var walkable = true
        private set
    var liquid = false
        private set
    var liquidFlow = 0
        private set
    var liquidViscosity = 0
        private set
    var climbable = false
        private set
    var sneakJumpClimbable = false
        private set
    var falling = false
        private set
    var clear = false
        private set
    var damagePerSecond = 0
        private set
    var light = 0
        private set

    private val textureCoordinates: HashMap<String, FloatArray> = HashMap()

    fun setID(ID: Int): BlockDefinition {
        id = ID
        return this
    }

    fun setTextures(textures: Array<String>): BlockDefinition {
        if (this.textures != null) {
            throw RuntimeException("BlockDefinition: Attempted to set textures more than once for block ($internalName)!")
        }
        if (textures.size != 6) {
            throw RuntimeException("BlockDefinition: Textures must have 6 faces in block ($internalName)!")
        }
        this.textures = textures
        return this
    }

    fun setReadableName(readableName: String): BlockDefinition {
        this.readableName = readableName
        return this
    }

    fun setDrawType(drawType: DrawType): BlockDefinition {
        if (drawType.value() == -1) {
            throw RuntimeException("BlockDefinition: Tried to set DrawType of block (" + internalName + ") to DEFAULT!")
        }
        this.drawType = drawType
        return this
    }

    fun setWalkable(walkable: Boolean): BlockDefinition {
        this.walkable = walkable
        return this
    }

    fun setLiquid(liquid: Boolean): BlockDefinition {
        this.liquid = liquid
        return this
    }

    fun setLiquidFlow(liquidFlow: Int): BlockDefinition {
        if (liquidFlow <= 0 || liquidFlow > 8) {
            throw RuntimeException("BlockDefinition: liquidFlow (" + liquidFlow + ") is out of bounds on block (" + internalName + ")! Min: 1 | max: 8")
        }
        this.liquidFlow = liquidFlow
        return this
    }

    fun setLiquidViscosity(liquidViscosity: Int): BlockDefinition {
        if (liquidViscosity <= 0 || liquidViscosity > 8) {
            throw RuntimeException("BlockDefinition: liquidViscosity (" + liquidViscosity + ") is out of bounds on block (" + internalName + ")! Min: 1 | max: 8")
        }
        this.liquidViscosity = liquidViscosity
        return this
    }

    fun setClimbable(climbable: Boolean): BlockDefinition {
        this.climbable = climbable
        return this
    }

    fun setSneakJumpClimbable(sneakJumpClimbable: Boolean): BlockDefinition {
        this.sneakJumpClimbable = sneakJumpClimbable
        return this
    }

    fun setFalling(falling: Boolean): BlockDefinition {
        this.falling = falling
        return this
    }

    fun setClear(clear: Boolean): BlockDefinition {
        this.clear = clear
        return this
    }

    fun setDamagePerSecond(damagePerSecond: Int): BlockDefinition {
        if (damagePerSecond <= 0) {
            throw RuntimeException("BlockDefinition: damagePerSecond (" + damagePerSecond + ") on block (" + internalName + ") must be higher than 0!")
        }
        this.damagePerSecond = damagePerSecond
        return this
    }

    fun setLight(light: Int): BlockDefinition {
        if (light <= 0 || light > 15) {
            throw RuntimeException("BlockDefinition: light (" + light + ") on block (" + internalName + ") is out of bounds! Min: 1 | Max: 15")
        }
        this.light = light
        return this
    }

    fun setTextureCoordinates(face: String, value: FloatArray): BlockDefinition {
        if (textureCoordinates.containsKey(face)) {
            throw RuntimeException("BlockDefinition: Tried to put duplicate of texture coordinate ($face) into block ($internalName)!")
        }
        //        System.out.println("BlockDefinition: Put texture coordinate (" + face + ") into block (" + internalName + ")!");
        textureCoordinates[face] = value
        return this
    }

    fun getTextures(): Array<String?> {
        // Don't allow external mutability
        val clone = arrayOfNulls<String>(textures!!.size)
        System.arraycopy(textures, 0, clone, 0, textures!!.size)
        return clone
    }

    fun containsTextureCoordinate(name: String): Boolean {
        return textureCoordinates.containsKey(name)
    }

    fun getTextureCoordinate(name: String): FloatArray? {
        return textureCoordinates[name]
    }

    /**
     * Attaches the faces of blocks into the block definition.
     * TODO: Will become extremely complex with different drawtypes, perhaps this needs to be handled by the container with an assembler object?
     */
    fun attachFaces() {
        if (drawType == DrawType.AIR) {
            return
        }
        val faces = arrayOf("front", "back", "left", "right", "bottom", "top")
        val atlas = instance
        for (i in textures!!.indices) {
            val textureCoordinates = atlas.getQuadOf(textures!![i])
            setTextureCoordinates(faces[i], textureCoordinates)
        }
    }

    /**
     * Finalizer method for BlockDefinitions. Utilized by Block Definition Container to ensure no corrupted blocks
     * will be inserted into the library. This will cause Block Definition Container to throw an error if true.
     */
    fun validate() {
        if (id == -1) {
            // If the internal name is missing then OOPS
            throw RuntimeException("BlockDefinition: Block ($internalName) was never assigned a block ID!")
        } else if (internalName == null) {
            throw RuntimeException("BlockDefinition: Block with ID (" + id + ") is somehow MISSING an internal name!")
        }

        // Skip air block types because it does not need texturing
        if (drawType == DrawType.AIR) {
            return
        }

        // Check the array
        if (textures == null) {
            throw RuntimeException("BlockDefinition: Block ($internalName) is MISSING texture array!")
        } else if (textures!!.size != 6) {
            throw RuntimeException("BlockDefinition: Block($internalName) has the WRONG array length for textures!")
        }
        val atlas = instance

        // Now check that all the textures are valid
        var i = 0
        for (texture in textures!!) {
            if (!atlas.fileNameExists(texture)) {
                throw RuntimeException("BlockDefinition: Block ($internalName) has INVALID texture in index ($i)! ($texture) is not a valid block texture in the texture atlas!")
            }
            i++
        }
    }
}
