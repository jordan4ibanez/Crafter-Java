package org.crafter.engine.api;

import java.util.HashMap;

import static org.crafter.engine.utility.FileReader.isFile;
import static org.crafter.engine.utility.FileReader.isFolder;

/**
 * Automatically parses and stored required values from a mod.json file.
 * Will throw an error if it does not have required file & definitions.
 * Optional values do not throw errors.
 */
public class ModConfParser {

    private final String jsonLocation;
    HashMap<String, String> directValues;
    HashMap<String, String[]> arrayValues;

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

        /**
         * TODO: Check checkDuplicateDirectValue when parsing singular values
         * TODO: Check checkDuplicateArrayValue when parsing array values (like dependencies)
         */
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
