var FileReader = Java.type("org.crafter.engine.utility.FileReader");
var JavaScriptAPI = Java.type("org.crafter.engine.api.JavaScriptAPI");

// Lua equivalent!
var doFile = JavaScriptAPI.runCode;

var crafter = [];

doFile("api/testing.js");