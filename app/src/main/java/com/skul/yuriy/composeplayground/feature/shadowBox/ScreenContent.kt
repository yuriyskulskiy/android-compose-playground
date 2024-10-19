package com.skul.yuriy.composeplayground.feature.shadowBox

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.util.shadowborder.ShadowBox
import com.skul.yuriy.composeplayground.util.shadowborder.multiLayersShadow
import com.skul.yuriy.composeplayground.util.shadowborder.shadowWithClippingBlurMask
import com.skul.yuriy.composeplayground.util.shadowborder.shadowWithClippingShadowLayer

@Composable
fun ScreenContent(modifier: Modifier) {
    Column(
        modifier = modifier
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {


        // shadow using many layers with different transparency
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp)
                .padding(horizontal = 56.dp)
                .background(Color.White.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp))
                .multiLayersShadow(
                    shape = RoundedCornerShape(8.dp),
                    elevation = 8.dp
                )
                .padding(vertical = 24.dp)

        ) {
            Text(text = stringResource(R.string.multi_layer_shadow))
        }


        //shadow using shadow layer to make spot shadow look like android regular shadow
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .zIndex(50f)
                .fillMaxWidth()
                .height(78.dp)
                .padding(horizontal = 56.dp)
                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .shadowWithClippingShadowLayer(
                    shape = RoundedCornerShape(8.dp),
                    elevation = 8.dp
                )
                .padding(vertical = 24.dp)

        ) {
            Text(text = stringResource(R.string.outlined_shadow_layer))
        }


        //shadow using blurmask filter to make spot shadow look like android regular shadow
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .zIndex(100f)
                .fillMaxWidth()
                .height(78.dp)
                .padding(horizontal = 56.dp)
                .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                .shadowWithClippingBlurMask(
                    shape = RoundedCornerShape(8.dp),
                    elevation = 8.dp
                )
                .padding(vertical = 24.dp)

        ) {
            Text(text = stringResource(R.string.outlined_blur_shadow))
        }

        // https://github.com/zed-alpha/shadow-gadgets
        // https://gist.github.com/zed-alpha/3dc931720292c1f3ff31fa6a130f52cd
        // great solution by zed-alpha
        RealClippedShadow(
            elevation = 8.dp,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .height(78.dp)
                .fillMaxWidth()
                .padding(horizontal = 56.dp)

        ) {
            Text(text = stringResource(R.string.clipped_default_android_shadow))
        }


        var isTransparent by remember { mutableStateOf(true) }
        //Just a regular shadow, looks wierd with transparent content
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(78.dp)
                .fillMaxWidth()
                .padding(horizontal = 56.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(8.dp),
                    clip = false
                )
                .clip(RoundedCornerShape(8.dp))
                .background(if (isTransparent) Color.White.copy(0.5f) else Color.White)
                .clickable { isTransparent = !isTransparent },
        ) {
            Text(text = stringResource(R.string.default_android_shadow))
        }
    }
}


@Composable
fun RealClippedShadow(
    elevation: Dp = 4.dp,
    shape: Shape = RectangleShape,
    modifier: Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    ShadowBox(
        elevation = elevation,
        modifier = modifier,
        shape = shape,
        interactiveMinimum = false
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(8.dp))

        ) {
            content()
        }
    }
}
