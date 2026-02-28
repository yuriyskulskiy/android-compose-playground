package com.skul.yuriy.composeplayground

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.rememberNavBackStack
import com.skul.yuriy.composeplayground.navigation.Screens
import com.skul.yuriy.composeplayground.ui.theme.ComposePlaygroundTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        setContent {
            ComposePlaygroundTheme {
                App()
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
