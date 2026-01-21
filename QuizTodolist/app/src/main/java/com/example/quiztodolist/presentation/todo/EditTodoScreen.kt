package com.example.quiztodolist.presentation.todo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quiztodolist.data.model.Todo
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTodoScreen(
    todo: Todo,
    onSave: (String, String, String) -> Unit, // title, priority, category
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(todo.title) }
    var selectedPriority by remember { mutableStateOf(todo.priority) }
    var selectedCategory by remember { mutableStateOf(todo.category) }
    var expandedPriority by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }

    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val dateString = sdf.format(Date(todo.createdAt))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Tugas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
        ) {
            // CARD UTAMA
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // HEADER
                    Text(
                        text = "Detail Tugas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // INPUT JUDUL TUGAS - SINGLE LINE
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Judul Tugas") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true, // ✅ SINGLE LINE
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // DROPDOWN KATEGORI
                    Text(
                        text = "Kategori",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedCategory,
                        onExpandedChange = { expandedCategory = it }
                    ) {
                        OutlinedTextField(
                            value = when (selectedCategory) {
                                "KERJA" -> "💼 Kerja"
                                "KULIAH" -> "📚 Kuliah"
                                "HOBBY" -> "🎮 Hobby"
                                else -> "Pilih Kategori"
                            },
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategory,
                            onDismissRequest = { expandedCategory = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("💼 Kerja") },
                                onClick = {
                                    selectedCategory = "KERJA"
                                    expandedCategory = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("📚 Kuliah") },
                                onClick = {
                                    selectedCategory = "KULIAH"
                                    expandedCategory = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🎮 Hobby") },
                                onClick = {
                                    selectedCategory = "HOBBY"
                                    expandedCategory = false
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // DROPDOWN PRIORITAS
                    Text(
                        text = "Prioritas",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedPriority,
                        onExpandedChange = { expandedPriority = it }
                    ) {
                        OutlinedTextField(
                            value = when (selectedPriority) {
                                "HIGH" -> "🔴 Tinggi"
                                "MEDIUM" -> "🟡 Sedang"
                                "LOW" -> "🟢 Rendah"
                                else -> "Pilih Priority"
                            },
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPriority)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedPriority,
                            onDismissRequest = { expandedPriority = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("🔴 Tinggi") },
                                onClick = {
                                    selectedPriority = "HIGH"
                                    expandedPriority = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🟡 Sedang") },
                                onClick = {
                                    selectedPriority = "MEDIUM"
                                    expandedPriority = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🟢 Rendah") },
                                onClick = {
                                    selectedPriority = "LOW"
                                    expandedPriority = false
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // INFO TANGGAL DIBUAT
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "Dibuat · ",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = dateString,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // TOMBOL SIMPAN
            Button(
                onClick = {
                    onSave(title, selectedPriority, selectedCategory)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Simpan Perubahan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}