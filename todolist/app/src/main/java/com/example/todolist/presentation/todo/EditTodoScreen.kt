package com.example.todolist.presentation.todo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.todolist.data.model.Todo
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTodoScreen(
    todo: Todo,
    onSave: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(todo.title) }
    var selectedPriority by remember { mutableStateOf(todo.priority) }
    var expanded by remember { mutableStateOf(false) }

    val dateText = remember {
        SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.getDefault())
            .format(Date(todo.createdAt))
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Task",
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // === MAIN CARD ===
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = "Task Detail",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Title input (soft, clean)
                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("Task title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // === PRIORITY SELECTOR ===
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Priority",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = it }
                        ) {
                            OutlinedButton(
                                onClick = { expanded = true },
                                modifier = Modifier.menuAnchor(),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    when (selectedPriority) {
                                        "HIGH" -> "🔴 High"
                                        "MEDIUM" -> "🟡 Medium"
                                        "LOW" -> "🟢 Low"
                                        else -> "Select"
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
                    }

                    // === CREATED DATE (SUBTLE) ===
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Created • ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = dateText,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // === SAVE BUTTON ===
            Button(
                onClick = { onSave(title, selectedPriority) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    "Save Changes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
