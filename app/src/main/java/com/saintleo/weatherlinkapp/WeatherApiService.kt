package com.saintleo.weatherlinkapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("v1/current.json")
    fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") city: String,
        @Query("lang") lang: String = "es"
    ): Call<WeatherApiResponse>

    @GET("forecast.json")
    fun getForecastByCity(
        @Query("key") apiKey: String,
        @Query("q") city: String,
        @Query("days") days: Int = 3,
        @Query("lang") lang: String = "es"
    ): Call<ForecastResponse>
}