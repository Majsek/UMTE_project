package com.example.umte_project.ui.home

import PokemonAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umte_project.databinding.FragmentHomeBinding
import com.example.umte_project.ui.pokemon.PokemonViewModel
import com.example.umte_project.ui.pokemon.PokemonViewModelFactory
import com.example.umte_project.ui.starter.StarterActivity
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var pokemonViewModel: PokemonViewModel
    private lateinit var adapter: PokemonAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val pokemonCount = getPokemonCountFromDatabase()
            if (pokemonCount == 0) {
                val intent = Intent(requireContext(), StarterActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private suspend fun getPokemonCountFromDatabase(): Int {
        return pokemonViewModel.getPokemonCount()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inicializace ViewModelu
        pokemonViewModel = ViewModelProvider(
            this,
            PokemonViewModelFactory(requireActivity().application)
        ).get(PokemonViewModel::class.java)

        // Nastavení RecyclerView
        adapter = PokemonAdapter(emptyList())  // Prázdný seznam, aktualizuje se dynamicky
        binding.recyclerViewPokemon.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewPokemon.adapter = adapter

        // Pozorování změn v databázi Pokémonů
        pokemonViewModel.pokemonList.observe(viewLifecycleOwner) { pokemons ->
            adapter.updateData(pokemons)
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
