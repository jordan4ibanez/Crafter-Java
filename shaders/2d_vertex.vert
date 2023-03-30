#version 410 core

// Frag is for tri positions.
// This is just your standard old glsl shader.

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textureCoordinate;

out vec2 outputTextureCoordinate;

uniform mat4 cameraMatrix;
uniform mat4 objectMatrix;

void main() {

    // Position in world without camera matrix application
    vec4 objectPosition = vec4(position, 1.0);

    // Position in world relative to camera
    vec4 cameraPosition = objectMatrix * objectPosition;

    // Output real coordinates into gpu
    gl_Position = cameraMatrix * cameraPosition;

    outputTextureCoordinate = textureCoordinate;
}