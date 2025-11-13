package com.saintleo.weatherlinkapp.models

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("forecast.json")
    fun getForecastByCity(
        @Query("key") apiKey: String,
        @Query("q") city: String,
        @Query("days") days: Int = 3,
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no"
    ): Call<ForecastResponse>
}