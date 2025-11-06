package com.saintleo.weatherlinkapp

data class WeatherApiResponse(
    val location: Location,
    val current: Current
)

data class Location(
    val name: String,
    val country: String
)

data class Current(
    val temp_c: Float,
    val condition: Condition
)

data class Condition(
    val text: String,
    val icon: String
)