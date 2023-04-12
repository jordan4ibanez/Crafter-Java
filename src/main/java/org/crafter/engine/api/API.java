package org.crafter.engine.api;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.Eval;
import org.crafter.engine.world.block.BlockDefinitionContainer;

import java.io.File;
import java.io.IOException;

public final class API {

    private static final Binding sharedData = new Binding();
    private static final GroovyShell shell = new GroovyShell(sharedData);

    private API(){}

    public static void initializeAPI() {

        sharedData.setProperty("BlockDefinitionContainer", BlockDefinitionContainer.getMainInstance());

        try {
            shell.run(new File("api/api.groovy"), new String[]{""});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
