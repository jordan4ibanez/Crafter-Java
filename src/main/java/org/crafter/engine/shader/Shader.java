package org.crafter.engine.shader;

import org.crafter.engine.utility.FileReader;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.util.HashMap;

import static org.crafter.engine.utility.FileReader.getFileString;
import static org.lwjgl.opengl.GL20.*;

/**
 * This is an OpenGL Shader Program!
 * Written as concisely as possible. :)
 * You can't actually directly interface with it, you can only talk to these through ShaderStorage!
 */
class Shader {

    private final String name;

    private final int programID;

    private final HashMap<String, Integer> uniforms;

    // An easy way to create shaders
    Shader (String name, String vertexLocation, String fragmentLocation) {

        programID = glCreateProgram();

        if (programID == 0) {
            throw new RuntimeException("Shader: Failed to create shader!");
        }

        final int vertexID = compileCode(vertexLocation, GL_VERTEX_SHADER);

        final int fragmentID = compileCode(fragmentLocation, GL_FRAGMENT_SHADER);

        link(vertexID, fragmentID);

        this.name = name;

        // Now that everything is good to go, we create the uniforms map
        uniforms =  new HashMap<>();
    }

    // Matrix4f version of uniform setter
    void setUniform(String name, Matrix4f matrix) {
         // Turn this into a C float* (float[16])
         try (MemoryStack stack = MemoryStack.stackPush()){
             FloatBuffer buffer = stack.mallocFloat(16);
             matrix.get(buffer);
             glUniformMatrix4fv(uniforms.get(name), false, buffer);
         }
    }

    // Vector3f version of uniform setter
    void setUniform(String name, Vector3f vector) {
        // Turn this into C float* (float[3])
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(3);
            vector.get(buffer);
            glUniform3fv(uniforms.get(name), buffer);
        }
    }

    // Vector2f version of uniform setter
    void setUniform(String name, Vector2f vector) {
        // Turn this into C float* (float[2])
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(2);
            vector.get(buffer);
            glUniform2fv(uniforms.get(name), buffer);
        }
    }

    // float version of uniform setter
    void setUniform(String name, float value) {
        // Turn this into C float* (float)
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(1);
            //! Might NOT have to flip
            buffer.put(value).flip();
            glUniform1fv(uniforms.get(name), buffer);
        }
    }

    // int version of uniform setter
    void setUniform(String name, int value) {
        // Turn this into C int* (int)
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer buffer = stack.mallocInt(1);
            //! Might NOT have to flip
            buffer.put(value).flip();
            glUniform1iv(uniforms.get(name), buffer);
        }
    }

    void createUniform(String name) {
        int location = glGetUniformLocation(programID, name);
        if (location < 0) {
            throw new RuntimeException("Shader (" + this.name + "): Could not find uniform (" + name + ")!");
        }
        uniforms.put(name, location);
        System.out.println("Shader (" + this.name + "): created uniform (" + name + ") successfully!");
    }

    // Start the shader program
    void start() {
        glUseProgram(programID);
    }

    // Stop the shader program
    void stop() {
        glUseProgram(0);
    }

    // Now bake the whole pipeline into the program
    private void link(int vertexID, int fragmentID) {
        glLinkProgram(programID);

        if (glGetProgrami(programID, GL_LINK_STATUS) == 0) {
            //1024 max chars
            throw new RuntimeException("Shader: Error linking shader code! " + glGetProgramInfoLog(programID, 1024));
        }

        // Why would the ID ever be 0? If it's 0 something went horribly wrong
        if (vertexID != 0) {
            glDetachShader(programID, vertexID);
        }
        if (fragmentID != 0) {
            glDetachShader(programID, fragmentID);
        }

        glValidateProgram(programID);

        // Note: This should probably just crash the program if there's a validation error
        if (glGetProgrami(programID, GL_VALIDATE_STATUS) == 0) {
            System.out.println("Shader: VALIDATION WARNING! " + glGetProgramInfoLog(programID, 1024));
        }
    }

    // Turn that fancy text into machine code
     private int compileCode(String fileLocation, int shaderType) {

        String code = getFileString(fileLocation);

        int shaderID = glCreateShader(shaderType);

        if (shaderID == 0) {
            String shaderTypeString = shaderType == GL_VERTEX_SHADER ? "GL_VERTEX_SHADER" : "GL_FRAGMENT_SHADER";
            throw new RuntimeException("Shader: Failed to create shader " + shaderTypeString + " located at " + fileLocation + "!");
        }

        glShaderSource(shaderID, code);

        glCompileShader(shaderID);

        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == 0) {
            //1024 max chars
            throw new RuntimeException("Shader: Error compiling shader code! " + glGetShaderInfoLog(shaderID, 1024));
        }

        glAttachShader(programID, shaderID);

        return shaderID;
    }

    // Completely obliterates the shader program
    public void destroy() {
        stop();
        if (programID != 0) {
            glDeleteProgram(programID);
        }

    }
}
