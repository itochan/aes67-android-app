package app.itochan.aes67mic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import app.itochan.aes67mic.databinding.FragmentMicBinding
import com.google.android.material.snackbar.Snackbar

class MicFragment : Fragment() {

    private val viewModel: MicViewModel by viewModels()

    private lateinit var binding: FragmentMicBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMicBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.recordingStatus.observe(viewLifecycleOwner, Observer {
            val text = when (it!!) {
                MicViewModel.Status.STARTED -> "Started"
                MicViewModel.Status.STOPPED -> "Stopped"
            }
            Snackbar.make(binding.micButton, text, Snackbar.LENGTH_SHORT).show()
        })
    }
}
