package com.example.umte_project.ui.fighters

import PokemonAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umte_project.databinding.FragmentFightersBinding
import com.example.umte_project.ui.pokemon.PokemonViewModel
import com.example.umte_project.ui.pokemon.PokemonViewModelFactory

class FightersFragment : Fragment() {

    private var _binding: FragmentFightersBinding? = null
    private val binding get() = _binding!!

    private lateinit var pokemonViewModel: PokemonViewModel
    private lateinit var adapter: PokemonAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFightersBinding.inflate(inflater, container, false)
        val root: View = binding.root


        // ViewModel
        pokemonViewModel = ViewModelProvider(
            this,
            PokemonViewModelFactory(requireActivity().application)
        ).get(PokemonViewModel::class.java)

        // Adapter
        adapter = PokemonAdapter(emptyList(), pokemonViewModel, viewLifecycleOwner.lifecycleScope)
        binding.recyclerViewFighters.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFighters.adapter = adapter

        // Sleduj jen fightery
        pokemonViewModel.fighterPokemonList.observe(viewLifecycleOwner) { pokemons ->
            adapter.updateData(pokemons)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}