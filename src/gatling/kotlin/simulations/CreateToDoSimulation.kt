package simulations

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl.http
import io.gatling.javaapi.http.HttpDsl.status
import java.time.Duration
import java.util.*
import kotlin.math.abs


class CreateToDoSimulation : Simulation() {

    val httpProtocol = http
            .baseUrl("http://localhost:8081")
            .acceptHeader("application/json")

    private val feederCsv = csv("testData.csv").random()
    private val idFeeder = generateSequence {
        val id = abs(Random().nextLong())
        mapOf("id" to id)
    }.iterator()

    private val scn = scenario("TestSimulation")
            .feed(idFeeder)
            .feed(feederCsv)
            .exec(http("ToDo create")
                    .post("/todos")
                    .body(StringBody("""{ 
                            |"id": #{id}, 
                            |"text": "Text text #{text}", 
                            |"completed": #{completed} }""".trimMargin()))
                    .check(status().`is`(201))
            )
    init {
        this.setUp(scn
                .injectOpen(constantUsersPerSec(500.0)
                        .during(Duration.ofSeconds(60)))
        ).protocols(httpProtocol)
    }
}