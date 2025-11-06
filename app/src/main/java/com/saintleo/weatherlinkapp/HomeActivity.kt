package com.saintleo.weatherlinkapp

import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import androidx.constraintlayout.widget.ConstraintLayout

class HomeActivity : AppCompatActivity() {
    private lateinit var etCity: EditText
    private lateinit var btnSearch: Button
    private lateinit var tvCityName: TextView
    private lateinit var tvTemperature: TextView
    private lateinit var tvDescription: TextView
    private lateinit var imgWeatherIcon: ImageView
    private lateinit var btnFavorite: Button
    private lateinit var forecastLayout: LinearLayout
    lateinit var mainLayout: ConstraintLayout

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
        mainLayout =  findViewById<ConstraintLayout>(R.id.mainLayout)

        // Acción buscar
        btnSearch.setOnClickListener {
            val city = etCity.text.toString().trim()
            if (city.isNotEmpty()) {
                getWeather(city)
                getForecast(city)
            } else {
                Toast.makeText(this, "Ingresa una ciudad", Toast.LENGTH_SHORT).show()
            }
        }

        // Acción favoritos
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

    private fun getWeather(city: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherApiService::class.java)
        val call = service.getCurrentWeather(apiKey, city)

        call.enqueue(object : Callback<WeatherApiResponse> {
            override fun onResponse(
                call: Call<WeatherApiResponse>,
                response: Response<WeatherApiResponse>
            ) {
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

                        tvDate.text = day.date
                        tvAvgTemp.text = "${day.day.avgtemp_c}°C"
                        tvCondition.text = day.day.condition.text
                        Glide.with(this@HomeActivity)
                            .load("https:${day.day.condition.icon}")
                            .into(imgIcon)

                        forecastLayout.addView(forecastView)
                    }
                }
            }

            override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Error al obtener el pronóstico", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 🎨 Cambia el color de fondo según el tipo de clima
    private fun updateBackground(conditionText: String) {
        val backgroundColor = when {
            conditionText.contains("sunny", ignoreCase = true) -> Color.parseColor("#FFD54F") // Amarillo claro
            conditionText.contains("clear", ignoreCase = true) -> Color.parseColor("#FFF176") // Amarillo suave
            conditionText.contains("cloud", ignoreCase = true) -> Color.parseColor("#90A4AE") // Gris azulado
            conditionText.contains("rain", ignoreCase = true) -> Color.parseColor("#64B5F6") // Azul lluvia
            conditionText.contains("snow", ignoreCase = true) -> Color.parseColor("#E0F7FA") // Blanco azulado
            conditionText.contains("storm", ignoreCase = true) -> Color.parseColor("#455A64") // Gris oscuro
            conditionText.contains("night", ignoreCase = true) -> Color.parseColor("#283593") // Azul noche
            else -> Color.parseColor("#B0BEC5") // Gris neutro
        }

        mainLayout.setBackgroundColor(backgroundColor)
    }
}