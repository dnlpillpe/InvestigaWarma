package com.investigawarma.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.investigawarma.app.data.local.entity.ExperimentParameterEntity

/**
 * Escenas ilustradas propias de InvestigaWarma, dibujadas con Canvas (mismo
 * enfoque que IrisAvatar y ZoneIcon): grandes, planas y coloridas, cero
 * imágenes externas, funcionan 100% offline y no pesan nada en el APK.
 *
 * Cada misión (m01..m40) tiene una escena asignada a mano en IllustrationCatalog
 * según su historia real, para que "observa la imagen" siempre tenga una imagen.
 */
enum class IllustrationKey {
    LUPA_DETALLE, HECHOS_OPINIONES, CUADERNO_NOTAS, MICROSCOPIO_MUESTRAS, ETIQUETAS_OBJETO,
    ERROR_DETECTIVE, PATRON_FORMAS, PREDICCION_IDEA, PLANTA_CRECIMIENTO, CANICAS_MOVIMIENTO,
    CALOR_MATERIALES, INSTRUMENTOS_LABORATORIO, BALANZA_PESO, VARIABLES_EXPERIMENTO,
    METODO_CIENTIFICO_CICLO, ANIMAL_RASTRO, HOJAS_COMPARAR, CADENA_ALIMENTICIA,
    ECOSISTEMA_ETIQUETAS, CAMUFLAJE_INSECTO, HABITAT_ANIMAL, CICLO_VIDA, CICLO_AGUA,
    CLIMA_ESTACIONES, VIENTO_ENERGIA, TEMPERATURA_SUPERFICIES, AMBIENTE_CUIDADO,
    CLIMA_DATOS_PATRON, PREDICCION_CLIMA, GRAFICO_BARRAS, COMPARAR_DATOS, PATRON_NUMERICO,
    CLASIFICAR_DATOS, CRONOLOGIA_DATOS, PREDICCION_DATOS, GRAFICO_PREGUNTA,
}

/** Asigna a cada una de las 40 misiones su escena, elegida a mano según su historia. */
object IllustrationCatalog {
    private val byMission: Map<String, IllustrationKey> = mapOf(
        "m01" to IllustrationKey.LUPA_DETALLE,
        "m02" to IllustrationKey.HECHOS_OPINIONES,
        "m03" to IllustrationKey.CUADERNO_NOTAS,
        "m04" to IllustrationKey.MICROSCOPIO_MUESTRAS,
        "m05" to IllustrationKey.ETIQUETAS_OBJETO,
        "m06" to IllustrationKey.ERROR_DETECTIVE,
        "m07" to IllustrationKey.PATRON_FORMAS,
        "m08" to IllustrationKey.PREDICCION_IDEA,
        "m09" to IllustrationKey.PLANTA_CRECIMIENTO,
        "m10" to IllustrationKey.CANICAS_MOVIMIENTO,
        "m11" to IllustrationKey.CALOR_MATERIALES,
        "m12" to IllustrationKey.INSTRUMENTOS_LABORATORIO,
        "m13" to IllustrationKey.BALANZA_PESO,
        "m14" to IllustrationKey.VARIABLES_EXPERIMENTO,
        "m15" to IllustrationKey.ERROR_DETECTIVE,
        "m16" to IllustrationKey.METODO_CIENTIFICO_CICLO,
        "m17" to IllustrationKey.ANIMAL_RASTRO,
        "m18" to IllustrationKey.HOJAS_COMPARAR,
        "m19" to IllustrationKey.CADENA_ALIMENTICIA,
        "m20" to IllustrationKey.ECOSISTEMA_ETIQUETAS,
        "m21" to IllustrationKey.CAMUFLAJE_INSECTO,
        "m22" to IllustrationKey.HABITAT_ANIMAL,
        "m23" to IllustrationKey.ERROR_DETECTIVE,
        "m24" to IllustrationKey.CICLO_VIDA,
        "m25" to IllustrationKey.CICLO_AGUA,
        "m26" to IllustrationKey.CLIMA_ESTACIONES,
        "m27" to IllustrationKey.VIENTO_ENERGIA,
        "m28" to IllustrationKey.TEMPERATURA_SUPERFICIES,
        "m29" to IllustrationKey.AMBIENTE_CUIDADO,
        "m30" to IllustrationKey.CLIMA_DATOS_PATRON,
        "m31" to IllustrationKey.PREDICCION_CLIMA,
        "m32" to IllustrationKey.ERROR_DETECTIVE,
        "m33" to IllustrationKey.GRAFICO_BARRAS,
        "m34" to IllustrationKey.COMPARAR_DATOS,
        "m35" to IllustrationKey.PATRON_NUMERICO,
        "m36" to IllustrationKey.CLASIFICAR_DATOS,
        "m37" to IllustrationKey.CRONOLOGIA_DATOS,
        "m38" to IllustrationKey.ERROR_DETECTIVE,
        "m39" to IllustrationKey.PREDICCION_DATOS,
        "m40" to IllustrationKey.GRAFICO_PREGUNTA,
    )

    fun forMission(missionId: String): IllustrationKey =
        byMission[missionId] ?: IllustrationKey.LUPA_DETALLE

