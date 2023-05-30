#version 410 core

// Vertex positioning shader
// This is a bit fancier.

layout (location = 0) in vec2 position;
layout (location = 1) in vec2 textureCoordinate;
layout (location = 2) in vec4 color;

out vec2 outputTextureCoordinate;
out vec4 newColoring;
// Workaround for Nvidia
out gl_PerVertex {
    vec4 gl_Position;
};

uniform mat4 cameraMatrix;
uniform mat4 objectMatrix;

void main() {

    vec4 objectPosition = vec4(position.x, position.y, 0.0, 1.0);

    vec4 cameraPosition = objectMatrix * objectPosition;

    gl_Position = cameraMatrix * cameraPosition;

    outputTextureCoordinate = textureCoordinate;

    newColoring = color;
}
