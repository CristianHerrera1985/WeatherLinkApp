package com.saintleo.weatherlinkapp

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.saintleo.weatherlinkapp.adapters.FavoritesAdapter
import com.saintleo.weatherlinkapp.models.FavoriteCity

class FavoritesActivity : AppCompatActivity() {

    private lateinit var recyclerFavorites: RecyclerView
    private lateinit var adapter: FavoritesAdapter
    private val db = FirebaseFirestore.getInstance()
    private val userId get() = FirebaseAuth.getInstance().currentUser?.uid
    private val favoriteCities = mutableListOf<FavoriteCity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        // 🔹 Referencias a las vistas
        recyclerFavorites = findViewById(R.id.recyclerFavorites)
        val btnBack = findViewById<Button>(R.id.btnBackHome)

        // 🔹 Configuración del RecyclerView
        recyclerFavorites.layoutManager = LinearLayoutManager(this)

        // 🔹 Adaptador con callback de eliminación
        adapter = FavoritesAdapter(favoriteCities) { city ->
            deleteFavorite(city)
        }

        recyclerFavorites.adapter = adapter

        // 🔹 Botón para regresar al Home
        btnBack.setOnClickListener { finish() }

        // 🔹 Cargar ciudades favoritas desde Firestore
        loadFavorites()
    }

    // --- Cargar favoritos desde Firestore ---
    private fun loadFavorites() {
        val uid = userId ?: return

        db.collection("users")
            .document(uid)
            .collection("favorites")
            .get()
            .addOnSuccessListener { result ->
                favoriteCities.clear()
                for (document in result) {

                    // 👉 Convertir a objeto
                    val city = document.toObject(FavoriteCity::class.java)

                    // 👉 Si el objeto no tenía name asignado,
                    // lo tomamos desde el ID del documento.
                    val fixedCity = city.copy(
                        name = if (city.name.isEmpty()) document.id else city.name
                    )

                    favoriteCities.add(fixedCity)
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar favoritos", Toast.LENGTH_SHORT).show()
            }
    }


    // --- Eliminar una ciudad de favoritos ---
    private fun deleteFavorite(city: FavoriteCity) {
        val uid = userId ?: return

        db.collection("users")
            .document(uid)
            .collection("favorites")
            .document(city.name)
            .delete()
            .addOnSuccessListener {
                favoriteCities.remove(city)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Ciudad eliminada de favoritos", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al eliminar ciudad", Toast.LENGTH_SHORT).show()
            }
    }
}
