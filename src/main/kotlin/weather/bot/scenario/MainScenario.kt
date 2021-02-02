package weather.bot.scenario

import com.justai.jaicf.activator.caila.caila
import com.justai.jaicf.model.scenario.Scenario

import WeatherApi
import com.beust.klaxon.KlaxonException


object MainScenario : Scenario() {

    init {
        state("main") {
            activators {
                regex("/start")
            }
            action {
                reactions.image("https://sun9-22.userapi.com/sun9-40/impg/lyJ2HA2Pknf2b6zN_Q4_bnfAPAR-Dq-Uan5qXA/hTv3Nt0BLoc.jpg?size=512x337&quality=96&proxy=1&sign=57c11956eaff2289d41a3eff9de758f9&type=album")
                reactions.say(
                    "Привет, я бот, который расскажет тебе о погоде, просто скажи мне кодовую фразу 'Погода *твой город*'. \n" +
                            "Можешь указать 'сегодня', 'завтра' или любую дату в ближайшие 5 дней"
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
                            "СПБ", "Спб", "спб", "Питер", "санкт петербург")
                    ) "Санкт-Петербург" else slots["city"]
                    if (city != null) {
                        try {
                            val weather = slots["city"]?.let {
                                WeatherApi().getWeather(city = it, timestamp = slots["date"])
                            }
                            if (slots["date"] == null) {
                                reactions.say(
                                        "В городе ${city.capitalize()} сейчас ${weather?.main?.temp} градусов, " +
                                            "${weather?.weather?.get(0)?.description}. Ощущается как ${weather?.main?.feelsLike}. " +
                                            "Давление ${weather?.main?.pressure} мм рт. ст., влажность ${weather?.main?.humidity}%. " +
                                            "Скорость ветра ${weather?.wind?.speed} м/c."
                                )
                            } else if (slots["date"] != null) {
                                val timestamp = slots["date"]?.let { DatetimeObj.fromJson(it) }?.timestamp
                                val timestampCut = timestamp.toString().take(10)
                                var flag = false
                                for (i in weather?.list!!) {
                                    if (timestampCut == i.dt.toString()) {
                                        reactions.say(
                                            "Прогноз погоды в городе $city на ${i.dtTxt.take(10)}: температура ${i.main.temp} градусов, " +
                                                    "${i.weather[0].description}. Ощущается как ${i.main.feelsLike}. Давление ${i.main.pressure} " +
                                                    "мм рт. ст., влажность ${i.main.humidity}%. Скорость ветра ${i.wind.speed} м/c."
                                        )
                                        flag = true
                                        break
                                    }
                                }
                                if (!flag) {
                                    reactions.say("У меня нет прогноза на эту дату, попробуй спросить прогноз на ближайшие пару дней.")
                                }
                            } else {
                                reactions.say("Что-то пошло не так, попробуй написать, например, 'Погода Москва'.")
                            }
                        } catch (e: KlaxonException) {
                            reactions.say("Я не самый умный бот. Попробуй написать просто 'Погода Рим'.")
                        }
                    }
                }
            }

            state("fallback") {
                activators {
                    catchAll()
                }

                action {
                    reactions.say("Напиши 'Погода *название города*', а я скажу тебе прогноз на сегодня.")
                }
            }
        }
    }
    }
