import okhttp3.OkHttpClient
import okhttp3.Request

var apiKey = "8f6265a81af3154d852b30ebf630da10"

class WeatherApi {
    fun getWeather(city: String, date: Boolean = false): WeatherObj? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://api.openweathermap.org/data/2.5/${if (date) "forecast" else "weather"}?q=$city&appid=$apiKey&lang=ru&units=metric")
            .build()
        val response = client.newCall(request).execute()
        return response.body?.string()?.let { WeatherObj.fromJson(it) }
    }
}
