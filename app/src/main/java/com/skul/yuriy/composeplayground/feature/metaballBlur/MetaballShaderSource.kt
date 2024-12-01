package com.skul.yuriy.composeplayground.feature.metaballBlur

import org.intellij.lang.annotations.Language

@Language("AGSL")
const val AgslOutlineBorderShaderSource = """
    uniform shader composable;

    // Constants for colors
    const float3 WHITE_BORDER_COLOR = float3(1.0, 1.0, 1.0);     
    const float3 BLACK_MASK_COLOR = float3(0.0, 0.0, 0.0); 
    
    const float CUTOFF = 0.5; // Define a constant cutoff value
    const float BORDER_THICKNESS = 0.05; // Border thickness

    half4 main(float2 fragCoord) {
        half4 color = composable.eval(fragCoord);

        if (color.a < CUTOFF || color.a > (CUTOFF + BORDER_THICKNESS)) {
            //  Pixel is outside the border range, make it fully transparent
            color.rgb = BLACK_MASK_COLOR;
            color.a = 0.0;
        } else {
            // Pixel is in the border range, assign border color and make it opaque
            color.rgb = WHITE_BORDER_COLOR;
            color.a = 1.0;
        }

        return color;
    }
"""

//on top of white background!
@Language("AGSL")
const val AgslShaderSourceSimple = """
    uniform shader composable;
    const float CUTOFF = 0.5; 
    const float3 RGB_COLOR_BLACK = float3(0.0, 0.0, 0.0); // constant  Black rgb
    
    half4 main(float2 fragCoord) {
        half4 color = composable.eval(fragCoord);
        color.a = step(CUTOFF, color.a);
        return color;
    }
"""

@Language("AGSL")
const val AgslShaderSourceSimpleSolidBlack = """
    uniform shader composable;
    const float CUTOFF = 0.5; 
    const float3 RGB_BLACK = float3(0.0, 0.0, 0.0); // constant  Black rgb

    half4 main(float2 fragCoord) {
        half4 color = composable.eval(fragCoord);
        color.a = step(CUTOFF, color.a);
        if (color.a == 1) {
            color.rgb = RGB_BLACK;
        }
        return color;
    }
"""