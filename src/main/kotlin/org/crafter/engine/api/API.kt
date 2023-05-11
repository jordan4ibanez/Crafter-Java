package org.crafter.engine.api

import org.crafter.engine.texture.TextureStorage
import org.crafter.engine.texture.WorldAtlas
import org.crafter.engine.utility.FileReader
import org.crafter.engine.world.block.BlockDefinitionContainer
import java.util.*
import java.util.function.Consumer
import javax.script.*

object API {
    private var javaScript: ScriptEngine? = null
    private var bindings: Bindings? = null
    private var compiler: Compilable? = null
    private var invoker: Invocable? = null

    // Keep this as a field in case it is ever decided to relocate it!
    private const val modPath = "mods/"
    private val requiredValues = arrayOf("name", "version", "description")
    fun initialize() {
        javaScript = ScriptEngineManager().getEngineByName("Nashorn")
        bindings = javaScript?.getBindings(ScriptContext.ENGINE_SCOPE)
        compiler = javaScript as Compilable?
        invoker = javaScript as Invocable?

        // Load up the actual javascript API elements
        runFile("api/api.js")

        // The gist: it loads mod textures
        // Read method for more information
        loadModTextures()

        // Now load up all mods
        loadMods()

        // Todo Note: This is how you invoke from java into javascript
//        Object blah = invoke("getX");
//        System.out.println(blah.getClass());
//        System.out.println(blah);
//
//        runCode("mods/crafter/main.js");

        // todo, Note: This is how you can create global javascript variables from Java!
//         javaScript.put("test", "hi there");

        // Fully lockout the container
        BlockDefinitionContainer.getMainInstance().lockCache()
    }

    private fun loadMods() {

        // Basic mod loading
        for (modFolder in FileReader.getFolderList(modPath)) {

//            System.out.println("Got mod: " + modFolder);

            // We need to look through this multiple times so turn it into an indexable container
            val fileExistence = HashMap<String, Boolean>()
            Arrays.stream(FileReader.getFileList(modPath + modFolder)).toList().forEach(
                Consumer { fileName: String -> fileExistence[fileName] = true })

            // Check mod.json existence
            if (!fileExistence.containsKey("mod.json")) {
                throw RuntimeException("API: Mod ($modFolder) does not have mod.json!")
            }

            // Check main.js existence
            if (!fileExistence.containsKey("main.js")) {
                throw RuntimeException("API: Mod ($modFolder) does not have main.js!")
            }

            // Automate required values in conf are checked here
            val confParser = checkParserConfValues(ModConfParser(modPath + modFolder), modFolder)

            // todo, but in java so it's readonly in the javascript API scope >:D
//            int nameSpaceTimeStamp = getInteger("return crafter.setNameSpace('" + confParser.getDirectValue("name") + "')");

            // Now run main.js
            runFile("$modPath$modFolder/main.js")

            // todo
//            // Now check it in case someone tried to mess with another mod
//            int newNameSpaceTimeStamp = getInteger("return crafter.getVerifier()");
//            if (nameSpaceTimeStamp != newNameSpaceTimeStamp) {
//                throw new RuntimeException("API: You CANNOT change your mod's namespace!");
//            }
        }
    }

    private fun loadModTextures() {
        // Each individual mod folder in root of /mods/ (crafter_base, my_cool_mod, etc)
        val modFolderList = FileReader.getFolderList(modPath)
        for (modFolder in modFolderList) {
            // Loads up all png files within mod's /textures/blocks/ folder into the WorldAtlas texture packer.
            loadModBlockTextures(modPath + modFolder)

            // Loads up all png files within mod's /textures/ folder EXCLUDING /blocks/. These are individual textures.
            loadModIndividualTextures(modPath + modFolder)
        }
        // All mod textures are loaded, close it out.
        WorldAtlas.lock()
        // TextureStorage now has an entry of "worldAtlas" that can be easily gotten!
    }

