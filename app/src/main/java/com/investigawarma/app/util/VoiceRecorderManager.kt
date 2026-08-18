package com.investigawarma.app.util

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.IOException

/**
 * Gestiona la grabación y reproducción real del "diario científico hablado" usando
 * las APIs nativas de Android (MediaRecorder / MediaPlayer). No usa reconocimiento
 * de voz ni servicios online: todo el audio se guarda en el almacenamiento privado
 * de la app (filesDir/voice_journal) y nunca sale del dispositivo.
 *
 * Límite: 60 segundos por grabación (se detiene automáticamente).
 */
class VoiceRecorderManager(private val context: Context) {

    companion object {
        const val MAX_DURATION_MS = 60_000
    }

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var currentFile: File? = null
    private var startTimeMs: Long = 0L

    private fun voiceDir(): File =
        File(context.filesDir, "voice_journal").apply { if (!exists()) mkdirs() }

    /** Empieza a grabar. Devuelve el archivo destino, o null si no se pudo iniciar. */
    fun startRecording(): File? {
        stopPlayback()
        val file = File(voiceDir(), "voice_${System.currentTimeMillis()}.m4a")
        val mediaRecorder = createRecorder()
        return try {
            mediaRecorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setMaxDuration(MAX_DURATION_MS)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            recorder = mediaRecorder
            currentFile = file
            startTimeMs = System.currentTimeMillis()
            file
        } catch (e: IOException) {
            mediaRecorder.release()
            null
        } catch (e: IllegalStateException) {
            mediaRecorder.release()
            null
        }
    }

    /** Detiene la grabación en curso. Devuelve el archivo y la duración real en segundos. */
    fun stopRecording(): Pair<File, Int>? {
        val r = recorder ?: return null
        val file = currentFile ?: return null
        return try {
            r.stop()
            r.release()
            recorder = null
            val durationSeconds = ((System.currentTimeMillis() - startTimeMs) / 1000).toInt().coerceIn(1, 60)
            file to durationSeconds
        } catch (e: RuntimeException) {
            // stop() sin datos grabados (grabación demasiado corta o cancelada)
            r.release()
            recorder = null
            file.delete()
            null
        }
    }

    fun cancelRecording() {
        try {
            recorder?.stop()
        } catch (e: RuntimeException) {
            // Ignorado: puede fallar si no hubo suficientes datos, el archivo se borra igualmente.
        }
        recorder?.release()
        recorder = null
        currentFile?.delete()
        currentFile = null
    }

    fun play(filePath: String, onCompletion: () -> Unit) {
        stopPlayback()
        player = MediaPlayer().apply {
            try {
                setDataSource(filePath)
                setOnCompletionListener { onCompletion() }
                prepare()
                start()
            } catch (e: IOException) {
                onCompletion()
            }
        }
    }

    fun stopPlayback() {
        player?.let {
            try {
                if (it.isPlaying) it.stop()
            } catch (e: IllegalStateException) {
                // ya detenido
            }
            it.release()
        }
        player = null
    }

    fun deleteFile(filePath: String): Boolean = File(filePath).let { it.exists() && it.delete() }

    @Suppress("DEPRECATION")
    private fun createRecorder(): MediaRecorder =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }
}
