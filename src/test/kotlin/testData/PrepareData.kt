package testData

import io.github.serpro69.kfaker.faker
import io.restassured.RestAssured
import io.restassured.specification.RequestSpecification
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.ToDo
import org.apache.http.HttpStatus

class PrepareData {


    fun generateData(reqSpec: RequestSpecification) {
        val faker = faker { }
        for (i in 1..10) {
            RestAssured.given()
                    .spec(reqSpec)
                    .body(Json.encodeToString(
                            ToDo(faker.random.nextLong().toULong(),
                                    faker.lovecraft.words(),
                                    faker.random.nextBoolean())))
                    .post("/todos")
                    .then()
                    .statusCode(HttpStatus.SC_CREATED)
        }
    }
}