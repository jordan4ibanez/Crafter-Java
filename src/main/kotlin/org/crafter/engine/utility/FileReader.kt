package org.crafter.engine.utility

import java.io.File
import java.nio.file.Files

object FileReader {
    // Loads a raw file into a usable string
    @JvmStatic
    fun getFileString(fileLocation: String): String {
        // Check if it exists
        val file = grabFile(fileLocation)

        // Now try to read it
        val data: String = try {
            Files.readString(file.toPath())
        } catch (errorString: Exception) {
            throw RuntimeException("FileReader: Failed to read file ($fileLocation)! Error: $errorString")
        }
        return data
    }

    /**
     * You can combine getFolderList and getFileList to dance through a directory!
     */
    fun getFolderList(folderLocation: String): Array<out String>? {
        val file = grabFile(folderLocation)
        if (!file.isDirectory) {
            throw RuntimeException("FileReader: Folder ($folderLocation) is not a directory!")
        }
        return file.list { current: File?, name: String? -> File(current, name!!).isDirectory }
    }

    fun getFileList(folderLocation: String): Array<out String> {
        val file = grabFile(folderLocation)
        if (!file.isDirectory) {
            throw RuntimeException("FileReader: Folder ($folderLocation) is not a directory!")
        }
        return file.list { current: File?, name: String? -> File(current, name!!).isFile }!!
    }

    private fun grabFile(fileLocation: String): File {
        val file = File(fileLocation)
        if (!file.exists()) {
            throw RuntimeException("FileReader: File ($fileLocation) does not exist!")
        }
        return file
    }

    /**
     * Little helper methods.
     */
    @JvmStatic
    fun isFolder(folderLocation: String): Boolean {
        return File(folderLocation).isDirectory
    }

    @JvmStatic
    fun isFile(fileLocation: String): Boolean {
        return File(fileLocation).isFile
    }

    @JvmStatic
    fun makeFolder(folderLocation: String?): Boolean {
        return File(folderLocation).mkdir()
    }

    @JvmStatic
    fun makeFile(fileLocation: String?): File {
        return File(fileLocation)
    }
}
