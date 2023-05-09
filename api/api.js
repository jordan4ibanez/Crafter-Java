//TODO: load up mods from JavaScript
/*
TODO: Use java classes as if they were compiled :D
TODO: Figure out more things like biomes
TODO:
*/

/*
API is the base of the API which loads up class variables into the crafter array.
This allows for mods to index DIRECTLY into the engine without needing to be compiled.
*/

// Lua equivalent!
var doFile;
// Lua addition!
var readFileToString;

// Auto executing localized scope discards
!function locals(){
    var FileReader = Java.type("org.crafter.engine.utility.FileReader");
    var JavaScriptAPI = Java.type("org.crafter.engine.api.JavaScriptAPI");
    doFile = JavaScriptAPI.runCode;
    readFileToString = FileReader.getFileString;
}()

var crafter = [];

doFile("api/testing.js");

var stringy = readFileToString("api/testing.js");
