package org.crafter_unit_tests;

import org.crafter.engine.world.block.BlockDefinition;
import org.crafter.engine.world.block.DrawType;
import org.joml.Random;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BlockDefinitionTest {
    private static final int testAmount = 65_535;

    @Test
    public void checkAllData() {

        final String namePrefix = "Crafter:";
        final String[] textureSelections = new String[]{"dirt.png", "stone.png", "flarp.png", "yada.png", "bloop.png", "smorf.png"};
        final String[] readableNames = new String[]{"dirt", "stone", "cactus", "air", "grass", "cloud", "sand"};
        final DrawType[] drawTypes = DrawType.getAsArray();
        Random random = new Random((int) (new Date().getTime()/1000));

        for (int i = 0; i < testAmount; i++) {

            final String[] chosenTextures = new String[6];
            for (int w = 0; w < chosenTextures.length; w++) {
                chosenTextures[w] = textureSelections[random.nextInt(textureSelections.length)];
            }

            BlockDefinition testDefinition = new BlockDefinition(namePrefix + i);
            testDefinition.setTextures(chosenTextures);

            assertEquals(namePrefix + i, testDefinition.getInternalName());

            final String[] gottenTextures = testDefinition.getTextures();
            for (int w = 0; w < chosenTextures.length; w++) {
                assertEquals(chosenTextures[w], gottenTextures[w]);
            }

//            System.out.println("---------");
//            System.out.println(testDefinition.getInternalName());
//            System.out.println(Arrays.toString(gottenTextures));
//            System.out.println(Arrays.toString(chosenTextures));

            final String chosenReadableName = readableNames[random.nextInt(readableNames.length)];
            testDefinition.setReadableName(chosenReadableName);
            final String gottenReadableName = testDefinition.getReadableName();

            assertEquals(chosenReadableName, gottenReadableName);

//            System.out.println(gottenReadableName);

            final DrawType chosenDrawType = drawTypes[random.nextInt(drawTypes.length)];
            testDefinition.setDrawType(chosenDrawType);
            final DrawType gottenDrawType = testDefinition.getDrawType();

            assertEquals(chosenDrawType, gottenDrawType);

//            System.out.println(gottenDrawType);

            final boolean chosenWalkable = random.nextInt(2) == 1;
            testDefinition.setWalkable(chosenWalkable);
            final boolean gottenWalkable = testDefinition.getWalkable();

            assertEquals(chosenWalkable, gottenWalkable);

//            System.out.println(gottenWalkable);

            final boolean chosenLiquid = random.nextInt(2) == 1;
            testDefinition.setLiquid(chosenLiquid);
            final boolean gottenLiquid = testDefinition.getLiquid();

            assertEquals(chosenLiquid, gottenLiquid);

//            System.out.println(gottenLiquid);

            final int chosenLiquidFLow = random.nextInt(8) + 1;
            testDefinition.setLiquidFlow(chosenLiquidFLow);
            final int gottenLiquidFlow = testDefinition.getLiquidFlow();

            assertEquals(chosenLiquidFLow, gottenLiquidFlow);

//            System.out.println(gottenLiquidFlow);

            final int chosenLiquidViscosity = random.nextInt(8) + 1;
            testDefinition.setLiquidViscosity(chosenLiquidViscosity);
            final int gottenLiquidViscosity = testDefinition.getLiquidViscosity();

            assertEquals(chosenLiquidViscosity, gottenLiquidViscosity);

//            System.out.println(gottenLiquidViscosity);

            final boolean chosenClimbable = random.nextInt(2) == 1;
            testDefinition.setClimbable(chosenClimbable);
            final boolean gottenClimbable = testDefinition.getClimbable();

            assertEquals(chosenClimbable, gottenClimbable);

//            System.out.println(gottenClimbable);

            final boolean chosenSneakJumpClimbable = random.nextInt(2) == 1;
            testDefinition.setSneakJumpClimbable(chosenSneakJumpClimbable);
            final boolean gottenSneakJumpClimbable = testDefinition.getSneakJumpClimbable();

            assertEquals(chosenSneakJumpClimbable, gottenSneakJumpClimbable);

//            System.out.println(gottenSneakJumpClimbable);

            final boolean chosenFalling = random.nextInt(2) == 1;
            testDefinition.setFalling(chosenFalling);
            final boolean gottenFalling = testDefinition.getFalling();

            assertEquals(chosenFalling, gottenFalling);

//            System.out.println(gottenFalling);

            final boolean chosenClear = random.nextInt(2) == 1;
            testDefinition.setClear(chosenClear);
            final boolean gottenClear = testDefinition.getClear();

            assertEquals(chosenClear, gottenClear);

//            System.out.println(gottenClear);

            // That's alotta damage!
            final int chosenDamagePerSecond = random.nextInt(100) + 1;
            testDefinition.setDamagePerSecond(chosenDamagePerSecond);
            final int gottenDamagePerSecond = testDefinition.getDamagePerSecond();

            assertEquals(chosenDamagePerSecond, gottenDamagePerSecond);

//            System.out.println(gottenDamagePerSecond);

            final int chosenLight = random.nextInt(15) + 1;
            testDefinition.setLight(chosenLight);
            final int gottenLight = testDefinition.getLight();

            assertEquals(chosenLight, gottenLight);

//            System.out.println(gottenLight);
        }

    }
}
