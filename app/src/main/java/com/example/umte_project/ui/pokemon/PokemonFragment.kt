package com.example.umte_project.ui.pokemon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.umte_project.R
import com.example.umte_project.api.RetrofitClient
import com.example.umte_project.data.Pokemon
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
        (view as Button).text = "Catch!"

        val randomId = (1..1010).random()
        val pokemonName = randomId.toString()

        RetrofitClient.instance.getPokemon(pokemonName).enqueue(object : retrofit2.Callback<Pokemon> {
            override fun onResponse(call: retrofit2.Call<Pokemon>, response: retrofit2.Response<Pokemon>) {
                if (response.isSuccessful) {
                    val pokemon = response.body()
                    val newText = "Wild ${pokemon?.name} appeared!\n"
                    println("Name: ${pokemon?.name}\nImage: ${pokemon?.sprites?.front_default}")
                    binding.textPokemon.text = newText
                    val imageUrl = "${pokemon?.sprites?.other?.official_artwork?.front_default}"
                    //val imageUrl = "${pokemon?.sprites?.other?.home?.front_default}"
                    loadPokemonImage(imageUrl)
                } else {
                    binding.textPokemon.text = "Failed to load Pokémon!"
                    println("Failed to load Pokémon!")
                }
            }

            override fun onFailure(call: retrofit2.Call<Pokemon>, t: Throwable) {
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