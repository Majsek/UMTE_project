package com.example.umte_project.ui.fighters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.umte_project.databinding.FragmentFightersBinding

class FightersFragment : Fragment() {

    private var _binding: FragmentFightersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fightersViewModel =
            ViewModelProvider(this).get(FightersViewModel::class.java)

        _binding = FragmentFightersBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textFighters
        fightersViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}