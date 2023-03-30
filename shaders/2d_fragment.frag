#version 410 core

// Frag is for tri texture mapping.
// This shader is a bit fancier

in vec2 outputTextureCoordinate;
in vec4 newColoring;

out vec4 fragColor;

uniform sampler2D textureSampler;


void main() {

    // Store what the pixel would have been colored and alphad on the vertex position
    vec4 pixelColor = texture(textureSampler, outputTextureCoordinate);

    // If the alpha of the text is less than the set alpha, use the set alpha
    // We do this because gl can't tell the difference between blank space and
    // text space.
    float alpha = newColoring.w < pixelColor.w ? newColoring.w : pixelColor.w;

    //! This is a new component in the shader, this allows multilayer 1d (z axis) manual manipulation of
    //! The current pixel buffer
    if (alpha <= 0.0) {
        //! This part is new
        //! So we have two choices here:
        //!
        //! 1. move it backwards behind the 2d camera
        //! 1a. Think of the camera as facing -z as forwards in position 0
        //!     we are moving it to +z which is behind what we can see
        //! 2. discard it
        //!
        //! Performance is based on your hardware platform
        //! No one shot solution for optimization woo
        gl_FragDepth = 1.0;
        // discard;
    }

    // Now we must colorize the rgba while also keeping the original alpha
    vec4 rgba = vec4 (newColoring.x, newColoring.y, newColoring.z, alpha);

    // newColoring
    fragColor = rgba;
}