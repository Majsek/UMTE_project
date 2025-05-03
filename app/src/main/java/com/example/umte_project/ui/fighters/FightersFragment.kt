package com.example.umte_project.ui.fighters

import PokemonAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        pokemonViewModel.fighterPokemonList.observe(viewLifecycleOwner) { fighters ->
            if (fighters.isEmpty()) {
                binding.textFighters.visibility = View.VISIBLE
                binding.textFighters.text = "You have no assigned fighters."
                binding.recyclerViewFighters.visibility = View.GONE
            } else {
                binding.textFighters.visibility = View.VISIBLE
                binding.recyclerViewFighters.visibility = View.VISIBLE
                binding.textFighters.text = "Unassign wounded fighters to let them heal."
                adapter.updateData(fighters)
            }
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}