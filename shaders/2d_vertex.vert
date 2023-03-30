#version 410 core

// Frag is for tri positions.
// This is a bit fancier.

layout (location = 0) in vec2 position;
layout (location = 1) in vec2 textureCoordinate;
// Notice: This is too big to be a uniform! Needs to be baked in.
// We create and destroy text vao every frame anyways.
layout (location = 2) in vec4 color;

out vec2 outputTextureCoordinate;
out vec4 newColoring;

uniform mat4 cameraMatrix;
uniform mat4 objectMatrix;

void main() {

    // Position in world without camera matrix application
    vec4 objectPosition = vec4(position.x, position.y, 0.0, 1.0);

    // Position in world relative to camera
    vec4 cameraPosition = objectMatrix * objectPosition;

    // Output real coordinates into gpu
    gl_Position = cameraMatrix * cameraPosition;

    outputTextureCoordinate = textureCoordinate;

    // Output vertex position color to frag shader
    newColoring = color;
}
