package com.adheesha.fitsnapai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.adheesha.fitsnapai.navigation.AppNavigation
import com.adheesha.fitsnapai.ui.theme.FitSnapAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FitSnapAITheme {
                AppNavigation()
            }
        }
    }
}