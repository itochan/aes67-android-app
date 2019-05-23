package app.itochan.aes67mic

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder

class SoundMeter {

    private lateinit var ar: AudioRecord
    private var minSize: Int = 0

    val amplitude: Double
        get() {
            val buffer = ShortArray(minSize)
            ar.read(buffer, 0, minSize)
            var max = 0
            for (s in buffer) {
                if (Math.abs(s.toInt()) > max) {
                    max = Math.abs(s.toInt())
                }
            }
            return max.toDouble()
        }

    fun start() {
        minSize = AudioRecord.getMinBufferSize(
            8000,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        ar = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            8000,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            minSize
        )
        ar.startRecording()
    }

    fun stop() {
        ar.stop()
    }
}
