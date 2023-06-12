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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import utils.SearchHelper
import java.util.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UpdateTest {

    private val faker = faker {}
    private lateinit var reqSpec: RequestSpecification
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
    @DisplayName("Happy Path. Correct data")
    @Order(0)
    fun positiveUpdateTodo() {
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

        //update
        val updatedToDo = ToDo(
                faker.random.nextLong().toULong(),
                faker.tolkien.characters(),
                true)
        given()
                .spec(reqSpec)
                .body(Json.encodeToString(updatedToDo))
                .put("/todos/${originalToDo.id}")
                .then()
                .statusCode(HttpStatus.SC_OK)

        val notExistToDo = SearchHelper().getToDoById(reqSpec, originalToDo.id)
        assertThat(notExistToDo).isNull()
        val foundToDo = SearchHelper().getToDoById(reqSpec, updatedToDo.id)
        assertThat(foundToDo).isNotEqualTo(originalToDo)
        assertThat(foundToDo).isEqualTo(updatedToDo)
    }

    @Test
    @DisplayName("Happy Path. Correct data id only")
    @Order(1)
    fun positiveUpdateIdOnly() {
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

        //update
        val updatedToDo = ToDo(
                faker.random.nextLong().toULong(),
                originalToDo.text,
                originalToDo.completed)
        given()
                .spec(reqSpec)
                .body(Json.encodeToString(updatedToDo))
                .put("/todos/${originalToDo.id}")
                .then()
                .statusCode(HttpStatus.SC_OK)

        val notExistToDo = SearchHelper().getToDoById(reqSpec, originalToDo.id)
        assertThat(notExistToDo).isNull()
        val foundToDo = SearchHelper().getToDoById(reqSpec, updatedToDo.id)
        assertThat(foundToDo).isNotEqualTo(originalToDo)
        assertThat(foundToDo).isEqualTo(updatedToDo)
    }

    @Test
    @DisplayName("Happy Path. Correct data text only")
    @Order(1)
    fun positiveUpdateTextOnly() {
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

        //update
        val updatedToDo = ToDo(
                originalToDo.id,
                faker.tolkien.characters(),
                originalToDo.completed)
        given()
                .spec(reqSpec)
                .body(Json.encodeToString(updatedToDo))
                .put("/todos/${originalToDo.id}")
                .then()
                .statusCode(HttpStatus.SC_OK)

        val existToDo = SearchHelper().getToDoById(reqSpec, originalToDo.id)
        assertThat(existToDo).isNotNull()
        val foundToDo = SearchHelper().getToDoById(reqSpec, updatedToDo.id)
        assertThat(foundToDo).isNotEqualTo(originalToDo)
        assertThat(foundToDo).isEqualTo(updatedToDo)
    }

    @Test
    @DisplayName("Happy Path. Correct data completed only")
    @Order(1)
    fun positiveUpdateCompletedOnly() {
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

        //update
        val updatedToDo = ToDo(
                originalToDo.id,
                originalToDo.text,
                true)
        given()
                .spec(reqSpec)
                .body(Json.encodeToString(updatedToDo))
                .put("/todos/${originalToDo.id}")
                .then()
                .statusCode(HttpStatus.SC_OK)

        val existToDo = SearchHelper().getToDoById(reqSpec, originalToDo.id)
        assertThat(existToDo).isNotNull()
        val foundToDo = SearchHelper().getToDoById(reqSpec, updatedToDo.id)
        assertThat(foundToDo).isNotEqualTo(originalToDo)
        assertThat(foundToDo).isEqualTo(updatedToDo)
    }

    @DisplayName("Negative. Incorrect Formats")
    @ParameterizedTest
    @CsvFileSource(resources = ["/updateNegative.csv"], numLinesToSkip = 0, delimiter = ';')
    @Order(1)
    fun negativeIncorrectFormats(input: String) {
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

        val responseMessage = given()
                .spec(reqSpec)
                .body(input)
                .put("/todos/${originalToDo.id}")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response().asPrettyString()
        assertThat(responseMessage).contains("Request body deserialize error:")
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
                .body(originalToDo)
                .put("/todos/${originalToDo.id}")
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .extract().response().prettyPrint()
    }
}