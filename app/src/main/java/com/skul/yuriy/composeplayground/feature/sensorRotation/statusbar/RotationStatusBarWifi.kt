package com.skul.yuriy.composeplayground.feature.sensorRotation.statusbar

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun RotationStatusBarWifiWidget() {
    val isWifiConnected = rememberWifiConnected()
    val density = LocalDensity.current
    val wifiIconSize = with(density) { 12.sp.toDp() }
    Icon(
        imageVector = Icons.Filled.Wifi,
        contentDescription = "Wi-Fi",
        tint = if (isWifiConnected) Color.White else Color.White.copy(alpha = 0.35f),
        modifier = Modifier.size(wifiIconSize),
    )
}

@Composable
private fun rememberWifiConnected(): Boolean {
    val context = LocalContext.current
    var isWifiConnected by remember { mutableStateOf(readWifiConnected(context)) }

    DisposableEffect(context) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val callback =
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    isWifiConnected = readWifiConnected(context)
                }

                override fun onLost(network: Network) {
                    isWifiConnected = readWifiConnected(context)
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities,
                ) {
                    isWifiConnected =
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                }
            }

        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder().build(),
            callback,
        )
        onDispose { connectivityManager.unregisterNetworkCallback(callback) }
    }

    return isWifiConnected
}

private fun readWifiConnected(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) ?: return false
    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
}
