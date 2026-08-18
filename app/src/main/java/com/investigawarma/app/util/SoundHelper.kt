package com.investigawarma.app.util

import android.media.AudioManager
import android.media.ToneGenerator

/**
 * Efectos de sonido cortos para feedback de interacción (correcto, incorrecto,
 * desbloqueo). Usa ToneGenerator del sistema en vez de archivos de audio
 * embebidos para mantener el proyecto ligero; se puede silenciar por completo
 * desde Ajustes (soundEnabled en el perfil del jugador).
 */
class SoundHelper {

    private val toneGenerator by lazy {
        runCatching { ToneGenerator(AudioManager.STREAM_MUSIC, 70) }.getOrNull()
    }

    fun playSuccess(enabled: Boolean) {
        if (!enabled) return
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 150)
    }

    fun playError(enabled: Boolean) {
        if (!enabled) return
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 200)
    }

    fun playUnlock(enabled: Boolean) {
        if (!enabled) return
        toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 250)
    }

    fun playTap(enabled: Boolean) {
        if (!enabled) return
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 60)
    }

    fun release() {
        toneGenerator?.release()
    }
}
