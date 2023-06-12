package model

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.serialization.Serializable

@Serializable

data class ToDoString(
        val id: String,
        val text: String,
        val completed: Boolean
)