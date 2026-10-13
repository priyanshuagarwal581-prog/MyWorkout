package com.example.util

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

object AudioCueManager {
    private const val TAG = "AudioCueManager"
    private var toneGenerator: ToneGenerator? = null

    var isMuted: Boolean = false

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 90)
        } catch (e: Throwable) {
            Log.w(TAG, "Failed to initialize ToneGenerator: ${e.message}")
        }
    }

    /**
     * Subtle warning beep during countdown at 10s, 5s, 4s, 3s, 2s, 1s.
     */
    fun playWarningBeep() {
        if (isMuted) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 80)
        } catch (e: Throwable) {
            Log.w(TAG, "Tone error: ${e.message}")
        }
    }

    fun playCountdownBeep() {
        playWarningBeep()
    }

    /**
     * Distinct high-pitch chime at 0s to signal transition into WORK interval.
     */
    fun playWorkTransition() {
        if (isMuted) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 300)
        } catch (e: Throwable) {
            Log.w(TAG, "Tone error: ${e.message}")
        }
    }

    /**
     * Distinct transition chime at 0s into REST interval.
     */
    fun playRestTransition() {
        if (isMuted) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 250)
        } catch (e: Throwable) {
            Log.w(TAG, "Tone error: ${e.message}")
        }
    }

    /**
     * Victory completion fanfare when all sets & exercises finish.
     */
    fun playWorkoutComplete() {
        if (isMuted) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 500)
        } catch (e: Throwable) {
            Log.w(TAG, "Tone error: ${e.message}")
        }
    }

    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (e: Throwable) {
            Log.w(TAG, "Release error: ${e.message}")
        }
    }
}
