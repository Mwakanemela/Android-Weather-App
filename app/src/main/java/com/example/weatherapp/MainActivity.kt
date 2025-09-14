package com.example.weatherapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.weatherapp.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.Executors


// API KEY = 75e37a80fa253b752fc389d8a42b8b68
//https://api.openweathermap.org/data/2.5/weather?q=London
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var API_KEY = "75e37a80fa253b752fc389d8a42b8b68"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.refreshButton.setOnClickListener {
            val cityName = binding.cityNameEditText.text.toString()

            if(cityName.isNotEmpty()) {
                fetchWeatherData(cityName)
            }else {
                binding.cityNameEditText.error = "City name is required"
            }
        }
        fetchWeatherData("Blantyre")

    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(result: String) {
        try {
            val jsonObject = JSONObject(result)

            //extract values from json object(response from weather-map api
            val cityName = jsonObject.getString("name")
            val main = jsonObject.getJSONObject("main")
            val temperature = main.getDouble("temp")
            val humidity = main.getInt("humidity")

            val windSpeed = jsonObject.getJSONObject("wind").getDouble("speed")
            val description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description")
            val iconCode = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon")

            val resourceName = "ic_$iconCode"

            val resourceId = resources.getIdentifier(resourceName, "drawable", packageName)

            //update views
            binding.weatherIconImageView.setImageResource(resourceId)
            binding.cityNameTextView.text = cityName
//            binding.cityTemperatureTextView.text = "${temperature.toString()}\\u00B0C"
            binding.cityTemperatureTextView.text = "${temperature.toString()}°C"
            binding.weatherDescriptionTextView.text = description
            binding.windTextView.text = windSpeed.toString()
            binding.humidityTextView.text = humidity.toString()

        }catch (e : JSONException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to extract JSONObject", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchWeatherData(cityName: String) {

        val url = "https://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=$API_KEY&units=metric"

        val executorService = Executors.newSingleThreadExecutor()
        executorService.execute{
            val client = OkHttpClient()

            val request = Request.Builder().url(url).build();

            try {
                val response = client.newCall(request).execute()
                val result1 = response.body.toString()
                val result = response.body?.string() ?: ""

//                Log.d("TAG", "fetchWeatherData: $result")
//                Log.d("TAG", "fetchWeatherData 1: $result1 ")
                runOnUiThread {
                    updateUI(result)
                }
            }catch (exception : IOException) {
                exception.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Check internet connection", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}