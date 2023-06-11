package org.crafter_unit_tests

import org.crafter.engine.world.block.BlockDefinition
import org.crafter.engine.world.block.DrawType.Companion.asArray
import org.joml.Random
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class BlockDefinitionTest {
    @Test
    fun checkAllData() {
        val namePrefix = "Crafter:"
        val textureSelections = arrayOf("dirt.png", "stone.png", "flarp.png", "yada.png", "bloop.png", "smorf.png")
        val readableNames = arrayOf("dirt", "stone", "cactus", "air", "grass", "cloud", "sand")
        val drawTypes = asArray
        val random = Random((Date().time / 1000).toInt().toLong())

        for (i in 0 until testAmount) {
            val chosenTextures = Array(6){""}
            for (w in chosenTextures.indices) {
                chosenTextures[w] = textureSelections[random.nextInt(textureSelections.size)]
            }
            val testDefinition = BlockDefinition(namePrefix + i)
            testDefinition.setTextures(chosenTextures)
            Assertions.assertEquals(namePrefix + i, testDefinition.internalName)
            val gottenTextures = testDefinition.getTextures()
            for (w in chosenTextures.indices) {
                Assertions.assertEquals(chosenTextures[w], gottenTextures[w])
            }

//            System.out.println("---------");
//            System.out.println(testDefinition.getInternalName());
//            System.out.println(Arrays.toString(gottenTextures));
//            System.out.println(Arrays.toString(chosenTextures));
            val chosenReadableName = readableNames[random.nextInt(readableNames.size)]
            testDefinition.setReadableName(chosenReadableName)
            val gottenReadableName = testDefinition.readableName
            Assertions.assertEquals(chosenReadableName, gottenReadableName)

//            System.out.println(gottenReadableName);
            val chosenDrawType = drawTypes[random.nextInt(drawTypes.size)]
            testDefinition.setDrawType(chosenDrawType)
            val gottenDrawType = testDefinition.drawType
            Assertions.assertEquals(chosenDrawType, gottenDrawType)

//            System.out.println(gottenDrawType);
            val chosenWalkable = random.nextInt(2) == 1
            testDefinition.setWalkable(chosenWalkable)
            val gottenWalkable = testDefinition.walkable
            Assertions.assertEquals(chosenWalkable, gottenWalkable)

//            System.out.println(gottenWalkable);
            val chosenLiquid = random.nextInt(2) == 1
            testDefinition.setLiquid(chosenLiquid)
            val gottenLiquid = testDefinition.liquid
            Assertions.assertEquals(chosenLiquid, gottenLiquid)

//            System.out.println(gottenLiquid);
            val chosenLiquidFLow = random.nextInt(8) + 1
            testDefinition.setLiquidFlow(chosenLiquidFLow)
            val gottenLiquidFlow = testDefinition.liquidFlow
            Assertions.assertEquals(chosenLiquidFLow, gottenLiquidFlow)

//            System.out.println(gottenLiquidFlow);
            val chosenLiquidViscosity = random.nextInt(8) + 1
            testDefinition.setLiquidViscosity(chosenLiquidViscosity)
            val gottenLiquidViscosity = testDefinition.liquidViscosity
            Assertions.assertEquals(chosenLiquidViscosity, gottenLiquidViscosity)

//            System.out.println(gottenLiquidViscosity);
            val chosenClimbable = random.nextInt(2) == 1
            testDefinition.setClimbable(chosenClimbable)
            val gottenClimbable = testDefinition.climbable
            Assertions.assertEquals(chosenClimbable, gottenClimbable)

//            System.out.println(gottenClimbable);
            val chosenSneakJumpClimbable = random.nextInt(2) == 1
            testDefinition.setSneakJumpClimbable(chosenSneakJumpClimbable)
            val gottenSneakJumpClimbable = testDefinition.sneakJumpClimbable
            Assertions.assertEquals(chosenSneakJumpClimbable, gottenSneakJumpClimbable)

//            System.out.println(gottenSneakJumpClimbable);
            val chosenFalling = random.nextInt(2) == 1
            testDefinition.setFalling(chosenFalling)
            val gottenFalling = testDefinition.falling
            Assertions.assertEquals(chosenFalling, gottenFalling)

//            System.out.println(gottenFalling);
            val chosenClear = random.nextInt(2) == 1
            testDefinition.setClear(chosenClear)
            val gottenClear = testDefinition.clear
            Assertions.assertEquals(chosenClear, gottenClear)

//            System.out.println(gottenClear);

            // That's alotta damage!
            val chosenDamagePerSecond = random.nextInt(100) + 1
            testDefinition.setDamagePerSecond(chosenDamagePerSecond)
            val gottenDamagePerSecond = testDefinition.damagePerSecond
            Assertions.assertEquals(chosenDamagePerSecond, gottenDamagePerSecond)

//            System.out.println(gottenDamagePerSecond);
            val chosenLight = random.nextInt(15) + 1
            testDefinition.setLight(chosenLight)
            val gottenLight = testDefinition.light
            Assertions.assertEquals(chosenLight, gottenLight)

//            System.out.println(gottenLight);
        }
    }

    companion object {
        private const val testAmount = 65535
    }
}
