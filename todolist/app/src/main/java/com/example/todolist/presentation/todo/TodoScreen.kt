package com.example.todolist.presentation.todo

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.todolist.data.model.UserData
import com.example.todolist.data.model.Todo
import androidx.compose.ui.graphics.luminance
import androidx.compose.foundation.BorderStroke


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    userData: UserData?,
    viewModel: TodoViewModel,
    onSignOut: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    var todoText by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("LOW") }
    var expanded by remember { mutableStateOf(false) }
    val todos by viewModel.todos.collectAsState()

    LaunchedEffect(userData?.userId) {
        userData?.userId?.let { viewModel.observeTodos(it) }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "TodoList App",
                        fontWeight = FontWeight.Medium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    userData?.let {
                        Text(
                            it.username ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        AsyncImage(
                            model = it.profilePictureUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                        )
                        TextButton(onClick = onSignOut) {
                            Text("Sign out", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            )
        }
    )
    { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // CARD INPUT - LEBIH MENARIK
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    TextField(
                        value = todoText,
                        onValueChange = { todoText = it },
                        placeholder = { Text("Add new task") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    )


                    Spacer(modifier = Modifier.height(8.dp))

                    // DROPDOWN PRIORITY
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = it }
                        ) {
                            OutlinedButton(
                                onClick = { expanded = true },
                                modifier = Modifier.menuAnchor()
                            ) {
                                Text(
                                    when (selectedPriority) {
                                        "HIGH" -> "🔴 High"
                                        "MEDIUM" -> "🟡 Medium"
                                        "LOW" -> "🟢 Low"
                                        else -> "Select Priority"
                                    }
                                )
                            }
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("🔴 High") },
                                    onClick = {
                                        selectedPriority = "HIGH"
                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("🟡 Medium") },
                                    onClick = {
                                        selectedPriority = "MEDIUM"
                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("🟢 Low") },
                                    onClick = {
                                        selectedPriority = "LOW"
                                        expanded = false
                                    }
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (todoText.isNotBlank()) {
                                    userData?.userId?.let {
                                        viewModel.add(it, todoText, selectedPriority)
                                    }
                                    todoText = ""
                                    selectedPriority = "LOW"
                                }
                            },
                            enabled = todoText.isNotBlank()
                        ) {
                            Text("+ Tambah")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ANIMASI MUNCUL DARI BAWAH
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(todos, key = { it.id }) { todo ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(
                            initialOffsetY = { it / 2 }
                        ),
                        exit = fadeOut() + slideOutHorizontally()
                    ) {
                        TodoItemCard(
                            todo = todo,
                            onToggle = {
                                userData?.userId?.let { uid ->
                                    viewModel.toggle(uid, todo)
                                }
                            },
                            onDelete = {
                                userData?.userId?.let { uid ->
                                    viewModel.delete(uid, todo.id)
                                }
                            },
                            onClick = { onNavigateToEdit(todo.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TodoItemCard(
    todo: Todo,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val isDark =
        MaterialTheme.colorScheme.background.luminance() < 0.5f

    val borderColor = if (isDark)
        MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
    else
        Color.Transparent

    val containerColor = if (todo.isCompleted)
        MaterialTheme.colorScheme.surfaceVariant
    else
        MaterialTheme.colorScheme.surface

    val priorityColor = when (todo.priority) {
        "HIGH" -> Color(0xFFFF8A80)
        "MEDIUM" -> Color(0xFFFFE082)
        "LOW" -> Color(0xFFA5D6A7)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(36.dp)
                    .background(priorityColor, RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = { onToggle() }
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = todo.priority,
                    style = MaterialTheme.typography.bodySmall,
                    color = priorityColor
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null
                )
            }
        }
    }
}
