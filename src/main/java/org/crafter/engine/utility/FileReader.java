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
package org.crafter.engine.utility;

import java.io.File;
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
