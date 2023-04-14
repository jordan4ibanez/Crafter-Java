package org.crafter.engine.api;

import org.crafter.engine.texture.WorldAtlas;
import org.crafter.engine.texture.texture_packer.TexturePacker;
import org.crafter.engine.world.block.BlockDefinition;
import org.crafter.engine.world.block.BlockDefinitionContainer;
import org.crafter.engine.world.block.DrawType;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.luajit.LuaJit;

import java.util.ArrayList;
import java.util.Arrays;

import static org.crafter.engine.utility.FileReader.*;

public final class API {
    private static final LuaJit luaJIT = new LuaJit();

    private static final String modPath = "mods/";

    private API(){}

    public static void initialize() {
        luaJIT.openLibraries();

        runFile("api/api.lua");

        // This scoops all the mod block texture data up and loads it up into the texture atlas
        loadModBlockTexturesIntoWorldAtlas();

        // This scoops all the general mod texture data up and loads it up into TextureStorage
        // loadModTextures();

        // This runs the main.lua file of all mods, so they can be dynamically loaded into memory (crafter.registerBlock, crafter.registerEntity, etc)
        loadMods();

        // This is the method which translates the block definitions from lua to java
        parseBlocks();

        // This was an old hack to delete the needed lua api functions
//        runCode("crafter.closeAPI()");
    }

    private static void loadModBlockTexturesIntoWorldAtlas() {
        for (String modFolder : getFolderList(modPath)) {
            loadLuaModBlockTextures(modPath + modFolder);
        }
        // All mod textures are loaded, close it out.
        WorldAtlas.lock();
        // TextureStorage now has an entry of "worldAtlas" that can be easily gotten!
    }

    public static void loadLuaModBlockTextures(String modDirectory) {

        String texturesDirectory = modDirectory + "/textures";

        if (!isFolder(texturesDirectory)) {
            System.out.println("API: No (textures) folder in mod directory (" + modDirectory + "). Skipping!");
            return;
        }

        String blockTexturesDirectory = texturesDirectory + "/blocks";

        if (!isFolder(blockTexturesDirectory)) {
            System.out.println("API: No (textures/blocks) folder in mod directory (" + texturesDirectory + "). Skipping!");
            return;
        }

        String[] foundFiles = getFileList(blockTexturesDirectory);

        if (foundFiles.length == 0) {
            System.out.println("WorldAtlas: (exit 1) No files found in mod blocks texture directory (" + blockTexturesDirectory + "). Skipping!");
            return;
        }

        int foundPNGs = 0;
        for (String thisFile : foundFiles) {
            System.out.println(thisFile);
            if (thisFile.contains(".png")) {
                foundPNGs++;
            }
        }
        if (foundPNGs == 0) {
            System.out.println("WorldAtlas: (exit 2) No block textures (.png) found in mod blocks texture directory (" + blockTexturesDirectory + "). Skipping!");
            return;
        }

        TexturePacker worldAtlas = WorldAtlas.getInstance();

        for (String thisFile : foundFiles) {
            if (thisFile.contains(".png")) {
                WorldAtlas.getInstance().add(thisFile, blockTexturesDirectory + "/" + thisFile);
            }
        }
    }

    private static void loadMods() {

        // Basic mod loading test

        for (String modFolder : getFolderList(modPath)) {

            boolean found = false;
//            System.out.println("Got mod: " + modFolder);

            String[] modFiles = getFileList(modPath  + modFolder);

            // Searching for the main mod file
            for (String file : modFiles) {
                if (file.equals("main.lua")) {
                    found = true;
//                    System.out.println("FOUND MAIN!");

                    runFile(modPath + modFolder + "/main.lua");

                    break;
                }
            }
            if (!found) {
                throw new RuntimeException("API: Mod (" + modFolder + ") does not have main.lua!");
            }
        }

    }