    fun description(key: IllustrationKey): String = when (key) {
        IllustrationKey.LUPA_DETALLE -> "Una lupa revela un detalle brillante escondido."
        IllustrationKey.HECHOS_OPINIONES -> "Dos globos de diálogo: uno con un hecho, otro con una opinión."
        IllustrationKey.CUADERNO_NOTAS -> "Un cuaderno de campo abierto con anotaciones."
        IllustrationKey.MICROSCOPIO_MUESTRAS -> "Dos muestras redondas con patrones distintos bajo el microscopio."
        IllustrationKey.ETIQUETAS_OBJETO -> "Un objeto misterioso rodeado de etiquetas por descubrir."
        IllustrationKey.ERROR_DETECTIVE -> "Una lupa detective señala un error en un documento."
        IllustrationKey.PATRON_FORMAS -> "Una fila de formas que sigue un patrón, con una incógnita al final."
        IllustrationKey.PREDICCION_IDEA -> "Una idea brillante en forma de bombilla con una pregunta."
        IllustrationKey.PLANTA_CRECIMIENTO -> "Una planta creciendo en una maceta bajo el sol."
        IllustrationKey.CANICAS_MOVIMIENTO -> "Dos canicas rodando por una rampa curva."
        IllustrationKey.CALOR_MATERIALES -> "Dos materiales calentándose de forma distinta."
        IllustrationKey.INSTRUMENTOS_LABORATORIO -> "Un matraz, una regla y una lupa listos para experimentar."
        IllustrationKey.BALANZA_PESO -> "Una balanza de laboratorio con dos platillos."
        IllustrationKey.VARIABLES_EXPERIMENTO -> "Dos cajas conectadas por flechas que representan variables."
        IllustrationKey.METODO_CIENTIFICO_CICLO -> "Una flecha circular que muestra los pasos del método científico."
        IllustrationKey.ANIMAL_RASTRO -> "Una huella de animal junto a una lupa de rastreo."
        IllustrationKey.HOJAS_COMPARAR -> "Dos hojas de plantas distintas, una junto a la otra."
        IllustrationKey.CADENA_ALIMENTICIA -> "El sol, una planta y un animal conectados por flechas."
        IllustrationKey.ECOSISTEMA_ETIQUETAS -> "Un paisaje con árbol y sol, con etiquetas flotando."
        IllustrationKey.CAMUFLAJE_INSECTO -> "Un insecto casi invisible sobre una hoja del mismo color."
        IllustrationKey.HABITAT_ANIMAL -> "Un pequeño hábitat con un animal y un árbol."
        IllustrationKey.CICLO_VIDA -> "Cuatro etapas de vida conectadas en círculo."
        IllustrationKey.CICLO_AGUA -> "El sol, una nube y el agua conectados en un ciclo."
        IllustrationKey.CLIMA_ESTACIONES -> "Cuatro símbolos de clima, uno por estación."
        IllustrationKey.VIENTO_ENERGIA -> "Líneas de viento moviendo un molinillo."
        IllustrationKey.TEMPERATURA_SUPERFICIES -> "Un termómetro junto a dos superficies distintas."
        IllustrationKey.AMBIENTE_CUIDADO -> "Flechas de reciclaje alrededor de una hoja verde."
        IllustrationKey.CLIMA_DATOS_PATRON -> "Una gráfica de línea con símbolos de clima."
        IllustrationKey.PREDICCION_CLIMA -> "Una nube con signo de pregunta y un sol asomando."
        IllustrationKey.GRAFICO_BARRAS -> "Un gráfico de barras de colores."
        IllustrationKey.COMPARAR_DATOS -> "Dos grupos de barras comparándose."
        IllustrationKey.PATRON_NUMERICO -> "Una fila de fichas numeradas con una incógnita al final."
        IllustrationKey.CLASIFICAR_DATOS -> "Un embudo que ordena puntos en dos canastas de color."
        IllustrationKey.CRONOLOGIA_DATOS -> "Una línea de tiempo con puntos marcados."
        IllustrationKey.PREDICCION_DATOS -> "Una gráfica con una línea que continúa punteada hacia el futuro."
        IllustrationKey.GRAFICO_PREGUNTA -> "Un gráfico de barras junto a un signo de pregunta."
    }
}

/** Contenedor grande y colorido para una escena. Reutilizable en observar, experimentar y descubrir. */
@Composable
fun SceneIllustration(
    key: IllustrationKey,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Surface(shape = RoundedCornerShape(24.dp), color = Color.Transparent, modifier = modifier) {
        Box(contentAlignment = Alignment.Center) {
            sceneBackdrop(tint)
            Box(modifier = Modifier.padding(18.dp), contentAlignment = Alignment.Center) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .semantics { contentDescription = IllustrationCatalog.description(key) },
                ) {
                    drawScene(key, tint)
                }
            }
        }
    }
}

/** Fondo con degradado suave + sombra de piso, para que las escenas se sientan con más profundidad. */
@Composable
private fun sceneBackdrop(tint: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(brush = Brush.verticalGradient(listOf(tint.copy(alpha = 0.28f), tint.copy(alpha = 0.06f))))
        drawOval(
            color = Color.Black.copy(alpha = 0.08f),
            topLeft = Offset(size.width * 0.1f, size.height * 0.84f),
            size = Size(size.width * 0.8f, size.height * 0.12f),
        )
    }
}

