package org.crafter.engine.shader;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.HashMap;

/**
 * This is the public interface where all shaders live!
 * To talk to a shader, you must talk to the shader storage.
 */
public final class ShaderStorage {
    // Here's where all the shaders live!
    private static final HashMap<String, Shader> container = new HashMap<>();

    // Currently running shader program - Only one can be running at a time, so automate
    private static Shader currentShader = null;

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

    // Matrix4f uniform setter - contextual
    public static void setUniform(String uniformName, Matrix4f matrix) {
        currentShader.setUniform(uniformName, matrix);
    }

    // Vector3f uniform setter - contextual
    public static void setUniform(String uniformName, Vector3f vector) {
        currentShader.setUniform(uniformName, vector);
    }

    // Vector2f uniform setter - contextual
    public static void setUniform(String uniformName, Vector2f vector) {
        currentShader.setUniform(uniformName, vector);
    }

    // float uniform setter - contextual
    public static void setUniform(String uniformName, float value) {
        currentShader.setUniform(uniformName, value);
    }

    // int uniform setter - contextual
    public static void setUniform(String uniformName, int value) {
        currentShader.setUniform(uniformName, value);
    }



    // Start a shader
    public static void start(String shaderName) {
        checkExistence(shaderName);
        currentShader = container.get(shaderName);
        currentShader.start();
    }

    // Stop the current running shader - This is probably unneeded but included for completeness
    public static void stop() {
        currentShader.stop();
        currentShader = null;
    }

    // Completely obliterates ALL shaders! ONLY call this after the main loop is finished
    public static void destroyAll() {
        for (Shader shader : container.values()) {
            shader.destroy();
        }
    }


    // Internal check to make sure nothing stupid is happening
    private static void checkExistence(String shaderName) {
        if (!container.containsKey(shaderName)) {
            throw new RuntimeException("ShaderStorage: Tried to access nonexistent shader (" + shaderName + "!");
        }
    }
}
