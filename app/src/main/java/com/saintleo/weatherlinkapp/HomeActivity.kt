package com.saintleo.weatherlinkapp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import androidx.cardview.widget.CardView

class HomeActivity : AppCompatActivity() {

    private lateinit var etCity: EditText
    private lateinit var btnSearch: Button
    private lateinit var tvCityName: TextView
    private lateinit var tvTemperature: TextView
    private lateinit var tvDescription: TextView
    private lateinit var imgWeatherIcon: ImageView
    private lateinit var btnFavorite: Button
    private lateinit var forecastLayout: LinearLayout
    private lateinit var mainLayout: ConstraintLayout

    private var isFavorite = false
    private val apiKey = "75c3bbccead74871b6734245250511" // https://www.weatherapi.com/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Inicialización de vistas
        etCity = findViewById(R.id.etCity)
        btnSearch = findViewById(R.id.btnSearch)
        tvCityName = findViewById(R.id.tvCityName)
        tvTemperature = findViewById(R.id.tvTemperature)
        tvDescription = findViewById(R.id.tvDescription)
        imgWeatherIcon = findViewById(R.id.imgWeatherIcon)
        btnFavorite = findViewById(R.id.btnFavorite)
        forecastLayout = findViewById(R.id.forecastLayout)
        mainLayout = findViewById(R.id.mainLayout)

        // Acción: Buscar ciudad
        btnSearch.setOnClickListener {
            val city = etCity.text.toString().trim()
            if (city.isNotEmpty()) {
                getWeather(city)
                getForecast(city)
            } else {
                Toast.makeText(this, "Ingresa una ciudad", Toast.LENGTH_SHORT).show()
            }
        }

