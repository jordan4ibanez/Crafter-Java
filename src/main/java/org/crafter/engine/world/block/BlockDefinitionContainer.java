package org.crafter.engine.world.block;

import java.util.HashMap;

/**
 * Works as a thread safe singleton that dispatches a clone of its internal data to worker threads.
 * Prevents having to synchronize halt on the main thread.
 */
public class BlockDefinitionContainer {

    private static BlockDefinitionContainer instance = null;

    final HashMap<Integer, BlockDefinition> idMap;
    final HashMap<String,BlockDefinition> nameMap;


    private BlockDefinitionContainer(){
        idMap = new HashMap<>();
        nameMap = new HashMap<>();
    }

    void addDefinition(BlockDefinition newDefinition) {

    }


    /**
     * Only call this on the main thread when loading the game!
     * @return the master instance of the Block Definition Container.
     */
    public static BlockDefinitionContainer getMainInstance() {
        return instance;
    }

    /**
     * Get a thread safe duplicate of the master instance of Block Definition Container.
     * @return A clone of the master instance of Block Definition Container.
     * @throws CloneNotSupportedException Should always be supported.
     */
    public static synchronized BlockDefinitionContainer getThreadSafeDuplicate() throws CloneNotSupportedException {
        return (BlockDefinitionContainer) instance.clone();
    }

    private void autoDispatch() {
        if (instance == null) {
            instance = new BlockDefinitionContainer();
        }
    }


}
