import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umte_project.R
import com.example.umte_project.data.PokemonEntity
import com.example.umte_project.databinding.ItemPokemonBinding
import com.example.umte_project.ui.pokemon.PokemonViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PokemonAdapter(private var pokemonList: List<PokemonEntity>,
                     private val pokemonViewModel: PokemonViewModel,
                     private val lifecycleScope: CoroutineScope
) :
    RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    inner class PokemonViewHolder(private val binding: ItemPokemonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pokemon: PokemonEntity) {
            binding.textPokemonName.text = pokemon.name

            binding.progressBarPokemonHP.progress = pokemon.hp

            // ZRUŠ starý listener
            binding.switchPokemonFighter.setOnCheckedChangeListener(null)
            binding.switchPokemonFighter.isChecked = pokemon.isFighter

            // NASTAV nový listener
            binding.switchPokemonFighter.setOnCheckedChangeListener { _, isChecked ->
                lifecycleScope.launch {
                val count = pokemonViewModel.getFighterCount()
                if (isChecked && count >= 5) {
                    Toast.makeText(binding.root.context, "You can only assign 5 fighters!", Toast.LENGTH_SHORT).show()
                    binding.switchPokemonFighter.isChecked = false
                    return@launch
                }

                pokemon.isFighter = isChecked

                // Aktualizuj fighter stav v databázi
                lifecycleScope.launch {
                    pokemonViewModel.updateIsFighter(pokemon.id, isChecked)
                }
}
            }

            Glide.with(binding.root)
                .load(pokemon.imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(binding.imagePokemon)
        }
    }
    
    

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val binding = ItemPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        holder.bind(pokemonList[position])
    }

    override fun getItemCount(): Int = pokemonList.size

    fun updateData(newPokemonList: List<PokemonEntity>) {
        pokemonList = newPokemonList
        notifyDataSetChanged()
    }
}
