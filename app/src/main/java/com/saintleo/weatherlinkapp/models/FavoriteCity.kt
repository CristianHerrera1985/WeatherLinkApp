package com.saintleo.weatherlinkapp.models

data class FavoriteCity(
    val name: String = "",
    val country: String = "",
    val temperature: Double = 0.0,
    val iconUrl: String = ""
)