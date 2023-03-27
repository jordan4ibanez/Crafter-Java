package org.crafter.engine.shader;

import java.util.HashMap;

public final class ShaderStorage {
    // Here's where all the shaders live!
    private static final HashMap<String, Shader> container = new HashMap<>();

    private ShaderStorage(){}

    // Create a new shader for the program
    public static void createShader(String shaderName, String vertexCodeLocation, String fragmentCodeLocation) {
        if (container.containsKey(shaderName)) {
            throw new RuntimeException("ShaderStorage: Tried to add " + shaderName + " more than once!");
        }
        container.put(shaderName, new Shader(shaderName, vertexCodeLocation, fragmentCodeLocation));
    }

    // Create one uniform for a shader
    public static void createUniform(String shaderName, String uniformName) {
        checkExistence(shaderName);
        container.get(shaderName).createUniform(uniformName);
    }

    // Create multiple uniforms for a shader
    public static void createUniform(String shaderName, String[] uniformNames) {
        checkExistence(shaderName);
        for (String name : uniformNames) {
            container.get(shaderName).createUniform(name);
        }
    }



    // Start a shader
    public static void start(String shaderName) {
        container.get(shaderName).start();
    }

    // Stop a shader
    public static void stop(String shaderName) {
        container.get(shaderName).stop();
    }

    // Completely obliterates ALL shaders! DO NOT call this before the end of the main loop
    public static void destroy() {
        container.forEach( (k, shader) -> shader.destroy() );
    }


    // Internal check to make sure nothing stupid is happening
    private static void checkExistence(String shaderName) {
        if (!container.containsKey(shaderName)) {
            throw new RuntimeException("ShaderStorage: Tried to access nonexistent shader (" + shaderName + "!");
        }
    }
}