private val Sun = Color(0xFFFFC93C)
private val Sky = Color(0xFF6FB7E8)
private val Leaf = Color(0xFF2ECC71)
private val LeafDark = Color(0xFF1E9E58)
private val Soil = Color(0xFF9C6B3E)
private val Ink = Color(0xFF2B2D5B)
private val Coral = Color(0xFFFF6B6B)
private val Amber = Color(0xFFFFB020)
private val Violet = Color(0xFF8B75F0)
private val Cyan = Color(0xFF20D3C2)
private val Cloud = Color(0xFFE7ECFA)

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawScene(key: IllustrationKey, tint: Color) {
    val w = size.width
    val h = size.height
    val dash = Stroke(width = w * 0.02f, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(w * 0.03f, w * 0.03f)))

    fun leafPath(cx: Float, cy: Float, r: Float): Path = Path().apply {
        moveTo(cx, cy - r)
        cubicTo(cx + r, cy - r * 0.5f, cx + r * 0.8f, cy + r * 0.8f, cx, cy + r)
        cubicTo(cx - r * 0.8f, cy + r * 0.8f, cx - r, cy - r * 0.5f, cx, cy - r)
        close()
    }

    when (key) {
        IllustrationKey.LUPA_DETALLE -> {
            drawCircle(tint, radius = w * 0.22f, center = Offset(w * 0.4f, h * 0.42f), style = Stroke(width = w * 0.05f))
            drawLine(tint, Offset(w * 0.56f, h * 0.58f), Offset(w * 0.78f, h * 0.8f), strokeWidth = w * 0.06f, cap = StrokeCap.Round)
            StarShape2(this, Amber, Offset(w * 0.72f, h * 0.28f), w * 0.1f)
        }
        IllustrationKey.HECHOS_OPINIONES -> {
            drawRoundRect(Cyan, topLeft = Offset(w * 0.06f, h * 0.18f), size = Size(w * 0.4f, h * 0.32f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.06f))
            val check = Path().apply { moveTo(w * 0.16f, h * 0.34f); lineTo(w * 0.24f, h * 0.42f); lineTo(w * 0.4f, h * 0.24f) }
            drawPath(check, Color.White, style = Stroke(width = w * 0.03f, cap = StrokeCap.Round))
            drawRoundRect(Coral, topLeft = Offset(w * 0.54f, h * 0.42f), size = Size(w * 0.4f, h * 0.32f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.06f))
            drawCircle(Color.White, radius = w * 0.025f, center = Offset(w * 0.68f, h * 0.58f))
            drawLine(Color.White, Offset(w * 0.68f, h * 0.5f), Offset(w * 0.68f, h * 0.54f), strokeWidth = w * 0.025f, cap = StrokeCap.Round)
        }
        IllustrationKey.CUADERNO_NOTAS -> {
            drawRoundRect(Color.White, topLeft = Offset(w * 0.2f, h * 0.14f), size = Size(w * 0.6f, h * 0.72f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.04f))
            drawLine(tint, Offset(w * 0.2f, h * 0.14f), Offset(w * 0.2f, h * 0.86f), strokeWidth = w * 0.05f)
            repeat(4) { i -> drawLine(tint.copy(alpha = 0.5f), Offset(w * 0.32f, h * (0.32f + i * 0.14f)), Offset(w * 0.72f, h * (0.32f + i * 0.14f)), strokeWidth = w * 0.02f, cap = StrokeCap.Round) }
            drawLine(Ink, Offset(w * 0.62f, h * 0.7f), Offset(w * 0.85f, h * 0.93f), strokeWidth = w * 0.035f, cap = StrokeCap.Round)
            drawLine(Amber, Offset(w * 0.82f, h * 0.9f), Offset(w * 0.88f, h * 0.96f), strokeWidth = w * 0.05f, cap = StrokeCap.Round)
        }
        IllustrationKey.MICROSCOPIO_MUESTRAS -> {
            drawCircle(Color.White, radius = w * 0.2f, center = Offset(w * 0.3f, h * 0.5f), style = Stroke(width = w * 0.03f))
            drawCircle(tint, radius = w * 0.04f, center = Offset(w * 0.24f, h * 0.44f))
            drawCircle(tint, radius = w * 0.03f, center = Offset(w * 0.36f, h * 0.56f))
            drawCircle(Color.White, radius = w * 0.2f, center = Offset(w * 0.7f, h * 0.5f), style = Stroke(width = w * 0.03f))
            repeat(5) { i ->
                val angle = i * (Math.PI * 2 / 5)
                val x = w * 0.7f + (w * 0.1f * kotlin.math.cos(angle)).toFloat()
                val y = h * 0.5f + (w * 0.1f * kotlin.math.sin(angle)).toFloat()
                drawCircle(Coral, radius = w * 0.015f, center = Offset(x, y))
            }
        }
        IllustrationKey.ETIQUETAS_OBJETO -> {
            drawCircle(tint, radius = w * 0.18f, center = Offset(w * 0.5f, h * 0.5f))
            listOf(-1 to -1, 1 to -1, 0 to 1).forEachIndexed { i, (dx, dy) ->
                val tx = w * 0.5f + dx * w * 0.28f
                val ty = h * 0.5f + dy * h * 0.28f
                drawLine(Ink.copy(alpha = 0.5f), Offset(w * 0.5f, h * 0.5f), Offset(tx, ty), strokeWidth = w * 0.015f)
                drawRoundRect(Amber, topLeft = Offset(tx - w * 0.09f, ty - h * 0.06f), size = Size(w * 0.18f, h * 0.12f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.02f))
            }
        }
        IllustrationKey.ERROR_DETECTIVE -> {
            drawRoundRect(Color.White, topLeft = Offset(w * 0.22f, h * 0.16f), size = Size(w * 0.42f, h * 0.6f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.03f))
            repeat(3) { i -> drawLine(tint.copy(alpha = 0.5f), Offset(w * 0.3f, h * (0.3f + i * 0.14f)), Offset(w * 0.56f, h * (0.3f + i * 0.14f)), strokeWidth = w * 0.02f, cap = StrokeCap.Round) }
            drawLine(Coral, Offset(w * 0.32f, h * 0.62f), Offset(w * 0.5f, h * 0.76f), strokeWidth = w * 0.035f, cap = StrokeCap.Round)
            drawLine(Coral, Offset(w * 0.5f, h * 0.62f), Offset(w * 0.32f, h * 0.76f), strokeWidth = w * 0.035f, cap = StrokeCap.Round)
            drawCircle(tint, radius = w * 0.16f, center = Offset(w * 0.68f, h * 0.42f), style = Stroke(width = w * 0.045f))
            drawLine(tint, Offset(w * 0.8f, h * 0.54f), Offset(w * 0.92f, h * 0.68f), strokeWidth = w * 0.05f, cap = StrokeCap.Round)
        }
        IllustrationKey.PATRON_FORMAS -> {
            val shapes = listOf(true, false, true, false)
            shapes.forEachIndexed { i, isCircle ->
                val cx = w * (0.14f + i * 0.2f)
                if (isCircle) drawCircle(Violet, radius = w * 0.08f, center = Offset(cx, h * 0.5f))
                else {
                    val p = Path().apply { moveTo(cx, h * 0.4f); lineTo(cx + w * 0.08f, h * 0.6f); lineTo(cx - w * 0.08f, h * 0.6f); close() }
                    drawPath(p, Amber)
                }
            }
            drawCircle(Ink.copy(alpha = 0.15f), radius = w * 0.1f, center = Offset(w * 0.86f, h * 0.5f))
            drawLine(Ink, Offset(w * 0.86f, h * 0.44f), Offset(w * 0.86f, h * 0.5f), strokeWidth = w * 0.025f, cap = StrokeCap.Round)
            drawCircle(Ink, radius = w * 0.012f, center = Offset(w * 0.86f, h * 0.56f))
        }
        IllustrationKey.PREDICCION_IDEA -> {
            drawCircle(Amber, radius = w * 0.2f, center = Offset(w * 0.5f, h * 0.38f))
            drawRoundRect(Ink.copy(alpha = 0.6f), topLeft = Offset(w * 0.42f, h * 0.56f), size = Size(w * 0.16f, h * 0.1f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.02f))
            repeat(6) { i ->
                val angle = i * (Math.PI * 2 / 6)
                val x1 = w * 0.5f + (w * 0.26f * kotlin.math.cos(angle)).toFloat()
                val y1 = h * 0.38f + (w * 0.26f * kotlin.math.sin(angle)).toFloat()
                val x2 = w * 0.5f + (w * 0.34f * kotlin.math.cos(angle)).toFloat()
                val y2 = h * 0.38f + (w * 0.34f * kotlin.math.sin(angle)).toFloat()
                drawLine(Amber, Offset(x1, y1), Offset(x2, y2), strokeWidth = w * 0.02f, cap = StrokeCap.Round)
            }
            drawCircle(Ink, radius = w * 0.02f, center = Offset(w * 0.42f, h * 0.36f))
            drawCircle(Ink, radius = w * 0.02f, center = Offset(w * 0.58f, h * 0.36f))
        }
        IllustrationKey.PLANTA_CRECIMIENTO -> {
            drawCircle(Sun, radius = w * 0.1f, center = Offset(w * 0.82f, h * 0.16f))
            val pot = Path().apply { moveTo(w * 0.34f, h * 0.7f); lineTo(w * 0.66f, h * 0.7f); lineTo(w * 0.58f, h * 0.92f); lineTo(w * 0.42f, h * 0.92f); close() }
            drawPath(pot, Soil)
            drawLine(LeafDark, Offset(w * 0.5f, h * 0.7f), Offset(w * 0.5f, h * 0.32f), strokeWidth = w * 0.025f, cap = StrokeCap.Round)
            drawPath(leafPath(w * 0.38f, h * 0.44f, w * 0.12f), Leaf)
            drawPath(leafPath(w * 0.62f, h * 0.36f, w * 0.13f), Leaf)
        }
        IllustrationKey.CANICAS_MOVIMIENTO -> {
            val ramp = Path().apply { moveTo(w * 0.1f, h * 0.28f); quadraticBezierTo(w * 0.5f, h * 0.28f, w * 0.9f, h * 0.78f) }
            drawPath(ramp, tint, style = Stroke(width = w * 0.04f, cap = StrokeCap.Round))
            drawCircle(Coral, radius = w * 0.07f, center = Offset(w * 0.28f, h * 0.34f))
            drawCircle(Violet, radius = w * 0.07f, center = Offset(w * 0.72f, h * 0.63f))
            repeat(3) { i -> drawLine(Violet.copy(alpha = 0.5f), Offset(w * (0.55f - i * 0.05f), h * (0.5f + i * 0.03f)), Offset(w * (0.62f - i * 0.05f), h * (0.55f + i * 0.03f)), strokeWidth = w * 0.015f, cap = StrokeCap.Round) }
        }
        IllustrationKey.CALOR_MATERIALES -> {
            drawRoundRect(Amber, topLeft = Offset(w * 0.18f, h * 0.5f), size = Size(w * 0.24f, h * 0.36f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.02f))
            drawRoundRect(Sky, topLeft = Offset(w * 0.58f, h * 0.5f), size = Size(w * 0.24f, h * 0.36f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.02f))
            repeat(3) { i ->
                val wave = Path().apply {
                    moveTo(w * (0.24f + i * 0.06f), h * 0.44f)
                    quadraticBezierTo(w * (0.28f + i * 0.06f), h * 0.32f, w * (0.24f + i * 0.06f), h * 0.2f)
                }
                drawPath(wave, Coral, style = Stroke(width = w * 0.02f, cap = StrokeCap.Round))
            }
            drawLine(Sky, Offset(w * 0.7f, h * 0.44f), Offset(w * 0.7f, h * 0.28f), strokeWidth = w * 0.02f, cap = StrokeCap.Round)
        }
        IllustrationKey.INSTRUMENTOS_LABORATORIO -> {
            val flask = Path().apply {
                moveTo(w * 0.42f, h * 0.18f); lineTo(w * 0.42f, h * 0.4f); lineTo(w * 0.22f, h * 0.78f); lineTo(w * 0.62f, h * 0.78f); lineTo(w * 0.52f, h * 0.4f); lineTo(w * 0.52f, h * 0.18f); close()
            }
            drawPath(flask, tint, style = Stroke(width = w * 0.03f))
            drawPath(Path().apply { moveTo(w * 0.28f, h * 0.6f); lineTo(w * 0.56f, h * 0.6f); lineTo(w * 0.6f, h * 0.75f); lineTo(w * 0.24f, h * 0.75f); close() }, Cyan.copy(alpha = 0.6f))
            drawLine(Ink, Offset(w * 0.68f, h * 0.5f), Offset(w * 0.92f, h * 0.74f), strokeWidth = w * 0.045f, cap = StrokeCap.Round)
            drawCircle(Ink, radius = w * 0.12f, center = Offset(w * 0.64f, h * 0.36f), style = Stroke(width = w * 0.03f))
        }
        IllustrationKey.BALANZA_PESO -> {
            drawLine(Ink, Offset(w * 0.5f, h * 0.2f), Offset(w * 0.5f, h * 0.86f), strokeWidth = w * 0.03f, cap = StrokeCap.Round)
            drawLine(Ink, Offset(w * 0.24f, h * 0.3f), Offset(w * 0.76f, h * 0.3f), strokeWidth = w * 0.025f, cap = StrokeCap.Round)
            drawArc(Amber, startAngle = 20f, sweepAngle = 140f, useCenter = false, topLeft = Offset(w * 0.1f, h * 0.32f), size = Size(w * 0.28f, h * 0.18f), style = Stroke(width = w * 0.03f))
            drawArc(Coral, startAngle = 20f, sweepAngle = 140f, useCenter = false, topLeft = Offset(w * 0.62f, h * 0.4f), size = Size(w * 0.28f, h * 0.18f), style = Stroke(width = w * 0.03f))
            drawLine(Ink, Offset(w * 0.3f, h * 0.86f), Offset(w * 0.7f, h * 0.86f), strokeWidth = w * 0.04f, cap = StrokeCap.Round)
        }
        IllustrationKey.VARIABLES_EXPERIMENTO -> {
            drawRoundRect(Violet, topLeft = Offset(w * 0.08f, h * 0.36f), size = Size(w * 0.28f, h * 0.28f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.03f))
            drawRoundRect(Cyan, topLeft = Offset(w * 0.64f, h * 0.36f), size = Size(w * 0.28f, h * 0.28f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.03f))
            drawLine(Ink, Offset(w * 0.38f, h * 0.5f), Offset(w * 0.62f, h * 0.5f), strokeWidth = w * 0.025f, cap = StrokeCap.Round)
            drawPath(Path().apply { moveTo(w * 0.56f, h * 0.44f); lineTo(w * 0.62f, h * 0.5f); lineTo(w * 0.56f, h * 0.56f) }, Ink, style = Stroke(width = w * 0.025f, cap = StrokeCap.Round))
        }
        IllustrationKey.METODO_CIENTIFICO_CICLO -> {
            drawArc(tint, startAngle = -80f, sweepAngle = 280f, useCenter = false, topLeft = Offset(w * 0.2f, h * 0.16f), size = Size(w * 0.6f, h * 0.6f), style = Stroke(width = w * 0.04f, cap = StrokeCap.Round))
            drawPath(Path().apply { moveTo(w * 0.72f, h * 0.16f); lineTo(w * 0.82f, h * 0.22f); lineTo(w * 0.7f, h * 0.26f) }, tint)
            repeat(5) { i ->
                val angle = -80.0 + i * (280.0 / 4)
                val rad = Math.toRadians(angle)
                val x = w * 0.5f + (w * 0.3f * kotlin.math.cos(rad)).toFloat()
                val y = h * 0.46f + (w * 0.3f * kotlin.math.sin(rad)).toFloat()
                drawCircle(Amber, radius = w * 0.025f, center = Offset(x, y))
            }
        }
        IllustrationKey.ANIMAL_RASTRO -> {
            drawCircle(Soil, radius = w * 0.09f, center = Offset(w * 0.4f, h * 0.62f))
            listOf(-1f to -0.3f, 1f to -0.3f, -0.7f to 0.3f, 0.7f to 0.3f).forEach { (dx, dy) ->
                drawCircle(Soil, radius = w * 0.035f, center = Offset(w * 0.4f + dx * w * 0.1f, h * 0.62f + dy * h * 0.14f))
            }
            drawCircle(tint, radius = w * 0.16f, center = Offset(w * 0.68f, h * 0.36f), style = Stroke(width = w * 0.04f))
            drawLine(tint, Offset(w * 0.78f, h * 0.46f), Offset(w * 0.88f, h * 0.56f), strokeWidth = w * 0.045f, cap = StrokeCap.Round)
        }
        IllustrationKey.HOJAS_COMPARAR -> {
            drawPath(leafPath(w * 0.32f, h * 0.5f, w * 0.2f), Leaf)
            drawLine(LeafDark, Offset(w * 0.32f, h * 0.32f), Offset(w * 0.32f, h * 0.68f), strokeWidth = w * 0.015f)
            drawPath(leafPath(w * 0.68f, h * 0.5f, w * 0.14f), LeafDark)
            drawLine(Leaf, Offset(w * 0.68f, h * 0.38f), Offset(w * 0.68f, h * 0.62f), strokeWidth = w * 0.012f)
        }
        IllustrationKey.CADENA_ALIMENTICIA -> {
            drawCircle(Sun, radius = w * 0.1f, center = Offset(w * 0.16f, h * 0.5f))
            drawPath(leafPath(w * 0.5f, h * 0.5f, w * 0.12f), Leaf)
            drawCircle(Amber, radius = w * 0.1f, center = Offset(w * 0.84f, h * 0.5f))
            listOf(0.28f to 0.4f, 0.62f to 0.4f).forEach { (x, tx) ->
                drawPath(Path().apply { moveTo(w * x, h * 0.5f); lineTo(w * tx, h * 0.5f); lineTo(w * (tx - 0.03f), h * 0.46f); moveTo(w * tx, h * 0.5f); lineTo(w * (tx - 0.03f), h * 0.54f) }, Ink, style = Stroke(width = w * 0.018f, cap = StrokeCap.Round))
            }
        }
        IllustrationKey.ECOSISTEMA_ETIQUETAS -> {
            drawCircle(Sun, radius = w * 0.09f, center = Offset(w * 0.78f, h * 0.18f))
            drawLine(Soil, Offset(w * 0.5f, h * 0.86f), Offset(w * 0.1f, h * 0.86f), strokeWidth = w * 0.04f, cap = StrokeCap.Round)
            drawLine(LeafDark, Offset(w * 0.3f, h * 0.86f), Offset(w * 0.3f, h * 0.5f), strokeWidth = w * 0.025f)
            drawPath(Path().apply { moveTo(w * 0.3f, h * 0.3f); lineTo(w * 0.44f, h * 0.56f); lineTo(w * 0.16f, h * 0.56f); close() }, Leaf)
            drawRoundRect(Amber, topLeft = Offset(w * 0.56f, h * 0.5f), size = Size(w * 0.3f, h * 0.14f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.02f))
            drawLine(Ink.copy(alpha = 0.4f), Offset(w * 0.56f, h * 0.57f), Offset(w * 0.34f, h * 0.44f), strokeWidth = w * 0.012f, pathEffect = dash.pathEffect)
        }
        IllustrationKey.CAMUFLAJE_INSECTO -> {
            drawPath(leafPath(w * 0.5f, h * 0.5f, w * 0.32f), Leaf)
            drawOval(LeafDark.copy(alpha = 0.7f), topLeft = Offset(w * 0.42f, h * 0.44f), size = Size(w * 0.16f, h * 0.1f))
            drawCircle(Ink, radius = w * 0.014f, center = Offset(w * 0.56f, h * 0.47f))
            drawCircle(tint, radius = w * 0.34f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(width = w * 0.015f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(w * 0.02f, w * 0.02f))))
        }
        IllustrationKey.HABITAT_ANIMAL -> {
            val roof = Path().apply { moveTo(w * 0.24f, h * 0.5f); lineTo(w * 0.5f, h * 0.24f); lineTo(w * 0.76f, h * 0.5f) }
            drawPath(roof, Soil, style = Stroke(width = w * 0.04f, cap = StrokeCap.Round))
            drawRoundRect(Amber.copy(alpha = 0.5f), topLeft = Offset(w * 0.3f, h * 0.5f), size = Size(w * 0.4f, h * 0.3f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.02f))
            drawCircle(Coral, radius = w * 0.08f, center = Offset(w * 0.5f, h * 0.66f))
            drawLine(LeafDark, Offset(w * 0.86f, h * 0.8f), Offset(w * 0.86f, h * 0.5f), strokeWidth = w * 0.025f)
            drawPath(leafPath(w * 0.86f, h * 0.42f, w * 0.1f), Leaf)
        }
        IllustrationKey.CICLO_VIDA -> {
            val radii = listOf(0.05f, 0.07f, 0.09f, 0.11f)
            val colors = listOf(Amber, Coral, Violet, Leaf)
            radii.forEachIndexed { i, r ->
                val angle = -90.0 + i * 90.0
                val rad = Math.toRadians(angle)
                val x = w * 0.5f + (w * 0.28f * kotlin.math.cos(rad)).toFloat()
                val y = h * 0.5f + (w * 0.28f * kotlin.math.sin(rad)).toFloat()
                drawCircle(colors[i], radius = w * r, center = Offset(x, y))
            }
            drawCircle(Ink.copy(alpha = 0.2f), radius = w * 0.28f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(width = w * 0.012f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(w * 0.015f, w * 0.015f))))
        }
        IllustrationKey.CICLO_AGUA -> {
            drawArc(Sun, startAngle = 180f, sweepAngle = 180f, useCenter = true, topLeft = Offset(w * 0.06f, h * 0.06f), size = Size(w * 0.28f, h * 0.28f))
            drawOval(Cloud, topLeft = Offset(w * 0.5f, h * 0.16f), size = Size(w * 0.4f, h * 0.2f))
            val water = Path().apply { moveTo(w * 0.12f, h * 0.86f); quadraticBezierTo(w * 0.3f, h * 0.78f, w * 0.5f, h * 0.86f); quadraticBezierTo(w * 0.7f, h * 0.94f, w * 0.9f, h * 0.86f) }
            drawPath(water, Sky, style = Stroke(width = w * 0.03f, cap = StrokeCap.Round))
            drawPath(Path().apply { moveTo(w * 0.68f, h * 0.72f); lineTo(w * 0.68f, h * 0.4f); lineTo(w * 0.63f, h * 0.48f); moveTo(w * 0.68f, h * 0.4f); lineTo(w * 0.73f, h * 0.48f) }, Sky, style = Stroke(width = w * 0.02f, cap = StrokeCap.Round))
        }
        IllustrationKey.CLIMA_ESTACIONES -> {
            val colors = listOf(Sun, Sky, Coral, Leaf)
            colors.forEachIndexed { i, c -> drawCircle(c, radius = w * 0.1f, center = Offset(w * (0.16f + i * 0.24f), h * 0.5f)) }
        }
        IllustrationKey.VIENTO_ENERGIA -> {
            repeat(3) { i ->
                val wave = Path().apply {
                    moveTo(w * 0.08f, h * (0.3f + i * 0.18f))
                    quadraticBezierTo(w * 0.3f, h * (0.22f + i * 0.18f), w * 0.5f, h * (0.3f + i * 0.18f))
                    quadraticBezierTo(w * 0.6f, h * (0.34f + i * 0.18f), w * 0.7f, h * (0.3f + i * 0.18f))
                }
                drawPath(wave, Cyan, style = Stroke(width = w * 0.025f, cap = StrokeCap.Round))
            }
            drawCircle(Ink, radius = w * 0.02f, center = Offset(w * 0.82f, h * 0.5f))
            listOf(0.0, 120.0, 240.0).forEach { angle ->
                val rad = Math.toRadians(angle)
                val x = w * 0.82f + (w * 0.14f * kotlin.math.cos(rad)).toFloat()
                val y = h * 0.5f + (w * 0.14f * kotlin.math.sin(rad)).toFloat()
                drawLine(Amber, Offset(w * 0.82f, h * 0.5f), Offset(x, y), strokeWidth = w * 0.03f, cap = StrokeCap.Round)
            }
        }
        IllustrationKey.TEMPERATURA_SUPERFICIES -> {
            drawRoundRect(Color.White, topLeft = Offset(w * 0.42f, h * 0.14f), size = Size(w * 0.1f, h * 0.5f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.05f))
            drawRoundRect(Coral, topLeft = Offset(w * 0.44f, h * 0.32f), size = Size(w * 0.06f, h * 0.32f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.03f))
            drawCircle(Coral, radius = w * 0.09f, center = Offset(w * 0.47f, h * 0.68f))
            repeat(6) { i -> drawCircle(Soil, radius = w * 0.012f, center = Offset(w * (0.1f + (i % 3) * 0.06f), h * (0.8f + (i / 3) * 0.06f))) }
            repeat(4) { i -> drawLine(Leaf, Offset(w * (0.72f + i * 0.05f), h * 0.86f), Offset(w * (0.72f + i * 0.05f), h * 0.76f), strokeWidth = w * 0.012f, cap = StrokeCap.Round) }
        }
        IllustrationKey.AMBIENTE_CUIDADO -> {
            repeat(3) { i ->
                val start = i * 120f
                drawArc(Leaf, startAngle = start, sweepAngle = 90f, useCenter = false, topLeft = Offset(w * 0.2f, h * 0.2f), size = Size(w * 0.6f, h * 0.6f), style = Stroke(width = w * 0.035f, cap = StrokeCap.Round))
            }
            drawPath(leafPath(w * 0.5f, h * 0.5f, w * 0.12f), LeafDark)
        }
        IllustrationKey.CLIMA_DATOS_PATRON -> {
            val points = listOf(0.75f, 0.55f, 0.62f, 0.35f, 0.4f)
            val path = Path().apply {
                points.forEachIndexed { i, v -> val x = w * (0.12f + i * 0.19f); val y = h * v; if (i == 0) moveTo(x, y) else lineTo(x, y) }
            }
            drawPath(path, Violet, style = Stroke(width = w * 0.025f, cap = StrokeCap.Round))
            points.forEachIndexed { i, v -> drawCircle(Cyan, radius = w * 0.02f, center = Offset(w * (0.12f + i * 0.19f), h * v)) }
            drawCircle(Sun, radius = w * 0.06f, center = Offset(w * 0.14f, h * 0.16f))
        }
        IllustrationKey.PREDICCION_CLIMA -> {
            drawCircle(Sun, radius = w * 0.12f, center = Offset(w * 0.34f, h * 0.32f))
            drawOval(Cloud, topLeft = Offset(w * 0.32f, h * 0.4f), size = Size(w * 0.5f, h * 0.28f))
            drawCircle(Ink, radius = w * 0.09f, center = Offset(w * 0.7f, h * 0.66f), style = Stroke(width = w * 0.03f))
            drawLine(Ink, Offset(w * 0.66f, h * 0.62f), Offset(w * 0.7f, h * 0.6f), strokeWidth = w * 0.025f, cap = StrokeCap.Round)
            drawCircle(Ink, radius = w * 0.012f, center = Offset(w * 0.7f, h * 0.72f))
        }
        IllustrationKey.GRAFICO_BARRAS -> {
            val bars = listOf(0.35f, 0.6f, 0.45f, 0.8f)
            val colors = listOf(Violet, Cyan, Amber, Coral)
            bars.forEachIndexed { i, hf ->
                val x = w * (0.16f + i * 0.2f)
                drawLine(colors[i], Offset(x, h * 0.86f), Offset(x, h * (0.86f - hf * 0.66f)), strokeWidth = w * 0.12f, cap = StrokeCap.Round)
            }
        }
        IllustrationKey.COMPARAR_DATOS -> {
            listOf(0.18f, 0.62f).forEachIndexed { g, base ->
                val bars = if (g == 0) listOf(0.3f, 0.5f) else listOf(0.6f, 0.4f)
                bars.forEachIndexed { i, hf ->
                    val x = w * (base + i * 0.14f)
                    drawLine(if (g == 0) Violet else Coral, Offset(x, h * 0.82f), Offset(x, h * (0.82f - hf * 0.6f)), strokeWidth = w * 0.1f, cap = StrokeCap.Round)
                }
            }
            drawLine(Ink.copy(alpha = 0.3f), Offset(w * 0.5f, h * 0.2f), Offset(w * 0.5f, h * 0.86f), strokeWidth = w * 0.015f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(w * 0.02f, w * 0.02f)))
        }
        IllustrationKey.PATRON_NUMERICO -> {
            val sizes = listOf(0.07f, 0.09f, 0.11f, 0.13f)
            sizes.forEachIndexed { i, r -> drawCircle(Cyan, radius = w * r, center = Offset(w * (0.16f + i * 0.2f), h * 0.5f)) }
            drawCircle(Ink.copy(alpha = 0.15f), radius = w * 0.13f, center = Offset(w * 0.92f, h * 0.5f))
        }
        IllustrationKey.CLASIFICAR_DATOS -> {
            drawPath(Path().apply { moveTo(w * 0.3f, h * 0.16f); lineTo(w * 0.7f, h * 0.16f); lineTo(w * 0.58f, h * 0.5f); lineTo(w * 0.42f, h * 0.5f); close() }, tint, style = Stroke(width = w * 0.03f))
            repeat(4) { i -> drawCircle(Amber, radius = w * 0.02f, center = Offset(w * (0.36f + i * 0.1f), h * 0.1f)) }
            drawRoundRect(Violet.copy(alpha = 0.5f), topLeft = Offset(w * 0.12f, h * 0.62f), size = Size(w * 0.3f, h * 0.24f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.02f))
            drawRoundRect(Coral.copy(alpha = 0.5f), topLeft = Offset(w * 0.58f, h * 0.62f), size = Size(w * 0.3f, h * 0.24f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.02f))
        }
        IllustrationKey.CRONOLOGIA_DATOS -> {
            drawLine(Ink.copy(alpha = 0.4f), Offset(w * 0.1f, h * 0.5f), Offset(w * 0.9f, h * 0.5f), strokeWidth = w * 0.015f)
            val positions = listOf(0.16f, 0.4f, 0.6f, 0.86f)
            positions.forEachIndexed { i, x -> drawCircle(listOf(Amber, Cyan, Violet, Coral)[i], radius = w * 0.045f, center = Offset(w * x, h * 0.5f)) }
        }
        IllustrationKey.PREDICCION_DATOS -> {
            val solid = Path().apply { moveTo(w * 0.1f, h * 0.7f); lineTo(w * 0.35f, h * 0.5f); lineTo(w * 0.55f, h * 0.58f) }
            drawPath(solid, Violet, style = Stroke(width = w * 0.025f, cap = StrokeCap.Round))
            val dashed = Path().apply { moveTo(w * 0.55f, h * 0.58f); lineTo(w * 0.9f, h * 0.2f) }
            drawPath(dashed, Cyan, style = Stroke(width = w * 0.025f, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(w * 0.03f, w * 0.025f))))
            drawPath(Path().apply { moveTo(w * 0.82f, h * 0.22f); lineTo(w * 0.9f, h * 0.2f); lineTo(w * 0.86f, h * 0.28f) }, Cyan)
        }
        IllustrationKey.GRAFICO_PREGUNTA -> {
            val bars = listOf(0.4f, 0.65f, 0.5f)
            bars.forEachIndexed { i, hf -> drawLine(listOf(Violet, Cyan, Amber)[i], Offset(w * (0.14f + i * 0.14f), h * 0.86f), Offset(w * (0.14f + i * 0.14f), h * (0.86f - hf * 0.6f)), strokeWidth = w * 0.09f, cap = StrokeCap.Round) }
            drawCircle(Coral, radius = w * 0.14f, center = Offset(w * 0.78f, h * 0.3f))
            drawLine(Color.White, Offset(w * 0.74f, h * 0.26f), Offset(w * 0.78f, h * 0.24f), strokeWidth = w * 0.02f, cap = StrokeCap.Round)
            drawCircle(Color.White, radius = w * 0.01f, center = Offset(w * 0.78f, h * 0.34f))
        }
    }
}

