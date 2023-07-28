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

/*
TODO: Figure out more things like mobs & items & entities
TODO! VERY IMPORTANT! Add comments! See if there is a way to link the java documentation into javascript....somehow?
*/

/*
API is the base of the API which loads up class variables into the crafter array.
This allows for mods to index DIRECTLY into the engine without needing to be compiled.
Limitations: Ecmascript 6. Class architecture is not implemented into nashorn yet.
Possible implementations: Typescript (one day)
*/

// Lua equivalents!
//
const dofile = Java.type("org.crafter.engine.api.API").runFile;
const readFileToString = Java.type("org.crafter.engine.utility.FileReader").getFileString;
const math = Java.type("org.joml.Math");

// Global java types assignment
//
const BlockDefinition = Java.type("org.crafter.engine.world.block.BlockDefinition");
const DrawType = Java.type("org.crafter.engine.world.block.DrawType");
const BiomeDefinition = Java.type("org.crafter.engine.world.biome.BiomeDefinition");

// Script actions
const OnJoin = Java.type("org.crafter.engine.api.actions.on_join.OnJoin");
const OnTick = Java.type("org.crafter.engine.api.actions.on_tick.OnTick");
const OnTimer = Java.type("org.crafter.engine.api.actions.on_timer.OnTimer");

// JOML types
//
const Vector2f = Java.type("org.joml.Vector2f");
const Vector3f = Java.type("org.joml.Vector3f");
const Vector2i = Java.type("org.joml.Vector2i");
const Vector3i = Java.type("org.joml.Vector3i");
// Readonly
const Vector2fc = Java.type("org.joml.Vector2fc");
const Vector3fc = Java.type("org.joml.Vector3fc");
const Vector2ic = Java.type("org.joml.Vector2ic");
const Vector3ic = Java.type("org.joml.Vector3ic");

// Block Manipulator gets it's own table for minor performance and clarity.
const blockManipulator = [];

// Very similar to minetest's api table, basically a clone of it in JS.
const crafter = [];

// Similar component to minetest's block manipulator data worker, but integrated into it's own table.
const blockData = []

