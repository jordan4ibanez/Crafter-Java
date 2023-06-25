/*
 Crafter - A blocky game (engine) written in Java with LWJGL.
 Copyright (C) 2023  jordan4ibanez

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.crafter.engine.world.block;

import org.crafter.engine.texture.WorldAtlas;
import org.crafter.engine.texture.texture_packer.TexturePacker;

import java.io.Serializable;
import java.util.HashMap;

/**
 * A simple container object for block definitions.
 * Can utilize the builder pattern.
 */
public class BlockDefinition implements Serializable {
    // Required
    private int ID = -1;
    private final String internalName;
    private String[] textures = null;

    // Optional
    private String readableName = null;
    private DrawType drawType = DrawType.BLOCK;
    private boolean walkable = true;
    private boolean liquid = false;
    private int liquidFlow = 0;
    private int liquidViscosity = 0;
    private boolean climbable = false;
    private boolean sneakJumpClimbable = false;
    private boolean falling = false;
    private boolean clear = false;
    private int damagePerSecond = 0;
    private int light = 0;

    // FIXME: this is a disastrous technique
    private final HashMap<String, float[]> textureCoordinates;

    /*
    todo: particle effects
     */

    public BlockDefinition(String internalName) {
        this.internalName = internalName;
        this.textureCoordinates = new HashMap<>();
    }

    public BlockDefinition setID(int ID) {
        this.ID = ID;
        return this;
    }
    public BlockDefinition setTextures(String[] textures) {
        if (this.textures != null) {
            throw new RuntimeException("BlockDefinition: Attempted to set textures more than once for block (" + internalName + ")!");
        }
        if (textures.length != 6) {
            throw new RuntimeException("BlockDefinition: Textures must have 6 faces in block (" + internalName + ")!");
        }
        this.textures = textures;
        return this;
    }
    public BlockDefinition setReadableName(String readableName) {
        if (this.readableName != null) {
            throw new RuntimeException("BlockDefinition: Tried to set (readableName) of block (" + this.internalName + ") more than once!");
        }
        this.readableName = readableName;
        return this;
    }
    public BlockDefinition setDrawType(DrawType drawType) {
        if (drawType.value() == -1) {
            throw new RuntimeException("BlockDefinition: Tried to set DrawType of block (" + this.internalName + ") to DEFAULT!");
        }
        this.drawType = drawType;
        return this;
    }
    public BlockDefinition setWalkable(boolean walkable) {
        this.walkable = walkable;
        return this;
    }
    public BlockDefinition setLiquid(boolean liquid) {
        this.liquid = liquid;
        return this;
    }
    public BlockDefinition setLiquidFlow(int liquidFlow) {
        if (liquidFlow <= 0 || liquidFlow > 8) {
            throw new RuntimeException("BlockDefinition: liquidFlow (" + liquidFlow + ") is out of bounds on block (" + this.internalName + ")! Min: 1 | max: 8");
        }
        this.liquidFlow = liquidFlow;
        return this;
    }
    public BlockDefinition setLiquidViscosity(int liquidViscosity) {
        if (liquidViscosity <= 0 || liquidViscosity > 8) {
            throw new RuntimeException("BlockDefinition: liquidViscosity (" + liquidViscosity + ") is out of bounds on block (" + this.internalName + ")! Min: 1 | max: 8");
        }
        this.liquidViscosity = liquidViscosity;
        return this;
    }
    public BlockDefinition setClimbable(boolean climbable) {
        this.climbable = climbable;
        return this;
    }
    public BlockDefinition setSneakJumpClimbable(boolean sneakJumpClimbable) {
        this.sneakJumpClimbable = sneakJumpClimbable;
        return this;
    }
    public BlockDefinition setFalling(boolean falling) {
        this.falling = falling;
        return this;
    }
    public BlockDefinition setClear(boolean clear) {
        this.clear = clear;
        return this;
    }
    public BlockDefinition setDamagePerSecond(int damagePerSecond) {
        if (damagePerSecond <= 0) {
            throw new RuntimeException("BlockDefinition: damagePerSecond (" + damagePerSecond + ") on block (" + this.internalName + ") must be higher than 0!");
        }
        this.damagePerSecond = damagePerSecond;
        return this;
    }
    public BlockDefinition setLight(int light) {
        if (light <= 0 || light > 15) {
            throw new RuntimeException("BlockDefinition: light (" + light + ") on block (" + this.internalName + ") is out of bounds! Min: 1 | Max: 15");
        }
        this.light = light;
        return this;
    }
    public BlockDefinition setTextureCoordinates(String face, float[] value) {
        if (textureCoordinates.containsKey(face)) {
            throw new RuntimeException("BlockDefinition: Tried to put duplicate of texture coordinate (" + face + ") into block (" + internalName + ")!");
        }
//        System.out.println("BlockDefinition: Put texture coordinate (" + face + ") into block (" + internalName + ")!");
        textureCoordinates.put(face, value);
        return this;
    }

