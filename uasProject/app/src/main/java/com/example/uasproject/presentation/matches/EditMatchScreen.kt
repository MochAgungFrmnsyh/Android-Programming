package com.example.uasproject.presentation.matches

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uasproject.data.model.FootballMatch
import com.example.uasproject.data.repository.MatchRepository
import java.text.SimpleDateFormat
import java.util.*

class EditMatchScreen : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val matchId = intent.getStringExtra("MATCH_ID")

        setContent {
            MaterialTheme {
                EditMatchContent(matchId = matchId)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMatchContent(matchId: String?) {

    val context = LocalContext.current
    val repository = remember { MatchRepository.getInstance(context) }
    val focusManager = LocalFocusManager.current

    // ===== WARNA TEMA SEPAKBOLA =====
    val FieldGreen = Color(0xFF1B5E20)
    val LightGreen = Color(0xFFE8F5E9)
    val AccentGreen = Color(0xFF43A047)

    // ===== DATA =====
    val existingMatch = remember { matchId?.let { repository.getMatchById(it) } }
    val isEditMode = existingMatch != null

    var homeTeam by remember { mutableStateOf(existingMatch?.homeTeam ?: "") }
    var awayTeam by remember { mutableStateOf(existingMatch?.awayTeam ?: "") }
    var competition by remember { mutableStateOf(existingMatch?.competition ?: "") }
    var venue by remember { mutableStateOf(existingMatch?.venue ?: "") }

    var selectedDate by remember {
        mutableStateOf(
            existingMatch?.matchDateTime ?: Calendar.getInstance().apply {
                add(Calendar.DAY_OF_MONTH, 1)
            }.time
        )
    }

    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
    val timeFormat = SimpleDateFormat("HH:mm", Locale("id", "ID"))

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        containerColor = LightGreen,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode) "Edit Match" else "Tambah Match",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        (context as? ComponentActivity)?.finish()
                    }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FieldGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus() // 🔥 KUNCI SOLUSI
                }
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ===== HEADER =====
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AccentGreen)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.SportsSoccer,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isEditMode)
                            "Edit detail match tim favoritmu"
                        else
                            "Tambah match tim favoritmu",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ===== FORM =====
            OutlinedTextField(
                value = homeTeam,
                onValueChange = { homeTeam = it },
                label = { Text("Tim Home") },
                placeholder = { Text("Contoh: Liverpool") },
                leadingIcon = { Icon(Icons.Default.Home, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = awayTeam,
                onValueChange = { awayTeam = it },
                label = { Text("Tim Away") },
                placeholder = { Text("Contoh: Manchester United") },
                leadingIcon = { Icon(Icons.Default.FlightTakeoff, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = competition,
                onValueChange = { competition = it },
                label = { Text("Kompetisi / Liga") },
                placeholder = { Text("Contoh: Premier League") },
                leadingIcon = { Icon(Icons.Default.EmojiEvents, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = venue,
                onValueChange = { venue = it },
                label = { Text("Stadion (Opsional)") },
                placeholder = { Text("Contoh: Anfield") },
                leadingIcon = { Icon(Icons.Default.Place, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // ===== TANGGAL =====
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val cal = Calendar.getInstance().apply { time = selectedDate }
                    DatePickerDialog(
                        context,
                        { _, y, m, d ->
                            cal.set(y, m, d)
                            selectedDate = cal.time
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Event, tint = FieldGreen, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Tanggal Pertandingan", fontSize = 12.sp, color = Color.Gray)
                        Text(dateFormat.format(selectedDate), fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ===== WAKTU =====
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val cal = Calendar.getInstance().apply { time = selectedDate }
                    TimePickerDialog(
                        context,
                        { _, h, m ->
                            cal.set(Calendar.HOUR_OF_DAY, h)
                            cal.set(Calendar.MINUTE, m)
                            selectedDate = cal.time
                        },
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        true
                    ).show()
                }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Schedule, tint = FieldGreen, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Kick-off", fontSize = 12.sp, color = Color.Gray)
                        Text("${timeFormat.format(selectedDate)} WIB", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ===== ERROR =====
            if (showError) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }

            // ===== SIMPAN =====
            Button(
                onClick = {
                    when {
                        homeTeam.isBlank() -> {
                            showError = true
                            errorMessage = "Tim Home harus diisi"
                        }
                        awayTeam.isBlank() -> {
                            showError = true
                            errorMessage = "Tim Away harus diisi"
                        }
                        competition.isBlank() -> {
                            showError = true
                            errorMessage = "Kompetisi harus diisi"
                        }
                        selectedDate.before(Date()) -> {
                            showError = true
                            errorMessage = "Tanggal pertandingan tidak valid"
                        }
                        else -> {
                            val match = if (isEditMode && existingMatch != null) {
                                existingMatch.copy(
                                    homeTeam = homeTeam.trim(),
                                    awayTeam = awayTeam.trim(),
                                    competition = competition.trim(),
                                    venue = venue.trim(),
                                    matchDateTime = selectedDate
                                )
                            } else {
                                FootballMatch(
                                    homeTeam = homeTeam.trim(),
                                    awayTeam = awayTeam.trim(),
                                    competition = competition.trim(),
                                    venue = venue.trim(),
                                    matchDateTime = selectedDate
                                )
                            }

                            if (isEditMode) {
                                repository.updateMatch(match)
                            } else {
                                repository.insertMatch(match)
                            }

                            // 🔥 KASIH TANDA KE SCREEN SEBELUMNYA
                            (context as ComponentActivity).setResult(RESULT_OK)

                            Toast.makeText(context, "Match berhasil disimpan", Toast.LENGTH_SHORT).show()
                            (context as ComponentActivity).finish()
                        }

                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FieldGreen),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    if (isEditMode) "Update Match" else "Simpan Match",
                    fontWeight = FontWeight.Bold
                )
            }

            if (isEditMode) {
                OutlinedButton(
                    onClick = { (context as? ComponentActivity)?.finish() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Batal")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}