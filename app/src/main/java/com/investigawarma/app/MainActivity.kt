package com.investigawarma.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.investigawarma.app.ui.navigation.InvestigaWarmaNavGraph
import com.investigawarma.app.ui.theme.InvestigaWarmaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as InvestigaWarmaApp).container
        setContent {
            InvestigaWarmaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    InvestigaWarmaNavGraph(container = container)
                }
            }
        }
    }
}
