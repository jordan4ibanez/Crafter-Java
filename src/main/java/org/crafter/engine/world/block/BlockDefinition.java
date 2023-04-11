package org.crafter.engine.world.block;

public class BlockDefinition {
    // Required
    private int ID = -1;
    private final String internalName;
    private final String[] textures;

    // Optional
    private String readableName = null;
    // Fixme: placeholder, needs enum. Default is undefined which would be a regular block
    private int drawType = -1;
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

    /*
    todo: particle effects
     */

    public BlockDefinition(String internalName, String[] textures) {
        this.internalName = internalName;
        if (textures.length != 6) {
            throw new RuntimeException("BlockDefinition: Textures must have 6 faces in block (" + internalName + ")!");
        }
        this.textures = textures;
    }

    public void setID(int ID) {
        duplicateCheck(this.ID, "ID");
        this.ID = ID;
    }
    public void setReadableName(String readableName) {
        if (readableName != null) {
            throw new RuntimeException("BlockDefinition: Tried to set (readableName) of block (" + this.internalName + ") more than once!");
        }
        this.readableName = readableName;
    }
    public void setDrawType(int drawType) {
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
        this.liquidFlow = liquidFlow;
    }
    public void setLiquidViscosity(int liquidViscosity) {
        duplicateCheck(this.liquidViscosity, "liquidViscosity");
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
        this.damagePerSecond = damagePerSecond;
    }
    public void setLight(int light) {
        duplicateCheck(this.light, "light");
        this.light = light;
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
    public int getDrawType() {
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

    private void duplicateCheck(int input, String fieldName) {
        if (duplicateIntSetCheck(input)) {
            throw new RuntimeException("BlockDefinition: Tried to set (" + fieldName + ") of block (" + this.internalName + ") more than once!");
        }
    }
    private boolean duplicateIntSetCheck(int input) {
        return input != -1;
    }
}
