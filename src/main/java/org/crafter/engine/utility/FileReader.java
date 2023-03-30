package org.crafter.engine.utility;

import java.io.File;
import java.nio.file.Files;

public class FileReader {

    private FileReader(){}

    // Loads a raw file into a usable string
    public static String getFileString(String fileLocation) {
        // Check if it exists
        File file = new File(fileLocation);
        if (!file.exists()) {
            throw new RuntimeException("Shader: File " + fileLocation + " does not exist!");
        }

        // Now try to read it
        String data;
        try {
            data = Files.readString(file.toPath());
        } catch (Exception errorString) {
            throw new RuntimeException("FileReader: Failed to read file " + fileLocation + "! Error: " + errorString);
        }

        return data;
    }
}
