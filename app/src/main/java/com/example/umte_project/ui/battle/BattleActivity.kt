package com.example.umte_project.ui.battle

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.umte_project.R
import com.example.umte_project.data.PokemonEntity
import com.example.umte_project.ui.pokemon.PokemonViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class BattleActivity : AppCompatActivity() {
    // ViewModel pro práci s databází
    private val pokemonViewModel: PokemonViewModel by viewModels()

    // Deklarace proměnných pro UI prvky
    private lateinit var battleText: TextView
    private lateinit var progressBarWild: ProgressBar
    private lateinit var progressBarPlayer: ProgressBar
    private lateinit var imagePokemonWild: ImageView
    private lateinit var imagePokemonPlayer: ImageView
    private lateinit var buttonAttack: Button

    private lateinit var wildPokemon: PokemonEntity

    private var fighterIndex = 0

    private lateinit var playerFighters: ArrayList<PokemonEntity>


    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battle)

        // Nastavení fullscreenu
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        // Inicializace UI prvků
        battleText = findViewById(R.id.battleText)
        progressBarWild = findViewById(R.id.progressBar_wild)
        progressBarPlayer = findViewById(R.id.progressBar_player)
        imagePokemonWild = findViewById(R.id.image_pokemon_wild)
        imagePokemonPlayer = findViewById(R.id.image_pokemon_player)
        buttonAttack = findViewById(R.id.button_attack)


        // Získání údajů wildPokemon
        wildPokemon = intent.getParcelableExtra("wildPokemon")!!
        progressBarWild.max = 100
        progressBarWild.progress = 100 // Wild Pokémon má plné HP
        progressBarPlayer.max = 100

        pokemonViewModel.fighterPokemonList.observe(this) { fighters ->
            playerFighters = fighters as ArrayList<PokemonEntity>

        if (wildPokemon != null) {
            // Načti obrázky pomocí Glide
            loadPokemonImage(wildPokemon.imageUrl, imagePokemonWild)

            loadNextFighter()

//            battleText.text = "${playerFighters.get(fighterIndex).name} VS ${wildPokemon.name}!"
//            loadPokemonImage(playerFighters.get(fighterIndex).imageUrl, imagePokemonPlayer)
//            progressBarPlayer.progress = playerFighters.get(fighterIndex).hp
        }




        // Event listener na tlačítko útoku
        buttonAttack.setOnClickListener {
            attackWildPokemon()
        }
        }
    }


    private fun calculateAttackDamage(): Int {
        val baseDamage = Random.nextInt(5, 16) // Damage 5–15
        val critChance = 0.2
        val missChance = 0.1

        val roll = Random.nextDouble()

        return when {
            roll < missChance -> {
//                Log.d("Battle", "Attack missed!")
                0
            }
            roll < missChance + critChance -> {
//                Log.d("Battle", "Critical hit! Damage: ${baseDamage * 2}")
                baseDamage * 2
            }
            else -> {
//                Log.d("Battle", "Normal hit. Damage: $baseDamage")
                baseDamage
            }
        }
    }

    private fun loadNextFighter(){
        if(fighterIndex >= playerFighters.size) {
            return
        }
        battleText.text = "${playerFighters.get(fighterIndex).name} VS ${wildPokemon.name}!"
        loadPokemonImage(playerFighters.get(fighterIndex).imageUrl, imagePokemonPlayer)
        progressBarPlayer.progress = playerFighters.get(fighterIndex).hp
    }

    // Simulace útoku na divokého Pokémona
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private fun attackWildPokemon() {
        if (progressBarWild.progress > 10) {
//            val playerDamage = calculateAttackDamage()
//            val wildDamage = calculateAttackDamage()


            lifecycleScope.launch {
                // 1. Hráč útočí
                val playerDamage = calculateAttackDamage()
                bumpView(imagePokemonPlayer)
                delay(100L)
                shakeView(imagePokemonWild)
                wildPokemon.hp = maxOf(0, wildPokemon.hp - playerDamage)
                progressBarWild.progress = wildPokemon.hp
                buttonAttack.visibility = View.GONE


                var toast : Toast
                if (fighterIndex in playerFighters.indices) {
                    toast = Toast.makeText(this@BattleActivity, "${playerFighters[fighterIndex].name} dealt $playerDamage damage!", Toast.LENGTH_SHORT)
                    toast.show()
                } else {
                    toast = Toast.makeText(this@BattleActivity, "No fighter available!", Toast.LENGTH_SHORT)
                    toast.show()
                }


//                val toast = Toast.makeText(this@BattleActivity, "${playerFighters.get(fighterIndex).name} dealt $playerDamage damage!", Toast.LENGTH_SHORT)
//                toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 200)
//                toast.show()
                Handler(Looper.getMainLooper()).postDelayed({
                    toast.cancel()
                }, 300) // zruší toast po 0.3 sekundách


                // 2. Počkej 1 sekundu
                delay(600L)


                // 3. Wild Pokémon útočí (pokud ještě žije)
                if (wildPokemon.hp > 1) {
                    val wildDamage = calculateAttackDamage()

                    bumpView(imagePokemonWild, -1f)
                    delay(100L)
                    shakeView(imagePokemonPlayer)
                    playerFighters[fighterIndex].hp = maxOf(0, playerFighters[fighterIndex].hp - wildDamage)
                    progressBarPlayer.progress = playerFighters.get(fighterIndex).hp

                    pokemonViewModel.updateHP(playerFighters.get(fighterIndex))

                    val toast = Toast.makeText(this@BattleActivity, "${wildPokemon.name} dealt $wildDamage damage!", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 200, 800)
                    toast.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        toast.cancel()
                    }, 300) // zruší toast po 0.3 sekundách
                    delay(400L)
                }
                buttonAttack.visibility = View.VISIBLE
            }




