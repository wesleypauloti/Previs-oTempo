data class WeatherResponse(
    val main: MainInfo,
    val weather: List<WeatherInfo>,
    val timezone: Int?,
    val dt: Long?,
    val sys: SysInfo?,
    val wind: WindInfo?,
    val name: String?
)

data class MainInfo(
    val temp: Float,
    val temp_min: Float,
    val temp_max: Float,
    val feels_like: Float,
    val humidity: Int,
    val pressure: Int
)

data class WeatherInfo(
    val main: String,
    val description: String,
    val icon: String
)

data class SysInfo(
    val country: String,
    val sunrise: Long?,
    val sunset: Long?
)

data class WindInfo(
    val speed: Double
)
