package com.example.weather_api_dummy.weather.network

import com.example.weather_api_dummy.weather.data.WeatherResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiInterface {

    // The api's Data  take someTime soo that's why we will be using Defferend
    @GET("current")
    fun getCurrentWeatherAsync(@Query("query") mLocation: String): Deferred<WeatherResponse>

}