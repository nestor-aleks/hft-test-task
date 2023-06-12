package model

import kotlinx.serialization.Serializable

@Serializable
data class ToDo(
        val id: ULong,
        val text: String,
        val completed: Boolean
)