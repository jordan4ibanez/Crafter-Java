#version 410 core

layout (location = 0) in vec2 position;
layout (location = 1) in vec2 textureCoordinate;
layout (location = 2) in vec4 color;

out vec2 outputTextureCoordinate;

void main() {
    outputTextureCoordinate = textureCoordinate;
    gl_Position = vec4(position, 0, 1);
}