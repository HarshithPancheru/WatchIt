package com.devmosaic.watchit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.devmosaic.watchit.navigation.MovieNavigation
import com.devmosaic.watchit.ui.theme.WatchItTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WatchItTheme {
                    MovieNavigation()
            }
        }
    }
}








@Preview(showBackground = true)
@Composable
fun DefaultPreview(){
    WatchItTheme {
            MovieNavigation()
    }
}