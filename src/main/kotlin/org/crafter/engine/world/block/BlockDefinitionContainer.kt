package org.crafter.engine.world.block

import org.apache.commons.lang3.SerializationUtils
import java.io.Serializable

/**
 * Works as a thread safe singleton that dispatches a clone of its internal data to worker threads.
 * Prevents having to synchronize halt on the main thread.
 */
class BlockDefinitionContainer private constructor() : Serializable {
    // This is basically a 2 way street, name to ID, ID to name
    private val idMap: HashMap<Int, BlockDefinition>
    private val nameMap: HashMap<String?, BlockDefinition>

    // This maps the internal name into an ID automatically
    private val cache: BlockNameToIDCache

    // This is an extreme edge case to prevent the cloned objects from being mutable
    private var isClone = false

    // Keeps track of IDs - 0 is reserved for air
    private var nextID = 1

    init {
        idMap = HashMap()
        nameMap = HashMap()
        cache = BlockNameToIDCache()
    }

    fun lockCache() {
        cache.lock()
    }

    fun registerBlock(definition: BlockDefinition?) {
        if (isClone) {
            throw RuntimeException("BlockDefinitionContainer: Tried to manipulate a clone of the master object!")
        }
        if (definition == null) {
            throw RuntimeException("BlockDefinitionContainer: Tried to upload a null block definition!")
        }

        // Ensure nothing else assigned an ID into the definition
        if (definition.id != -1) {
            throw RuntimeException("BlockDefinitionContainer: Block (" + definition.internalName + ") was shipped with an existing ID before cache assignment!")
        }

        // Now automatically inject the stored ID or create a new ID from the cache
        definition.setID(cache.assign(definition.internalName))

        // Check the cache did its job
        if (definition.id == -1) {
            throw RuntimeException("BlockDefinitionContainer: Block (" + definition.internalName + ") was assigned an invalid (-1) ID from the cache!")
        }
        checkDuplicate(definition)

//        System.out.println("BlockDefinitionContainer: Added block (" + definition.getInternalName() + ") at ID (" + definition.getID() + ")");
        definition.validate()
        definition.attachFaces()

        // TODO: inject texture coordinates
        idMap[definition.id] = definition
        nameMap[definition.internalName] = definition
    }

    val allBlockNames: Array<String?>
        /**
         * Debug testing!
         */
        get() = nameMap.keys.toTypedArray<String?>()

    fun getDefinition(ID: Int): BlockDefinition? {
        checkExistence(ID)
        return idMap[ID]
    }

    fun getDefinition(name: String): BlockDefinition? {
        checkExistence(name)
        return nameMap[name]
    }

    private val thisID: Int
        private get() {
            val thisID = nextID
            nextID++
            return thisID
        }

    private fun doubleCheckData() {
        if (isEmpty) {
            throw RuntimeException(
                "BlockDefinitionContainer:" +
                        " Tried to create a clone of an empty container!"
            )
        }
        if (isUnequal) {
            throw RuntimeException(
                "BlockDefinitionContainer:" +
                        " Tried to create a clone of an UNEVEN container! Something has gone horribly wrong!"
            )
        }
    }

    private fun checkDuplicate(definition: BlockDefinition) {
        val ID = definition.id
        if (checkID(ID)) {
            throw RuntimeException(
                "BlockDefinitionContainer: " +
                        "Block (" + definition.internalName + ") contains duplicate ID of block (" +
                        (idMap[ID]?.internalName ?: "NULL") + ")!"
            )
        }
        val name = definition.internalName
        if (checkName(name)) {
            throw RuntimeException(
                "BlockDefinitionContainer" +
                        "Block: (" + name + ") contains duplicate internal name of block(" +
                        (nameMap[name]?.internalName ?: "NULL") + ")!"
            )
        }
    }

    private fun checkExistence(ID: Int) {
        if (!checkID(ID)) {
            throw RuntimeException("BlockDefinitionContainer: Tried to access undefined ID ($ID)!")
        }
    }

    private fun checkExistence(name: String) {
        if (!checkName(name)) {
            throw RuntimeException("BlockDefinitionContainer: Tried to access undefined name ($name)!")
        }
    }

    private fun checkID(ID: Int): Boolean {
        return idMap.containsKey(ID)
    }

    private fun checkName(name: String?): Boolean {
        return nameMap.containsKey(name)
    }

    private val isUnequal: Boolean
        private get() = idMap.size != nameMap.size
    private val isEmpty: Boolean
        private get() = idMap.isEmpty() || nameMap.isEmpty()

    private fun setClone() {
        isClone = true
    }

    companion object {
        private var instance: BlockDefinitionContainer? = null
        val mainInstance: BlockDefinitionContainer?
            /**
             * Only call this on the main thread when loading the game!
             * @return the master instance of the Block Definition Container.
             */
            get() {
                autoDispatch()
                return instance
            }

        @JvmStatic
        @get:Synchronized
        val threadSafeDuplicate: BlockDefinitionContainer?
            /**
             * Get a thread safe duplicate of the master instance of Block Definition Container.
             * @return A clone of the master instance of Block Definition Container.
             * WARNING! This is slow, only do this at start of game!
             */
            get() {
                if (instance == null) {
                    throw RuntimeException("BlockDefinitionContainer: Attempted to get duplicate of master object before it was created!")
                }
                instance!!.doubleCheckData()
                val clone = SerializationUtils.clone(
                    mainInstance
                )
                clone!!.setClone()
                return clone
            }

        private fun autoDispatch() {
            if (instance == null) {
                instance = BlockDefinitionContainer()
            }
        }
    }
}
