package com.example.weather_api_dummy.core.data.network

import com.example.weather_api_dummy.core.data.WeatherResponse
import com.example.weather_api_dummy.core.data.utils.CURRENT
import com.example.weather_api_dummy.core.data.utils.QUERY
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiInterface {

    // The api's Data  take someTime soo that's why we will be using Defferend
    @GET(CURRENT)
    fun getCurrentWeatherAsync(@Query(QUERY) mLocation: String): Deferred<WeatherResponse>

}