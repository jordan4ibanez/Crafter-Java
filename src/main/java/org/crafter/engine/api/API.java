package org.crafter.engine.api;

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

        runCode("return crafter.getNextBlock()");

        String output = luaJIT.toString(-1);

        System.out.println("test: " + output);
    }

    private static void parseBlocks() {
        boolean complete = false;
        while (!complete) {

            // Grab name
            runCode("return crafter.getNextBlock()");
            String output = luaJIT.toString(-1);
            System.out.println(output);

            complete = output == null;
            if (complete) {
                continue;
            }

            boolean newField = true;

            while (newField) {
                
            }
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