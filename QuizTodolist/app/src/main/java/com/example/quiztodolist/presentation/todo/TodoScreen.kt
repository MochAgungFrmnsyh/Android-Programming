package com.example.quiztodolist.presentation.todo

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.quiztodolist.data.model.UserData
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    userData: UserData?,
    viewModel: TodoViewModel,
    onSignOut: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    var todoText by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var expandedPriority by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val filteredTodos by viewModel.filteredTodos.collectAsState()
    val statistics by viewModel.statistics.collectAsState()

    LaunchedEffect(userData?.userId) {
        userData?.userId?.let { viewModel.observeTodos(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WriteDo", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    userData?.let {
                        Text(it.username ?: "", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.width(8.dp))
                        AsyncImage(
                            model = it.profilePictureUrl,
                            contentDescription = null,
                            modifier = Modifier.size(35.dp).clip(CircleShape)
                        )
                        IconButton(onClick = onSignOut) {
                            Text("SignOut", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            DashboardCard(statistics)

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.setSearchQuery(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Cari tugas...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "SEMUA" to "Semua",
                        "BELUM_SELESAI" to "Belum Selesai",
                        "KERJA" to "💼 Kerja",
                        "KULIAH" to "📚 Kuliah",
                        "HOBBY" to "🎮 Hobby"
                    ).forEach { (value, label) ->
                        item {
                            FilterChip(
                                selected = selectedFilter == value,
                                onClick = { viewModel.setFilter(value) },
                                label = { Text(label) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        TextField(
                            value = todoText,
                            onValueChange = { todoText = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Tambah tugas baru...") },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )

                        if (todoText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ExposedDropdownMenuBox(
                                    expanded = expandedCategory,
                                    onExpandedChange = { expandedCategory = it },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    OutlinedButton(
                                        onClick = { expandedCategory = true },
                                        modifier = Modifier.fillMaxWidth().menuAnchor()
                                    ) {
                                        Text(
                                            when (selectedCategory) {
                                                "KERJA" -> "💼 Kerja"
                                                "KULIAH" -> "📚 Kuliah"
                                                "HOBBY" -> "🎮 Hobby"
                                                else -> "Pilih Kategori"
                                            }
                                        )
                                    }

                                    ExposedDropdownMenu(
                                        expanded = expandedCategory,
                                        onDismissRequest = { expandedCategory = false }
                                    ) {
                                        listOf("KERJA", "KULIAH", "HOBBY").forEach {
                                            DropdownMenuItem(
                                                text = { Text(it) },
                                                onClick = {
                                                    selectedCategory = it
                                                    expandedCategory = false
                                                }
                                            )
                                        }
                                    }
                                }

                                ExposedDropdownMenuBox(
                                    expanded = expandedPriority,
                                    onExpandedChange = { expandedPriority = it },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    OutlinedButton(
                                        onClick = { expandedPriority = true },
                                        modifier = Modifier.fillMaxWidth().menuAnchor()
                                    ) {
                                        Text(
                                            when (selectedPriority) {
                                                "HIGH" -> "🔴 Tinggi"
                                                "MEDIUM" -> "🟡 Sedang"
                                                "LOW" -> "🟢 Rendah"
                                                else -> "Pilih Prioritas"
                                            }
                                        )
                                    }

                                    ExposedDropdownMenu(
                                        expanded = expandedPriority,
                                        onDismissRequest = { expandedPriority = false }
                                    ) {
                                        listOf("HIGH", "MEDIUM", "LOW").forEach {
                                            DropdownMenuItem(
                                                text = { Text(it) },
                                                onClick = {
                                                    selectedPriority = it
                                                    expandedPriority = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (
                                    todoText.isNotBlank() &&
                                    selectedCategory != null &&
                                    selectedPriority != null
                                ) {
                                    userData?.userId?.let {
                                        viewModel.add(
                                            it,
                                            todoText,
                                            selectedPriority!!,
                                            selectedCategory!!
                                        )
                                    }
                                    todoText = ""
                                    selectedCategory = null
                                    selectedPriority = null
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = todoText.isNotBlank()
                        ) {
                            Text("+ Tambah Tugas")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredTodos, key = { it.id }) { todo ->
                    val deleteAction = SwipeAction(
                        icon = {},
                        background = Color.Red,
                        onSwipe = {
                            userData?.userId?.let { uid ->
                                viewModel.delete(uid, todo.id)
                            }
                        }
                    )

                    SwipeableActionsBox(
                        endActions = listOf(deleteAction),
                        swipeThreshold = 100.dp
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


// ✅ Dashboard Statistics Card
@Composable
fun DashboardCard(statistics: TodoStatistics) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "📊 Dashboard Statistik",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Statistics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Total Tugas",
                    value = statistics.total.toString(),
                    icon = "📝",
                    color = MaterialTheme.colorScheme.primary
                )
                StatItem(
                    label = "Selesai",
                    value = statistics.completed.toString(),
                    icon = "✅",
                    color = Color(0xFF4CAF50)
                )
                StatItem(
                    label = "Pending",
                    value = statistics.pending.toString(),
                    icon = "⏳",
                    color = Color(0xFFFF9800)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Progress Bar Section
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Progress Penyelesaian",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "${(statistics.progress * 100).toInt()}%",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Animated Progress Bar
                LinearProgressIndicator(
                    progress = { statistics.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = when {
                        statistics.progress >= 0.8f -> Color(0xFF4CAF50)
                        statistics.progress >= 0.5f -> Color(0xFFFFC107)
                        else -> Color(0xFFFF5252)
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Text
                Text(
                    text = when {
                        statistics.total == 0 -> "Belum ada tugas. Yuk mulai tambahkan!"
                        statistics.progress == 1f -> "🎉 Sempurna! Semua tugas selesai!"
                        statistics.progress >= 0.8f -> "💪 Hampir selesai! Tetap semangat!"
                        statistics.progress >= 0.5f -> "👍 Progres bagus! Teruskan!"
                        else -> "📝 Masih banyak yang harus diselesaikan!"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

// ✅ Stat Item Component
@Composable
fun StatItem(label: String, value: String, icon: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

// ✅ Todo Item Card Component
@Composable
fun TodoItemCard(
    todo: com.example.quiztodolist.data.model.Todo,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val priorityColor = when (todo.priority) {
        "HIGH" -> Color(0xFFFF5252)
        "MEDIUM" -> Color(0xFFFFC107)
        "LOW" -> Color(0xFF4CAF50)
        else -> Color.Gray
    }

    val priorityText = when (todo.priority) {
        "HIGH" -> "🔴 Tinggi"
        "MEDIUM" -> "🟡 Sedang"
        "LOW" -> "🟢 Rendah"
        else -> ""
    }

    val categoryIcon = when (todo.category.ifEmpty { "KERJA" }) {
        "KERJA" -> "💼"
        "KULIAH" -> "📚"
        "HOBBY" -> "🎮"
        else -> "📝"
    }

    val categoryText = when (todo.category.ifEmpty { "KERJA" }) {
        "KERJA" -> "Kerja"
        "KULIAH" -> "Kuliah"
        "HOBBY" -> "Hobby"
        else -> "Kerja"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (todo.isCompleted)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Priority Indicator Bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .background(priorityColor, RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Checkbox
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = { onToggle() }
            )

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (todo.isCompleted) FontWeight.Normal else FontWeight.Medium
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "$categoryIcon $categoryText",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = priorityText,
                        style = MaterialTheme.typography.bodySmall,
                        color = priorityColor
                    )
                }
            }

            // Delete Button
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}