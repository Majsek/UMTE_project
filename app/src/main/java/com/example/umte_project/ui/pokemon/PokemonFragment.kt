package com.example.umte_project.ui.pokemon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.umte_project.databinding.FragmentPokemonBinding

class PokemonFragment : Fragment() {

    private var _binding: FragmentPokemonBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val pokemonViewModel =
            ViewModelProvider(this).get(PokemonViewModel::class.java)

        _binding = FragmentPokemonBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textPokemon
        pokemonViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        binding.buttonGetPokemon.setOnClickListener(::onGetPokemonButtonClick)

        return root
    }

    fun onGetPokemonButtonClick(view: View) {
        binding.textPokemon.text = "Changed text!"
        //We need to cast the view to a Button, because view itself does not have text property.
        (view as Button).text = "Clicked!"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}