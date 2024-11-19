package com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util

import org.intellij.lang.annotations.Language

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