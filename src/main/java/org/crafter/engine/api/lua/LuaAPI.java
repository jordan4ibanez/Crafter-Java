package org.crafter.engine.api.lua;

import org.crafter.engine.texture.TextureStorage;
import org.crafter.engine.texture.WorldAtlas;
import org.crafter.engine.texture.texture_packer.TexturePacker;
import org.crafter.engine.world.block.BlockDefinition;
import org.crafter.engine.world.block.BlockDefinitionContainer;
import org.crafter.engine.world.block.DrawType;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.luajit.LuaJit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.crafter.engine.utility.FileReader.*;

public final class LuaAPI {
    private static final LuaJit luaJIT = new LuaJit();

    // Keep this as a field in case it is ever decided to relocate it!
    private static final String modPath = "mods/";

    private static final String[] requiredValues = new String[]{"name", "version", "description"};

    private LuaAPI(){}

    public static void initialize() {
        luaJIT.openLibraries();

        runFile("api/api.lua");

        // The gist: it loads mod textures
        // Read method for more information
        loadModTextures();

        // This runs the main.lua file of all mods, so they can be dynamically loaded into memory (crafter.registerBlock, crafter.registerEntity, etc)
        loadMods();

        // This is the method which translates the block definitions from lua to java
        parseBlocks();

        // This was an old hack to delete the needed lua api functions
//        runCode("crafter.closeAPI()");
    }

    private static void loadModTextures() {
        // Each individual mod folder in root of /mods/ (crafter_base, my_cool_mod, etc)
        String[] modFolderList = getFolderList(modPath);

        for (String modFolder : modFolderList) {
            // Loads up all png files within mod's /textures/blocks/ folder into the WorldAtlas texture packer.
            loadLuaModBlockTextures(modPath + modFolder);

            // Loads up all png files within mod's /textures/ folder EXCLUDING /blocks/. These are individual textures.
            loadLuaModIndividualTextures(modPath + modFolder);

        }
        // All mod textures are loaded, close it out.
        WorldAtlas.lock();
        // TextureStorage now has an entry of "worldAtlas" that can be easily gotten!

    }

    private static void loadLuaModIndividualTextures(String modDirectory) {

        String texturesDirectory = modDirectory + "/textures";

        if (!isFolder(texturesDirectory)) {
//            System.out.println("API: No (textures) folder in mod directory (" + modDirectory + "). Skipping!");
            return;
        }

        String[] foundFiles = getFileList(texturesDirectory);

        if (foundFiles.length == 0) {
//            System.out.println("API: (exit 1) No files found in mod texture directory (" + texturesDirectory + "). Skipping!");
            return;
        }

        int foundPNGs = 0;
        for (String thisFile : foundFiles) {
            if (thisFile.contains(".png")) {
                foundPNGs++;
            }
        }
        if (foundPNGs == 0) {
//            System.out.println("API: (exit 2) No block textures (.png) found in mod blocks texture directory (" + texturesDirectory + "). Skipping!");
            return;
        }

        for (String thisFile : foundFiles) {
            if (thisFile.contains(".png")) {
                TextureStorage.createTexture(thisFile, texturesDirectory + "/" + thisFile);
            }
        }
    }

    private static void loadLuaModBlockTextures(String modDirectory) {

        String texturesDirectory = modDirectory + "/textures";

        if (!isFolder(texturesDirectory)) {
//            System.out.println("API: No (textures) folder in mod directory (" + modDirectory + "). Skipping!");
            return;
        }

        String blockTexturesDirectory = texturesDirectory + "/blocks";

        if (!isFolder(blockTexturesDirectory)) {
//            System.out.println("API: No (textures/blocks) folder in mod directory (" + texturesDirectory + "). Skipping!");
            return;
        }

        String[] foundFiles = getFileList(blockTexturesDirectory);

        if (foundFiles.length == 0) {
//            System.out.println("API: (exit 1) No files found in mod blocks texture directory (" + blockTexturesDirectory + "). Skipping!");
            return;
        }

        int foundPNGs = 0;
        for (String thisFile : foundFiles) {
            if (thisFile.contains(".png")) {
                foundPNGs++;
            }
        }
        if (foundPNGs == 0) {
//            System.out.println("API: (exit 2) No block textures (.png) found in mod blocks texture directory (" + blockTexturesDirectory + "). Skipping!");
            return;
        }

        TexturePacker worldAtlasTexturePacker = WorldAtlas.getInstance();

        for (String thisFile : foundFiles) {
            if (thisFile.contains(".png")) {
                worldAtlasTexturePacker.add(thisFile, blockTexturesDirectory + "/" + thisFile);
            }
        }
    }