    private static void parseBlocks() {
        BlockDefinitionContainer container = BlockDefinitionContainer.getMainInstance();

        while (true) {

            // Grab name
            String blockName = getString("return crafter.getNextBlock()");

            if (blockName == null) {
                break;
            }

            BlockDefinition definition = new BlockDefinition(blockName);

            if (blockName.equals("air")) {
                definition.setID(0);
            }

            while (true) {

                String fieldName = getString("return crafter.getNextField()");

                if (fieldName == null) {
                    break;
                }

//                System.out.println(fieldName);

                switch (fieldName) {
                    case ("getTextures") -> {
                        // String[]
                        if (definition.getDrawType() == DrawType.AIR) {
                            System.out.println("API: Block (" + definition.getInternalName() + ") is air drawtype, skipping textures!");
                            return;
                        }
                        String[] textures = getBlockStringArrayField(blockName, fieldName);
                        System.out.println(Arrays.toString(textures));

                        // Double check

                        boolean textureInvalid = false;

                        //FIXME: REPLACE WITH PLACEHOLDER
                        if (textures.length != 6) {
//                            throw new RuntimeException("API: Block (" + definition.getInternalName() + ") has wrong texture array size! Required: 6: | Gotten: " + textures.length);
                            System.out.println("API: Replace block (" + definition.getInternalName() + ") with a placeholder texture definition!");
                            return;
                        }
                        for (String texture : textures) {
                            if (texture == null) {
//                                throw new RuntimeException("API: Block (" + definition.getInternalName() + ") has a null texture string defined at index (" + i + ")!");
                                System.out.println("API: Replace block (" + definition.getInternalName() + ") with a placeholder texture definition!");
                                return;
                            }
                        }

                        definition.setTextures(textures);
                    }
                    case ("getReadableName") -> {
                        // String
                        String readableName = getBlockStringField(blockName, fieldName);
                        if (readableName == null) {
                            readableName = "UNDEFINED!";
                        }
                        definition.setReadableName(readableName);
                    }
                    case ("getWalkable") -> {
                        // Boolean
                        boolean walkable = getBlockBooleanField(blockName, fieldName);
                        definition.setWalkable(walkable);
                    }
                    case ("getDrawType") -> {
                        // Integer
                        int drawType = getBlockIntegerField(blockName, fieldName);
                        DrawType trueDrawType = DrawType.intToDrawType(drawType);
                        definition.setDrawType(trueDrawType);
                    }
                    case ("getLiquid") -> {
                        // Boolean
                        boolean liquid = getBlockBooleanField(blockName, fieldName);
                        definition.setLiquid(liquid);
                    }
                    case ("getLiquidViscosity") -> {
                        // Integer
                        int liquidViscosity = getBlockIntegerField(blockName, fieldName);
                        if (liquidViscosity < 1 || liquidViscosity > 8) {
                            break;
                        }
                        definition.setLiquidViscosity(liquidViscosity);
                    }
                    case ("getClimbable") -> {
                        // Boolean
                        boolean climbable = getBlockBooleanField(blockName, fieldName);
                        definition.setClimbable(climbable);
                    }
                    case ("getSneakJumpClimbable") -> {
                        // Boolean
                        boolean sneakJumpClimbable = getBlockBooleanField(blockName, fieldName);
                        definition.setSneakJumpClimbable(sneakJumpClimbable);
                    }
                    case ("getFalling") -> {
                        // Boolean
                        boolean falling = getBlockBooleanField(blockName, fieldName);
                        definition.setFalling(falling);
                    }
                    case ("getClear") -> {
                        // Boolean
                        boolean clear = getBlockBooleanField(blockName, fieldName);
                        definition.setClear(clear);
                    }
                    case ("getDamagePerSecond") -> {
                        // Integer
                        int damagePerSecond = getBlockIntegerField(blockName, fieldName);
                        if (damagePerSecond <= 0) {
                            break;
                        }
                        definition.setDamagePerSecond(damagePerSecond);
                    }
                    case ("getLight") -> {
                        // Integer
                        int light = getBlockIntegerField(blockName, fieldName);
                        if (light <= 0 || light > 15) {
                            break;
                        }
                        definition.setLight(light);
                    }
                    default -> {
                        // ¯\_(ツ)_/¯
                        throw new RuntimeException("API: In-took INVALID field! https://media.tenor.com/LzbqCkSnfFcAAAAd/tommy-boy-what-did-you-do.gif");
                    }
                }
            }

            // Finally added it into game's memory pool
            container.addDefinition(definition);
        }
    }

    private static String[] getBlockStringArrayField(String blockName, String fieldName) {

        ArrayList<String> builder = new ArrayList<>();
        int index = 1;

        while (true) {

            String gottenData = getString("return crafter.getBlockDataArray('" + blockName + "','" + fieldName + "'," + index + ")");

            if (gottenData == null) {
                break;
            }

            builder.add(gottenData);

            index++;
        }
        return builder.toArray(new String[0]);
    }
    private static int getBlockIntegerField(String blockName, String fieldName) {
        return getInteger("return crafter.getBlockData('" + blockName + "','" + fieldName + "')");
    }
    private static boolean getBlockBooleanField(String blockName, String fieldName) {
        return getBoolean("return crafter.getBlockData('" + blockName + "','" + fieldName + "')");
    }
    private static String getBlockStringField(String blockName, String fieldName) {
        return getString("return crafter.getBlockData('" + blockName + "','" + fieldName + "')");
    }

    private static int getInteger(String luaCode) {
        runCode(luaCode);
        return (int)Math.round(luaJIT.toNumber(-1));
    }
    private static boolean getBoolean(String luaCode) {
        runCode(luaCode);
        return luaJIT.toBoolean(-1);
    }
    private static String getString(String luaCode) {
        runCode(luaCode);
        return luaJIT.toString(-1);
    }

    private static void runFile(String luaCodeDirectory) {
        String luaCode = getFileString(luaCodeDirectory);
        runCode(luaCode);
    }

    private static void runCode(String luaCode) {
        checkError(luaJIT.run(luaCode));
    }

    private static void checkError(Lua.LuaError error){
        if (error != Lua.LuaError.OK) {
            throw new RuntimeException(luaJIT.toString(-1));
        }
    }

    public static void destroy() {
        luaJIT.close();
    }
}