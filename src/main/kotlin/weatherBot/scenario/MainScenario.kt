package weatherBot.scenario

import DatetimeObj
import WeatherApi
import com.beust.klaxon.KlaxonException
import com.justai.jaicf.activator.caila.caila
import com.justai.jaicf.model.scenario.Scenario
import java.time.LocalDate
import java.time.LocalDateTime
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

                    if (city != null) {
                        try {
                            val currentDate = LocalDateTime.now().toString().take(10)
                            val date = slots["date"]?.let { DatetimeObj.fromJson(it) }?.value?.take(10)

                            val weather = WeatherApi().getWeather(city = city, date = slots["date"] != null && currentDate != date)

                            if (slots["date"] == null || currentDate == date) {
                                reactions.say(
                                    "В городе ${city.capitalize()} сейчас ${weather?.main?.temp} градусов, " +
                                            "${weather?.weather?.get(0)?.description}. Ощущается как ${weather?.main?.feelsLike}. " +
                                            "Давление ${weather?.main?.pressure} мм рт. ст., влажность ${weather?.main?.humidity}%. " +
                                            "Скорость ветра ${weather?.wind?.speed} м/c."
                                )
                            } else {
                                var isDateValid = false
                                for (i in weather?.list!!) {
                                    if (date == i.dtTxt.take(10) ) {
                                        val parsedDate = LocalDate.parse(i.dtTxt.take(10))
                                        val formatter = DateTimeFormatter.ofPattern("dd MMMM").withLocale(Locale("ru"))
                                        val formattedDate = formatter.format(parsedDate)
                                        reactions.say(
                                            "Прогноз погоды в городе ${city.capitalize()} на $formattedDate: температура ${i.main.temp} градусов, " +
                                                    "будет ${i.weather[0].description}. Ожидается давление ${i.main.pressure} " +
                                                    "мм рт. ст., влажность ${i.main.humidity}% при скорости ветра ${i.wind.speed} м/c."
                                        )
                                        isDateValid = true
                                        break
                                    }
                                }
                                if (!isDateValid) {
                                    reactions.say("У меня нет прогноза на эту дату, я знаю прогноз только на ближайшие пять дней.")
                                }
                            }
                        } catch (e: KlaxonException) {
                            reactions.say("Я не самый умный бот. Попробуй написать просто 'Погода Рим'.")
                        }
                    } else {
                        reactions.say("С названием города возникли проблемы, может быть такого города нет? Попробуй, например, 'Погода Пекин'.")
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
}
