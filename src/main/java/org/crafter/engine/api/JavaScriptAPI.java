package org.crafter.engine.api;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import static org.crafter.engine.utility.FileReader.getFileString;

public final class JavaScriptAPI {
    private static ScriptEngine javaScript;
    private static Bindings bindings;

    private JavaScriptAPI(){}
    public static void initialize() {
        javaScript = new ScriptEngineManager().getEngineByName("Nashorn");
        bindings = javaScript.getBindings(ScriptContext.ENGINE_SCOPE);

        // javaScript.put("test", "hi there");
        runCode("api/api.js");


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
}