    public int getID() {
        return ID;
    }
    public String getInternalName() {
        return internalName;
    }
    public String[] getTextures() {
        // Don't allow external mutability
        String[] clone = new String[textures.length];
        System.arraycopy(textures, 0, clone, 0, textures.length);
        return clone;
    }
    public String getReadableName() {
        return readableName;
    }
    public DrawType getDrawType() {
        return drawType;
    }
    public boolean getWalkable() {
        return walkable;
    }
    public boolean getLiquid() {
        return liquid;
    }
    public int getLiquidFlow() {
        return liquidFlow;
    }
    public int getLiquidViscosity() {
        return liquidViscosity;
    }
    public boolean getClimbable() {
        return climbable;
    }
    public boolean getSneakJumpClimbable() {
        return sneakJumpClimbable;
    }
    public boolean getFalling() {
        return falling;
    }
    public boolean getClear() {
        return clear;
    }
    public int getDamagePerSecond() {
        return damagePerSecond;
    }
    public int getLight() {
        return light;
    }
    public boolean containsTextureCoordinate(String name) {
        return textureCoordinates.containsKey(name);
    }
    public float[] getTextureCoordinate(String name) {
        return textureCoordinates.get(name);
    }

    /**
     * Attaches the faces of blocks into the block definition.
     * TODO: Will become extremely complex with different drawtypes, perhaps this needs to be handled by the container with an assembler object?
     */

     
    public void attachFaces() {

        if (drawType == DrawType.AIR) {
            return;
        }
        final String[] faces = new String[]{"front", "back", "left", "right", "bottom", "top"};
        TexturePacker atlas = WorldAtlas.getInstance();
        for (int i = 0; i < textures.length; i++) {
            final float[] textureCoordinates = atlas.getQuadOf(textures[i]);
            setTextureCoordinates(faces[i], textureCoordinates);
        }
    }

    /**
     * Finalizer method for BlockDefinitions. Utilized by Block Definition Container to ensure no corrupted blocks
     * will be inserted into the library. This will cause Block Definition Container to throw an error if true.
     */
    public void validate() {

        if (ID == -1) {
            // If the internal name is missing then OOPS
            throw new RuntimeException("BlockDefinition: Block (" + internalName + ") was never assigned a block ID!");
        } else if (internalName == null) {
            throw new RuntimeException("BlockDefinition: Block with ID (" + ID + ") is somehow MISSING an internal name!");
        }

        // Skip air block types because it does not need texturing
        if (drawType == DrawType.AIR) {
            return;
        }

        // Check the array
        if (textures == null) {
            throw new RuntimeException("BlockDefinition: Block (" + internalName + ") is MISSING texture array!");
        } else if (textures.length != 6) {
            throw new RuntimeException("BlockDefinition: Block(" + internalName + ") has the WRONG array length for textures!" );
        }

        TexturePacker atlas = WorldAtlas.getInstance();

        // Now check that all the textures are valid
        int i = 0;
        for (String texture : textures) {
            if (!atlas.fileNameExists(texture)) {
                throw new RuntimeException("BlockDefinition: Block (" + internalName + ") has INVALID texture in index (" + i + ")! (" + texture + ") is not a valid block texture in the texture atlas!");
            }
            i++;
        }
    }
}
