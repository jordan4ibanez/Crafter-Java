package org.crafter.engine.shader;

import java.util.HashMap;

public final class ShaderStorage {
    // Here's where all the shaders live!
    private static final HashMap<String, Shader> container = new HashMap<String, Shader>();

    private ShaderStorage(){}
}
