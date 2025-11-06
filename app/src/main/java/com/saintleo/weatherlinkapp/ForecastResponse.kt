package com.saintleo.weatherlinkapp

data class ForecastResponse(
    val location: WeatherLocation,
    val forecast: Forecast
)

data class WeatherLocation(
    val name: String,
    val region: String,
    val country: String
)

data class Forecast(
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val day: Day
)

data class Day(
    val avgtemp_c: Float,
    val condition: ForecastCondition
)

data class ForecastCondition(
    val text: String,
    val icon: String
)
