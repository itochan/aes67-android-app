package app.itochan.aes67mic

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MicViewModel : ViewModel() {

    private val handler = Handler()
    private val statusChecker = object : Runnable {
        override fun run() {
            showAmplitude()
            handler.postDelayed(this, 100)
        }
    }

    private val meter = SoundMeter()

    val recordingStatus = MutableLiveData<Status>()
    val micButtonText = MutableLiveData<Int>()

    val statusText = MutableLiveData<String>()


    init {
        recordingStatus.value = Status.STOPPED
        micButtonText.value = R.string.button_mic_on
    }

    fun clickMicButton() {
        when (recordingStatus.value) {
            Status.STOPPED -> startRecord()
            Status.STARTED -> stopRecord()
        }
    }

    private fun startRecord() {
        recordingStatus.value = Status.STARTED
        micButtonText.value = R.string.button_mic_off
        statusText.value = "Recording..."

        meter.start()
        handler.post(statusChecker)
    }

    private fun stopRecord() {
        recordingStatus.value = Status.STOPPED
        micButtonText.value = R.string.button_mic_on
        statusText.value = "Stopped"

        meter.stop()
        handler.removeCallbacks(statusChecker)
    }

    private fun showAmplitude() {
        val amplitude = meter.amplitude
        statusText.value = "Amplitude: $amplitude"
    }

    enum class Status { STARTED, STOPPED }
}
