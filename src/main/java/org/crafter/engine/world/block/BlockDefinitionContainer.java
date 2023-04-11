package org.crafter.engine.world.block;

import java.util.HashMap;

/**
 * Works as a thread safe singleton that dispatches a clone of its internal data to worker threads.
 * Prevents having to synchronize halt on the main thread.
 */
public class BlockDefinitionContainer {

    private static BlockDefinitionContainer instance = null;

    // This is basically a 2 way street, name to ID, ID to name
    final HashMap<Integer, BlockDefinition> idMap;
    final HashMap<String,BlockDefinition> nameMap;

    // This is an extreme edge case to prevent the cloned objects from being mutable
    private boolean isClone = false;

    // Keeps track of IDs
    private int nextID = 0;


    private BlockDefinitionContainer(){
        idMap = new HashMap<>();
        nameMap = new HashMap<>();
    }

    public void addDefinition(BlockDefinition definition) {
        if (isClone) {
            throw new RuntimeException("BlockDefinitionContainer: Tried to manipulate a clone of the master object!");
        }
        if (definition.getID() == -1) {
            definition.setID(getThisID());
        }
        checkDuplicate(definition);

        idMap.put(definition.getID(), definition);
        nameMap.put(definition.getInternalName(), definition);
    }

    public BlockDefinition getDefinition(int ID) {
        checkExistence(ID);
        return idMap.get(ID);
    }
    public BlockDefinition getDefinition(String name) {
        checkExistence(name);
        return nameMap.get(name);
    }

    private int getThisID() {
        final int thisID = nextID;
        nextID++;
        return thisID;
    }

    private void checkDuplicate(BlockDefinition definition) {
        final int ID = definition.getID();
        if (checkID(ID)) {
            throw new RuntimeException("BlockDefinitionContainer: " +
                    "Block (" + definition.getInternalName() +") contains duplicate ID of block (" +
                    idMap.get(ID).getInternalName() + ")!");
        }
        final String name = definition.getInternalName();
        if (checkName(name)) {
            throw new RuntimeException("BlockDefinitionContainer" +
                    "Block: (" + name + ") contains duplicate internal name of block(" +
                    nameMap.get(name).getInternalName() + ")!");
        }
    }

    private void checkExistence(int ID) {
        if (!checkID(ID)) {
            throw new RuntimeException("BlockDefinitionContainer: Tried to access undefined ID (" + ID + ")!");
        }
    }
    private void checkExistence(String name) {
        if (!checkName(name)) {
            throw new RuntimeException("BlockDefinitionContainer: Tried to access undefined name (" + name + ")!");
        }
    }

    private boolean checkID(int ID) {
        return idMap.containsKey(ID);
    }
    private boolean checkName(String name) {
        return nameMap.containsKey(name);
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
        BlockDefinitionContainer cloneOf = (BlockDefinitionContainer) instance.clone();
        cloneOf.setClone();
        return cloneOf;
    }

    private void autoDispatch() {
        if (instance == null) {
            instance = new BlockDefinitionContainer();
        }
    }

    private void setClone() {
        isClone = true;
    }

}
