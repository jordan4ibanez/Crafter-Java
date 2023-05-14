package org.crafter.engine.world_generation.chunk_mesh_generation

import org.crafter.engine.world.block.BlockDefinitionContainer
import org.crafter.engine.world.block.BlockDefinitionContainer.Companion.threadSafeDuplicate
import org.crafter.engine.world.chunk.ChunkStorage.getThreadSafeChunkClone
import org.joml.Vector2i
import org.joml.Vector3i
import org.joml.Vector3ic
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean

class ChunkMeshGenerator private constructor() : Runnable {
    // Instance local
//    private val delta: DeltaObject = DeltaObject()

    // This and meshWorker share the instance of this BlockDefinitionContainer!
    private val blockDefinitionContainer: BlockDefinitionContainer = threadSafeDuplicate
    private val meshRequestQueue: BlockingQueue<Vector3ic>
    private val meshOutputQueue: BlockingQueue<ChunkMeshRecord>
    private val shouldRun: AtomicBoolean
    private val meshWorker: ChunkMeshWorker

    init {
        meshRequestQueue = LinkedBlockingQueue()
        meshOutputQueue = LinkedBlockingQueue()
        shouldRun = AtomicBoolean(true)
        // This and the meshWorker share the BlockDefinitionContainer
        meshWorker = ChunkMeshWorker(blockDefinitionContainer)
    }

    override fun run() {
        println("ChunkMeshGenerator: Started!")
        println("ChunkMeshGenerator: gotten blocks (" + blockDefinitionContainer.allBlockNames.contentToString() + ")!")
        while (shouldRun.get()) {
            sleepCheck()
            processInputQueue()
        }
        println("ChunkMeshGenerator: Stopped!")
    }

    private fun processInputQueue() {
        while (!meshRequestQueue.isEmpty()) {
            createMesh(meshRequestQueue.remove())
        }
    }

    private fun createMesh(position: Vector3ic) {
        val chunk = blockProcessingProcedure(position)
        meshOutputQueue.add(chunk)
    }

    /**
     * Actual side effects happen here!
     * This is where biomes & blocks are applied into the data container (Chunk)
     */
    private fun blockProcessingProcedure(position: Vector3ic): ChunkMeshRecord {
        val uuid = UUID.randomUUID().toString()
        val threadSafeClone = getThreadSafeChunkClone(Vector2i(position.x(), position.z()))
//        println("ChunkMeshGenerator: Processing (" + position.x() + ", " + position.z() + ") stack (" + position.y() + ")")

        // Todo: Note! Perhaps a linked list would be more performant?

        // TODO: NOTE! REUSE THIS! UTILIZE (vertices.clear();) FOR EXAMPLE!
        val positionsBuilder = ArrayList<Float>()
        val textureCoordinatesBuilder = ArrayList<Float>()
        val indicesBuilder = ArrayList<Int>()

        // Insert block builder here

        // Mutably pass the references to the arraylists into the ChunkMeshWorker so this doesn't become thousands of lines long.
        meshWorker.process(position.y(), threadSafeClone, positionsBuilder, textureCoordinatesBuilder, indicesBuilder)

        // End block builder here


        // NOTE: This is a new piece of memory, it must be a new array
        val positions = FloatArray(positionsBuilder.size)
        for (i in positions.indices) {
            positions[i] = positionsBuilder[i]
        }
        val textureCoordinates = FloatArray(textureCoordinatesBuilder.size)
        for (i in textureCoordinates.indices) {
            textureCoordinates[i] = textureCoordinatesBuilder[i]
        }
        val indices = IntArray(indicesBuilder.size)
        for (i in indices.indices) {
            indices[i] = indicesBuilder[i]
        }

        // todo: this will be created after the array builders have been filled out
        //        println("ChunkMeshGenerator: Generated Chunk(" + outputMesh.destinationChunkPosition.x() + ", " + outputMesh.destinationChunkPosition.y() + ")")
        return ChunkMeshRecord(
            uuid,
            position.y(),  // Separates the pointer internally
            Vector2i(position.x(), position.z()),
            positions,
            textureCoordinates,
            indices
        )
    }

    fun checkUpdate(): Boolean {
        return !meshOutputQueue.isEmpty()
    }

    fun grabUpdate(): ChunkMeshRecord {
        return meshOutputQueue.remove()
    }

    private fun sleepCheck() {
        if (meshRequestQueue.size == 0) {
            try {
                Thread.sleep(200)
            } catch (e: Exception) {
                throw RuntimeException("ChunkMeshGenerator: Thread failed to sleep! $e")
            }
        }
    }

    private fun addRequest(position: Vector3ic) {
        //Fixme: test if traversing the list causes severe performance penalty
//        if (meshRequestQueue.contains(position)) {
//            return;
//        }
        meshRequestQueue.add(position)
    }

    private fun stopThread() {
        shouldRun.set(false)
    }

    /**
     * This function is helpful in case something ever gets completely mangled.
     */
//    private fun debugQueueSizes() {
//        println("ChunkMeshGenerator: (INPUT: " + meshRequestQueue.size + ") | (OUTPUT: " + meshOutputQueue.size + ")")
//    }

    companion object {
        // Class local
        private var instance: ChunkMeshGenerator? = null
        private var thread: Thread? = null

        // External statics from here below
        fun start() {
            if (thread != null) {
                throw RuntimeException("ChunkMeshGenerator: Cannot start thread! It's already running!")
            }
            // Package the instance into the thread, so it can be talked to
            instance = ChunkMeshGenerator()
            thread = Thread(instance)
            thread!!.start()
        }

        fun stop() {
            nullCheck("stop")
            instance!!.stopThread()
        }

        fun pushRequest(x: Int, stack: Int, z: Int) {
            nullCheck("pushRequest")
            if (stack < 0 || stack > 7) {
                throw RuntimeException("ChunkMeshGenerator: Stack is out of bounds! Got: ($stack) | Min: 0 | Max: 7")
            }
            // Separate out thread data internal pointers
            instance!!.addRequest(Vector3i(x, stack, z))
        }

        fun hasUpdate(): Boolean {
            return instance!!.checkUpdate()
        }

        val update: ChunkMeshRecord
            get() {
                // This is an extremely important safety check
                if (!hasUpdate()) {
                    throw RuntimeException("ChunkMeshGenerator: You need to check (hasUpdate) before you try to getUpdate!")
                }
                return instance!!.grabUpdate()
            }

        private fun nullCheck(methodName: String) {
            if (thread == null) {
                throw RuntimeException("ChunkMeshGenerator: Cannot utilize method ($methodName)! The THREAD has not been instantiated!")
            }
            if (instance == null) {
                throw RuntimeException("ChunkMeshGenerator: Cannot utilize method ($methodName)! The INSTANCE has not been instantiated!")
            }
            if (!thread!!.isAlive) {
                throw RuntimeException("ChunkMeshGenerator: Thread has crashed! Cannot utilize ($methodName)!")
            }
        }
    }
}
