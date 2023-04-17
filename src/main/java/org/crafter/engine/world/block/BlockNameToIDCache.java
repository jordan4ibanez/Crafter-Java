package org.crafter.engine.world.block;

import java.io.Serializable;
import java.util.HashMap;

import static org.crafter.engine.utility.FileReader.isFolder;
import static org.crafter.engine.utility.FileReader.makeFolder;

public class BlockNameToIDCache implements Serializable {
    private final HashMap<String, Integer> mapper;

    public BlockNameToIDCache() {
        if (!isFolder("cache")) {
            makeFolder("cache");
            System.out.println("BlockNameToIDCache: Created the cache folder!");
        } else {
            System.out.println("BlockNameToIDCache: cache folder already exists!");
        }
        mapper = new HashMap<>();
    }
}
