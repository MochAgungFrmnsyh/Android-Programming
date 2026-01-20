package com.example.todolist.data.model

import com.google.firebase.firestore.PropertyName

data class Todo(
    val id: String = "",
    val title: String = "",

    @get:PropertyName("isCompleted")
    @set:PropertyName("isCompleted")
    var isCompleted: Boolean = false,

    val createdAt: Long = System.currentTimeMillis(),

    // TAMBAHAN BARU
    val priority: String = "HIGH" // HIGH, MEDIUM, LOW
)

// Enum untuk Priority
enum class Priority {
    HIGH, MEDIUM, LOW;

    fun toDisplayString(): String = when(this) {
        HIGH -> "Tinggi"
        MEDIUM -> "Sedang"
        LOW -> "Rendah"
    }
}