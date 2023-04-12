package org.crafter.engine.api;

import party.iroiro.luajava.Lua;
import party.iroiro.luajava.luajit.LuaJit;


import static org.crafter.engine.utility.FileReader.getFileString;

public final class API {
    private static final LuaJit luaJIT = new LuaJit();

    private API(){}

    public static void initialize() {
        luaJIT.openLibraries();

        runFile("api/api.lua");

    }

    private static void runFile(String fileLocation) {
        checkError(luaJIT.run(getFileString(fileLocation)));

    }

    private static void checkError(Lua.LuaError error){
        if (error != Lua.LuaError.OK) {
            throw new RuntimeException(luaJIT.toString(-1));
        }
    }
}