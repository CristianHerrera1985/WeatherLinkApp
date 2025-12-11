package com.saintleo.weatherlinkapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import com.saintleo.weatherlinkapp.models.ForecastResponse
import com.saintleo.weatherlinkapp.models.WeatherApiService
import com.saintleo.weatherlinkapp.models.FavoriteCity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var searchButton: Button
    private lateinit var cityEditText: EditText
    private lateinit var weatherIcon: ImageView
    private lateinit var temperatureText: TextView
    private lateinit var cityNameText: TextView
    private lateinit var conditionText: TextView
    private lateinit var forecastLayout: LinearLayout

    // 🔹 NUEVOS BOTONES
    private lateinit var addToFavoritesButton: Button
    private lateinit var favoritesButton: Button

    private val db = FirebaseFirestore.getInstance()

    private val apiKey = "75c3bbccead74871b6734245250511"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Referencias UI
        searchButton = findViewById(R.id.searchButton)
        cityEditText = findViewById(R.id.cityEditText)
        weatherIcon = findViewById(R.id.weatherIcon)
        temperatureText = findViewById(R.id.temperatureText)
        cityNameText = findViewById(R.id.cityNameText)
        conditionText = findViewById(R.id.conditionText)
        forecastLayout = findViewById(R.id.forecastLayout)

        // 🔹 Nuevos botones
        addToFavoritesButton = findViewById(R.id.addToFavoritesButton)
        favoritesButton = findViewById(R.id.favoritesButton)

        searchButton.setOnClickListener {
            val city = cityEditText.text.toString().trim()
            if (city.isNotEmpty()) {
                getForecast(city)
            } else {
                Toast.makeText(this, "Ingresa una ciudad", Toast.LENGTH_SHORT).show()
            }
        }

        // 🔹 Guardar en favoritos
        addToFavoritesButton.setOnClickListener {

            val cityName = cityNameText.text.toString()
            val tempC = temperatureText.text.toString().replace("°C", "").trim()
            val icon = weatherIcon.tag?.toString() ?: ""

            if (cityName == "Ciudad") {
                Toast.makeText(this, "Busca una ciudad primero", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener

            val favorite = FavoriteCity(
                name = cityName,
                country = "",          // Si luego quieres guardar el país real, se agrega fácil
                temperature = tempC.toDoubleOrNull() ?: 0.0,
                iconUrl = icon
            )

            db.collection("users")
                .document(uid)
                .collection("favorites")
                .document(cityName)   // ← El nombre será el ID del documento
                .set(favorite)
                .addOnSuccessListener {
                    Toast.makeText(this, "Ciudad guardada en favoritos", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
                }
        }

        // 🔹 Ir a la pantalla de favoritos
        favoritesButton.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }
    }

    /** 🔹 Obtener clima actual y pronóstico */
    private fun getForecast(city: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherApiService::class.java)
        val call = service.getForecastByCity(apiKey, city)

        call.enqueue(object : Callback<ForecastResponse> {
            override fun onResponse(
                call: Call<ForecastResponse>,
                response: Response<ForecastResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val forecastResponse = response.body()!!
                    val location = forecastResponse.location
                    val current = forecastResponse.current
                    val forecast = forecastResponse.forecast

                    // --- Clima actual ---
                    weatherIcon.tag = "https:${current.condition.icon}"
                    Glide.with(this@HomeActivity)
                        .load("https:${current.condition.icon}")
                        .into(weatherIcon)

                    temperatureText.text = "${current.temp_c}°C"
                    cityNameText.text = location.name
                    conditionText.text = current.condition.text

                    // --- Pronóstico diario ---
                    forecastLayout.removeAllViews()
                    for (day in forecast.forecastday) {
                        val cardView = CardView(this@HomeActivity).apply {
                            val params = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            params.setMargins(0, 0, 0, 24)
                            layoutParams = params
                            radius = 24f
                            setCardBackgroundColor(Color.WHITE)
                            cardElevation = 8f
                            useCompatPadding = true
                        }

                        val contentLayout = LinearLayout(this@HomeActivity).apply {
                            orientation = LinearLayout.HORIZONTAL
                            setPadding(16, 16, 16, 16)
                            gravity = Gravity.CENTER_VERTICAL
                        }

                        val iconView = ImageView(this@HomeActivity).apply {
                            layoutParams = LinearLayout.LayoutParams(100, 100)
                            Glide.with(this@HomeActivity)
                                .load("https:${day.day.condition.icon}")
                                .into(this)
                        }

                        val textLayout = LinearLayout(this@HomeActivity).apply {
                            orientation = LinearLayout.VERTICAL
                            setPadding(24, 0, 0, 0)
                        }

                        val dateView = TextView(this@HomeActivity).apply {
                            text = day.date
                            setTextColor(Color.BLACK)
                            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                        }

                        val conditionView = TextView(this@HomeActivity).apply {
                            text = day.day.condition.text
                            setTextColor(Color.GRAY)
                            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                        }

                        val tempView = TextView(this@HomeActivity).apply {
                            text = "Máx: ${day.day.maxtemp_c}°C / Mín: ${day.day.mintemp_c}°C"
                            setTextColor(Color.parseColor("#3A6EA5"))
                            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                        }

                        textLayout.addView(dateView)
                        textLayout.addView(conditionView)
                        textLayout.addView(tempView)

                        contentLayout.addView(iconView)
                        contentLayout.addView(textLayout)

                        cardView.addView(contentLayout)
                        forecastLayout.addView(cardView)
                    }

                } else {
                    Toast.makeText(this@HomeActivity, "Ciudad no encontrada", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Error al obtener datos del clima", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
