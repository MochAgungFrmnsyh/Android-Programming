package com.example.todolist.presentation.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.model.Priority
import com.example.todolist.data.model.Todo
import com.example.todolist.data.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TodoViewModel : ViewModel() {
    private val repository = TodoRepository()
    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos = _todos.asStateFlow()

    fun observeTodos(userId: String) {
        viewModelScope.launch {
            repository.getTodos(userId).collect { todoList ->
                // SORTING BY PRIORITY: HIGH -> MEDIUM -> LOW
                _todos.value = todoList.sortedWith(
                    compareBy<Todo> { it.isCompleted } // Belum selesai duluan
                        .thenBy {
                            when(it.priority) {
                                "HIGH" -> 0
                                "MEDIUM" -> 1
                                "LOW" -> 2
                                else -> 3
                            }
                        }
                        .thenByDescending { it.createdAt } // Terbaru duluan
                )
            }
        }
    }

    // UPDATE: Tambah parameter priority
    fun add(userId: String, title: String, priority: String = "LOW") = viewModelScope.launch {
        repository.addTodo(userId, title, priority)
    }

    fun toggle(userId: String, todo: Todo) = viewModelScope.launch {
        repository.updateTodoStatus(userId, todo.id, !todo.isCompleted)
    }

    // UPDATE: Tambah parameter priority
    fun updateTodo(userId: String, todoId: String, newTitle: String, priority: String) =
        viewModelScope.launch {
            repository.updateTodo(userId, todoId, newTitle, priority)
        }

    fun delete(userId: String, todoId: String) = viewModelScope.launch {
        repository.deleteTodo(userId, todoId)
    }
}