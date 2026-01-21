package com.example.quiztodolist.presentation.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiztodolist.data.model.Priority
import com.example.quiztodolist.data.model.Todo
import com.example.quiztodolist.data.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.sortedWith

class TodoViewModel : ViewModel() {
    private val repository = TodoRepository()

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos = _todos.asStateFlow()

    // Filter State
    private val _selectedFilter = MutableStateFlow("SEMUA")
    val selectedFilter = _selectedFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Filtered Todos
    private val _filteredTodos = MutableStateFlow<List<Todo>>(emptyList())
    val filteredTodos = _filteredTodos.asStateFlow()

    // ✅ PERBAIKAN: Statistics sebagai StateFlow yang update otomatis
    private val _statistics = MutableStateFlow(TodoStatistics(0, 0, 0, 0f))
    val statistics = _statistics.asStateFlow()

    fun observeTodos(userId: String) {
        viewModelScope.launch {
            repository.getTodos(userId).collect { todoList ->
                val sorted = todoList.sortedWith(
                    compareBy<Todo> { it.isCompleted }
                        .thenBy {
                            when(it.priority) {
                                "HIGH" -> 0
                                "MEDIUM" -> 1
                                "LOW" -> 2
                                else -> 3
                            }
                        }
                        .thenByDescending { it.createdAt }
                )
                _todos.value = sorted
                updateStatistics(sorted) // ✅ Update statistics
                applyFilter()
            }
        }
    }

    // ✅ TAMBAHAN BARU: Update Statistics
    private fun updateStatistics(todoList: List<Todo>) {
        val total = todoList.size
        val completed = todoList.count { it.isCompleted }
        val pending = total - completed
        val progress = if (total > 0) (completed.toFloat() / total.toFloat()) else 0f

        _statistics.value = TodoStatistics(
            total = total,
            completed = completed,
            pending = pending,
            progress = progress
        )
    }

    // Filter Function
    fun setFilter(filter: String) {
        _selectedFilter.value = filter
        applyFilter()
    }

    // Search Function
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilter()
    }

    // Apply Filter & Search
    private fun applyFilter() {
        var result = _todos.value

        // Filter by category/status
        result = when (_selectedFilter.value) {
            "BELUM_SELESAI" -> result.filter { !it.isCompleted }
            "KERJA" -> result.filter { it.category == "KERJA" }
            "KULIAH" -> result.filter { it.category == "KULIAH" }
            "HOBBY" -> result.filter { it.category == "HOBBY" }
            else -> result // SEMUA
        }

        // Search by title
        if (_searchQuery.value.isNotBlank()) {
            result = result.filter {
                it.title.contains(_searchQuery.value, ignoreCase = true)
            }
        }

        _filteredTodos.value = result
    }

    // UPDATE: Tambah category
    fun add(
        userId: String,
        title: String,
        priority: String = "LOW",
        category: String = "KERJA"
    ) = viewModelScope.launch {
        repository.addTodo(userId, title, priority, category)
    }

    fun toggle(userId: String, todo: Todo) = viewModelScope.launch {
        repository.updateTodoStatus(userId, todo.id, !todo.isCompleted)
    }

    // UPDATE: Tambah category
    fun updateTodo(
        userId: String,
        todoId: String,
        newTitle: String,
        priority: String,
        category: String
    ) = viewModelScope.launch {
        repository.updateTodo(userId, todoId, newTitle, priority, category)
    }

    fun delete(userId: String, todoId: String) = viewModelScope.launch {
        repository.deleteTodo(userId, todoId)
    }
}

// Data class untuk statistics
data class TodoStatistics(
    val total: Int,
    val completed: Int,
    val pending: Int,
    val progress: Float
)