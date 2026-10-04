package com.example.core.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * High-performance, offline arcade sound synthesizer.
 * Generates instant playful sound effects using PCM audio synthesis.
 */
class ArcadeSoundManager {

    private val scope = CoroutineScope(Dispatchers.Default)

    var isSoundEnabled: Boolean = true
    var isMusicEnabled: Boolean = true
    var masterVolume: Float = 0.8f

    fun playClick() {
        if (!isSoundEnabled) return
        scope.launch {
            playTone(frequency = 600f, durationMs = 40, waveform = Waveform.SQUARE, volume = 0.3f * masterVolume)
        }
    }

    fun playPop() {
        if (!isSoundEnabled) return
        scope.launch {
            // Rising chirp pop
            playChirp(startFreq = 300f, endFreq = 950f, durationMs = 60, volume = 0.5f * masterVolume)
        }
    }

    fun playCoin() {
        if (!isSoundEnabled) return
        scope.launch {
            // Classic 2-tone arcade coin (B5 -> E6)
            playTone(frequency = 987f, durationMs = 60, waveform = Waveform.SINE, volume = 0.6f * masterVolume)
            playTone(frequency = 1318f, durationMs = 120, waveform = Waveform.SINE, volume = 0.6f * masterVolume)
        }
    }

    fun playJump() {
        if (!isSoundEnabled) return
        scope.launch {
            playChirp(startFreq = 220f, endFreq = 660f, durationMs = 90, volume = 0.5f * masterVolume)
        }
    }

    fun playCombo(comboLevel: Int) {
        if (!isSoundEnabled) return
        scope.launch {
            val base = 440f + (comboLevel.coerceAtMost(10) * 80f)
            playTone(frequency = base, durationMs = 50, waveform = Waveform.TRIANGLE, volume = 0.5f * masterVolume)
            playTone(frequency = base * 1.25f, durationMs = 70, waveform = Waveform.TRIANGLE, volume = 0.6f * masterVolume)
        }
    }

    fun playSuccess() {
        if (!isSoundEnabled) return
        scope.launch {
            val melody = listOf(523f, 659f, 784f, 1046f) // C5, E5, G5, C6
            melody.forEach { f ->
                playTone(frequency = f, durationMs = 80, waveform = Waveform.SINE, volume = 0.5f * masterVolume)
            }
        }
    }

    fun playFailure() {
        if (!isSoundEnabled) return
        scope.launch {
            playChirp(startFreq = 400f, endFreq = 160f, durationMs = 160, volume = 0.5f * masterVolume)
        }
    }

    fun playLevelUp() {
        if (!isSoundEnabled) return
        scope.launch {
            val melody = listOf(440f, 554f, 659f, 880f)
            melody.forEach { f ->
                playTone(frequency = f, durationMs = 90, waveform = Waveform.TRIANGLE, volume = 0.6f * masterVolume)
            }
        }
    }

    fun playCountDownTick() {
        if (!isSoundEnabled) return
        scope.launch {
            playTone(frequency = 440f, durationMs = 60, waveform = Waveform.SQUARE, volume = 0.4f * masterVolume)
        }
    }

    fun playCountDownGo() {
        if (!isSoundEnabled) return
        scope.launch {
            playTone(frequency = 880f, durationMs = 180, waveform = Waveform.SQUARE, volume = 0.6f * masterVolume)
        }
    }

    enum class Waveform { SINE, SQUARE, TRIANGLE }

    private fun playTone(frequency: Float, durationMs: Int, waveform: Waveform, volume: Float) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
            val buffer = ShortArray(numSamples)
            val angularFreq = 2.0 * Math.PI * frequency / sampleRate

            for (i in 0 until numSamples) {
                // Envelope fade-out to prevent clicks
                val envelope = (1.0 - (i.toDouble() / numSamples)).toFloat()
                val wave = when (waveform) {
                    Waveform.SINE -> sin(angularFreq * i).toFloat()
                    Waveform.SQUARE -> if (sin(angularFreq * i) >= 0) 0.8f else -0.8f
                    Waveform.TRIANGLE -> (2.0 / Math.PI * Math.asin(sin(angularFreq * i))).toFloat()
                }
                buffer[i] = (wave * Short.MAX_VALUE * volume * envelope).toInt().toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            Thread.sleep(durationMs.toLong() + 20)
            audioTrack.release()
        } catch (_: Exception) {
            // Graceful fallback if audio is not supported
        }
    }

    private fun playChirp(startFreq: Float, endFreq: Float, durationMs: Int, volume: Float) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / numSamples
                val currentFreq = startFreq + (endFreq - startFreq) * t
                val angularFreq = 2.0 * Math.PI * currentFreq / sampleRate
                val envelope = (1.0 - t).toFloat()
                val sample = (sin(angularFreq * i) * Short.MAX_VALUE * volume * envelope).toInt()
                buffer[i] = sample.toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            Thread.sleep(durationMs.toLong() + 20)
            audioTrack.release()
        } catch (_: Exception) {
            // Graceful fallback
        }
    }
}
