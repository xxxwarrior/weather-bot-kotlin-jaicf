import weatherBot.scenario.MainScenario
import com.justai.jaicf.test.ScenarioTest
import org.junit.jupiter.api.Test


class MainScenarioTest: ScenarioTest(MainScenario) {
    @Test
    fun `Starting conversation`() {
        query("/start") endsWithState "/main"
    }

    @Test
    fun `Weather forecast` () {
        intent("Прогноз погоды") endsWithState "/city"
    }
}