// Auto executing lambda localized variable scope discards
!function(){

    // Classes from the engine which will disappear after this scope.
    const BlockDefinitionContainer = Java.type("org.crafter.engine.world.block.BlockDefinitionContainer");
    const BiomeDefinitionContainer = Java.type("org.crafter.engine.world.biome.BiomeDefinitionContainer");
    const API = Java.type("org.crafter.engine.api.API");
    const ChunkStorage = Java.type("org.crafter.engine.world.chunk.ChunkStorage");
    const ActionStorage = Java.type("org.crafter.engine.api.ActionStorage");
    const PlayerStorage = Java.type("org.crafter.game.entity.player.PlayerStorage");
    const Chunk = Java.type("org.crafter.engine.world.chunk.Chunk");

    // Javascript level Block Definition registration function.
    // This is why I avoid singletons, cannot reduce this.
    crafter.registerBlock = function(newBlockDefinition) {
        BlockDefinitionContainer.getMainInstance().registerBlock(newBlockDefinition);
    }

    // Javascript level Biome Definition registration function.
    // This is why I avoid singletons, cannot reduce this.
    crafter.registerBiome = function(newBiomeDefinition) {
        BiomeDefinitionContainer.getMainInstance().registerBiome(newBiomeDefinition);
    }

    // Will get the mod directory of the current mod.
    // Returns: String
    crafter.getCurrentModDirectory = API.getCurrentModDirectory;

    // Gets if a chunk is loaded in a position in the world.
    // Parameters: [x,y,z or Vector3f]
    // Returns: boolean
    crafter.chunkIsLoaded = ChunkStorage.chunkIsLoaded;

    // Gets an iterable collection of all players currently online.
    // Returns: Collection<Player>
    crafter.getConnectedPlayers = PlayerStorage.getConnectedPlayers;

    //fixme ----- BEGIN SINGLE BLOCK API -----

    // Get a block's RAW data using a raw in world position. (Using this in bulk can be very expensive)
    // ONLY USE THIS IF YOU KNOW WHAT YOU ARE DOING!
    // Parameters: [x,y,z or Vector3f]
    // Returns: integer
    crafter.getBlockRAW = ChunkStorage.getBlockRAW;

    // Get a block's internal name using a raw in world position. (Using this in bulk can be very expensive)
    // Parameters: [x,y,z or Vector3f]
    // Returns: String
    crafter.getBlockName = ChunkStorage.getBlockName;

    // Get a block's ID using a raw in world position. (Using this in bulk can be very expensive)
    // Parameters: [x,y,z or Vector3f]
    // Returns: integer
    crafter.getBlockID = ChunkStorage.getBlockID;

    // Get a block's light level using a raw in world position. (Using this in bulk can be very expensive)
    // Parameters: [x,y,z or Vector3f]
    // Returns: integer
    crafter.getBlockLightLevel = ChunkStorage.getBlockLightLevel;

    // Get a block's state using a raw in world position. (Using this in bulk can be very expensive)
    // Parameters: [x,y,z or Vector3f]
    // Returns: integer
    crafter.getBlockState = ChunkStorage.getBlockState;

    // Set a block's RAW data using a raw in world position. (Using this in bulk can be very expensive)
    // ONLY USE THIS IF YOU KNOW WHAT YOU ARE DOING!
    // Parameters: [x,y,z or Vector3f] [integer]
    crafter.setBlockRAW = ChunkStorage.setBlockRAW;

    // Set a block's ID with the internal name of the block using a raw in world position. (Using this in bulk can be very expensive)
    // Parameters: [x,y,z or Vector3f] [String]
    crafter.setBlockName = ChunkStorage.setBlockName;

    // Set a block's ID using a raw in world position. (Using this in bulk can be very expensive)
    // Parameters: [x,y,z or Vector3f] [integer]
    crafter.setBlockID = ChunkStorage.setBlockID;

    // Set a block's light level using a raw in world position. (Using this in bulk can be very expensive)
    // Parameters: [x,y,z or Vector3f] [integer]
    crafter.setBlockLightLevel = ChunkStorage.setBlockLightLevel;

    // Set a block's state using a raw in world position. (Using this in bulk can be very expensive)
    // Parameters: [x,y,z or Vector3f] [integer]
    crafter.setBlockState = ChunkStorage.setBlockState;

    //fixme ----- BEGIN ACTIONS API -----

    // Run some logic when a player joins the game.
    // Parameters: [OnJoin]
    crafter.registerOnJoin = ActionStorage.registerOnJoin;

    // Run some logic every game tick.
    // Parameters: [OnTick]
    crafter.registerOnTick = ActionStorage.registerOnTick;

    // Run some logic at X second intervals. Or, execute it after X seconds if repeat is off.
    // Parameters:
    // [float] interval OR delay. (depends if repeat is on)
    // [boolean] Repeat. If true, this function will run every X seconds. If false, it will run once, then be deleted.
    // [OnTimer] OnTimer function.
    crafter.registerOnTimer = ActionStorage.registerOnTimer;

    //fixme ---- BEGIN BLOCK MANIPULATOR API ----

    // Set the min and max positions of the Block Manipulator.
    // Parameters: [Vector3i] [Vector3i] OR  min: [int] [int] [int] max: [int] [int] [int]
    blockManipulator.setPositions = ChunkStorage.setBlockManipulatorPositions;

    // Make the block manipulator read the map.
    blockManipulator.readData = ChunkStorage.blockManipulatorReadData;

    // Get RAW data from the Block Manipulator.
    // Parameters: [Vector3i] OR [int] [int] [int]
    blockManipulator.getData = ChunkStorage.getBlockManipulatorData;

    // Set RAW data into the Block Manipulator.
    // Parameters: [Vector3i] [int] OR [int] [int] [int] [int]
    blockManipulator.setData = ChunkStorage.setBlockManipulatorData;

    // Write the Block Manipulator's data into the map.
    blockManipulator.writeData = ChunkStorage.writeManipulatorWriteData;

    //fixme ----- BEGIN BLOCK DATA MANIPULATOR API -----

    // Get the Block ID from raw Block data.
    // Parameters: [int] Raw block data.
    // Returns: [int] Block ID.
    blockData.getID = Chunk.getBlockID;

    // Get the Block light level from raw Block data.
    // Parameters: [int] Raw block data.
    // Returns: [int] Block light level.
    blockData.getLightLevel = Chunk.getBlockLightLevel;

    // Get the Block state from raw Block data.
    // Parameters: [int] Raw block data.
    // Returns: [int] Block state.
    blockData.getState = Chunk.getBlockState;

    // Set the Block ID into raw Block data.
    // Parameters: [int] Raw data [int] New Block ID.
    // Returns: [int] Manipulated raw Block data.
    blockData.setID = Chunk.setBlockID;

    // Set the Block light level into raw Block data.
    // Parameters: [int] Raw data [int] New Block light level.
    // Returns: [int] Manipulated raw Block data.
    blockData.setLightLevel = Chunk.setBlockLightLevel;

    // Set the Block state into raw Block data.
    // Parameters: [int] Raw data [int] New Block state.
    // Returns: [int] Manipulated raw Block data.
    blockData.setState = Chunk.setBlockState;

}()

// Air is reserved here
crafter.registerBlock(
    new BlockDefinition("air")
        .setDrawType(DrawType.AIR)
        .setWalkable(false)
        .setReadableName("Air")
);