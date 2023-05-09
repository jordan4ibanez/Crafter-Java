//TODO: load up mods from JavaScript
/*
TODO: Use java classes as if they were compiled :D
TODO: Figure out more things like biomes
TODO:
*/


var FileReader = Java.type("org.crafter.engine.utility.FileReader");
const JavaScriptAPI = Java.type("org.crafter.engine.api.JavaScriptAPI");

// Lua equivalent!
var doFile = JavaScriptAPI.runCode;

var crafter = [];

doFile("api/testing.js");
