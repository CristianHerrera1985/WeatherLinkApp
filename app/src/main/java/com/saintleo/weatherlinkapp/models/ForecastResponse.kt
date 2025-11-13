package com.saintleo.weatherlinkapp.models

// --- Modelo principal de la respuesta del forecast ---
data class ForecastResponse(
    val location: WeatherLocation,
    val current: CurrentWeather,
    val forecast: Forecast
)

// --- Información de la ciudad ---
data class WeatherLocation(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val tz_id: String,
    val localtime: String
)

// --- Clima actual ---
data class CurrentWeather(
    val temp_c: Double,
    val temp_f: Double,
    val condition: WeatherCondition,
    val wind_kph: Double,
    val humidity: Int,
    val feelslike_c: Double,
    val feelslike_f: Double
)

// --- Condición de clima (icono y descripción) ---
data class WeatherCondition(
    val text: String,
    val icon: String,
    val code: Int
)

// --- Pronóstico por días ---
data class Forecast(
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val day: Day
)

data class Day(
    val maxtemp_c: Double,
    val mintemp_c: Double,
    val avgtemp_c: Double,
    val condition: WeatherCondition
)
