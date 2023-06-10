package org.crafter.engine.world.block;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import static org.crafter.engine.utility.FileReader.*;

/**
 * This object's soul purpose is to parse and map the block_cache.json file for the BlockDefinitionContainer to get
 * existing block values, or to assign new block values!
 * IDs are stored numerically, 0,1,2,3,4, etc. If there is a gap then there is a problem!
 * FIXME: This could be slow if there are thousands of blocks, consider using a database!
 */
public class BlockNameToIDCache implements Serializable {

    private final String jsonLocation = "cache/block_cache.json";
    private final HashMap<String, Integer> nameToIDMap;

    // 0 is reserved for air
    private int nextFreeSlot = 1;

    public BlockNameToIDCache() {
        nameToIDMap = new HashMap<>();
        if (!isFolder("cache")) {
            makeFolder("cache");
//            System.out.println("BlockNameToIDCache: Created the cache folder!");
        } /*else {
            System.out.println("BlockNameToIDCache: cache folder already exists!");
        }*/

        if (isFile(jsonLocation)) {
//            System.out.println("BlockNameToIDCache: JSON exists! Parsing!");
            try {
                processJsonNodes(new ObjectMapper().readTree(getFileString("cache/block_cache.json")));
            } catch (Exception e) {
                throw new RuntimeException("BlockNameToIDCache: Failure reading (cache/block_cache.json)! " + e);
            }
        } /*else {
            System.out.println("BlockNameToIDCache: JSON does not exist! Will create!");
        }*/
        // Else the nameToIDMap is blank!
    }

    /**
     * Nodes should ALWAYS be flat in here! If they aren't, throw an error!
     */
    private void processJsonNodes(JsonNode nodes) {
//        System.out.println("JSON BLOCK CACHE PROCESSING STARTED");

        // Crawl up the JSON tree

        Iterator<String> keys = nodes.fieldNames();

        for (Iterator<JsonNode> it = nodes.elements(); it.hasNext(); ) {

            String key = keys.next();
            JsonNode value = it.next();

            JsonNodeType type = value.getNodeType();

            duplicateCheck(key);

            if (type != JsonNodeType.NUMBER) {
                throw new RuntimeException("BlockNameToIDCache: BLOCK DEFINITION CACHE FAILURE! ID was type: (" + type + ")! Did you modify the block cache?");
            }

            double rawValue = value.asDouble();
            int numericValue = value.asInt();

            if ((int)(rawValue * 100) != numericValue * 100) {
                throw new RuntimeException("BlockNameToIDCache: BLOCK DEFINITION CACHE FAILURE! Value was floating! Did you modify the block cache?");
            }

            // Automatically tick up the free slot for the next numeric value
            if (numericValue >= nextFreeSlot) {
                nextFreeSlot = numericValue + 1;
            }

            nameToIDMap.put(key, numericValue);
        }

//        System.out.println("PARSE OUTPUT: " + nameToIDMap.toString());
    }

    public int assign(String internalName) {

        // ID 0 is reserved for air
        if (internalName.equals("air") && !containsInternalName("air")) {
            nameToIDMap.put(internalName, 0);
        }

        return getIDAssignment(internalName);
    }


    private int getIDAssignment(String internalName) {
        if (containsInternalName(internalName)) {
            return nameToIDMap.get(internalName);
        }
        final int thisID = nextFreeSlot;
        nameToIDMap.put(internalName, thisID);
        nextFreeSlot++;
        return thisID;
    }

    private boolean containsInternalName(String internalName) {
        return nameToIDMap.containsKey(internalName);
    }

    public void lock() {
//        System.out.println("BlockNameToIDCache: Updated block_cache.json!");
        System.out.println(nameToIDMap.toString());
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(makeFile(jsonLocation), nameToIDMap);
        } catch (Exception e) {
            throw new RuntimeException("BlockNameToIDCache: Failed to write the Internal Name to ID cache! " + e);
        }
    }

    private void duplicateCheck(String internalName) {
        if (nameToIDMap.containsKey(internalName)) {
            throw new RuntimeException("BlockNameToIDCache: Attempted to assign duplicate into cache! Name: (" + internalName + ")! Did you modify the block cache?");
        }
    }
}
