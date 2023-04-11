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

    final int value;

    DrawType(int value){
        this.value = value;
    }

    public int value() {
        return value;
    }
}
