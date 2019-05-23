package app.itochan.aes67mic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MicViewModel : ViewModel() {

    val micButtonText = MutableLiveData<Int>()
    val micLiveData = MutableLiveData<Unit>()

    init {
        micButtonText.value = R.string.button_mic_on
    }

    fun clickMicButton() {
        toggleMicButtonText()
        micLiveData.value = Unit
    }

    private fun toggleMicButtonText() {
        micButtonText.value = if (micButtonText.value == R.string.button_mic_on) {
            R.string.button_mic_off
        } else {
            R.string.button_mic_on
        }
    }
}
