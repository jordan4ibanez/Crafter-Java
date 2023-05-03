package org.crafter.engine.api;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.Eval;

import static org.crafter.engine.utility.FileReader.getFileString;

/**
 * The API handling for the game.
 * Groovy.
 */
public final class API {

    private API(){}

    // All dynamic data lives here :)
    private static Binding sharedData;
    private static GroovyShell shell;

    public static void initialize() {

        sharedData = new Binding();
        shell = new GroovyShell(sharedData);


        System.out.println("hi there, I'm the API");

        shell.evaluate(getFileString("api/api.groovy"));
        shell.evaluate(getFileString("mods/crafter/main.groovy"));
    }


}
