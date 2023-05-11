package org.crafter.engine.shader

import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f

/**
 * This is the public interface where all shaders live!
 * To talk to a shader, you must talk to the shader storage.
 */
object ShaderStorage {
    // Here's where all the shaders live!
    private val container = HashMap<String, Shader>()

    // Currently running shader program - Only one can be running at a time, so automate
    private var currentShader: Shader? = null

    // Create a new shader for the program
    fun createShader(shaderName: String, vertexCodeLocation: String?, fragmentCodeLocation: String?) {
        if (container.containsKey(shaderName)) {
            throw RuntimeException("ShaderStorage: Tried to add $shaderName more than once!")
        }
        container[shaderName] = Shader(shaderName, vertexCodeLocation!!, fragmentCodeLocation!!)
    }

    // Create one uniform for a shader
    fun createUniform(shaderName: String, uniformName: String?) {
        checkExistence(shaderName)
        container[shaderName]!!.createUniform(uniformName!!)
    }

    // Create multiple uniforms for a shader
    fun createUniform(shaderName: String, uniformNames: Array<String?>) {
        checkExistence(shaderName)
        if (uniformNames.isEmpty()) {
            throw RuntimeException("ShaderStorage: ERROR! You called createUniform with an empty array!")
        }
        for (name in uniformNames) {
            container[shaderName]!!.createUniform(name!!)
        }
    }

    // Matrix4f uniform setter - contextual
    fun setUniform(uniformName: String?, matrix: Matrix4f?) {
        currentShader!!.setUniform(uniformName!!, matrix!!)
    }

    // Vector3f uniform setter - contextual
    fun setUniform(uniformName: String?, vector: Vector3f?) {
        currentShader!!.setUniform(uniformName!!, vector!!)
    }

    // Vector2f uniform setter - contextual
    fun setUniform(uniformName: String?, vector: Vector2f?) {
        currentShader!!.setUniform(uniformName!!, vector!!)
    }

    // float uniform setter - contextual
    fun setUniform(uniformName: String?, value: Float) {
        currentShader!!.setUniform(uniformName!!, value)
    }

    // int uniform setter - contextual
    fun setUniform(uniformName: String?, value: Int) {
        currentShader!!.setUniform(uniformName!!, value)
    }

    // Start a shader
    @JvmStatic
    fun start(shaderName: String) {
        checkExistence(shaderName)
        currentShader = container[shaderName]
        currentShader!!.start()
    }

    // Stop the current running shader - This is probably unneeded but included for completeness
    fun stop() {
        currentShader!!.stop()
        currentShader = null
    }

    // Completely obliterates ALL shaders! ONLY call this after the main loop is finished
    fun destroyAll() {
        for (shader in container.values) {
            shader.destroy()
        }
        container.clear()
    }

    // Internal check to make sure nothing stupid is happening
    private fun checkExistence(shaderName: String) {
        if (!container.containsKey(shaderName)) {
            throw RuntimeException("ShaderStorage: Tried to access nonexistent shader ($shaderName!")
        }
    }
}
