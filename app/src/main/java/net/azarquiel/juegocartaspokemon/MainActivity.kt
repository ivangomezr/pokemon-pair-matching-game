package net.azarquiel.juegocartaspokemon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var tapando: Boolean = false
    private var time: Long = 0L
    private lateinit var ivPrimera: ImageView
    private var primerClick: Boolean = true
    private var aciertos = 0
    private lateinit var lv : LinearLayout
    private lateinit var random: Random
    private val vectorPokemons = IntArray(809) { i-> i+1 }
    private val vectorTablero = IntArray(20) {0}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lv = findViewById<LinearLayout>(R.id.lv)
        random = Random(System.currentTimeMillis())
        nuevaPartida()
    }

    private fun nuevaPartida() {
        time = System.currentTimeMillis()
        tapando = false
        primerClick = true
        aciertos = 0
        inventaPokemon()
        pintaPokemon()
    }

    private fun pintaPokemon() {
        var c = 0
        for (i in 0 until lv.childCount) {
            val lh = lv.getChildAt(i) as LinearLayout
            for (j in 0 until lh.childCount) {
                val iv = lh.getChildAt(j) as ImageView //como no sabemos que los hijos del lh son imágenes, lo casteamos
                val id = resources.getIdentifier("pokemon${vectorTablero[c]}", "drawable", packageName) //sacamos los identificadores de las fotos que tenemos que sacar
                iv.tag = vectorTablero[c]
                c++
                iv.setBackgroundResource(id)
                iv.setImageResource(R.drawable.cartapokemon)
                iv.setOnClickListener(this)
                iv.isEnabled = true
            }
        }
    }

    private fun inventaPokemon() {
        vectorPokemons.shuffle(random) //el array que contiene todos los pokemons se ha "barajeado"
        var c = 0
        for (i in 1..2) { //del 1 al 2    //bucle para sacar 20 pokemon, repetidos 2 veces cada uno
            for (j in 0 until 10) { //del 0 al 9
                vectorTablero[c] = vectorPokemons[j]
                c++
            }
        }
        vectorTablero.shuffle(random)
    }

    override fun onClick(v: View?) {
        if (tapando) return
        val ivpulsada = v as ImageView
        if (!primerClick && ivpulsada==ivPrimera) return
        ivpulsada.setImageResource(android.R.color.transparent)
        if (primerClick) {
            ivPrimera = ivpulsada
        } else {
            check(ivpulsada)
        }
        primerClick = !primerClick
        //quitar tapa primer y segundo click
    }

    private fun check(ivSegunda: ImageView) { //le pasamos la segunda imagen pulsada porque la primera ya la tenemos guardada arriba
        if (ivSegunda.tag == ivPrimera.tag) {
            aciertos++
            ivPrimera.isEnabled = false
            ivSegunda.isEnabled = false
            if (aciertos==10) {
                gameOver()
            }
        } else {
            taparCartas(ivSegunda)
        }

    }

    private fun taparCartas(ivSegunda: ImageView) {
        GlobalScope.launch() {
            tapando = true
            SystemClock.sleep(500)
            launch(Main) {
                ivPrimera.setImageResource(R.drawable.cartapokemon)
                ivSegunda.setImageResource(R.drawable.cartapokemon)
                tapando = false
            }
        }
    }

    private fun gameOver() {
        val segundos = ((System.currentTimeMillis()) - time)/1000
        AlertDialog.Builder(this)
            .setTitle("GameOver")
            .setMessage("Felicidades, lo conseguiste en ${segundos} segundos")
            .setCancelable(false)
            .setPositiveButton("New Game") { dialog, which ->
                nuevaPartida()
            }
            .setNegativeButton("Exit") { dialog, which ->
                finish()
            }
            .show()
    }

}