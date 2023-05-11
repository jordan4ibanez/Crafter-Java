package org.crafter.engine.api

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeType
import org.crafter.engine.utility.FileReader

/**
 * Automatically parses and stored required values from a mod.json file.
 * Will throw an error if it does not have required file & definitions.
 * Optional values do not throw errors.
 */
class ModConfParser(modDirectory: String?) {
    private val jsonLocation: String
    private val directValues: HashMap<String, String>
    private val arrayValues: HashMap<String, Array<String>>

    init {
        if (modDirectory == null) {
            throw RuntimeException("ModConfParser: Initialized with a NULL mod directory!")
        }
        if (!FileReader.isFolder(modDirectory)) {
            throw RuntimeException("ModConfParser: Initialized with an INVALID mod directory!")
        }
        jsonLocation = "$modDirectory/mod.json"
        if (!FileReader.isFile(jsonLocation)) {
            throw RuntimeException("ModConfParser: ($modDirectory) MUST contain a mod.json!")
        }
        directValues = HashMap()
        arrayValues = HashMap()
        parseJson()
    }

    private fun parseJson() {
        val mapper = ObjectMapper()
        val nodes: JsonNode
        nodes = try {
            mapper.readTree(FileReader.getFileString(jsonLocation))
        } catch (e: Exception) {
            throw RuntimeException("Font: ERROR loading! $e")
        }

        // Crawl up the JSON tree
        val keys = nodes.fieldNames()
        val it = nodes.elements()
        while (it.hasNext()) {
            val key = keys.next()
            val value = it.next()
            val type = value.nodeType
            when (key) {
                "name" -> {
                    assert(type == JsonNodeType.STRING)
                    checkDuplicateDirectValue("name")
                    directValues["name"] = value.asText()
                    //                    System.out.println("got name");
                }

                "version" -> {
                    assert(type == JsonNodeType.STRING)
                    checkDuplicateDirectValue("version")
                    directValues["version"] = value.asText()
                    //                    System.out.println("got version");
                }

                "description" -> {
                    assert(type == JsonNodeType.STRING)
                    checkDuplicateDirectValue("description")
                    directValues["description"] = value.asText()
                    //                    System.out.println("got description");
                }

                "dependencies" -> {
                    assert(type == JsonNodeType.ARRAY)
                    checkDuplicateArrayValue("dependencies")
                    // TODO: PUT THE ARRAY OF STRINGS HERE
                    throw RuntimeException("ModConfParser: dependencies is not implemented!")
                }

                else -> {}
            }
        }
    }

    fun containsDirectValue(key: String): Boolean {
        return directValues.containsKey(key)
    }

    fun containsArrayValue(key: String): Boolean {
        arrayValueCheckExistence(key)
        return arrayValues.containsKey(key)
    }

    fun getDirectValue(key: String): String? {
        directValueCheckExistence(key)
        return directValues[key]
    }

    fun getArrayValue(key: String): Array<String> {
        arrayValueCheckExistence(key)
        return arrayValues[key]!!
    }

    private fun checkDuplicateDirectValue(key: String) {
        if (containsDirectValue(key)) {
            throw RuntimeException("ModConfParser: Mod configuration ($jsonLocation) contains duplicate of ($key)!")
        }
    }

    private fun checkDuplicateArrayValue(key: String) {
        if (containsArrayValue(key)) {
            throw RuntimeException("ModConfParser: Mod configuration ($jsonLocation) contains duplicate of ($key)!")
        }
    }

    /**
     * Enforce running containsX for complete safety!
     */
    private fun directValueCheckExistence(key: String) {
        if (!directValues.containsKey(key)) {
            throw RuntimeException("ModConfParser: Tried to parse an invalid direct value on mod configuration file ($jsonLocation)! ($key) does not exist.")
        }
    }

    private fun arrayValueCheckExistence(key: String) {
        if (!arrayValues.containsKey(key)) {
            throw RuntimeException("ModConfParser: Tried to parse an invalid array value on mod configuration file ($jsonLocation)! ($key) does not exist.")
        }
    }
}
