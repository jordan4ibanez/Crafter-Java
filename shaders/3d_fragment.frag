#version 410 core

// Frag is for tri texture mapping.
// This is just your standard old glsl shader.

in vec2 outputTextureCoordinate;
in vec4 newColoring;

out vec4 fragColor;

uniform sampler2D textureSampler;

void main() {
    vec4 textureColor = texture(textureSampler, outputTextureCoordinate);

    fragColor = textureColor * newColoring;
}
