package org.crafter.engine.lua;

import party.iroiro.luajava.luajit.LuaJit;


import static org.crafter.engine.utility.FileReader.getFileString;

public final class LuaAPI {
    private static final LuaJit luaJIT = new LuaJit();

    private LuaAPI(){}

    public static void initialize() {

        luaJIT.run(getFileString("lua_api/entry_point.lua"));

    }


}
