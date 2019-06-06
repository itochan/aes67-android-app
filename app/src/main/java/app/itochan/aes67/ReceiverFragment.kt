package app.itochan.aes67

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

class ReceiverFragment : Fragment() {

    private val viewModel by viewModels<ReceiverViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(viewModel)
    }
}
