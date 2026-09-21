package com.smritisetu.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread

class WavAudioRecorder(private val sampleRate: Int = 16000) {
    private var recorder: AudioRecord? = null
    @Volatile private var isRecording = false
    private var pcmFile: File? = null

    @SuppressLint("MissingPermission")
    fun start(context: Context) {
        val minBuf = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        recorder = AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minBuf)
        pcmFile = File(context.cacheDir, "rec_${System.currentTimeMillis()}.pcm")
        isRecording = true
        recorder?.startRecording()

        thread {
            FileOutputStream(pcmFile).use { fos ->
                val buffer = ByteArray(minBuf)
                while (isRecording) {
                    val read = recorder?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0) fos.write(buffer, 0, read)
                }
            }
        }
    }

    /** Stops recording, converts to a proper WAV file, and returns it. */
    fun stop(context: Context): File? {
        isRecording = false
        try { recorder?.stop() } catch (_: Exception) {}
        recorder?.release()
        recorder = null

        val pcm = pcmFile ?: return null
        val wav = File(context.cacheDir, "rec_${System.currentTimeMillis()}.wav")
        writeWav(pcm, wav)
        pcm.delete()
        return wav
    }

    private fun writeWav(pcmFile: File, wavFile: File) {
        val pcmData = pcmFile.readBytes()
        val byteRate = sampleRate * 2
        FileOutputStream(wavFile).use { out ->
            val header = ByteArray(44)
            writeString(header, 0, "RIFF")
            writeInt(header, 4, 36 + pcmData.size)
            writeString(header, 8, "WAVE")
            writeString(header, 12, "fmt ")
            writeInt(header, 16, 16)
            writeShort(header, 20, 1)          // PCM
            writeShort(header, 22, 1)          // mono
            writeInt(header, 24, sampleRate)
            writeInt(header, 28, byteRate)
            writeShort(header, 32, 2)          // block align
            writeShort(header, 34, 16)         // bits per sample
            writeString(header, 36, "data")
            writeInt(header, 40, pcmData.size)
            out.write(header)
            out.write(pcmData)
        }
    }

    private fun writeString(h: ByteArray, o: Int, s: String) { s.forEachIndexed { i, c -> h[o + i] = c.code.toByte() } }
    private fun writeInt(h: ByteArray, o: Int, v: Int) { h[o]=(v and 0xff).toByte(); h[o+1]=((v shr 8) and 0xff).toByte(); h[o+2]=((v shr 16) and 0xff).toByte(); h[o+3]=((v shr 24) and 0xff).toByte() }
    private fun writeShort(h: ByteArray, o: Int, v: Int) { h[o]=(v and 0xff).toByte(); h[o+1]=((v shr 8) and 0xff).toByte() }
}