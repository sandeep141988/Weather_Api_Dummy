package com.example.weather_api_dummy

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Looper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weather_api_dummy.core.data.WeatherResponse
import com.example.weather_api_dummy.core.data.network.ApiInterface
import com.example.weather_api_dummy.core.data.network.RetrofitClient
import com.example.weather_api_dummy.features.weather.viewmodel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: WeatherViewModel
    private lateinit var application: Application
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        val fusedLocationProviderClient = mockk<FusedLocationProviderClient>()
        every { LocationServices.getFusedLocationProviderClient(any()) } returns fusedLocationProviderClient
        application = mockk(relaxed = true)
        Dispatchers.setMain(testDispatcher)
        viewModel = WeatherViewModel(application)
        mockkStatic(Looper::class)
        every { Looper.getMainLooper() } returns mockk()

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `isNetworkAvailable returns true when network is available`() {
        val connectivityManager = mockk<ConnectivityManager>()
        val networkCapabilities = mockk<NetworkCapabilities>()

        every { application.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        every { connectivityManager.activeNetwork } returns mockk()
        every { connectivityManager.getNetworkCapabilities(any()) } returns networkCapabilities
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true

        val result = viewModel.isNetworkAvailable()

        assertEquals(true, result)
    }

    @Test
    fun `isNetworkAvailable returns false when network is unavailable`() {
        val connectivityManager = mockk<ConnectivityManager>()

        every { application.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        every { connectivityManager.activeNetwork } returns null

        val result = viewModel.isNetworkAvailable()

        assertEquals(false, result)
    }

    @Test
    fun `weatherApi updates LiveData correctly`() = runTest {
        val apiInterface = mockk<ApiInterface>()
        val mockResponse = mockk<WeatherResponse>(relaxed = true) {
            every { current?.temperature } returns 25
            every { location?.country } returns "Test Country"
            every { current?.weatherDescriptions } returns listOf("Sunny")
            every { current?.weatherIcons } returns listOf("test_icon_url")
        }
        mockkObject(RetrofitClient)
        every { RetrofitClient.service } returns apiInterface
        coEvery { apiInterface.getCurrentWeatherAsync(any()) } returns mockk {
            coEvery { await() } returns mockResponse
        }

        viewModel.cityName.value = "Test City"
        viewModel.loadWeather()

        advanceUntilIdle()

        assertEquals("25", viewModel.temperature.value)
        assertEquals("Test Country", viewModel.country.value)
        assertEquals("Sunny", viewModel.weatherDescription.value)
        assertEquals("test_icon_url", viewModel.weatherIconUrl.value)
    }

   /* @Test
    fun `setUpLocationListener sets cityName LiveData`() {
        val fusedLocationClient = mockk<FusedLocationProviderClient>(relaxed = true)
        val locationResult = mockk<LocationResult>(relaxed = true)
        val locationCallbackSlot = slot<LocationCallback>()
        val location = mockk<Location> {
            every { latitude } returns 37.7749
            every { longitude } returns -122.4194
        }
        val geocoder = mockk<Geocoder>(relaxed = true)

        every { application.getSystemService(FusedLocationProviderClient::class.java) } returns fusedLocationClient
        every { locationResult.lastLocation } returns location
        every { fusedLocationClient.requestLocationUpdates(any(), capture(locationCallbackSlot), any()) } just Runs

        viewModel.setUpLocationListener()
        locationCallbackSlot.captured.onLocationResult(locationResult)

        assertEquals("San Francisco", viewModel.cityName.value)
    }*/
}