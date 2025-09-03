@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)
package com.yg.mileage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yg.mileage.ui.theme.MileageCalculatorTheme
import com.yg.mileage.ui.theme.primaryLight

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MileageCalculatorTheme {
                MainScreen()
            }
        }
    }
}

@Preview
@Composable
fun MainScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Welcome to Expressive UI",
                style = MaterialTheme.typography.headlineMedium,
                color = primaryLight
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {},
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text("Click Me")
            }
        }
    }
}
@Preview
@Composable
fun ContainedLoading() {
    LoadingIndicator(
        color = primaryLight,
        polygons = listOf(
            MaterialShapes.SoftBurst,
            MaterialShapes.Cookie9Sided,
            MaterialShapes.Pentagon,
            MaterialShapes.Pill,
            MaterialShapes.Sunny,
            MaterialShapes.Cookie4Sided,
            MaterialShapes.Oval,
            MaterialShapes.VerySunny,
            MaterialShapes.Ghostish,
            MaterialShapes.Cookie12Sided
        )
    )
    ContainedLoadingIndicator()
}

/**
 * Optional Path to be created for the RoundRect if the corner radii are not identical This
 * is because Canvas has a built in API for drawing round rectangles with the same corner
 * radii in all 4 corners. However, if each corner has a different corner radii, a path must
 * be drawn instead
 */


@Preview
@Composable
fun MainScreenPreview() {
    MainScreen()
}



