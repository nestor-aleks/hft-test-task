import io.github.serpro69.kfaker.faker
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import model.ToDoString
import org.apache.http.HttpStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import testData.PrepareData


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReadTest {

    private lateinit var reqSpec: RequestSpecification

    @BeforeAll
    fun beforeAll() {
        val requestSpecBuilder = RequestSpecBuilder()
        requestSpecBuilder.setBaseUri("http://localhost")
        requestSpecBuilder.setPort(8081)
        requestSpecBuilder.setContentType(ContentType.JSON)
        reqSpec = requestSpecBuilder.build()
        PrepareData().generateData(reqSpec)
    }

    @AfterAll
    fun afterAll() {
        RestAssured.reset()
    }

    @Test
    @DisplayName("Happy Path. Get data")
    fun positiveCreateTodo() {
        val result: MutableList<ToDoString> = given()
                .spec(reqSpec)
                .get("/todos?offset=0&limit=5")
                .then()
                .extract()
                .jsonPath()
                .getList(".", ToDoString::class.java)
        assertThat(result).isNotNull()
        assertThat(result.size).isEqualTo(5)
    }

    @DisplayName("Negative Checks")
    @ParameterizedTest
    @CsvFileSource(resources = ["/readNegative.csv"], numLinesToSkip = 0, delimiter = ',')
    fun negativeChecks(offset: String, limit: String) {
        val responseMessage = given()
                .spec(reqSpec)
                .get("/todos?offset=$offset&limit=$limit")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response().asPrettyString()
        assertThat(responseMessage).isEqualTo("Invalid query string")
    }

    @Test
    @DisplayName("Happy Path. Check Pagination")
    fun positiveCheckPagination() {
        val firstPart: MutableList<ToDoString> = given()
                .spec(reqSpec)
                .get("/todos?offset=0&limit=5")
                .then()
                .extract()
                .jsonPath()
                .getList(".", ToDoString::class.java)
        assertThat(firstPart).isNotNull()
        assertThat(firstPart.size).isEqualTo(5)

        val secondPart: MutableList<ToDoString> = given()
                .spec(reqSpec)
                .get("/todos?offset=4&limit=5")
                .then()
                .extract()
                .jsonPath()
                .getList(".", ToDoString::class.java)
        assertThat(secondPart).isNotNull()
        assertThat(secondPart.size).isEqualTo(5)

        //check the pagination
        assertThat(firstPart[4]).isEqualTo(secondPart[0])
    }

    @Test
    @DisplayName("Happy Path. Check Boundary")
    fun positiveCheckBoundary() {
        val allRecords: MutableList<ToDoString> = given()
                .spec(reqSpec)
                .get("/todos?offset=0")
                .then()
                .extract()
                .jsonPath()
                .getList(".", ToDoString::class.java)
        assertThat(allRecords).isNotNull()
        println("total: ${allRecords.size}")

        val latestRecord: MutableList<ToDoString> = given()
                .spec(reqSpec)
                .get("/todos?offset=${allRecords.size - 1}&limit=1")
                .then()
                .extract()
                .jsonPath()
                .getList(".", ToDoString::class.java)
        assertThat(latestRecord).isNotNull()
        assertThat(latestRecord.size).isEqualTo(1)

        assertThat(allRecords[allRecords.size - 1]).isEqualTo(latestRecord[0])
    }
}