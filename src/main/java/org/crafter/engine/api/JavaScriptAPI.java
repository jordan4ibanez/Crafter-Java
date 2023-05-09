package org.crafter.engine.api;

import org.openjdk.nashorn.api.scripting.JSObject;

import javax.script.*;

import static org.crafter.engine.utility.FileReader.getFileString;

public final class JavaScriptAPI {
    private static ScriptEngine javaScript;
    private static Bindings bindings;

    private static Compilable compiler;
    private static Invocable invoker;

    private JavaScriptAPI(){}
    public static void initialize() {
        javaScript = new ScriptEngineManager().getEngineByName("Nashorn");
        bindings = javaScript.getBindings(ScriptContext.ENGINE_SCOPE);
        compiler = (Compilable) javaScript;
        invoker  = (Invocable) javaScript;

        // javaScript.put("test", "hi there");
        runCode("api/api.js");

//        Object blah = invoke("getX");
//        System.out.println(blah.getClass());
//        System.out.println(blah);
//
//        runCode("mods/crafter/main.js");

    }


    public static void runCode(String fileLocation) {
        runCodeRaw(getFileString(fileLocation));
    }

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
     */
    public static Object invoke(String functionName, Object... args) {

        try {
            return invoker.invokeFunction(functionName, args);
        } catch (Exception e) {
            throw new RuntimeException("API ERROR!: " + e);
        }
//        return test;
    }
    /**
     * Below are the basic typing implementations of the invoke function.
     */

}