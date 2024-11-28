package com.example.weather_api_dummy.features.weather.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weather_api_dummy.core.data.network.ApiInterface
import com.example.weather_api_dummy.core.data.network.RetrofitClient
import com.example.weather_api_dummy.core.data.utils.DEFAULT_LOCATION
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale


class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)
    private var currentLocation: Location? = null
    var cityName = MutableLiveData<String>()
    val temperature = MutableLiveData<String>()
    val country = MutableLiveData<String>()
    val weatherDescription = MutableLiveData<String>()
    val weatherIconUrl = MutableLiveData<String>()

    fun loadWeather() {
        viewModelScope.launch {
            if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    isNetworkAvailable()
                } else {
                    TODO("VERSION.SDK_INT < M")
                }
            ) {
                weatherApi()
            }
        }
    }

    private suspend fun weatherApi() {
        val client: ApiInterface = RetrofitClient.service
        val weatherResponse =
            client.getCurrentWeatherAsync(cityName.value.orEmpty().ifEmpty { DEFAULT_LOCATION })
                .await()

        withContext(Dispatchers.Main) {
            temperature.value = weatherResponse.current?.temperature.toString()
            country.value = weatherResponse.location?.country.toString()
            weatherIconUrl.value = weatherResponse.current?.weatherIcons?.get(0).toString()
            cityName.value = weatherResponse.location?.name ?: DEFAULT_LOCATION
            weatherDescription.value =
                weatherResponse.current?.weatherDescriptions?.get(0).toString()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    internal fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    @SuppressLint("MissingPermission")
    internal fun setUpLocationListener() {
        // for getting the current location update after every 2 seconds with high accuracy
        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    currentLocation = locationResult.lastLocation
                    val lat = currentLocation!!.latitude
                    val long = currentLocation!!.longitude
                    val geocoder = Geocoder(getApplication(), Locale.getDefault())
                    val addresses: MutableList<Address>? = geocoder.getFromLocation(lat, long, 1)
                    //   cityName.value = addresses?.get(0)?.getAddressLine(0) ?: " nodata"
                }
            },
            Looper.myLooper()!!
        )
    }
}
