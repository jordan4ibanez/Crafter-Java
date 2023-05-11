package org.crafter.engine.world_generation

import org.crafter.engine.delta.DeltaObject
import org.crafter.engine.utility.FastNoise
import org.crafter.engine.world.block.BlockDefinitionContainer
import org.crafter.engine.world.block.BlockDefinitionContainer.Companion.threadSafeDuplicate
import org.crafter.engine.world.chunk.Chunk
import org.joml.Vector2i
import org.joml.Vector2ic
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean

/**
 * The Chunk Generator is basically a thread facade.
 * You talk into it through this class, but you can't talk directly to the instance.
 */
class ChunkGenerator private constructor() : Runnable {
    // Instance local
    private val delta: DeltaObject
    private val noise: FastNoise
    private val blockDefinitionContainer: BlockDefinitionContainer?
    private val chunkRequestQueue: BlockingQueue<Vector2ic>
    private val chunkOutputQueue: BlockingQueue<Chunk>
    private val shouldRun: AtomicBoolean

    init {
        delta = DeltaObject()
        noise = FastNoise()
        blockDefinitionContainer = threadSafeDuplicate
        chunkRequestQueue = LinkedBlockingQueue()
        chunkOutputQueue = LinkedBlockingQueue()
        shouldRun = AtomicBoolean(true)
    }

    override fun run() {
        println("ChunkGenerator: Started!")
        println("ChunkGenerator: gotten blocks (" + Arrays.toString(blockDefinitionContainer!!.allBlockNames) + ")!")
        while (shouldRun.get()) {
            sleepCheck()
            processInputQueue()
        }
        println("ChunkGenerator: Stopped!")
    }

    private fun processInputQueue() {
        while (!chunkRequestQueue.isEmpty()) {
            generateChunk(chunkRequestQueue.remove())
        }
    }

    private fun generateChunk(position: Vector2ic) {
        //TODO: biome registration
        //TODO: noise generation
        val chunk = processBiomesAndBlocks(Chunk(position))
        chunkOutputQueue.add(chunk)
    }

    /**
     * Actual side effects happen here!
     * This is where biomes & blocks are applied into the data container (Chunk)
     */
    private fun processBiomesAndBlocks(chunk: Chunk): Chunk {

//        Random random = new Random((int) (new Date().getTime()/1000));
        val grass = blockDefinitionContainer!!.getDefinition("crafter:grass")!!.id //"crafter:grass"
        val dirt = blockDefinitionContainer.getDefinition("crafter:dirt")!!.id //"crafter:dirt"
        val stone = blockDefinitionContainer.getDefinition("crafter:stone")!!.id //"crafter:stone"
        val xOffset = chunk.x * chunk.width
        // Y is Z in 2d!
        val zOffset = chunk.y * chunk.depth
        for (x in 0 until chunk.width) {
            for (z in 0 until chunk.depth) {
                val test = noise.getSimplex((x + xOffset).toFloat(), (z + zOffset).toFloat()) + 0.5f

//                System.out.println("test: " + test);
                val height = (test * 20.0f).toInt() + 40
                for (y in 0 until chunk.height) {
                    var id = 0 // Start off as air
                    if (y < height - 6) {
                        id = stone
                    } else if (y < height - 1) {
                        id = dirt
                    } else if (y < height) {
                        id = grass
                    }
                    val index = chunk.positionToIndex(x, y, z)
                    var blockData = chunk.getBlockData(index)
                    blockData = chunk.setBlockID(blockData, id)
                    chunk.setBlockData(index, blockData)
                }
            }
        }
        //        for (int i = 0; i < chunk.getArraySize(); i++) {
//            int blockData = chunk.getBlockData(i);
//
//
////            int randomlyChosen = random.nextInt(5);
//            int randomlyChosen = 0;
//
//            if (chunk.indexToPosition(i).y() < 30) {
//                randomlyChosen = 1;
//            }
//
//
//
//            BlockDefinition definition = blockDefinitionContainer.getDefinition(randomlyChosen); //"crafter:grass"
//            blockData = chunk.setBlockID(blockData, definition.getID());
//            chunk.setBlockData(i, blockData);
//        }
//        System.out.println("ChunkGenerator: Generated Chunk(" + chunk.getX() + ", " + chunk.getY() + ")");
        return chunk
    }

    fun checkUpdate(): Boolean {
        return !chunkOutputQueue.isEmpty()
    }

    fun grabUpdate(): Chunk {
        return chunkOutputQueue.remove()
    }

    private fun sleepCheck() {
        if (chunkRequestQueue.size == 0) {
            try {
                Thread.sleep(200)
            } catch (e: Exception) {
                throw RuntimeException("ChunkGenerator: Thread failed to sleep! $e")
            }
        }
    }

    private fun addRequest(requestedChunk: Vector2ic) {
        chunkRequestQueue.add(requestedChunk)
    }

    private fun stopThread() {
        shouldRun.set(false)
    }

    /**
     * This function is helpful in case something ever gets completely mangled.
     */
    private fun debugQueueSizes() {
        println("ChunkGenerator: (INPUT: " + chunkRequestQueue.size + ") | (OUTPUT: " + chunkOutputQueue.size + ")")
    }

    companion object {
        // Class local
        private var instance: ChunkGenerator? = null
        private var thread: Thread? = null

        // External statics from here below
        fun start() {
            if (thread != null) {
                throw RuntimeException("ChunkGenerator: Cannot start thread! It's already running!")
            }
            // Package the instance into the thread, so it can be talked to
            instance = ChunkGenerator()
            thread = Thread(instance)
            thread!!.start()
        }

        fun stop() {
            nullCheck("stop")
            instance!!.stopThread()
        }

        fun pushRequest(requestedChunk: Vector2ic?) {
            nullCheck("pushRequest")
            // Separate out thread data internal pointers
            instance!!.addRequest(Vector2i(requestedChunk))
        }

        fun hasUpdate(): Boolean {
            return instance!!.checkUpdate()
        }

        val update: Chunk
            get() {
                // This is an extremely important safety check
                if (!hasUpdate()) {
                    throw RuntimeException("ChunkGenerator: You need to check (hasUpdate) before you try to getUpdate!")
                }
                return instance!!.grabUpdate()
            }

        private fun nullCheck(methodName: String) {
            if (thread == null) {
                throw RuntimeException("ChunkGenerator: Cannot utilize method ($methodName)! The THREAD has not been instantiated!")
            }
            if (instance == null) {
                throw RuntimeException("ChunkGenerator: Cannot utilize method ($methodName)! The INSTANCE has not been instantiated!")
            }
            if (!thread!!.isAlive) {
                throw RuntimeException("ChunkGenerator: Thread has crashed! Cannot utilize ($methodName)!")
            }
        }
    }
}
