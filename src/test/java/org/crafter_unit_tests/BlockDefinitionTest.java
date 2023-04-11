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

            BlockDefinition testDefinition = new BlockDefinition(
                    namePrefix + i,
                    chosenTextures
            );

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

            assertEquals(1,2);



        }

    }
}
