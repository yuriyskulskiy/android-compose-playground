package com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util.shader

import org.intellij.lang.annotations.Language


//todo link to the medium and names to the chapter
//article test coloring
@Language("AGSL")
const val ShaderSource_outlined_color_test_1 = """
    uniform shader composable;
    
    uniform float cutoff_min; // Minimum cutoff value for transparency
    uniform float border_thickness; // Thickness of the border
    uniform float3 rgbColor;  // Custom RGB color (not directly used in this logic)
    
    // no usage in current render script - could be removed
    uniform float3 rgbMarkerColor;
    
    half4 main(float2 fragCoord) {
        half4 color = composable.eval(fragCoord);

        // Calculate cutoff_max dynamically using border_thickness
        float cutoff_max = cutoff_min + border_thickness;

        // If alpha is exactly 0.0 (fully transparent), set it to yellow
        if (color.a == 0.0) {
            color.rgb = half3(1.0, 1.0, 0.0); // Yellow for fully transparent
        }
        // If alpha is below the minimum cutoff, make it green
        else if (color.a < cutoff_min) {
            color.rgb = half3(0.0, 1.0, 0.0); // Green
        } 
        // If alpha is within the cutoff range (border area), make it black
        else if (cutoff_min <= color.a && color.a <= cutoff_max) {
            color.rgb = half3(0.0, 0.0, 0.0); // Black
        } 
        // If alpha is above the maximum cutoff but less than fully opaque (outer area), make it blue
        else if (cutoff_max < color.a && color.a < 1.0) {
            color.rgb = half3(0.0, 0.0, 1.0); // Blue
        } 
        // Fully opaque case where alpha = 1, make it red
        else {
            color.rgb = half3(1.0, 0.0, 0.0); // Red
        }

        // Ensure alpha is fully opaque for all cases
        color.a = 1.0;

        return color;
    }
"""


// simple

//Simple AGSL: filter areas by alpha value only
@Language("AGSL")
const val ShaderSource_outlined_simple = """
    uniform shader composable;
    
    uniform float cutoff_min; // Minimum cutoff value for transparency
    uniform float border_thickness; // Thickness of the border
    uniform float3 rgbColor;  // RGB color passed as parameter
    
     // Constant for black (zero) RGB color
    const half3 BLACK_MASK_RGB = half3(0.0, 0.0, 0.0);
    
    // no usage in current render script - could be removed
    uniform float3 rgbMarkerColor;  

    half4 main(float2 fragCoord) {
        half4 color = composable.eval(fragCoord);
        
        // Calculate cutoff_max dynamically using border_thickness
        float cutoff_max = cutoff_min + border_thickness;

        // If alpha is exactly 0.0 (fully transparent), do nothing
        if (color.a == 0.0) {
            // Do nothing
        }
        // If alpha is below the minimum cutoff,
        // make it fully transparent and black
        else if (color.a < cutoff_min) {
            color.rgb = BLACK_MASK_RGB; // Set to black
            color.a = 0.0; // Fully transparent
        } 
        // If alpha is within the cutoff range (border area), 
        // make it the custom color
        else if (cutoff_min <= color.a && color.a <= cutoff_max) {
            color.rgb = rgbColor; // Set to custom color
            color.a = 1.0; // Fully opaque
        } 
        // If alpha is above the maximum cutoff but less than 
        //  fully opaque (outer area), make it fully transparent and black
        else if (cutoff_max < color.a && color.a < 1.0) {
            color.rgb = BLACK_MASK_RGB; // Set to black
            color.a = 0.0; // Fully transparent
        } 
        // Fully opaque case where alpha = 1 (Icon area),
        // make it the custom color
        else {
            color.rgb = rgbColor; // Set to custom color
            color.a = 1.0; // Fully opaque
        }

        return color;
    }
"""

