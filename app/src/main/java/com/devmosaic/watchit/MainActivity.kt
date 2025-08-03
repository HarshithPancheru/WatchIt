package com.devmosaic.watchit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.devmosaic.watchit.navigation.MovieNavigation
import com.devmosaic.watchit.ui.theme.WatchItTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen() // Call this before super.onCreate()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WatchItTheme {
                    MovieNavigation()
            }
        }
    }
}