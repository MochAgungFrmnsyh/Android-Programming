package com.example.quiztodolist.data.model

import com.google.firebase.firestore.PropertyName

data class Todo(
    val id: String = "",
    val title: String = "",

    @get:PropertyName("isCompleted")
    @set:PropertyName("isCompleted")
    var isCompleted: Boolean = false,

    val createdAt: Long = System.currentTimeMillis(),
    val priority: String = "LOW", // DEFAULT: MEDIUM
    val category: String = "KERJA" // DEFAULT: KERJA - INI PENTING!
)

enum class Priority {
    HIGH, MEDIUM, LOW;

    fun toDisplayString(): String = when(this) {
        HIGH -> "Tinggi"
        MEDIUM -> "Sedang"
        LOW -> "Rendah"
    }
}

enum class Category {
    KERJA, KULIAH, HOBBY;

    fun toDisplayString(): String = when(this) {
        KERJA -> "💼 Kerja"
        KULIAH -> "📚 Kuliah"
        HOBBY -> "🎮 Hobby"
    }

    fun getIcon(): String = when(this) {
        KERJA -> "💼"
        KULIAH -> "📚"
        HOBBY -> "🎮"
    }
}