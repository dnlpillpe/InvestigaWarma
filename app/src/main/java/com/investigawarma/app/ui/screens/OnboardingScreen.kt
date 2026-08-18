package com.investigawarma.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.investigawarma.app.ui.components.AvatarIcon
import com.investigawarma.app.ui.components.IrisAvatar
import com.investigawarma.app.ui.components.IrisExpression
import com.investigawarma.app.ui.components.MagnifierLogo
import com.investigawarma.app.ui.components.PrimaryButton
import com.investigawarma.app.ui.components.SecondaryButton
import com.investigawarma.app.ui.viewmodel.OnboardingViewModel
import com.investigawarma.app.ui.viewmodel.ViewModelFactory

/**
 * Onboarding de 4 pantallas: presentación, personaje guía, cómo funciona el
 * método científico, y privacidad + creación de perfil (alias + avatar).
 * No exige nombre real ni ningún dato personal identificable.
 */
@Composable
fun OnboardingScreen(factory: ViewModelFactory, onFinished: () -> Unit) {
    val viewModel: OnboardingViewModel = viewModel(factory = factory)
    var page by remember { mutableIntStateOf(0) }
    var alias by remember { mutableStateOf("") }
    var selectedAvatar by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Box(modifier = Modifier.weight(1f)) {
            when (page) {
                0 -> OnboardingPageIntro()
                1 -> OnboardingPageIris()
                2 -> OnboardingPageMethod()
                else -> OnboardingPageProfile(
                    alias = alias,
                    onAliasChange = { alias = it.take(20) },
                    selectedAvatar = selectedAvatar,
                    onAvatarSelected = { selectedAvatar = it },
                    avatarCount = viewModel.avatarCount,
                )
            }
        }

        PageDots(count = 4, current = page)

        Column(modifier = Modifier.padding(top = 16.dp)) {
            if (page < 3) {
                PrimaryButton(text = "Siguiente", onClick = { page++ }, modifier = Modifier.fillMaxWidth())
            } else {
                PrimaryButton(
                    text = "Comenzar",
                    enabled = alias.isNotBlank(),
                    onClick = { viewModel.finishOnboarding(alias, selectedAvatar, onFinished) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (page > 0) {
                Spacer(Modifier.height(8.dp))
                SecondaryButton(text = "Atrás", onClick = { page-- }, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun PageDots(count: Int, current: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        repeat(count) { i ->
            Surface(
                modifier = Modifier
                    .padding(4.dp)
                    .size(width = if (i == current) 20.dp else 8.dp, height = 8.dp),
                color = if (i == current) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                shape = CircleShape,
            ) {}
        }
    }
}

@Composable
private fun OnboardingPageIntro() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        MagnifierLogo(modifier = Modifier.size(120.dp))
        Spacer(Modifier.height(24.dp))
        Text("Bienvenido a la Academia", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(12.dp))
        Text(
            "InvestigaWarma es una academia científica donde tú eres el investigador. Explora zonas, resuelve misiones y construye tu propio museo de descubrimientos.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun OnboardingPageIris() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        IrisAvatar(expression = IrisExpression.FELIZ, modifier = Modifier.size(96.dp))
        Spacer(Modifier.height(24.dp))
        Text("Conoce a IRIS", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(12.dp))
        Text(
            "IRIS es tu guía. Te acompaña en cada misión, celebra tus avances y te da pistas cuando las necesitas.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun OnboardingPageMethod() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Piensa como científico", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(12.dp))
        Text(
            "Observa, pregunta, crea hipótesis, experimenta y descubre. Cada misión sigue el método científico, paso a paso.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun OnboardingPageProfile(
    alias: String,
    onAliasChange: (String) -> Unit,
    selectedAvatar: Int,
    onAvatarSelected: (Int) -> Unit,
    avatarCount: Int,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Tus datos, tu privacidad", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "No necesitas nombre real ni internet. Elige un alias y un avatar. Todo tu progreso se guarda solo en este dispositivo.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = alias,
            onValueChange = onAliasChange,
            label = { Text("Tu alias de investigador") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
        Text("Elige tu avatar", style = MaterialTheme.typography.titleMedium)
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.height(180.dp)) {
            items(avatarCount) { i ->
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { onAvatarSelected(i) },
                    contentAlignment = Alignment.Center,
                ) {
                    AvatarIcon(avatarId = i, selected = i == selectedAvatar, modifier = Modifier.size(48.dp))
                }
            }
        }
    }
}
