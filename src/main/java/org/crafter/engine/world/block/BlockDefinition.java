package org.crafter.engine.world.block;

import java.io.Serializable;
import java.util.HashMap;

public class BlockDefinition implements Serializable {
    // Required
    private int ID = -1;
    private final String internalName;
    private String[] textures;

    // Optional
    private String readableName = null;
    private DrawType drawType = DrawType.DEFAULT;
    private int walkable = -1;
    private int liquid = -1;
    private int liquidFlow = -1;
    private int liquidViscosity = -1;
    private int climbable = -1;
    private int sneakJumpClimbable = -1;
    private int falling = -1;
    private int clear = -1;
    private int damagePerSecond = -1;
    private int light = -1;

    private final HashMap<String, float[]> textureCoordinates;

    /*
    todo: particle effects
     */

    public BlockDefinition(String internalName) {
        this.internalName = internalName;
        this.textureCoordinates = new HashMap<>();
    }

    public void setID(int ID) {
        duplicateCheck(this.ID, "ID");
        this.ID = ID;
    }
    public void setTextures(String[] textures) {
        if (this.textures != null) {
            throw new RuntimeException("BlockDefinition: Attempted to set textures more than once for block (" + internalName + ")!");
        }
        if (textures.length != 6) {
            throw new RuntimeException("BlockDefinition: Textures must have 6 faces in block (" + internalName + ")!");
        }
        this.textures = textures;
    }
    public void setReadableName(String readableName) {
        if (this.readableName != null) {
            throw new RuntimeException("BlockDefinition: Tried to set (readableName) of block (" + this.internalName + ") more than once!");
        }
        this.readableName = readableName;
    }
    public void setDrawType(DrawType drawType) {
        if (drawType.value() == -1) {
            throw new RuntimeException("BlockDefinition: Tried to set DrawType of block (" + this.internalName + ") to DEFAULT!");
        }
        duplicateCheck(this.drawType, "drawType");
        this.drawType = drawType;
    }
    public void setWalkable(boolean walkable) {
        duplicateCheck(this.walkable, "walkable");
        this.walkable = boolToInt(walkable);
    }
    public void setLiquid(boolean liquid) {
        duplicateCheck(this.liquid, "liquid");
        this.liquid = boolToInt(liquid);
    }
    public void setLiquidFlow(int liquidFlow) {
        duplicateCheck(this.liquidFlow, "liquidFlow");
        if (liquidFlow <= 0 || liquidFlow > 8) {
            throw new RuntimeException("BlockDefinition: liquidFlow (" + liquidFlow + ") is out of bounds on block (" + this.internalName + ")! Min: 1 | max: 8");
        }
        this.liquidFlow = liquidFlow;
    }
    public void setLiquidViscosity(int liquidViscosity) {
        duplicateCheck(this.liquidViscosity, "liquidViscosity");
        if (liquidViscosity <= 0 || liquidViscosity > 8) {
            throw new RuntimeException("BlockDefinition: liquidViscosity (" + liquidViscosity + ") is out of bounds on block (" + this.internalName + ")! Min: 1 | max: 8");
        }
        this.liquidViscosity = liquidViscosity;
    }
    public void setClimbable(boolean climbable) {
        duplicateCheck(this.climbable, "climbable");
        this.climbable = boolToInt(climbable);
    }
    public void setSneakJumpClimbable(boolean sneakJumpClimbable) {
        duplicateCheck(this.sneakJumpClimbable, "sneakJumpClimbable");
        this.sneakJumpClimbable = boolToInt(sneakJumpClimbable);
    }
    public void setFalling(boolean falling) {
        duplicateCheck(this.falling, "falling");
        this.falling = boolToInt(falling);
    }
    public void setClear(boolean clear) {
        duplicateCheck(this.clear, "clear");
        this.clear = boolToInt(clear);
    }
    public void setDamagePerSecond(int damagePerSecond) {
        duplicateCheck(this.damagePerSecond, "damagePerSecond");
        if (damagePerSecond <= 0) {
            throw new RuntimeException("BlockDefinition: damagePerSecond (" + damagePerSecond + ") on block (" + this.internalName + ") must be higher than 0!");
        }
        this.damagePerSecond = damagePerSecond;
    }
    public void setLight(int light) {
        duplicateCheck(this.light, "light");
        if (light <= 0 || light > 15) {
            throw new RuntimeException("BlockDefinition: light (" + light + ") on block (" + this.internalName + ") is out of bounds! Min: 1 | Max: 15");
        }
        this.light = light;
    }
    public void setTextureCoordinates(String face, float[] value) {
        if (textureCoordinates.containsKey(face)) {
            throw new RuntimeException("BlockDefinition: Tried to put duplicate of texture coordinate (" + face + ") into block (" + internalName + ")!");
        }
//        System.out.println("BlockDefinition: Put texture coordinate (" + face + ") into block (" + internalName + ")!");
        textureCoordinates.put(face, value);
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
        return intToBool(walkable);
    }
    public boolean getLiquid() {
        return intToBool(liquid);
    }
    public int getLiquidFlow() {
        return liquidFlow;
    }
    public int getLiquidViscosity() {
        return liquidViscosity;
    }
    public boolean getClimbable() {
        return intToBool(climbable);
    }
    public boolean getSneakJumpClimbable() {
        return intToBool(sneakJumpClimbable);
    }
    public boolean getFalling() {
        return intToBool(falling);
    }
    public boolean getClear() {
        return intToBool(clear);
    }
    public int getDamagePerSecond() {
        return damagePerSecond;
    }
    public int getLight() {
        return light;
    }

    private int boolToInt(boolean input) {
        return input ? 1 : 0;
    }
    private boolean intToBool(int input) {
        return input == 1;
    }

    private void duplicateCheck(DrawType input, String fieldName) {
        if (duplicateDrawTypeSetCheck(input)) {
            throw new RuntimeException("BlockDefinition: Tried to set (" + fieldName + ") of block (" + this.internalName + ") more than once!");
        }

    }
    private void duplicateCheck(int input, String fieldName) {
        if (duplicateIntSetCheck(input)) {
            throw new RuntimeException("BlockDefinition: Tried to set (" + fieldName + ") of block (" + this.internalName + ") more than once!");
        }
    }
    private boolean duplicateDrawTypeSetCheck(DrawType input) {
        return input.value() != -1;
    }
    private boolean duplicateIntSetCheck(int input) {
        return input != -1;
    }
}
