import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather")
    fun getWeather(
        @Query("q") location: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric", // Unidades: "metric" para Celsius, "imperial" para Fahrenheit
        @Query("lang") lang: String = "pt" // Idioma da resposta
    ): Call<WeatherResponse>
}
