/*
 Crafter - A blocky game (engine) written in Java with LWJGL.
 Copyright (C) 2023  jordan4ibanez

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.crafter.engine.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.util.HashMap;
import java.util.Iterator;

import static org.crafter.engine.utility.FileReader.*;

/**
 * Automatically parses and stored required values from a mod.json file.
 * Will throw an error if it does not have required file & definitions.
 * Optional values do not throw errors.
 */
public class ModConfParser {

    private final String jsonLocation;
    private final HashMap<String, String> directValues;
    private final HashMap<String, String[]> arrayValues;

    public ModConfParser(String modDirectory) {
        if (modDirectory == null) {
            throw new RuntimeException("ModConfParser: Initialized with a NULL mod directory!");
        }
        if (!isFolder(modDirectory)) {
            throw new RuntimeException("ModConfParser: Initialized with an INVALID mod directory!");
        }
        jsonLocation = modDirectory + "/mod.json";
        if (!isFile(jsonLocation)) {
            throw new RuntimeException("ModConfParser: (" + modDirectory + ") MUST contain a mod.json!");
        }
        directValues = new HashMap<>();
        arrayValues = new HashMap<>();

        parseJson();
    }

    private void parseJson() {

        ObjectMapper mapper = new ObjectMapper();

        JsonNode nodes;

        try {
            nodes = mapper.readTree(getFileString(jsonLocation));
        } catch (Exception e) {
            throw new RuntimeException("Font: ERROR loading! " + e);
        }

        // Crawl up the JSON tree

        Iterator<String> keys = nodes.fieldNames();

        for (Iterator<JsonNode> it = nodes.elements(); it.hasNext(); ) {

            String key = keys.next();
            JsonNode value = it.next();

            JsonNodeType type = value.getNodeType();

            switch (key) {
                case "name" -> {
                    assert (type == JsonNodeType.STRING);
                    checkDuplicateDirectValue("name");
                    directValues.put("name", value.asText());
//                    System.out.println("got name");
                }
                case "version" -> {
                    assert (type == JsonNodeType.STRING);
                    checkDuplicateDirectValue("version");
                    directValues.put("version", value.asText());
//                    System.out.println("got version");
                }
                case "description" -> {
                    assert (type == JsonNodeType.STRING);
                    checkDuplicateDirectValue("description");
                    directValues.put("description", value.asText());
//                    System.out.println("got description");
                }
                case "dependencies" -> {
                    assert (type == JsonNodeType.ARRAY);
                    checkDuplicateArrayValue("dependencies");
                    // TODO: PUT THE ARRAY OF STRINGS HERE
                    throw new RuntimeException("ModConfParser: dependencies is not implemented!");
                }
                default -> {}
            }

        }
    }

    public boolean containsDirectValue(String key) {
        return directValues.containsKey(key);
    }
    public boolean containsArrayValue(String key) {
        arrayValueCheckExistence(key);
        return arrayValues.containsKey(key);
    }

    public String getDirectValue(String key) {
        directValueCheckExistence(key);
        return directValues.get(key);
    }
    public String[] getArrayValue(String key) {
        arrayValueCheckExistence(key);
        return arrayValues.get(key);
    }


    private void checkDuplicateDirectValue(String key) {
        if (containsDirectValue(key)) {
            throw new RuntimeException("ModConfParser: Mod configuration (" + jsonLocation + ") contains duplicate of (" + key + ")!");
        }
    }
    private void checkDuplicateArrayValue(String key) {
        if (containsArrayValue(key)) {
            throw new RuntimeException("ModConfParser: Mod configuration (" + jsonLocation + ") contains duplicate of (" + key + ")!");
        }
    }

    /**
     * Enforce running containsX for complete safety!
     */
    private void directValueCheckExistence(String key) {
        if (!directValues.containsKey(key)) {
            throw new RuntimeException("ModConfParser: Tried to parse an invalid direct value on mod configuration file (" + jsonLocation + ")! (" + key + ") does not exist.");
        }
    }
    private void arrayValueCheckExistence(String key) {
        if (!arrayValues.containsKey(key)) {
            throw new RuntimeException("ModConfParser: Tried to parse an invalid array value on mod configuration file (" + jsonLocation + ")! (" + key + ") does not exist.");
        }
    }
}
