package app.itochan.aes67

import android.app.Application
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.net.wifi.WifiManager
import androidx.core.content.getSystemService
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket
import java.net.SocketException


class ReceiverViewModel(application: Application) : AndroidViewModel(application),
    LifecycleObserver {

    val multicastAddress = MutableLiveData<String>()
    val remoteAddress = MutableLiveData<String>()

    private lateinit var multicastLock: WifiManager.MulticastLock
    private lateinit var socket: MulticastSocket
    private lateinit var group: InetAddress
    private lateinit var audioTrack: AudioTrack
    private var receiverJob: Job? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        acquireMulticastPacket()
        startReceiver()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        multicastLock.release()
        stopReceiver()
    }

    private fun acquireMulticastPacket() {
        val wifiManager = getApplication<Application>().getSystemService<WifiManager>()!!
        multicastLock = wifiManager.createMulticastLock(MULTICAST_TAG).apply {
            setReferenceCounted(true)
            acquire()
        }
    }

    private fun startReceiver() {
        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLING_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                    .build()
            )
            .build()

        socket = MulticastSocket(AES67_PORT).apply {
            group = InetAddress.getByName(MULTICAST_ADDRESS)
            joinGroup(group)
        }
        multicastAddress.value = MULTICAST_ADDRESS

        receiverJob = viewModelScope.launch(Dispatchers.Default) {
            audioTrack.play()
            try {
                val buf24 = ByteArray(PACKET_LENGTH)
                while (true) {
                    val recv = DatagramPacket(buf24, buf24.size)
                    socket.receive(recv)
                    remoteAddress.postValue(recv.address.hostAddress)

                    val buf16 = ByteArray(BUFFER_16BIT_SIZE) {
                        buf24[it + (it / 2) + PAYLOAD_OFFSET]
                    }
                    audioTrack.write(buf16, 0, BUFFER_16BIT_SIZE)
                }
            } catch (e: SocketException) {
            }
        }
    }

    private fun stopReceiver() {
        socket.apply {
            leaveGroup(group)
            close()
        }
        audioTrack.stop()
        receiverJob?.cancel()
    }

    companion object {
        private val MULTICAST_TAG: String = ReceiverViewModel::class.java.name
        private const val MULTICAST_ADDRESS = "239.69.128.165"
        private const val AES67_PORT = 5004
        private const val SAMPLING_RATE = 48000
        private const val PACKET_LENGTH = 300
        private const val PAYLOAD_OFFSET = 12
        private const val BUFFER_16BIT_SIZE = ((PACKET_LENGTH - PAYLOAD_OFFSET) / 1.5).toInt()
    }
}
