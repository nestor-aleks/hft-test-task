package utils

import io.restassured.RestAssured
import io.restassured.specification.RequestSpecification
import model.ToDo
import model.ToDoString
import java.util.Arrays

class SearchHelper {

    fun searchToDoById(searchId: ULong, allToDos: MutableList<ToDoString>): ToDo? {
        allToDos.forEach {
            if (it.id.toULong() == searchId) {
                return ToDo(it.id.toULong(), it.text, it.completed)
            }
        }
        return null
    }


    /**
     * method to return all exist todos
     * better to use sql call to db instead rest request
     */
    fun getAllToDos(reqSpec: RequestSpecification): MutableList<ToDoString>? {
        return RestAssured.given()
                .spec(reqSpec)
                .get("/todos")
                .then()
                .extract()
                .jsonPath()
                .getList(".", ToDoString::class.java)
    }

    fun getToDoById(reqSpec: RequestSpecification, searchId: ULong): ToDo? {
        return searchToDoById(searchId, RestAssured.given()
                .spec(reqSpec)
                .get("/todos")
                .then()
                .extract()
                .jsonPath()
                .getList(".", ToDoString::class.java))
    }
}