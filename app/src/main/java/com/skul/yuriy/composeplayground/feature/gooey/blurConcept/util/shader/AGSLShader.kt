package com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util.shader

import org.intellij.lang.annotations.Language

//works fine for when the color is black
@Language("AGSL")
const val ShaderSourceSimple = """
    uniform shader composable;
    
    uniform float transparencyLimit;
    uniform float3 rgbColor;
    
    half4 main(float2 fragCoord) {
        half4 color = composable.eval(fragCoord);
        color.a = step(transparencyLimit, color.a);
        if (color == half4(0.0, 0.0, 0.0, 1.0)) {
            color.rgb = half3(rgbColor[0], rgbColor[1], rgbColor[2]);
        }
        return color;
    }
"""

//makes everything inside metaball  to be the rgbColor but icon gets not visible
@Language("AGSL")
const val ShaderSourceWrong = """
    uniform shader composable;
    
    uniform float transparencyLimit;
    uniform float3 rgbColor;
    
    half4 main(float2 fragCoord) {
        half4 color = composable.eval(fragCoord);
        color.a = step(transparencyLimit, color.a);
        
        // make it rgbColor if alpha = 1
        if (color.a == 1) {
           color.rgb = half3(rgbColor[0], rgbColor[1], rgbColor[2]);
        }
        return color;
    }
"""


@Language("AGSL")
const val ShaderSource_coloring_test = """
    uniform shader composable;
    uniform float3 rgbColor;
    uniform float transparencyLimit; // The alpha threshold for transparency logic
    
    half4 main(float2 fragCoord) {
        half4 color = composable.eval(fragCoord);

        // Debugging alpha ranges with distinct colors
        if (color.a == 0.0) {
            // Green for fully transparent pixels
            color.rgb = half3(0.0, 1.0, 0.0); 
        } else if (color.a == 1.0) {
            // Red for fully opaque pixels
            color.rgb = half3(1.0, 0.0, 0.0); 
            // color.a = 1.0; // Uncomment if additional visibility is required for debugging
        } else {
            // Case for semi-transparent pixels
            if (color.a > transparencyLimit) {
                // Blue for semi-transparent pixels above the transparency limit (cutoff)
                color.rgb = half3(0.0, 0.0, 1.0); 
            } else {
                // Pink for semi-transparent pixels below the transparency limit
                color.rgb = half3(1.0, 0.0, 1.0); 
            }
        }

        // Set alpha to 1.0 to ensure all pixels are visible for debugging
        color.a = 1.0;

        return color;
    }
"""

// sets proper color inside composable but icon is still visible
@Language("AGSL")
const val ShaderSourceProperColoring = """
    uniform shader composable;
    
    uniform float transparencyLimit;
    uniform float3 rgbColor;
    
    half4 main(float2 fragCoord) {
        half4 color = composable.eval(fragCoord);
        
        if (color.a < 1) {
            color.rgb = rgbColor; 
        }
        color.a = step(transparencyLimit, color.a);
        return color;
    }
"""

