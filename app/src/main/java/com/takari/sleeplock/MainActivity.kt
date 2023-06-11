package com.takari.sleeplock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.takari.sleeplock.ui.theme.SleepLockTheme

/*
 * Since this projects depedencies have way to many conflics and issues, we'll just rewrite this from
 * scratch. It makes more sense to do this since we're using completely different frameworks.
 *
 * This will be a great learning process, learn as you go.
 *
 * TODO rewrite and republish Sleeplock using the following libraries:
 *  Kotlin
 *  Jetpack Compose
 *  MVVM
 *  Dagger Hilt
 *  Unit Test
 */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SleepLockTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SleepLockTheme {
        Greeting("Android")
    }
}