    private fun loadModIndividualTextures(modDirectory: String) {
        val texturesDirectory = "$modDirectory/textures"
        if (!FileReader.isFolder(texturesDirectory)) {
//            System.out.println("API: No (textures) folder in mod directory (" + modDirectory + "). Skipping!");
            return
        }
        val foundFiles = FileReader.getFileList(texturesDirectory)
        if (foundFiles.isEmpty()) {
//            System.out.println("API: (exit 1) No files found in mod texture directory (" + texturesDirectory + "). Skipping!");
            return
        }
        var foundPNGs = 0
        for (thisFile in foundFiles) {
            if (thisFile.contains(".png")) {
                foundPNGs++
            }
        }
        if (foundPNGs == 0) {
//            System.out.println("API: (exit 2) No block textures (.png) found in mod blocks texture directory (" + texturesDirectory + "). Skipping!");
            return
        }
        for (thisFile in foundFiles) {
            if (thisFile.contains(".png")) {
                TextureStorage.createTexture(thisFile, "$texturesDirectory/$thisFile")
            }
        }
    }

    private fun loadModBlockTextures(modDirectory: String) {
        val texturesDirectory = "$modDirectory/textures"
        if (!FileReader.isFolder(texturesDirectory)) {
//            System.out.println("API: No (textures) folder in mod directory (" + modDirectory + "). Skipping!");
            return
        }
        val blockTexturesDirectory = "$texturesDirectory/blocks"
        if (!FileReader.isFolder(blockTexturesDirectory)) {
//            System.out.println("API: No (textures/blocks) folder in mod directory (" + texturesDirectory + "). Skipping!");
            return
        }
        val foundFiles = FileReader.getFileList(blockTexturesDirectory)
        if (foundFiles.isEmpty()) {
//            System.out.println("API: (exit 1) No files found in mod blocks texture directory (" + blockTexturesDirectory + "). Skipping!");
            return
        }
        var foundPNGs = 0
        for (thisFile in foundFiles) {
            if (thisFile.contains(".png")) {
                foundPNGs++
            }
        }
        if (foundPNGs == 0) {
//            System.out.println("API: (exit 2) No block textures (.png) found in mod blocks texture directory (" + blockTexturesDirectory + "). Skipping!");
            return
        }
        val worldAtlasTexturePacker = WorldAtlas.getInstance()
        for (thisFile in foundFiles) {
            if (thisFile.contains(".png")) {
                worldAtlasTexturePacker.add(thisFile, "$blockTexturesDirectory/$thisFile")
            }
        }
    }

    private fun checkParserConfValues(modConfParser: ModConfParser, modDirectory: String): ModConfParser {
        for (requiredValue in requiredValues) {
            if (!modConfParser.containsDirectValue(requiredValue)) {
                throw RuntimeException("API: Mod ($modDirectory) is missing ($requiredValue)!")
            }
        }
        return modConfParser
    }

    /**
     * Runs a javascript file. Extracts the string of the text & passes it into runCodeRaw.
     * @param fileLocation The location of the javascript file.
     */
    fun runFile(fileLocation: String?) {
        runCode(FileReader.getFileString(fileLocation))
    }

    /**
     * Run raw javascript code.
     * @param rawCode The raw code string.
     */
    fun runCode(rawCode: String?) {
        //TODO: Maybe a game error catcher thing, print out the string like minetest?
        try {
            javaScript!!.eval(rawCode)
        } catch (e: Exception) {
            throw RuntimeException("API ERROR!: $e")
        }
    }

    /**
     * This is a runtime exception version of the javax invoker method.
     * Object needs to have it's class checked or instanceOf switch statement.
     * @param functionName Name of a global scope javascript function.
     * @param args Function arguments.
     * @return Object, can be of any Java type.
     */
    operator fun invoke(functionName: String?, vararg args: Any?): Any {
        return try {
            invoker!!.invokeFunction(functionName, *args)
        } catch (e: Exception) {
            throw RuntimeException("API ERROR!: $e")
        }
    }
}
