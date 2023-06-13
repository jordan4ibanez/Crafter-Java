package org.crafter.engine.world.block;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Works as a thread safe singleton that dispatches a clone of its internal data to worker threads.
 * Prevents having to synchronize halt on the main thread.
 */
public class BlockDefinitionContainer implements Serializable {

    private static BlockDefinitionContainer instance = null;

    // This is basically a 2 way street, name to ID, ID to name
    private final HashMap<Integer, BlockDefinition> idMap;
    private final HashMap<String,BlockDefinition> nameMap;

    // This maps the internal name into an ID automatically
    private final BlockNameToIDCache cache;


    // This is an extreme edge case to prevent the cloned objects from being mutable
    private boolean isClone = false;

    // Keeps track of IDs - 0 is reserved for air
    private int nextID = 1;

    private BlockDefinitionContainer(){
        idMap = new HashMap<>();
        nameMap = new HashMap<>();
        cache = new BlockNameToIDCache();
    }

    public void lockCache() {
        cache.lock();
    }

    public void registerBlock(BlockDefinition definition) {
        if (isClone) {
            throw new RuntimeException("BlockDefinitionContainer: Tried to manipulate a clone of the master object!");
        }
        if (definition == null) {
            throw new RuntimeException("BlockDefinitionContainer: Tried to upload a null block definition!");
        }

        // Ensure nothing else assigned an ID into the definition
        if (definition.getID() != -1) {
            throw new RuntimeException("BlockDefinitionContainer: Block (" + definition.getInternalName() + ") was shipped with an existing ID before cache assignment!");
        }

        // Now automatically inject the stored ID or create a new ID from the cache
        definition.setID(cache.assign(definition.getInternalName()));

        // Check the cache did its job
        if (definition.getID() == -1) {
            throw new RuntimeException("BlockDefinitionContainer: Block (" + definition.getInternalName() + ") was assigned an invalid (-1) ID from the cache!");
        }

        checkDuplicate(definition);

//        System.out.println("BlockDefinitionContainer: Added block (" + definition.getInternalName() + ") at ID (" + definition.getID() + ")");

        definition.validate();

        definition.attachFaces();

        // TODO: inject texture coordinates
        idMap.put(definition.getID(), definition);
        nameMap.put(definition.getInternalName(), definition);
    }

    /**
     * Debug testing!
     */
    public String[] getAllBlockNames() {
        return nameMap.keySet().toArray(new String[0]);
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

    private void doubleCheckData() {
        if (isEmpty()) {
            throw new RuntimeException("BlockDefinitionContainer:" +
                    " Tried to create a clone of an empty container!");
        }
        if (isUnequal()) {
            throw new RuntimeException("BlockDefinitionContainer:" +
                    " Tried to create a clone of an UNEVEN container! Something has gone horribly wrong!");
        }
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

    private boolean isUnequal() {
        return idMap.size() != nameMap.size();
    }
    private boolean isEmpty() {
        return idMap.isEmpty() || nameMap.isEmpty();
    }

    /**
     * Only call this on the main thread when loading the game!
     * @return the master instance of the Block Definition Container.
     */
    public static BlockDefinitionContainer getMainInstance() {
        autoDispatch();
        return instance;
    }

    /**
     * Get a thread safe duplicate of the master instance of Block Definition Container.
     * @return A clone of the master instance of Block Definition Container.
     * WARNING! This is slow, only do this at start of game!
     */
    public static synchronized BlockDefinitionContainer getThreadSafeDuplicate() {
        if (instance == null) {
            throw new RuntimeException("BlockDefinitionContainer: Attempted to get duplicate of master object before it was created!");
        }
        instance.doubleCheckData();

        BlockDefinitionContainer clone = SerializationUtils.clone(getMainInstance());
        clone.setClone();
        return clone;
    }

    private static void autoDispatch() {
        if (instance == null) {
            instance = new BlockDefinitionContainer();
        }
    }

    private void setClone() {
        isClone = true;
    }

}