/**
 * Visualización reactiva del simulador (planta/movimiento/temperatura): se
 * redibuja en vivo mientras el jugador mueve los sliders, para que el
 * experimento "interactivo" también se vea y no sean solo números.
 */
@Composable
fun ExperimentLiveVisual(
    experimentId: String?,
    parameters: List<ExperimentParameterEntity>,
    values: Map<String, Float>,
    modifier: Modifier = Modifier,
) {
    fun normalized(name: String): Float {
        val param = parameters.firstOrNull { it.name == name } ?: return 0.5f
        val value = values[name] ?: param.defaultValue
        val range = param.maxValue - param.minValue
        if (range <= 0f) return 0.5f
        return ((value - param.minValue) / range).coerceIn(0f, 1f)
    }

    Surface(shape = RoundedCornerShape(24.dp), color = Color.Transparent, modifier = modifier) {
        Box(contentAlignment = Alignment.Center) {
            sceneBackdrop(Sky)
            Box(modifier = Modifier.padding(18.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                when {
                    experimentId?.startsWith("exp_plant") == true -> {
                        val growth = (normalized("luz") + normalized("agua")) / 2f
                        drawCircle(Sun, radius = w * 0.08f, center = Offset(w * 0.84f, h * 0.14f))
                        val pot = Path().apply { moveTo(w * 0.34f, h * 0.78f); lineTo(w * 0.66f, h * 0.78f); lineTo(w * 0.58f, h * 0.94f); lineTo(w * 0.42f, h * 0.94f); close() }
                        drawPath(pot, Soil)
                        val stemTop = h * (0.78f - growth * 0.5f)
                        drawLine(LeafDark, Offset(w * 0.5f, h * 0.78f), Offset(w * 0.5f, stemTop), strokeWidth = w * 0.025f, cap = StrokeCap.Round)
                        val leafColor = lerp(Color(0xFFC9A97E), Leaf, growth)
                        val leafSize = w * (0.08f + growth * 0.1f)
                        drawPath(leafPath2(w * 0.5f - leafSize * 0.7f, stemTop + leafSize * 0.4f, leafSize), leafColor)
                        drawPath(leafPath2(w * 0.5f + leafSize * 0.7f, stemTop + leafSize * 0.7f, leafSize * 0.8f), leafColor)
                    }
                    experimentId?.startsWith("exp_move") == true -> {
                        val friction = normalized("superficie")
                        val push = (normalized("fuerza") * 0.7f + (1f - normalized("peso")) * 0.3f)
                        val travel = (push * (1f - friction * 0.6f)).coerceIn(0.05f, 0.95f)
                        val ramp = Path().apply { moveTo(w * 0.08f, h * 0.3f); lineTo(w * 0.92f, h * 0.82f) }
                        drawPath(ramp, Ink.copy(alpha = 0.3f), style = Stroke(width = w * 0.02f, cap = StrokeCap.Round))
                        val bx = w * (0.08f + travel * 0.84f)
                        val by = h * (0.3f + travel * 0.52f)
                        repeat(3) { i -> drawCircle(Violet.copy(alpha = 0.25f - i * 0.07f), radius = w * (0.05f - i * 0.01f), center = Offset(bx - (i + 1) * w * 0.05f, by - (i + 1) * h * 0.03f)) }
                        drawCircle(Violet, radius = w * 0.06f, center = Offset(bx, by))
                    }
                    experimentId?.startsWith("exp_temp") == true -> {
                        val heat = normalized("temperaturaInicial")
                        val insulation = normalized("aislamiento")
                        drawRoundRect(Color.White, topLeft = Offset(w * 0.42f, h * 0.1f), size = Size(w * 0.1f, h * 0.56f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.05f))
                        val fillTop = h * (0.62f - heat * 0.5f)
                        drawRoundRect(lerp(Sky, Coral, heat), topLeft = Offset(w * 0.44f, fillTop), size = Size(w * 0.06f, h * 0.66f - fillTop), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.03f))
                        drawCircle(lerp(Sky, Coral, heat), radius = w * 0.09f, center = Offset(w * 0.47f, h * 0.72f))
                        drawCircle(Ink.copy(alpha = 0.15f + insulation * 0.35f), radius = w * 0.16f, center = Offset(w * 0.47f, h * 0.72f), style = Stroke(width = w * 0.02f))
                    }
                    else -> {
                        drawCircle(Ink.copy(alpha = 0.15f), radius = w * 0.2f, center = Offset(w * 0.5f, h * 0.5f))
                    }
                }
            }
            }
        }
    }
}

private fun leafPath2(cx: Float, cy: Float, r: Float): Path = Path().apply {
    moveTo(cx, cy - r)
    cubicTo(cx + r, cy - r * 0.5f, cx + r * 0.8f, cy + r * 0.8f, cx, cy + r)
    cubicTo(cx - r * 0.8f, cy + r * 0.8f, cx - r, cy - r * 0.5f, cx, cy - r)
    close()
}

/** Estrella simple para acentos (sparkle). Reutiliza la misma matemática que StarShape pero como helper directo. */
private fun StarShape2(scope: androidx.compose.ui.graphics.drawscope.DrawScope, color: Color, center: Offset, r: Float) {
    with(scope) {
        val path = Path()
        for (i in 0 until 8) {
            val angle = Math.PI / 4 * i
            val radius = if (i % 2 == 0) r else r * 0.4f
            val x = center.x + (radius * kotlin.math.cos(angle)).toFloat()
            val y = center.y + (radius * kotlin.math.sin(angle)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        drawPath(path, color)
    }
}
