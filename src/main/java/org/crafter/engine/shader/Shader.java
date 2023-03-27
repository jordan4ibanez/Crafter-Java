package org.crafter.engine.shader;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;

import static org.lwjgl.opengl.GL20.*;

/**
 * This is an OpenGL Shader Program!
 * Written as concisely as possible. :)
 *
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

    void createUniform(String name) {
        int location = glGetUniformLocation(programID, name);
        if (location < 0) {
            throw new RuntimeException("Shader (" + this.name + "): Could not find uniform (" + name + ")!");
        }
        uniforms.put(name, location);
        System.out.println("Shader (" + this.name + "): created uniform " + name + " successfully!");
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

    // Internal auto checker to load file
    private String getFileString(String fileLocation) {
        // Check if it exists
        File file = new File(fileLocation);
        if (!file.exists()) {
            throw new RuntimeException("Shader: File " + fileLocation + " does not exist!");
        }

        // Now try to read it
        String code;
        try {
            code = Files.readString(file.toPath());
        } catch (Exception errorString) {
            throw new RuntimeException("Shader: Failed to read file " + fileLocation + "! Error: " + errorString);
        }

        return code;
    }

    // Completely obliterates the shader program
    public void destroy() {
        stop();
        if (programID != 0) {
            glDeleteProgram(programID);
        }

    }
}