    private static void loadMods() {

        // Basic mod loading
        for (String modFolder : getFolderList(modPath)) {

//            System.out.println("Got mod: " + modFolder);

            // We need to look through this multiple times so turn it into an indexable container
            HashMap<String, Boolean> fileExistence = new HashMap<>();
            Arrays.stream(getFileList(modPath  + modFolder)).toList().forEach((fileName) -> {
                fileExistence.put(fileName, true);
            });

            // Check mod.json existence
            if (!fileExistence.containsKey("mod.json")) {
                throw new RuntimeException("API: Mod (" + modFolder + ") does not have mod.json!");
            }

            // Check main.lua existence
            if (!fileExistence.containsKey("main.lua")) {
                throw new RuntimeException("API: Mod (" + modFolder + ") does not have main.lua!");
            }

            // Automate required values in conf are checked here
            ModConfParser confParser = checkParserConfValues(new ModConfParser(modPath + modFolder), modFolder);

            int nameSpaceTimeStamp = getInteger("return crafter.setNameSpace('" + confParser.getDirectValue("name") + "')");

            // Now run main.lua
            runFile(modPath + modFolder + "/main.lua");

            // Now check it in case someone tried to mess with another mod
            int newNameSpaceTimeStamp = getInteger("return crafter.getVerifier()");
            if (nameSpaceTimeStamp != newNameSpaceTimeStamp) {
                throw new RuntimeException("API: You CANNOT change your mod's namespace!");
            }
        }

    }
    private static ModConfParser checkParserConfValues(ModConfParser modConfParser, String modDirectory) {
        for (String requiredValue : requiredValues) {
            if (!modConfParser.containsDirectValue(requiredValue)) {
                throw new RuntimeException("API: Mod (" + modDirectory + ") is missing (" + requiredValue + ")!");
            }
        }
        return modConfParser;
    }

    private static void parseBlocks() {
        BlockDefinitionContainer container = BlockDefinitionContainer.getMainInstance();

        // This is used to ensure the existence of mod-defined textures
        TexturePacker worldAtlasTexturePacker = WorldAtlas.getInstance();

        while (true) {

            // Grab name
            String blockName = getString("return crafter.getNextBlock()");

            if (blockName == null) {
                break;
            }

            BlockDefinition definition = new BlockDefinition(blockName);

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
//                            System.out.println("API: Block (" + definition.getInternalName() + ") is air drawtype, skipping textures!");
                            continue;
                        }
                        String[] textures = getBlockStringArrayField(blockName, fieldName);
//                        System.out.println(Arrays.toString(textures));

                        // Double check

//                        boolean textureInvalid = false;

                        //FIXME: REPLACE WITH PLACEHOLDER - This should never flow into this scope!
                        if (textures.length != 6) {
//                            throw new RuntimeException("API: Block (" + definition.getInternalName() + ") has wrong texture array size! Required: 6: | Gotten: " + textures.length);
                            System.out.println("API: Replace block (" + definition.getInternalName() + ") with a placeholder texture definition!");
                            continue;
                        }
                        for (int i = 0; i < textures.length; i++) {
                            if (textures[i] == null) {
                                throw new RuntimeException("API: Block (" + definition.getInternalName() + ") has a NULL texture string defined at index (" + i + ")!");
//                                System.out.println("API: Replace block (" + definition.getInternalName() + ") with a placeholder texture definition!");
                            } else if (textures[i].equals("")) {
                                throw new RuntimeException("API: Block (" + definition.getInternalName() + ") has a BLANK texture at index (" + i + ")!");
                            } else if (!worldAtlasTexturePacker.fileNameExists(textures[i])) {
                                throw new RuntimeException("API: Block (" + definition.getInternalName() + ") has an UNREGISTERED texture at index (" + i + "). Texture (" + textures[i] + ") is not a registered texture!");
                            }
                        }

                        final String[] faces = new String[]{"front", "back", "left", "right", "bottom", "top"};
                        for (int i = 0; i < textures.length; i++) {
                            final float[] textureCoordinates = WorldAtlas.getInstance().getQuadOf(textures[i]);
                            definition.setTextureCoordinates(faces[i], textureCoordinates);
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
                            continue;
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
                            continue;
                        }
                        definition.setDamagePerSecond(damagePerSecond);
                    }
                    case ("getLight") -> {
                        // Integer
                        int light = getBlockIntegerField(blockName, fieldName);
                        if (light <= 0 || light > 15) {
                            continue;
                        }
                        definition.setLight(light);
                    }
                    default -> {
                        // ¯\_(ツ)_/¯
                        throw new RuntimeException("API: In-took INVALID field! https://media.tenor.com/LzbqCkSnfFcAAAAd/tommy-boy-what-did-you-do.gif");
                    }
                }
            }

            // Finally, added it into game's memory pool
            container.addDefinition(definition);
        }

        // This makes the BlockNameToIDCache output a json with the stored InternalID -> Integral ID map,
        // so nothing gets overwritten
        container.lockCache();
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