package com.example.weatherapp.weatheramp_response

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)