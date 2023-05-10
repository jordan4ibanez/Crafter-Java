//TODO: load up mods from JavaScript
/*
TODO: Use java classes as if they were compiled :D
TODO: Figure out more things like biomes
TODO:
*/

/*
API is the base of the API which loads up class variables into the crafter array.
This allows for mods to index DIRECTLY into the engine without needing to be compiled.
Limitations: Ecmascript 5 :( No constants, anything can be overridden.
*/

// Lua equivalent!
var doFile;
// Lua addition!
var readFileToString;

// Very similar to minetest's api table, basically a clone of it in JS
var crafter = [];

// Auto executing lambda localized variable scope discards
!function(){
    // Classes from the engine which will disappear after this scope
    var FileReader = Java.type("org.crafter.engine.utility.FileReader");
    var API = Java.type("org.crafter.engine.api.API");
    var BlockDefinitionContainer = Java.type("org.crafter.engine.world.block.BlockDefinitionContainer");

    // Global scope variables
    doFile = API.runCode;
    readFileToString = FileReader.getFileString;

    // Crafter array functions
    crafter.blah = function() {
        print("I'm a blah!");
    }
}()


//doFile("api/testing.js");
//var stringy = readFileToString("api/testing.js");
//print(stringy);


