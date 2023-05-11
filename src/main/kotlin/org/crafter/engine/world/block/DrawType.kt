package org.crafter.engine.world.block

enum class DrawType(private val value: Int) {
    AIR(0),
    BLOCK(1),
    BLOCK_BOX(2),
    TORCH(3),
    LIQUID_SOURCE(4),
    LIQUID_FLOW(5),
    GLASS(6),
    PLANT(7),
    LEAVES(8);

    fun value(): Int {
        return value
    }

    companion object {
        fun intToDrawType(input: Int): DrawType {
            for (drawType in asArray) {
                if (drawType.value() == input) {
                    return drawType
                }
            }
            throw RuntimeException("DrawType: Attempted to convert invalid value! ($input)")
        }

        @JvmStatic
        val asArray: Array<DrawType>
            get() = arrayOf(AIR, BLOCK, BLOCK_BOX, TORCH, LIQUID_SOURCE, LIQUID_FLOW, GLASS, PLANT, LEAVES)
    }
}