        // Acción: Favoritos
        btnFavorite.setOnClickListener {
            isFavorite = !isFavorite
            if (isFavorite) {
                btnFavorite.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.baseline_favorite_24, 0, 0, 0
                )
                btnFavorite.text = "Quitar de favoritos"
                Toast.makeText(this, "Ciudad agregada a favoritos", Toast.LENGTH_SHORT).show()
            } else {
                btnFavorite.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.baseline_favorite_border_24, 0, 0, 0
                )
                btnFavorite.text = "Agregar a favoritos"
                Toast.makeText(this, "Ciudad eliminada de favoritos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- Clima actual ---
    private fun getWeather(city: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherApiService::class.java)
        val call = service.getCurrentWeather(apiKey, city)

        call.enqueue(object : Callback<WeatherApiResponse> {
            override fun onResponse(call: Call<WeatherApiResponse>, response: Response<WeatherApiResponse>) {
                if (response.isSuccessful) {
                    val weather = response.body()
                    weather?.let {
                        tvCityName.text = "${it.location.name}, ${it.location.country}"
                        tvTemperature.text = "${it.current.temp_c} °C"
                        tvDescription.text = it.current.condition.text

                        val iconUrl = "https:${it.current.condition.icon}"
                        Glide.with(this@HomeActivity).load(iconUrl).into(imgWeatherIcon)

                        // Cambiar fondo según el clima
                        updateBackground(it.current.condition.text)
                    }
                } else {
                    Toast.makeText(this@HomeActivity, "Ciudad no encontrada", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<WeatherApiResponse>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // --- Pronóstico ---
    private fun getForecast(city: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherApiService::class.java)
        val call = service.getForecastByCity(apiKey, city)

        call.enqueue(object : Callback<ForecastResponse> {
            override fun onResponse(call: Call<ForecastResponse>, response: Response<ForecastResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val forecast = response.body()!!.forecast.forecastday
                    forecastLayout.removeAllViews()

                    for (day in forecast) {
                        val forecastView = layoutInflater.inflate(R.layout.item_forecast, null)

                        val tvDate = forecastView.findViewById<TextView>(R.id.tvDate)
                        val tvAvgTemp = forecastView.findViewById<TextView>(R.id.tvAvgTemp)
                        val tvCondition = forecastView.findViewById<TextView>(R.id.tvCondition)
                        val imgIcon = forecastView.findViewById<ImageView>(R.id.imgIcon)
                        val cardView = forecastView.findViewById<CardView>(R.id.forecastCard)

                        tvDate.text = day.date
                        tvAvgTemp.text = "${day.day.avgtemp_c}°C"
                        tvCondition.text = day.day.condition.text
                        Glide.with(this@HomeActivity)
                            .load("https:${day.day.condition.icon}")
                            .into(imgIcon)

                        val condition = day.day.condition.text.lowercase()

                        // 🎨 Colores coherentes con la nueva paleta (con soporte español + inglés)
                        val cardColor = when {
                            condition.contains("sunny") || condition.contains("soleado") ->
                                Color.parseColor("#FFF59D") // Amarillo suave

                            condition.contains("clear") || condition.contains("despejado") ->
                                Color.parseColor("#FFF9C4") // Luz diurna

                            condition.contains("cloud") || condition.contains("nublado") ->
                                Color.parseColor("#CFD8DC") // Gris claro

                            condition.contains("rain") || condition.contains("lluvia") ->
                                Color.parseColor("#81D4FA") // Azul lluvia

                            condition.contains("storm") || condition.contains("tormenta") ->
                                Color.parseColor("#90A4AE") // Gris azulado medio

                            condition.contains("snow") || condition.contains("nieve") ->
                                Color.parseColor("#E1F5FE") // Azul muy claro

                            condition.contains("night") || condition.contains("noche") ->
                                Color.parseColor("#3F51B5") // Azul noche

                            else -> Color.parseColor("#ECEFF1") // Neutro claro
                        }

                        // Aplicar color a la card
                        cardView.setCardBackgroundColor(cardColor)

                        forecastLayout.addView(forecastView)
                    }
                }
            }

            override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Error al obtener el pronóstico", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // --- Fondo dinámico del layout principal ---
    private fun updateBackground(conditionText: String) {
        val text = conditionText.lowercase()

        val backgroundColor: Int
        val textColor: Int

        when {
            // Soleado / Sunny
            text.contains("sunny") || text.contains("soleado") -> {
                backgroundColor = Color.parseColor("#FFE082") // Amarillo cálido
                textColor = Color.parseColor("#3E2723") // Marrón oscuro
            }

            // Despejado / Clear
            text.contains("clear") || text.contains("despejado") -> {
                backgroundColor = Color.parseColor("#FFF9C4") // Luz diurna
                textColor = Color.parseColor("#3E2723")
            }

            // Nublado / Cloudy
            text.contains("cloud") || text.contains("nublado") -> {
                backgroundColor = Color.parseColor("#CFD8DC") // Gris claro
                textColor = Color.parseColor("#263238") // Gris oscuro
            }

            // Lluvioso / Rain
            text.contains("rain") || text.contains("lluvia") -> {
                backgroundColor = Color.parseColor("#81D4FA") // Azul lluvia
                textColor = Color.parseColor("#0D47A1") // Azul profundo
            }

            // Nevado / Snow
            text.contains("snow") || text.contains("nieve") -> {
                backgroundColor = Color.parseColor("#E1F5FE") // Azul muy claro
                textColor = Color.parseColor("#01579B") // Azul oscuro
            }

            // Tormenta / Storm
            text.contains("storm") || text.contains("tormenta") -> {
                backgroundColor = Color.parseColor("#607D8B") // Gris azulado medio
                textColor = Color.parseColor("#ECEFF1") // Texto claro
            }

            // Noche / Night
            text.contains("night") || text.contains("noche") -> {
                backgroundColor = Color.parseColor("#3F51B5") // Azul noche
                textColor = Color.parseColor("#FFFFFF") // Blanco
            }

            // Por defecto
            else -> {
                backgroundColor = Color.parseColor("#ECEFF1") // Neutro
                textColor = Color.parseColor("#212121") // Gris oscuro
            }
        }

        // Cambia el color de fondo
        findViewById<View>(R.id.mainLayout).setBackgroundColor(backgroundColor)

        // Cambia el color de los textos principales
        tvCityName.setTextColor(textColor)
        tvTemperature.setTextColor(textColor)
        tvDescription.setTextColor(textColor)
    }
}
