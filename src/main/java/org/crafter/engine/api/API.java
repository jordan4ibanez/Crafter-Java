package org.crafter.engine.api;

import party.iroiro.luajava.luajit.LuaJit;


import static org.crafter.engine.utility.FileReader.getFileString;

public final class API {
    private static final LuaJit luaJIT = new LuaJit();

    private API(){}

    public static void initialize() {

        luaJIT.run(getFileString("api/api.lua"));

    }


}