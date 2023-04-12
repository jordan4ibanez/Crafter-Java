package org.crafter.engine.api;

import party.iroiro.luajava.Lua;
import party.iroiro.luajava.luajit.LuaJit;

import static org.crafter.engine.utility.FileReader.getFileString;

public final class API {
    private static final LuaJit luaJIT = new LuaJit();

    private API(){}

    public static void initialize() {
        luaJIT.openLibraries();

//        luaJIT.push("string test");
//        luaJIT.setGlobal("crafter");
//        run("assert(crafter == 'string test')");

        run("api/api.lua");

        luaJIT.getGlobal("crafter");
        luaJIT.getField(-1, "test");
        double test = luaJIT.toNumber(-1);
        System.out.println("crafter.test is: " + test);

//        luaJIT.getField(-1, );

//
//        luaJIT.getGlobal("crafter");
//
//        String test = luaJIT.toString(-1);

//        System.out.println(test);

    }

    private static void run(String luaCodeDirectory) {
        String luaCode = getFileString(luaCodeDirectory);
        checkError(luaJIT.run(luaCode));
    }

    private static void runFile(String fileLocation) {
        checkError(luaJIT.run(getFileString(fileLocation)));

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