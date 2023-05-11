package org.crafter.engine.world.block

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeType
import org.crafter.engine.utility.FileReader.getFileString
import org.crafter.engine.utility.FileReader.isFile
import org.crafter.engine.utility.FileReader.isFolder
import org.crafter.engine.utility.FileReader.makeFile
import org.crafter.engine.utility.FileReader.makeFolder
import java.io.Serializable

/**
 * This object's soul purpose is to parse and map the block_cache.json file for the BlockDefinitionContainer to get
 * existing block values, or to assign new block values!
 * IDs are stored numerically, 0,1,2,3,4, etc. If there is a gap then there is a problem!
 * FIXME: This could be slow if there are thousands of blocks, consider using a database!
 */
class BlockNameToIDCache : Serializable {
    private val jsonLocation = "cache/block_cache.json"
    private val nameToIDMap: HashMap<String?, Int>

    // 0 is reserved for air
    private var nextFreeSlot = 1

    init {
        nameToIDMap = HashMap()
        if (!isFolder("cache")) {
            makeFolder("cache")
            //            System.out.println("BlockNameToIDCache: Created the cache folder!");
        } /*else {
            System.out.println("BlockNameToIDCache: cache folder already exists!");
        }*/
        if (isFile(jsonLocation)) {
//            System.out.println("BlockNameToIDCache: JSON exists! Parsing!");
            try {
                processJsonNodes(ObjectMapper().readTree(getFileString("cache/block_cache.json")))
            } catch (e: Exception) {
                throw RuntimeException("BlockNameToIDCache: Failure reading (cache/block_cache.json)! $e")
            }
        } /*else {
            System.out.println("BlockNameToIDCache: JSON does not exist! Will create!");
        }*/
        // Else the nameToIDMap is blank!
    }

    /**
     * Nodes should ALWAYS be flat in here! If they aren't, throw an error!
     */
    private fun processJsonNodes(nodes: JsonNode) {
//        System.out.println("JSON BLOCK CACHE PROCESSING STARTED");

        // Crawl up the JSON tree
        val keys = nodes.fieldNames()
        val it = nodes.elements()
        while (it.hasNext()) {
            val key = keys.next()
            val value = it.next()
            val type = value.nodeType
            duplicateCheck(key)
            if (type != JsonNodeType.NUMBER) {
                throw RuntimeException("BlockNameToIDCache: BLOCK DEFINITION CACHE FAILURE! ID was type: ($type)! Did you modify the block cache?")
            }
            val rawValue = value.asDouble()
            val numericValue = value.asInt()
            if ((rawValue * 100).toInt() != numericValue * 100) {
                throw RuntimeException("BlockNameToIDCache: BLOCK DEFINITION CACHE FAILURE! Value was floating! Did you modify the block cache?")
            }

            // Automatically tick up the free slot for the next numeric value
            if (numericValue >= nextFreeSlot) {
                nextFreeSlot = numericValue + 1
            }
            nameToIDMap[key] = numericValue
        }

//        System.out.println("PARSE OUTPUT: " + nameToIDMap.toString());
    }

    fun assign(internalName: String?): Int {

        // ID 0 is reserved for air
        if (internalName == "air" && !containsInternalName("air")) {
            nameToIDMap[internalName] = 0
        }
        return getIDAssignment(internalName)
    }

    private fun getIDAssignment(internalName: String?): Int {
        if (containsInternalName(internalName)) {
            return nameToIDMap[internalName]!!
        }
        val thisID = nextFreeSlot
        nameToIDMap[internalName] = thisID
        nextFreeSlot++
        return thisID
    }

    private fun containsInternalName(internalName: String?): Boolean {
        return nameToIDMap.containsKey(internalName)
    }

    fun lock() {
//        System.out.println("BlockNameToIDCache: Updated block_cache.json!");
        println(nameToIDMap.toString())
        val mapper = ObjectMapper()
        try {
            mapper.writeValue(makeFile(jsonLocation), nameToIDMap)
        } catch (e: Exception) {
            throw RuntimeException("BlockNameToIDCache: Failed to write the Internal Name to ID cache! $e")
        }
    }

    private fun duplicateCheck(internalName: String) {
        if (nameToIDMap.containsKey(internalName)) {
            throw RuntimeException("BlockNameToIDCache: Attempted to assign duplicate into cache! Name: ($internalName)! Did you modify the block cache?")
        }
    }
}
