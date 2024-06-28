package com.example.weathersphere

import WeatherApi
import WeatherResponse
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private val API_KEY = "cafc431a51fa603ff1d2e5bb5ac0b769"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherApi = retrofit.create(WeatherApi::class.java)

        val call = weatherApi.getWeather("sao paulo,br", API_KEY)

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    if (weatherResponse != null) {

                        val temperature = weatherResponse.main?.temp ?: 0.0
                        val feelsLike = weatherResponse.main?.feels_like ?: 0.0
                        val tempMin = weatherResponse.main?.temp_min ?: 0.0
                        val tempMax = weatherResponse.main?.temp_max ?: 0.0
                        val weatherDescription = weatherResponse.weather?.get(0)?.description ?: "N/A"
                        val humidity = weatherResponse.main?.humidity ?: 0
                        val pressure = weatherResponse.main?.pressure ?: 0
                        val speedWind = weatherResponse.wind?.speed ?: 0
                        val city = weatherResponse.name ?: "N/A"
                        val country = weatherResponse.sys?.country ?: "N/A"
                        val sunriseTime = weatherResponse.sys?.sunrise ?: 0
                        val sunsetTime = weatherResponse.sys?.sunset ?: 0

                        val sunriseDate = Date(sunriseTime * 1000)
                        val sunsetDate = Date(sunsetTime * 1000)

                        val date = weatherResponse.dt ?: 0
                        val currentDate = Date(date * 1000)
                        val timeZ =
                            android.icu.util.TimeZone.getTimeZone("America/Sao_Paulo") // Substitua pelo fuso horário desejado
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        dateFormat.timeZone = timeZ
                        val formattedDate = dateFormat.format(currentDate)

                        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                        timeFormat.timeZone = timeZ

                        val formattedTime = timeFormat.format(currentDate)
                        val formattedSunrise = timeFormat.format(sunriseDate)
                        val formattedSunset = timeFormat.format(sunsetDate)

                        val temperaturaCelsius = temperature?.toDouble() ?: 0.0

                        if (temperature != null) {
                            val temperaturaTextView = findViewById<TextView>(R.id.text_temperatura)
                            temperaturaTextView.text = String.format("%.0f °| ", temperaturaCelsius)

                            val feelsLikeTextView = findViewById<TextView>(R.id.text_feels)
                            feelsLikeTextView.text =
                                String.format("RealFeels: %.0f °", feelsLike)

                            val tempMaxTextView = findViewById<TextView>(R.id.text_temp_max)
                            tempMaxTextView.text = String.format("%.0f°", tempMax)

                            val tempMinTextView = findViewById<TextView>(R.id.text_temp_min)
                            tempMinTextView.text = String.format("%.0f°", tempMin)

                            val descricaoTextView = findViewById<TextView>(R.id.text_descricao)
                            descricaoTextView.text = "$weatherDescription"

                            val umidadeTextView = findViewById<TextView>(R.id.text_umidade)
                            umidadeTextView.text = "$humidity%"

                            val pressureTextView = findViewById<TextView>(R.id.text_pressure)
                            pressureTextView.text = "$pressure hPa"

                            val speedWindTextView = findViewById<TextView>(R.id.text_speed_wind)
                            speedWindTextView.text = "$speedWind m/s"

                            val cityTextView = findViewById<TextView>(R.id.text_city)
                            cityTextView.text = "$city, $country"

//                            val countryTextView = findViewById<TextView>(R.id.text_country)
//                            countryTextView.text = "País: $country"

//                            val dateTextView = findViewById<TextView>(R.id.text_date)
//                            dateTextView.text = "$formattedDate"
//
//                            val hourTextView = findViewById<TextView>(R.id.text_hour)
//                            hourTextView.text = "$formattedTime"

                            val sunriseTextView = findViewById<TextView>(R.id.text_sunrise)
                            sunriseTextView.text = "$formattedSunrise"

                            val sunsetTextView = findViewById<TextView>(R.id.text_sunset)
                            sunsetTextView.text = "$formattedSunset"

                            val weatherImageView = findViewById<ImageView>(R.id.image_response)
                            val weatherImageResource = getWeatherImage(weatherDescription)
                            weatherImageView.setImageResource(weatherImageResource)
                            weatherImageView.scaleType = ImageView.ScaleType.CENTER_CROP

                            Log.d("MainActivity", "Resposta da API: ${response.body()}")

                        } else {
                            Log.e("MainActivity", "Temperature property is null")
                            Toast.makeText(
                                baseContext,
                                "Temperature property is null",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Log.e("MainActivity", "WeatherResponse body is null")
                        Toast.makeText(
                            baseContext,
                            "WeatherResponse body is null",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    Log.e("MainActivity", "API request failed with code: ${response.code()}")
                    Toast.makeText(
                        baseContext,
                        "API request failed with code: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("MainActivity", "API request failed", t)
                Toast.makeText(baseContext, "API request failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun getWeatherImage(weatherDescription: String): Int {
        return when (weatherDescription) {
            "céu limpo" -> R.drawable.chuvoso
            "poucas nuvens" -> R.drawable.nublado_parcial
            "nuvens dispersas", "nublado" -> R.drawable.nublado
            "nuvens quebradas" -> R.drawable.nublado_parcial
            "chuva fraca","chuva moderada", "rain", "chuvisco fraco" -> R.drawable.chuvoso
            "tempestade" -> R.drawable.tempestade
            "neve" -> R.drawable.neve
            else -> R.drawable.gradient_background // Imagem padrão para condições desconhecidas
        }
    }
}
