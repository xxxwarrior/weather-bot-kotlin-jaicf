import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon

private val klaxon = Klaxon()

data class DatetimeData(
    val year: String,
    val month: String,
    val day: String,
    val dayOfWeek: String,
    val timestamp: Long,

    @Json(name = "exact_values")
    val exactValues: ExactValues,

    val value: String
) {

    companion object {
        fun fromJson(json: String) = klaxon.parse<DatetimeData>(json)
    }
}

data class ExactValues(
    val year: Long,
    val month: Long,
    val day: Long
)
