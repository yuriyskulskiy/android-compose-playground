package com.skul.yuriy.composeplayground

import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation3.runtime.rememberNavBackStack
import com.skul.yuriy.composeplayground.navigation.Screens
import com.skul.yuriy.composeplayground.ui.theme.ComposePlaygroundTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //todo
//        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT))
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        setContent {
            ComposePlaygroundTheme {
//                App()
                Box(
                    modifier = Modifier
                        .fillMaxSize()
//                        .background(color = androidx.compose.ui.graphics.Color(0xFF202124)),
                        .background(color = androidx.compose.ui.graphics.Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    AvdIcon(
                        resId = R.drawable.ic_playground_avd,
                        modifier = Modifier.size(160.dp)
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun App() {
    val backStack = rememberNavBackStack(Screens.Starter)

    CompositionLocalProvider(LocalNavBackStack provides backStack) {
        AppRoot(modifier = Modifier.fillMaxSize(), backStack = backStack)
    }
}



@Composable
fun AvdIcon(
    @DrawableRes resId: Int,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            ImageView(context).apply {
                setImageResource(resId)
                val avd = drawable as? AnimatedVectorDrawable
                if (avd != null) {
                    val callback = object : Animatable2.AnimationCallback() {
                        override fun onAnimationEnd(drawable: Drawable?) {
                            (drawable as? Animatable)?.start()
                        }
                    }
                    setTag(callback)
                    avd.registerAnimationCallback(callback)
                    if (!avd.isRunning) avd.start()
                } else {
                    val anim = drawable as? Animatable
                    if (anim?.isRunning != true) anim?.start()
                }
            }
        },
        update = { view ->
            val anim = view.drawable as? Animatable
            if (anim?.isRunning != true) anim?.start()
        }
    )
}

@Composable
fun AnimatedIconAvd(
    @DrawableRes resId: Int,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            ImageView(context).apply {
                setImageResource(resId)
                val anim = drawable as? Animatable
                if (anim?.isRunning != true) anim?.start()
            }
        },
        update = { imageView ->
            val anim = imageView.drawable as? Animatable
            if (anim?.isRunning != true) anim?.start()
        }
    )
}

@Preview
@Composable
private fun IconPreview() {
    androidx.compose.foundation.Image(
        painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_playground_vector),
        contentDescription = null,
        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
            androidx.compose.ui.graphics.Color.Black
        )
    )
}
