package com.example.umte_project.ui.pokemon

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.umte_project.R
import com.example.umte_project.api.RetrofitClient
import com.example.umte_project.data.PokemonJSON
import com.example.umte_project.data.PokemonEntity
import com.example.umte_project.databinding.FragmentPokemonBinding
import com.example.umte_project.ui.battle.BattleActivity
import kotlinx.coroutines.launch

class PokemonFragment : Fragment() {

    private var _binding: FragmentPokemonBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var pokemonViewModel: PokemonViewModel
    private lateinit var launcher: ActivityResultLauncher<Intent>

    private lateinit var wildPokemonEntity: PokemonEntity
    private var wasCaught: Boolean = false
    private var pokemonLoaded: Boolean = false

    private var fighterSize:Int = 0

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.getStringExtra("wasCaught")
                wasCaught = data == "true"
                updateText()

            }
        }
    }



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
        //binding.buttonGetPokemon.setOnClickListener(::onFightPokemonClick)



//        pokemonViewModel.pokemonList.observe(viewLifecycleOwner) { pokemons ->
//            //binding.textPokemon.text = pokemons.joinToString("\n") { it.name }
//             pokemons.joinToString("\n") { it.name }
//        }

//        var playerFighters = ArrayList<PokemonEntity>()
//        pokemonViewModel.fighterPokemonList.observe(viewLifecycleOwner) { fighters ->
//            playerFighters = fighters as ArrayList<PokemonEntity>
//        }
//        if (playerFighters.size == 0){
//            binding.textPokemon.text = "You have no fighters!"
//        }

        pokemonViewModel.fighterPokemonList.observe(viewLifecycleOwner) { fighters ->
            fighterSize = fighters.size
            if (!pokemonLoaded) {

            if (fighters.isEmpty()) {
                binding.textPokemon.text = "You have no fighters!"
                binding.buttonGetPokemon.text = "Assign a Pokémon as a fighter first!"
            } else {
                if (fighters.size == 1) {
                    binding.textPokemon.text = "You have ${fighters.size} selected fighter!"
                } else {
                    binding.textPokemon.text = "You have ${fighters.size} selected fighters!"
                }
            }
            }
        }







        return root
    }



    private fun updateText() {
        val newText = if (wasCaught) {
            "You caught ${wildPokemonEntity.name}!"
        }else{
            "You let ${wildPokemonEntity.name} get away..."
        }
        binding.textPokemon.text = newText
        binding.buttonGetPokemon.visibility = View.VISIBLE

    }

    fun onFightPokemonClick(view: View, wildPokemonEntity: PokemonEntity) {
        lifecycleScope.launch {
            updateText()

            val intent = Intent(requireContext(), BattleActivity::class.java)
            intent.putExtra("wildPokemon", wildPokemonEntity) // Posíláme celou entitu!

            //intent.putExtra("playerPokemon", fighterPokemonList) // Posíláme celou entitu hráčského Pokémona!
            //startActivity(intent)
            launcher.launch(intent)
            updateText()
        }


    }

    fun onGetPokemonButtonClick(view: View) {
        if (fighterSize == 0){

            findNavController().navigate(R.id.navigation_home)
            return
        }


        binding.textPokemon.text = "..."
        //We need to cast the view to a Button, because view itself does not have text property.
        (view as Button).text = "Prepare to fight!"

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
                        name = pokemon?.name?.replaceFirstChar { it.uppercase() } ?: "Unknown", // Zajištění, že name není null
                        imageUrl = imageUrl // Zde můžeš použít imageUrl, pokud chceš uložit URL obrázku
                    )

                    binding.textPokemon.text = newText

//                    lifecycleScope.launch {
//                        pokemonViewModel.insertPokemon(pokemonEntity)
//                    }

                    wildPokemonEntity = pokemonEntity
                    onFightPokemonClick(view, pokemonEntity)
                    view.text = "Search for pokémon!"


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
        (view as Button).visibility = View.GONE
    }

    fun loadPokemonImage(url: String) {
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.placeholder) // when the image is loading
            .error(R.drawable.error) // when the image cant load
            .into(binding.imagePokemon)
        pokemonLoaded = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}