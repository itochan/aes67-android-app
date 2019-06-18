package app.itochan.aes67

import android.app.Application
import android.net.wifi.WifiManager
import android.util.Log
import androidx.core.content.getSystemService
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket


class ReceiverViewModel(application: Application) : AndroidViewModel(application),
    LifecycleObserver {

    private lateinit var multicastLock: WifiManager.MulticastLock
    private lateinit var socket: MulticastSocket
    private lateinit var group: InetAddress
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
        socket = MulticastSocket(AES67_PORT).apply {
            group = InetAddress.getByName(MULTICAST_ADDRESS)
            joinGroup(group)
        }

        receiverJob = viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                val buf = ByteArray(PACKET_LENGTH)
                val recv = DatagramPacket(buf, buf.size, group, AES67_PORT)
                socket.receive(recv)
            }
        }
    }

    private fun stopReceiver() {
        socket.apply {
            leaveGroup(group)
            close()
        }
        receiverJob?.cancel()
    }

    companion object {
        private val MULTICAST_TAG: String = ReceiverViewModel::class.java.name
        private const val MULTICAST_ADDRESS = "239.69.128.165"
        private const val AES67_PORT = 5004
        private const val PACKET_LENGTH = 342
    }
}
