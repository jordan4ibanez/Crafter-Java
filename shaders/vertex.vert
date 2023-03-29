#version 410 core

// Frag is for tri positions.
// This is just your standard old glsl shader.

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textureCoordinate;

out vec2 outputTextureCoordinate;

//uniform mat4 cameraMatrix;
//uniform mat4 objectMatrix;

void main() {

    // cameraMatrix * objectMatrix *

    // Output real coordinates into gpu
    gl_Position = vec4(position, 1.0);

    outputTextureCoordinate = textureCoordinate;
}
