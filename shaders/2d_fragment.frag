#version 410 core

in vec2 outputTextureCoordinate;

out vec4 fragColor;

uniform sampler2D textureSampler;

void main() {

    vec4 pixelColor = texture(textureSampler, outputTextureCoordinate);

//    float alpha = pixelColor.w;

//    if (alpha <= 0.0) {
//        discard;
//    }

    fragColor = mix(texture(textureSampler, outputTextureCoordinate), vec4(1,0,0,1), 0.55);
}