package org.crafter.engine.api;

import org.crafter.engine.world.block.BlockDefinition;
import org.crafter.engine.world.block.BlockDefinitionContainer;
import org.crafter.engine.world.block.DrawType;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.luajit.LuaJit;

import java.util.Objects;

import static org.crafter.engine.utility.FileReader.getFileString;

public final class API {
    private static final LuaJit luaJIT = new LuaJit();

    private API(){}

    public static void initialize() {
        luaJIT.openLibraries();

        runFile("api/api.lua");

//        runCode("return crafter.getNextBlock()");

//        String output = luaJIT.toString(-1);

//        System.out.println("test: " + output);
        parseBlocks();

        runCode("crafter.closeAPI()");
    }

    private static void parseBlocks() {
        BlockDefinitionContainer container = BlockDefinitionContainer.getMainInstance();

        while (true) {

            // Grab name
            String blockName = getString("return crafter.getNextBlock()");

            if (blockName == null) {
                break;
            }

            BlockDefinition definition = new BlockDefinition(blockName);

            if (blockName.equals("air")) {
                definition.setID(0);
            }

            while (true) {

                String fieldName = getString("return crafter.getNextField()");

                if (fieldName == null) {
                    break;
                }

//                System.out.println(fieldName);

                switch (fieldName) {
                    case ("getTextures"): {
                        // String[]

                        break;
                    }
                    case("getReadableName"): {
                        // String
                        String readableName = getBlockStringField(blockName, fieldName);
                        if (readableName == null) {
                            readableName = "UNDEFINED!";
                        }
                        definition.setReadableName(readableName);
                        break;
                    }
                    case("getWalkable"): {
                        // Boolean
                        boolean walkable = getBlockBooleanField(blockName, fieldName);
                        definition.setWalkable(walkable);
                        break;
                    }
                    case("getDrawType"): {
                        // Integer
                        int drawType = getBlockIntegerField(blockName, fieldName);
                        DrawType trueDrawType = DrawType.intToDrawType(drawType);
                        definition.setDrawType(trueDrawType);
                        break;
                    }
                    case("getLiquid"): {
                        // Boolean
                        boolean liquid = getBlockBooleanField(blockName, fieldName);
                        definition.setLiquid(liquid);
                        break;
                    }
                    case("getLiquidViscosity"): {
                        // Integer
                        int liquidViscosity = getBlockIntegerField(blockName, fieldName);
                        definition.setLiquidViscosity(liquidViscosity);
                        break;
                    }
                    case("getClimbable"): {
                        // Boolean
                        boolean climbable = getBlockBooleanField(blockName, fieldName);
                        definition.setClimbable(climbable);
                        break;
                    }
                    case("getSneakJumpClimbable"): {
                        // Boolean
                        boolean sneakJumpClimbable = getBlockBooleanField(blockName, fieldName);
                        definition.setSneakJumpClimbable(sneakJumpClimbable);
                        break;
                    }
                    case("getFalling"): {
                        // Boolean
                        boolean falling = getBlockBooleanField(blockName, fieldName);
                        definition.setFalling(falling);
                        break;
                    }
                    case("getClear"): {
                        // Boolean
                        boolean clear = getBlockBooleanField(blockName, fieldName);
                        definition.setClear(clear);
                        break;
                    }
                    case("getDamagePerSecond"): {
                        // Integer
                        int damagePerSecond = getBlockIntegerField(blockName, fieldName);
                        definition.setDamagePerSecond(damagePerSecond);
                        break;
                    }
                    case("getLight"): {
                        // Integer
                        int light = getBlockIntegerField(blockName, fieldName);
                        definition.setLight(light);
                        break;
                    }
                    default: {
                        // ¯\_(ツ)_/¯
                        throw new RuntimeException("API: In-took INVALID field! https://media.tenor.com/LzbqCkSnfFcAAAAd/tommy-boy-what-did-you-do.gif");
                    }
                }


            }

//            container.addDefinition(definition);
        }

    }

    private static int getBlockIntegerField(String blockName, String fieldName) {
        return getInteger("return crafter.getBlockData('" + blockName + "','" + fieldName + "')");
    }
    private static boolean getBlockBooleanField(String blockName, String fieldName) {
        return getBoolean("return crafter.getBlockData('" + blockName + "','" + fieldName + "')");
    }
    private static String getBlockStringField(String blockName, String fieldName) {
        return getString("return crafter.getBlockData('" + blockName + "','" + fieldName + "')");
    }

    private static int getInteger(String luaCode) {
        runCode(luaCode);
        return (int)Math.round(luaJIT.toNumber(-1));
    }
    private static boolean getBoolean(String luaCode) {
        runCode(luaCode);
        return luaJIT.toBoolean(-1);
    }
    private static String getString(String luaCode) {
        runCode(luaCode);
        return luaJIT.toString(-1);
    }

    private static void runFile(String luaCodeDirectory) {
        String luaCode = getFileString(luaCodeDirectory);
        runCode(luaCode);
    }

    private static void runCode(String luaCode) {
        checkError(luaJIT.run(luaCode));
    }

    private static void checkError(Lua.LuaError error){
        if (error != Lua.LuaError.OK) {
            throw new RuntimeException(luaJIT.toString(-1));
        }
    }

    public static void destroy() {
        luaJIT.close();
    }
}