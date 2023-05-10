package org.crafter.engine.world.block;

public enum DrawType {
    AIR(0),
    BLOCK(1),
    BLOCK_BOX(2),
    TORCH(3),
    LIQUID_SOURCE(4),
    LIQUID_FLOW(5),
    GLASS(6),
    PLANT(7),
    LEAVES(8);

    private final int value;

    DrawType(int value){
        this.value = value;
    }

    public int value() {
        return value;
    }


    public static DrawType intToDrawType(int input) {
        for (DrawType drawType : getAsArray()) {
            if (drawType.value() == input) {
                return drawType;
            }
        }
        throw new RuntimeException("DrawType: Attempted to convert invalid value! (" + input + ")");
    }

    public static DrawType[] getAsArray() {
        return new DrawType[] {AIR, BLOCK, BLOCK_BOX, TORCH, LIQUID_SOURCE, LIQUID_FLOW, GLASS, PLANT, LEAVES};
    }
}
