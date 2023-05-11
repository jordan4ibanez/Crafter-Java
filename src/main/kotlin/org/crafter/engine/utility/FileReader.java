package org.crafter.engine.utility;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;

public final class FileReader {

    private FileReader(){}

    // Loads a raw file into a usable string
    public static String getFileString(String fileLocation) {
        // Check if it exists
        File file = grabFile(fileLocation);

        // Now try to read it
        String data;
        try {
            data = Files.readString(file.toPath());
        } catch (Exception errorString) {
            throw new RuntimeException("FileReader: Failed to read file (" + fileLocation + ")! Error: " + errorString);
        }

        return data;
    }

    /**
     * You can combine getFolderList and getFileList to dance through a directory!
     */
    public static String[] getFolderList(String folderLocation) {

        File file = grabFile(folderLocation);

        if (!file.isDirectory()) {
            throw new RuntimeException("FileReader: Folder (" + folderLocation + ") is not a directory!");
        }

        return file.list((current, name) -> new File(current, name).isDirectory());
    }

    public static String[] getFileList(String folderLocation) {
        File file = grabFile(folderLocation);

        if (!file.isDirectory()) {
            throw new RuntimeException("FileReader: Folder (" + folderLocation + ") is not a directory!");
        }

        return file.list((current, name) -> new File(current, name).isFile());
    }

    private static File grabFile(String fileLocation) {
        File file = new File(fileLocation);
        if (!file.exists()) {
            throw new RuntimeException("FileReader: File (" + fileLocation + ") does not exist!");
        }
        return file;
    }

    /**
     * Little helper methods.
     */
    public static boolean isFolder(String folderLocation) {
        return new File(folderLocation).isDirectory();
    }
    public static boolean isFile(String fileLocation) {
        return new File(fileLocation).isFile();
    }

    public static boolean makeFolder(String folderLocation) {
        return new File(folderLocation).mkdir();
    }

    public static File makeFile(String fileLocation) {
        return new File(fileLocation);
    }
}
