package org.crafter.engine.world.block;

/**
 * Works as a thread safe singleton that dispatches a clone of its internal data to worker threads.
 * Prevents having to synchronize halt on the main thread.
 */
public class BlockDefinitionContainer {

    private static BlockDefinitionContainer instance = null;

    private BlockDefinitionContainer(){}




}
