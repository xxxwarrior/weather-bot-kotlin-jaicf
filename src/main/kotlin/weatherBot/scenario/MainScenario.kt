package weatherBot.scenario

import DatetimeData
import WeatherData
import WeatherApi
import com.beust.klaxon.KlaxonException
import com.justai.jaicf.activator.caila.caila
import com.justai.jaicf.model.scenario.Scenario
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

object MainScenario : Scenario() {

    init {
        state("main") {
            activators {
                regex("/start")
            }
            action {
                reactions.image("https://sun9-22.userapi.com/sun9-40/impg/lyJ2HA2Pknf2b6zN_Q4_bnfAPAR-Dq-Uan5qXA/hTv3Nt0BLoc.jpg?size=512x337&quality=96&proxy=1&sign=57c11956eaff2289d41a3eff9de758f9&type=album")
                reactions.say(
                    "Привет, я бот, который расскажет тебе о погоде. Напиши 'Погода *твой город*' или просто название города, " +
                    "а я дам тебе прогноз на сегодня. \nМожешь также указать 'завтра', 'послезавтра' или любую дату на ближайшие 5 дней."
                )
            }
        }

        state("hello") {
            activators {
                intent("Привет")
            }
            action {
                reactions.sayRandom(
                    "Добрый день!",
                    "Привет!",
                    "Салют!",
                    "Здравствуй!"
                )
            }
        }

        state("bye") {
            activators {
                intent("Пока")
            }

            action {
                reactions.sayRandom(
                    "Пока!",
                    "До свидания",
                    "До скорого!",
                    "Еще увидимся!"
                )
            }
        }

        state("city") {
            activators {
                intent("Прогноз погоды")
            }
            action {
                activator.caila?.run {
                    val city: String? = if (slots["city"] in arrayOf(
                            "СПБ", "Спб", "спб", "Петербург", "санкт петербург")
                    ) "Санкт-Петербург" else slots["city"]

                    if (city == null) {
                        reactions.say("С названием города возникли проблемы, может быть такого города нет? Попробуй, например, 'Погода Пекин'.")
                    } else {
                        val dateLength = 10

                        val currentDate = LocalDate.now().toString().take(dateLength)
                        val date = slots["date"]?.let { DatetimeData.fromJson(it) }?.value?.take(dateLength)

                        // If there's a date and it's not today then it's a forecast
                        val forecastNeeded = slots["date"] != null && currentDate != date

                        val weather: WeatherData? = try { WeatherApi.getWeather(city, forecastNeeded) } catch (e: KlaxonException) { null }
                        if (weather == null) {
                            reactions.say("Я не самый умный бот. Попробуй написать просто 'Погода Рим'.")
                        } else {
                            if (forecastNeeded) {
                                val dayWeather = weather.list?.find { date == it.dtTxt.take(dateLength) }
                                if (dayWeather == null) {
                                    reactions.say("У меня нет прогноза на эту дату, я знаю прогноз только на ближайшие пять дней.")
                                } else {
                                    val parsedDate = LocalDate.parse(date)
                                    val formatter = DateTimeFormatter.ofPattern("dd MMMM").withLocale(Locale("ru"))
                                    val formattedDate = formatter.format(parsedDate)
                                    reactions.say(
                                        "Прогноз погоды в городе ${city.capitalize()} на $formattedDate: температура ${dayWeather.main.temp} градусов, " +
                                        "будет ${dayWeather.weather[0].description}. Ожидается давление ${dayWeather.main.pressure} " +
                                        "мм рт. ст., влажность ${dayWeather.main.humidity}% при скорости ветра ${dayWeather.wind.speed} м/c.")
                                }
                            } else {
                                reactions.say(
                                    "В городе ${city.capitalize()} сейчас ${weather.main?.temp} градусов, " +
                                    "${weather.weather?.get(0)?.description}. Ощущается как ${weather.main?.feelsLike}. " +
                                    "Давление ${weather.main?.pressure} мм рт. ст., влажность ${weather.main?.humidity}%. " +
                                    "Скорость ветра ${weather.wind?.speed} м/c.")
                            }
                        }
                    }
                }
            }
        }

        state("fallback") {
            activators {
                catchAll()
            }

            action {
                reactions.sayRandom(
                    "Просто назови город, а я дам тебе прогноз на сегодня.",
                    "Я всего лишь бот и мало что понимаю. Ты уже пробовал(а) написать название города?",
                    "Поробуй 'Погода *название города*'",
                    "А это точно город?"
                )
            }
        }
    }
}
