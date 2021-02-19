import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon

private val klaxon = Klaxon()

data class WeatherData(
    val base: String? = null,
    val city: City? = null,
    val clouds: Clouds? = null,
    val cnt: Long? = null,
    val cod: Any,
    val coord: Coord? = null,
    val dt: Long? = null,
    val id: Long? = null,
    val list: List<ListElement>? = null,
    val main: Main? = null,
    val message: Long? = null,
    val name: String? = null,
    val sys: Sys? = null,
    val timezone: Long? = null,
    val visibility: Long? = null,
    val weather: List<Weather>? = null,
    val wind: Wind? = null,
) {
    companion object {
        fun fromJson(json: String) = klaxon.parse<WeatherData>(json)
    }
}

data class City(
    val id: Long,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Long,
    val timezone: Long,
    val sunrise: Long,
    val sunset: Long
)

data class Clouds(
    val all: Long
)

data class Coord(
    val lon: Double?,
    val lat: Double?
)

data class ListElement(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Long,
    val pop: Double,
    val sys: Sys,

    @Json(name = "dt_txt")
    val dtTxt: String,

    val snow: Snow? = null
)

data class Main(
    val temp: Double,

    @Json(name = "feels_like")
    val feelsLike: Double,

    @Json(name = "temp_min")
    val tempMin: Double,

    @Json(name = "temp_max")
    val tempMax: Double,

    @Json(name = "sea_level")
    val seaLevel: Long? = null,

    @Json(name = "grnd_level")
    val grndLevel: Long? = null,

    @Json(name = "temp_kf")
    val tempKf: Double? = null,

    val pressure: Long,
    val humidity: Long
)

data class Snow(
    @Json(name = "3h")
    val the3H: Double
)

data class Sys(
    val country: String? = null,
    val id: Long? = null,
    val pod: String? = null,
    val sunrise: Long? = null,
    val sunset: Long? = null,
    val type: Long? = null
)

data class Weather(
    val id: Long,
    val main: String,
    val description: String,
    val icon: String
)

data class Wind(
    val speed: Double,
    val deg: Long
)