//            wildPokemon.hp = maxOf(0, wildPokemon.hp - playerDamage)
//            shakeView(imagePokemonWild) // Když hráč útočí
//            bumpView(imagePokemonWild)
//            playerFighters.get(fighterIndex).hp = maxOf(0, playerFighters.get(fighterIndex).hp - wildDamage)
//            shakeView(imagePokemonPlayer) // Když wild útočí
//            bumpView(imagePokemonPlayer)



        }
        else {
            insertPokemonIntoDatabase()

            val resultIntent = Intent()
            resultIntent.putExtra("wasCaught", "true")
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        if (progressBarWild.progress <= 10){
            battleText.text = "${playerFighters.get(fighterIndex).name} defeated ${wildPokemon.name}!"
            buttonAttack.text = "Catch ${wildPokemon.name}!"
        }else{
            if(playerFighters.get(fighterIndex).hp <= 5){
                battleText.text = "${playerFighters.get(fighterIndex).name} fainted!"
                Toast.makeText(this, "${playerFighters.get(fighterIndex).name} fainted!", Toast.LENGTH_SHORT).show()

                fighterIndex++

                if(fighterIndex >= playerFighters.size) {
                    val resultIntent = Intent()
                    resultIntent.putExtra("wasCaught", "false")
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                } else{
                    loadNextFighter()
                }
                //buttonAttack.visibility = View.GONE
            }
        }
    }//

    fun shakeView(view: View) {
        view.animate()
            .translationXBy(10f)
            .setDuration(50)
            .withEndAction {
                view.animate()
                    .translationXBy(-20f)
                    .setDuration(100)
                    .withEndAction {
                        view.animate()
                            .translationXBy(10f)
                            .setDuration(50)
                            .start()
                    }
                    .start()
            }
            .start()
    }

    fun bumpView(view: View, isEnemy : Float = 1f) {
        view.animate()
            .scaleX(1.2f).scaleY(1.2f)
            .translationX(290f * isEnemy)
            .translationY(-290f * isEnemy)
            .setDuration(100)
            .withEndAction {
                view.animate()
                    .scaleX(1f).scaleY(1f)
                    .translationX(0f)
                    .translationY(0f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }



    private fun insertPokemonIntoDatabase() {
        lifecycleScope.launch {
            pokemonViewModel.insertPokemon(wildPokemon)
            battleText.text = "${wildPokemon.name} byl chycen!"
        }
    }




    private fun loadPokemonImage(url: String, imageView: ImageView) {
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.placeholder) // Obrázek při načítání
            .error(R.drawable.error) // Obrázek pokud načtení selže
            .into(imageView)
    }
}