//article. for stable version with color marker
//AGSL: filter areas by alpha value and marker color
@Language("AGSL")
const val ShaderSource_outlined_marker_color = """
    uniform shader composable;
    
    uniform float cutoff_min; // Minimum cutoff value for transparency
    uniform float border_thickness; // Thickness of the border
    uniform float3 rgbColor;  // RGB color passed as parameter
    
    uniform float3 rgbMarkerColor;  // marker for Icon tint color
    
     // Constant for black (zero) RGB color
    const half3 BLACK_MASK_RGB = half3(0.0, 0.0, 0.0);
    
    half4 main(float2 fragCoord) {
        half4 color = composable.eval(fragCoord);
        
        // Calculate cutoff_max dynamically using border_thickness
        float cutoff_max = cutoff_min + border_thickness;

        // If alpha is exactly 0.0 (fully transparent), do nothing
        if (color.a == 0.0) {
            // Do nothing
        }
        // If alpha is below the minimum cutoff,
        // make it fully transparent and black
        else if (color.a < cutoff_min) {
            color.rgb = BLACK_MASK_RGB; // Set to black
            color.a = 0.0; // Fully transparent
        } 
        // If alpha is within the cutoff range (border area), 
        // make it the custom color
        else if (cutoff_min <= color.a && color.a <= cutoff_max) {
            color.rgb = rgbColor; // Set to custom color
            color.a = 1.0; // Fully opaque
        } 
        // If alpha is above the maximum cutoff but less than 
        //  fully opaque (outer area), make it fully transparent and black
        else if (cutoff_max < color.a && color.a < 1.0) {
            color.rgb = BLACK_MASK_RGB; // Set to black
            color.a = 0.0; // Fully transparent
        } else {
         // Fully opaque case where alpha = 1
             if(color.rgb == rgbMarkerColor){
             //Icon area
                color.rgb = rgbColor;  // Set to Icon color
             }else{
             //pixel is out of icon shape - make it transparent
                color.rgb = BLACK_MASK_RGB;
                color.a = 0.0;
            }
        }

        return color;
    }
"""

@Language("AGSL")
const val ShaderSource_outlined_marker_color_and_marker_brightness = """
    uniform shader composable;
    
    uniform float cutoff_min; // Minimum cutoff value for transparency
    uniform float border_thickness; // Thickness of the border
    uniform float3 rgbColor;  // RGB color passed as a parameter
    uniform float3 rgbMarkerColor;  // Marker for Icon tint color
    
    // Constant for black (zero) RGB color
    const half3 BLACK_MASK_RGB = half3(0.0, 0.0, 0.0);

    // Helper function to adjust alpha dynamically
    float adjustAlpha(float alpha) {
        // Define the threshold value
        const float threshold = 0.01;
    
        // Calculate the alpha raised to the power of 4
        float adjustedAlpha = pow(alpha, 4.0);
    
        // Return either the adjusted alpha or 0 if below the threshold
        return adjustedAlpha < threshold ? 0.0 : adjustedAlpha;
    }
    
    half4 main(float2 fragCoord) {
        half4 color = composable.eval(fragCoord);
        
        // Calculate cutoff_max dynamically using border_thickness
        float cutoff_max = cutoff_min + border_thickness;

        // If alpha is exactly 0.0 (fully transparent), do nothing
        if (color.a == 0.0) {
            // Do nothing
        }
        // If alpha is below the minimum cutoff, make it fully transparent and black
        else if (color.a < cutoff_min) {
            color.rgb = BLACK_MASK_RGB; // Set to black
            color.a = 0.0; // Fully transparent
        } 
        // If alpha is within the cutoff range (border area), make it the custom color
        else if (cutoff_min <= color.a && color.a <= cutoff_max) {
            color.rgb = rgbColor; // Set to custom color
            color.a = 1.0; // Fully opaque
        } 
        // If alpha is above the maximum cutoff but less than fully opaque (outer area),
        // make it fully transparent and black
        else if (cutoff_max < color.a && color.a < 1.0) {
            color.rgb = BLACK_MASK_RGB; // Set to black
            color.a = 0.0; // Fully transparent
        } 
        // Fully opaque case where alpha = 1
        else {
            if (color.rgb == rgbMarkerColor) {
                // Icon area
                color.rgb = rgbColor; // Set to Icon color
            } else if (color.rgb == rgbColor) {
                // Some black inner metaball area could be opaque - make it transparent
                color.rgb = BLACK_MASK_RGB;
                color.a = 0.0;
            } else {
                // Compute current brightness
                float brightness = (color.r + color.g + color.b) / 3.0;
                float adjustedBrightness = adjustAlpha(brightness);

                // Set brightness as the alpha channel
                color.a = adjustedBrightness;

                // Set RGB color based on adjusted brightness
                if (adjustedBrightness == 0) {
                    color.rgb = BLACK_MASK_RGB; // Fully transparent
                } else {
                    color.rgb = rgbColor; // Custom color
                }
            }
        }

        return color;
    }
"""


