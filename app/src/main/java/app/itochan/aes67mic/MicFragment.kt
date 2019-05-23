package app.itochan.aes67mic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.itochan.aes67mic.databinding.FragmentMicBinding

class MicFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMicBinding.inflate(inflater, container, false)
        return binding.root
    }
}
