import io.github.serpro69.kfaker.faker
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.ToDo
import org.apache.http.HttpStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import utils.SearchHelper
import java.util.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeleteTest {

    val faker = faker {}
    lateinit var reqSpec: RequestSpecification
    private val host = "http://localhost"
    private val port = 8081

    @BeforeAll
    fun beforeAll() {
        val requestSpecBuilder = RequestSpecBuilder()
        requestSpecBuilder.setBaseUri(host)
        requestSpecBuilder.setPort(port)
        requestSpecBuilder.setContentType(ContentType.JSON)


        val passString = "admin:admin"
        val encodedString: String = Base64.getEncoder().encodeToString(passString.toByteArray())
        requestSpecBuilder.addHeader("Authorization", "Basic $encodedString")
        reqSpec = requestSpecBuilder.build()
    }

    @AfterAll
    fun afterAll() {
        RestAssured.reset()
    }

    @Test
    @DisplayName("Happy Path. Delete ToDo")
    @Order(0)
    fun positiveDeleteTodo() {
        val originalToDo = ToDo(
                faker.random.nextLong().toULong(),
                faker.tolkien.characters(),
                false)

        given()
                .spec(reqSpec)
                .body(Json.encodeToString(originalToDo))
                .post("/todos")
                .then()
                .statusCode(HttpStatus.SC_CREATED)

        //delete
        given()
                .spec(reqSpec)
                .delete("/todos/${originalToDo.id}")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT)

        val notExistToDo = SearchHelper().getToDoById(reqSpec, originalToDo.id)
        assertThat(notExistToDo).isNull()
    }

    @DisplayName("Negative. Access check")
    @Test
    @Order(999)
    fun negativeAccessCheck() {
        val originalToDo = ToDo(
                faker.random.nextLong().toULong(),
                faker.tolkien.characters(),
                faker.random.nextBoolean())
        given()
                .spec(reqSpec)
                .body(Json.encodeToString(originalToDo))
                .post("/todos")
                .then()
                .statusCode(HttpStatus.SC_CREATED)

        RestAssured.reset()
        given()
                .baseUri(host)
                .port(port)
                .delete("/todos/${originalToDo.id}")
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
    }

    @DisplayName("Negative. Incorrect ID")
    @Test
    @Order(1)
    fun negativeIncorrectId() {
        given()
                .spec(reqSpec)
                .delete("/todos/-1")
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
    }
}