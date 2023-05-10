package org.crafter.engine.api;

import org.crafter.engine.utility.FileReader;
import org.crafter.engine.world.block.BlockDefinitionContainer;

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

    private API(){}
    public static void initialize() {
        javaScript = new ScriptEngineManager().getEngineByName("Nashorn");
        bindings = javaScript.getBindings(ScriptContext.ENGINE_SCOPE);
        compiler = (Compilable) javaScript;
        invoker  = (Invocable) javaScript;

        // javaScript.put("test", "hi there");
        runFile("api/api.js");

        // Todo Note: This is how you invoke from java into javascript
//        Object blah = invoke("getX");
//        System.out.println(blah.getClass());
//        System.out.println(blah);
//
//        runCode("mods/crafter/main.js");

        // Fully lockout the container
        BlockDefinitionContainer.getMainInstance().lockCache();
    }

    private static void loadMods() {

        // Basic mod loading
        for (String modFolder : getFolderList(modPath)) {

//            System.out.println("Got mod: " + modFolder);

            // We need to look through this multiple times so turn it into an indexable container
            HashMap<String, Boolean> fileExistence = new HashMap<>();
            Arrays.stream(FileReader.getFileList(modPath  + modFolder)).toList().forEach((fileName) -> {
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

            // todo, but in java so it's readonly in api scope >:D
//            int nameSpaceTimeStamp = getInteger("return crafter.setNameSpace('" + confParser.getDirectValue("name") + "')");

            // Now run main.js
//            runFile(modPath + modFolder + "/main.lua");
            runFile();

            // todo
//            // Now check it in case someone tried to mess with another mod
//            int newNameSpaceTimeStamp = getInteger("return crafter.getVerifier()");
//            if (nameSpaceTimeStamp != newNameSpaceTimeStamp) {
//                throw new RuntimeException("API: You CANNOT change your mod's namespace!");
//            }
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
