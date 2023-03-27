package org.crafter.engine.shader;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.HashMap;

/**
 * This is the public interface where all shaders live!
 *
 * To talk to a shader, you must talk to the shader storage.
 */
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

    // Matrix4f uniform setter
    public static void setUniform(String shaderName, String uniformName, Matrix4f matrix) {
        checkExistence(shaderName);
        container.get(shaderName).setUniform(uniformName, matrix);
    }

    // Vector3f uniform setter
    public static void setUniform(String shaderName, String uniformName, Vector3f vector) {
        checkExistence(shaderName);
        container.get(shaderName).setUniform(uniformName, vector);
    }

    // Vector2f uniform setter
    public static void setUniform(String shaderName, String uniformName, Vector2f vector) {
        checkExistence(shaderName);
        container.get(shaderName).setUniform(uniformName, vector);
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

    // Completely obliterates ALL shaders! ONLY call this after the main loop is finished
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
