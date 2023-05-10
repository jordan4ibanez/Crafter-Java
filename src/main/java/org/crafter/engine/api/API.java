package org.crafter.engine.api;

import org.crafter.engine.world.block.BlockDefinitionContainer;

import javax.script.*;

import static org.crafter.engine.utility.FileReader.getFileString;

public final class API {
    private static ScriptEngine javaScript;
    private static Bindings bindings;

    private static Compilable compiler;
    private static Invocable invoker;

    private API(){}
    public static void initialize() {
        javaScript = new ScriptEngineManager().getEngineByName("Nashorn");
        bindings = javaScript.getBindings(ScriptContext.ENGINE_SCOPE);
        compiler = (Compilable) javaScript;
        invoker  = (Invocable) javaScript;

        // javaScript.put("test", "hi there");
        runCode("api/api.js");

        // Todo Note: This is how you invoke from java into javascript
//        Object blah = invoke("getX");
//        System.out.println(blah.getClass());
//        System.out.println(blah);
//
//        runCode("mods/crafter/main.js");

        // Fully lockout the container
        BlockDefinitionContainer.getMainInstance().lockCache();
    }


    /**
     * Runs a javascript file. Extracts the string of the text & passes it into runCodeRaw.
     * @param fileLocation The location of the javascript file.
     */
    public static void runCode(String fileLocation) {
        runCodeRaw(getFileString(fileLocation));
    }

    /**
     * Run raw javascript code.
     * @param rawCode The raw code string.
     */
    public static void runCodeRaw(String rawCode) {
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
