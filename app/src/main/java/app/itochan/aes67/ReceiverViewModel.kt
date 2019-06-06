package app.itochan.aes67

import android.app.Application
import android.net.wifi.WifiManager
import androidx.core.content.getSystemService
import androidx.lifecycle.*


class ReceiverViewModel(application: Application) : AndroidViewModel(application),
    LifecycleObserver {

    private lateinit var multicastLock: WifiManager.MulticastLock

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        acquireMulticastPacket()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        multicastLock.release()
    }

    private fun acquireMulticastPacket() {
        val wifiManager = getApplication<Application>().getSystemService<WifiManager>()!!
        multicastLock = wifiManager.createMulticastLock(MULTICAST_TAG).apply {
            setReferenceCounted(true)
            acquire()
        }
    }

    companion object {
        private val MULTICAST_TAG: String = ReceiverViewModel::class.java.name
    }
}
