package model

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.serpro69.kfaker.faker
import kotlinx.serialization.Serializable

@Serializable

data class ToDoUpdate(
        val text: String,
        val completed: Boolean
)
