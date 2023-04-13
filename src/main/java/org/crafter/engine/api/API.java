package org.crafter.engine.api;

import org.crafter.engine.world.block.BlockDefinition;
import org.crafter.engine.world.block.BlockDefinitionContainer;
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
    }

    private static void parseBlocks() {
        BlockDefinitionContainer container = BlockDefinitionContainer.getMainInstance();

        while (true) {

            // Grab name
            runCode("return crafter.getNextBlock()");
            String blockName = luaJIT.toString(-1);

            if (blockName == null) {
                break;
            }

            BlockDefinition definition = new BlockDefinition(blockName);

            while (true) {

                runCode("return crafter.getNextField()");
                String fieldName = luaJIT.toString(-1);

                if (fieldName == null) {
                    break;
                }

                System.out.println(fieldName);
                switch (fieldName) {
                    case ("getInternalName"): {
                        // String

                    }
                    case ("getTextures"): {
                        // String[]

                    }
                    case("getReadableName"): {
                        // String

                    }
                    case("getWalkable"): {
                        // Boolean

                    }
                    case("getDrawType"): {
                        // Integer

                    }
                    case("getLiquid"): {
                        // Boolean

                    }
                    case("getLiquidViscosity"): {
                        // Integer

                    }
                    case("getClimbable"): {
                        // Boolean

                    }
                    case("getSneakJumpClimbable"): {
                        // Boolean

                    }
                    case("getFalling"): {
                        // Boolean

                    }
                    case("getClear"): {
                        // Boolean

                    }
                    case("getDamagePerSecond"): {
                        // Integer

                    }
                    case("getLight"): {
                        // Integer

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