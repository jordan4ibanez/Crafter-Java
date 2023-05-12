package org.crafter.engine.shader

import org.crafter.engine.utility.FileUtility
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.opengl.GL20
import org.lwjgl.system.MemoryStack

/**
 * This is an OpenGL Shader Program!
 * Written as concisely as possible. :)
 * You can't actually directly interface with it, you can only talk to these through ShaderStorage!
 */
internal class Shader(name: String, vertexLocation: String, fragmentLocation: String) {
    private val name: String
    private val programID: Int
    private val uniforms: HashMap<String, Int>

    // An easy way to create shaders
    init {
        programID = GL20.glCreateProgram()
        if (programID == 0) {
            throw RuntimeException("Shader: Failed to create shader!")
        }
        val vertexID = compileCode(vertexLocation, GL20.GL_VERTEX_SHADER)
        val fragmentID = compileCode(fragmentLocation, GL20.GL_FRAGMENT_SHADER)
        link(vertexID, fragmentID)
        this.name = name

        // Now that everything is good to go, we create the uniforms map
        uniforms = HashMap()
    }

    // Matrix4f version of uniform setter
    fun setUniform(name: String, matrix: Matrix4f) {
        // Turn this into a C float* (float[16])
        MemoryStack.stackPush().use { stack ->
            val buffer = stack.mallocFloat(16)
            matrix[buffer]
            GL20.glUniformMatrix4fv(uniforms[name]!!, false, buffer)
        }
    }

    // Vector3f version of uniform setter
    fun setUniform(name: String, vector: Vector3f) {
        // Turn this into C float* (float[3])
        MemoryStack.stackPush().use { stack ->
            val buffer = stack.mallocFloat(3)
            vector[buffer]
            GL20.glUniform3fv(uniforms[name]!!, buffer)
        }
    }

    // Vector2f version of uniform setter
    fun setUniform(name: String, vector: Vector2f) {
        // Turn this into C float* (float[2])
        MemoryStack.stackPush().use { stack ->
            val buffer = stack.mallocFloat(2)
            vector[buffer]
            GL20.glUniform2fv(uniforms[name]!!, buffer)
        }
    }

    // float version of uniform setter
    fun setUniform(name: String, value: Float) {
        // Turn this into C float* (float)
        MemoryStack.stackPush().use { stack ->
            val buffer = stack.mallocFloat(1)
            //! Might NOT have to flip
            buffer.put(value).flip()
            GL20.glUniform1fv(uniforms[name]!!, buffer)
        }
    }

    // int version of uniform setter
    fun setUniform(name: String, value: Int) {
        // Turn this into C int* (int)
        MemoryStack.stackPush().use { stack ->
            val buffer = stack.mallocInt(1)
            //! Might NOT have to flip
            buffer.put(value).flip()
            GL20.glUniform1iv(uniforms[name]!!, buffer)
        }
    }

    fun createUniform(name: String) {
        val location = GL20.glGetUniformLocation(programID, name)
        if (location < 0) {
            throw RuntimeException("Shader (" + this.name + "): Could not find uniform (" + name + ")!")
        }
        uniforms[name] = location
        //        System.out.println("Shader (" + this.name + "): created uniform (" + name + ") successfully!");
    }

    // Start the shader program
    fun start() {
        GL20.glUseProgram(programID)
    }

    // Stop the shader program
    fun stop() {
        GL20.glUseProgram(0)
    }

    // Now bake the whole pipeline into the program
    private fun link(vertexID: Int, fragmentID: Int) {
        GL20.glLinkProgram(programID)
        if (GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == 0) {
            //1024 max chars
            throw RuntimeException("Shader: Error linking shader code! " + GL20.glGetProgramInfoLog(programID, 1024))
        }

        // Why would the ID ever be 0? If it's 0 something went horribly wrong
        if (vertexID != 0) {
            GL20.glDetachShader(programID, vertexID)
        }
        if (fragmentID != 0) {
            GL20.glDetachShader(programID, fragmentID)
        }
        GL20.glValidateProgram(programID)

        // Note: This should probably just crash the program if there's a validation error
        if (GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == 0) {
            println("Shader: VALIDATION WARNING! " + GL20.glGetProgramInfoLog(programID, 1024))
        }
    }

    // Turn that fancy text into machine code
    private fun compileCode(fileLocation: String, shaderType: Int): Int {
        val code = FileUtility.getFileString(fileLocation)
        val shaderID = GL20.glCreateShader(shaderType)
        if (shaderID == 0) {
            val shaderTypeString = if (shaderType == GL20.GL_VERTEX_SHADER) "GL_VERTEX_SHADER" else "GL_FRAGMENT_SHADER"
            throw RuntimeException("Shader: Failed to create shader $shaderTypeString located at $fileLocation!")
        }
        GL20.glShaderSource(shaderID, code)
        GL20.glCompileShader(shaderID)
        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == 0) {
            //1024 max chars
            throw RuntimeException("Shader: Error compiling shader code! " + GL20.glGetShaderInfoLog(shaderID, 1024))
        }
        GL20.glAttachShader(programID, shaderID)
        return shaderID
    }

    // Completely obliterates the shader program
    fun destroy() {
        stop()
        if (programID != 0) {
            GL20.glDeleteProgram(programID)
        }
    }
}
