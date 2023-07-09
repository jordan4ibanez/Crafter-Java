/*
 Crafter - A blocky game (engine) written in Java with LWJGL.
 Copyright (C) 2023  jordan4ibanez

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.crafter.engine.api;

import org.crafter.engine.texture.TextureStorage;
import org.crafter.engine.texture.WorldAtlas;
import org.crafter.engine.texture.texture_packer.TexturePacker;
import org.crafter.engine.utility.FileReader;
import org.crafter.engine.world.block.BlockDefinitionContainer;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.*;

import java.util.Arrays;
import java.util.HashMap;

import static org.crafter.engine.utility.FileReader.getFileString;
import static org.crafter.engine.utility.FileReader.getFolderList;

public final class API {
    private static ScriptEngine javaScript;
    private static Bindings bindings;

    private static Compilable compiler;
    private static Invocable invoker;

    // Keep this as a field in case it is ever decided to relocate it!
    private static final String modPath = "mods/";

    private static final String[] requiredValues = new String[]{"name", "version", "description"};

    private static String currentModDirectory = "";

    private API(){}
    public static void initialize() {
        // Ecmascript 6, it's a miracle!
        // Thank you, eugene, on this stackoverflow article response https://stackoverflow.com/questions/43406743/call-javascript-that-contains-const-from-java
        javaScript = new NashornScriptEngineFactory().getScriptEngine("--language=es6");
        // This one is ecmascript 5.1
//        javaScript = new ScriptEngineManager().getEngineByName("Nashorn");
        bindings = javaScript.getBindings(ScriptContext.ENGINE_SCOPE);
        compiler = (Compilable) javaScript;
        invoker  = (Invocable) javaScript;

        // Load up the actual javascript API elements
        runFile("api/api.js");

        // The gist: it loads mod textures
        // Read method for more information
        loadModTextures();

        // Now load up all mods
        loadMods();

        // Todo Note: This is how you invoke from java into javascript
//        Object blah = invoke("getX");
//        System.out.println(blah.getClass());
//        System.out.println(blah);
//
//        runCode("mods/crafter/main.js");

        // todo, Note: This is how you can create global javascript variables from Java!
//         javaScript.put("test", "hi there");

        // Fully lockout the container
        BlockDefinitionContainer.getMainInstance().lockCache();
    }

    private static void loadMods() {

        // Basic mod loading
        for (String modFolder : getFolderList(modPath)) {

//            System.out.println("Got mod: " + modFolder);

            currentModDirectory = modPath  + modFolder;

            // We need to look through this multiple times so turn it into an indexable container
            HashMap<String, Boolean> fileExistence = new HashMap<>();
            Arrays.stream(FileReader.getFileList(modPath  + modFolder)).toList().forEach((fileName) -> {
                fileExistence.put(fileName, true);
            });

            // Check mod.json existence
            if (!fileExistence.containsKey("mod.json")) {
                throw new RuntimeException("API: Mod (" + modFolder + ") does not have mod.json!");
            }

            // Check main.js existence
            if (!fileExistence.containsKey("main.js")) {
                throw new RuntimeException("API: Mod (" + modFolder + ") does not have main.js!");
            }

            // Automate required values in conf are checked here
            ModConfParser confParser = checkParserConfValues(new ModConfParser(modPath + modFolder), modFolder);

            // todo, but in java so it's readonly in the javascript API scope >:D
//            int nameSpaceTimeStamp = getInteger("return crafter.setNameSpace('" + confParser.getDirectValue("name") + "')");

            // Now run main.js
            runFile(modPath + modFolder + "/main.js");

            // todo
//            // Now check it in case someone tried to mess with another mod
//            int newNameSpaceTimeStamp = getInteger("return crafter.getVerifier()");
//            if (nameSpaceTimeStamp != newNameSpaceTimeStamp) {
//                throw new RuntimeException("API: You CANNOT change your mod's namespace!");
//            }
        }
    }


    private static void loadModTextures() {
        // Each individual mod folder in root of /mods/ (crafter_base, my_cool_mod, etc)
        String[] modFolderList = FileReader.getFolderList(modPath);

        for (String modFolder : modFolderList) {
            // Loads up all png files within mod's /textures/blocks/ folder into the WorldAtlas texture packer.
            loadModBlockTextures(modPath + modFolder);

            // Loads up all png files within mod's /textures/ folder EXCLUDING /blocks/. These are individual textures.
            loadModIndividualTextures(modPath + modFolder);

        }
        // All mod textures are loaded, close it out.
        WorldAtlas.lock();
        // TextureStorage now has an entry of "worldAtlas" that can be easily gotten!
    }

    private static void loadModIndividualTextures(String modDirectory) {

        String texturesDirectory = modDirectory + "/textures";

        if (!FileReader.isFolder(texturesDirectory)) {
//            System.out.println("API: No (textures) folder in mod directory (" + modDirectory + "). Skipping!");
            return;
        }

        String[] foundFiles = FileReader.getFileList(texturesDirectory);

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

    private static void loadModBlockTextures(String modDirectory) {

        String texturesDirectory = modDirectory + "/textures";

        if (!FileReader.isFolder(texturesDirectory)) {
//            System.out.println("API: No (textures) folder in mod directory (" + modDirectory + "). Skipping!");
            return;
        }

        String blockTexturesDirectory = texturesDirectory + "/blocks";

        if (!FileReader.isFolder(blockTexturesDirectory)) {
//            System.out.println("API: No (textures/blocks) folder in mod directory (" + texturesDirectory + "). Skipping!");
            return;
        }

        String[] foundFiles = FileReader.getFileList(blockTexturesDirectory);

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
    private static ModConfParser checkParserConfValues(ModConfParser modConfParser, String modDirectory) {
        for (String requiredValue : requiredValues) {
            if (!modConfParser.containsDirectValue(requiredValue)) {
                throw new RuntimeException("API: Mod (" + modDirectory + ") is missing (" + requiredValue + ")!");
            }
        }
        return modConfParser;
    }

    public static String getCurrentModDirectory() {
        return currentModDirectory;
    }

    /**
     * Runs a javascript file. Extracts the string of the text & passes it into runCodeRaw.
     * @param fileLocation The location of the javascript file.
     */
    public static void runFile(String fileLocation) {
        runCode(getFileString(fileLocation));
    }

    /**
     * Run raw javascript code.
     * @param rawCode The raw code string.
     */
    public static void runCode(String rawCode) {

        //TODO: Maybe a game error catcher thing, print out the string like minetest?
        try {
            javaScript.eval(rawCode);
        } catch (Exception e) {
            throw new RuntimeException("API ERROR!: " + e);
        }
    }

    /**
     * This is a runtime exception version of the javax invoker method.
     * Object needs to have it's class checked or instanceOf switch statement.
     * @param functionName Name of a global scope javascript function.
     * @param args Function arguments.
     * @return Object, can be of any Java type.
     */
    public static Object invoke(String functionName, Object... args) {
        try {
            return invoker.invokeFunction(functionName, args);
        } catch (Exception e) {
            throw new RuntimeException("API ERROR!: " + e);
        }
    }

}
