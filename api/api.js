//TODO: load up mods from JavaScript
/*
TODO: Use java classes as if they were compiled :D
TODO: Figure out more things like biomes


TODO! VERY IMPORTANT! Add comments! See if there is a way to link the java documentation into javascript....somehow?
*/

/*
API is the base of the API which loads up class variables into the crafter array.
This allows for mods to index DIRECTLY into the engine without needing to be compiled.
Limitations: Ecmascript 5 :( No constants, anything can be overridden.
*/

// Lua equivalents!
var doFile;
var readFileToString;

// Global java types
var BlockDefinition;
var DrawType;

// Very similar to minetest's api table, basically a clone of it in JS
var crafter = [];

// Auto executing lambda localized variable scope discards
!function(){
    // Classes from the engine which will disappear after this scope
    var FileReader = Java.type("org.crafter.engine.utility.FileUtility");
    var API = Java.type("org.crafter.engine.api.API");
    var BlockDefinitionContainer = Java.type("org.crafter.engine.world.block.BlockDefinitionContainer");
    BlockDefinition = Java.type("org.crafter.engine.world.block.BlockDefinition");
    DrawType = Java.type("org.crafter.engine.world.block.DrawType");

    // Global scope variables
    doFile = API.runCode;
    readFileToString = FileReader.getFileString;

    // Crafter array functions
    crafter.registerBlock = function(newBlockDefinition) {
        BlockDefinitionContainer.getMainInstance().registerBlock(newBlockDefinition);
    }
}()

// Air is reserved here
crafter.registerBlock(
    new BlockDefinition("air")
        .setDrawType(DrawType.AIR)
);