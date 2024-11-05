package com.example.weather_api_dummy.weather.view

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.weather_api_dummy.databinding.ActivityMainBinding
import com.example.weather_api_dummy.weather.utils.PermissionUtils
import com.example.weather_api_dummy.weather.viewmodel.WeatherViewModel


private const val LOCATION_PERMISSION_REQUEST_CODE =34

class WeatherScreenActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val weatherViewModel: WeatherViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupObservers()
        weatherViewModel.loadWeather()
    }

    override fun onStart() {
        super.onStart()
        when {
            PermissionUtils.isAccessFineLocationGranted(this) -> {
                when {
                    PermissionUtils.isLocationEnabled(this) -> {
                        weatherViewModel.setUpLocationListener()
                    }
                }
            }
            else -> {
                PermissionUtils.requestAccessFineLocationPermission(
                    this,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun setupObservers() {
        weatherViewModel.temperature.observe(this) {
            binding.temperatureTextView.text = it
        }
        weatherViewModel.country.observe(this) {
            binding.countryTextView.text = it
        }
        weatherViewModel.cityName.observe(this) {
            binding.cityTextView.text = it
        }
        weatherViewModel.weatherDescription.observe(this) {
            binding.weatherDescriptionTextView.text = it
        }
        weatherViewModel.weatherIconUrl.observe(this) { url ->
            Glide.with(this).load(url).into(binding.weatherIconImageView)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when {
                        PermissionUtils.isLocationEnabled(this) -> {
                            weatherViewModel.setUpLocationListener()
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Permission not granted",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

}