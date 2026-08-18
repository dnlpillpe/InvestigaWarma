package com.investigawarma.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.investigawarma.app.ui.components.MagnifierLogo
import com.investigawarma.app.ui.theme.DeepSpace
import com.investigawarma.app.ui.theme.LabViolet
import com.investigawarma.app.ui.theme.PaperWhite

/** Splash / portada de InvestigaWarma: "la lupa del joven investigador". */
@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(DeepSpace, LabViolet))),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            MagnifierLogo(modifier = Modifier.size(120.dp))
            androidx.compose.foundation.layout.Spacer(Modifier.padding(8.dp))
            Text(
                "InvestigaWarma",
                color = PaperWhite,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                "Academia de Jóvenes Científicos",
                color = PaperWhite.copy(alpha = 0.85f),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}
