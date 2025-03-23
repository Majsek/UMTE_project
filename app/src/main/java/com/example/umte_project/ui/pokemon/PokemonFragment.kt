package com.example.umte_project.ui.pokemon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.umte_project.R
import com.example.umte_project.api.RetrofitClient
import com.example.umte_project.data.PokemonJSON
import com.example.umte_project.data.PokemonEntity
import com.example.umte_project.data.PokemonDAO
import com.example.umte_project.databinding.FragmentPokemonBinding
import kotlinx.coroutines.launch

class PokemonFragment : Fragment() {

    private var _binding: FragmentPokemonBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var pokemonViewModel: PokemonViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        pokemonViewModel = ViewModelProvider(
            this,
            PokemonViewModelFactory(requireActivity().application)
        ).get(PokemonViewModel::class.java)



        _binding = FragmentPokemonBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textPokemon
        pokemonViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        binding.buttonGetPokemon.setOnClickListener(::onGetPokemonButtonClick)

//        pokemonViewModel.pokemonList.observe(viewLifecycleOwner) { pokemons ->
//            //binding.textPokemon.text = pokemons.joinToString("\n") { it.name }
//             pokemons.joinToString("\n") { it.name }
//        }


        return root
    }

    fun onGetPokemonButtonClick(view: View) {
        binding.textPokemon.text = "Changed text!"
        //We need to cast the view to a Button, because view itself does not have text property.
        (view as Button).text = "Catch!"

        val randomId = (1..1010).random()
        val pokemonName = randomId.toString()

        RetrofitClient.instance.getPokemon(pokemonName).enqueue(object : retrofit2.Callback<PokemonJSON> {
            override fun onResponse(call: retrofit2.Call<PokemonJSON>, response: retrofit2.Response<PokemonJSON>) {
                if (response.isSuccessful) {
                    val pokemon = response.body()
                    val newText = "Wild ${pokemon?.name} appeared!\n"
                    println("Name: ${pokemon?.name}\nImage: ${pokemon?.sprites?.frontDefault}")
                    binding.textPokemon.text = newText
                    val imageUrl = "${pokemon?.sprites?.other?.officialArtwork?.frontDefault}"
                    //val imageUrl = "${pokemon?.sprites?.other?.home?.front_default}"
                    loadPokemonImage(imageUrl)

                    val pokemonEntity = PokemonEntity(
                        id = pokemon?.id ?: 0, // Zajištění, že id není null
                        name = pokemon?.name ?: "Unknown", // Zajištění, že name není null
                        imageUrl = imageUrl // Zde můžeš použít imageUrl, pokud chceš uložit URL obrázku
                    )

                    binding.textPokemon.text = newText

                    lifecycleScope.launch {
                        pokemonViewModel.insertPokemon(pokemonEntity)
                    }



                } else {
                    binding.textPokemon.text = "Failed to load Pokémon!"
                    println("Failed to load Pokémon!")
                }
            }

            override fun onFailure(call: retrofit2.Call<PokemonJSON>, t: Throwable) {
                binding.textPokemon.text = "Error: ${t.message}"
                println("Error: ${t.message}")
            }



        })
    }

    fun loadPokemonImage(url: String) {
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.placeholder) // when the image is loading
            .error(R.drawable.error) // when the image cant load
            .into(binding.imagePokemon)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}