package org.crafter.engine.api;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public final class JavaScriptAPI {
    private static ScriptEngine javaScript;
    private static Bindings bindings;

    private JavaScriptAPI(){}
    public static void initialize() {
        javaScript = new ScriptEngineManager().getEngineByName("Nashorn");
        bindings = javaScript.getBindings(ScriptContext.ENGINE_SCOPE);

        // javaScript.put("test", "hi there");

        try {
            javaScript.eval("print(test)");
        } catch (Exception e) {
            System.out.println(e);
        }

    }


}
