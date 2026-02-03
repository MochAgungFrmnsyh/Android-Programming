package com.example.uasproject.presentation.matches

import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.uasproject.R
import com.example.uasproject.data.model.FootballMatch
import com.example.uasproject.data.repository.MatchRepository
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchListContent(
    onLogout: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember { MatchRepository.getInstance(context) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val focusManager = LocalFocusManager.current

    val user = FirebaseAuth.getInstance().currentUser
    val photoUrl = user?.photoUrl
    val userName = user?.displayName
    val userEmail = user?.email

    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var allMatches by remember { mutableStateOf(repository.getAllMatches()) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedMatch by remember { mutableStateOf<FootballMatch?>(null) }

    val displayedMatches by remember {
        derivedStateOf {
            try {
                if (searchQuery.isEmpty()) {
                    filterMatches(selectedTab, allMatches)
                } else {
                    searchMatches(allMatches, searchQuery)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = System.currentTimeMillis()
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            allMatches = repository.getAllMatches()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                allMatches = repository.getAllMatches()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0B6623),
                                Color(0xFF1B5E20),
                                Color(0xFF2E7D32)
                            )
                        )
                    )
            ) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.width(1.dp))
                            Text(
                                "Football Match Reminder",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToProfile) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFFA5D6A7),
                                                Color(0xFF2E7D32),
                                                Color(0xFF1B5E20)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                ) {
                                    if (photoUrl != null) {
                                        AsyncImage(
                                            model = photoUrl,
                                            contentDescription = "Profile",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = userName?.firstOrNull()?.uppercase() ?: "?",
                                                color = Color(0xFF2E7D32),
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = {
                        Text(
                            "Cari pertandingan...",
                            color = Color.Gray
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = Color.Gray
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2E7D32),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = Color(0xFF2E7D32),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp)
                )

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[selectedTab])
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = Color.White
                        )
                    },
                    divider = {}
                ) {
                    val tabs = listOf("Semua", "Akan Datang", "Hari Ini", "Selesai")

                    tabs.forEachIndexed { i, title ->
                        val isSelected = selectedTab == i

                        Tab(
                            selected = isSelected,
                            onClick = {
                                selectedTab = i
                                searchQuery = ""
                                focusManager.clearFocus()
                            },
                            modifier = Modifier.padding(vertical = 8.dp),
                            text = {
                                Text(
                                    text = title,
                                    color = if (isSelected)
                                        Color.White
                                    else
                                        Color.White.copy(alpha = 0.6f),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            var isFabPressed by remember { mutableStateOf(false) }
            val fabScale by animateFloatAsState(
                targetValue = if (isFabPressed) 0.9f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "fabScale"
            )

            val fabRotation by animateFloatAsState(
                targetValue = if (isFabPressed) 90f else 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "fabRotation"
            )

            FloatingActionButton(
                onClick = {
                    isFabPressed = true
                    context.startActivity(
                        Intent(context, EditMatchScreen::class.java)
                    )
                },
                modifier = Modifier
                    .offset(y = (-30).dp)
                    .scale(fabScale)
                    .rotate(fabRotation),
                shape = RoundedCornerShape(16.dp),
                containerColor = Color(0xFF2E7D32),
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Tambah Pertandingan",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            LaunchedEffect(isFabPressed) {
                if (isFabPressed) {
                    delay(150)
                    isFabPressed = false
                }
            }
        }
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize()) {

            Image(
                painter = painterResource(id = R.drawable.football_field),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(10.dp),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        focusManager.clearFocus()
                    }
            ) {
                if (displayedMatches.isEmpty()) {
                    EmptyStateView(selectedTab, searchQuery)
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = displayedMatches,
                            key = { it.id }
                        ) { match ->
                            MatchItem(
                                match = match,
                                currentTime = currentTime,
                                onClick = {
                                    selectedMatch = match
                                    showDialog = true
                                },
                                onDelete = {
                                    repository.deleteMatch(match.id)
                                    allMatches = repository.getAllMatches()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog && selectedMatch != null) {
        MatchOptionsDialog(
            match = selectedMatch!!,
            onDismiss = { showDialog = false },
            onEdit = {
                val i = Intent(context, EditMatchScreen::class.java)
                i.putExtra("MATCH_ID", selectedMatch!!.id)
                context.startActivity(i)
                showDialog = false
            },
            onDelete = {
                repository.deleteMatch(selectedMatch!!.id)
                allMatches = repository.getAllMatches()
                showDialog = false
            },
            onOpenUrl = {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, android.net.Uri.parse(selectedMatch!!.matchUrl))
                )
                showDialog = false
            }
        )
    }
}

@Composable
fun LogoutDialog(
    userName: String?,
    userEmail: String?,
    photoUrl: String?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            if (photoUrl != null) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName?.firstOrNull()?.uppercase() ?: "?",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        },
        title = {
            Text(
                "Keluar Akun",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = userName ?: "User",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
                if (userEmail != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = userEmail,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Yakin ingin keluar dari akun ini?",
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Keluar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFF2E7D32))
            ) {
                Text(
                    "Batal",
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchItem(match: FootballMatch, currentTime: Long, onClick: () -> Unit, onDelete: () -> Unit) {
    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale("id", "ID")) }

    val matchTime = remember(match.id) {
        Calendar.getInstance().apply {
            timeInMillis = match.matchDateTime.time
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    val remainingMillis = matchTime - currentTime
    val isFinished = remainingMillis <= 0

    val statusColor = remember(remainingMillis) {
        when {
            remainingMillis <= 0 -> Color.Gray
            match.isUpcoming(60) -> Color(0xFFE53935)
            match.isToday() -> Color(0xFFFF9800)
            else -> Color(0xFF43A047)
        }
    }

    var showDeleteConfirm by remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.EndToStart -> {
                    showDeleteConfirm = true
                    false
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    val intent = Intent(context, EditMatchScreen::class.java)
                    intent.putExtra("MATCH_ID", match.id)
                    context.startActivity(intent)
                    false
                }
                else -> false
            }
        },
        positionalThreshold = { it * 0.4f }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val scale by animateFloatAsState(
                targetValue = if (dismissState.targetValue != SwipeToDismissBoxValue.Settled) 1.1f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "scaleAnimation"
            )

            if (direction == SwipeToDismissBoxValue.StartToEnd) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF2E7D32),
                                    Color(0xFF43A047)
                                )
                            )
                        )
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.scale(scale)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Edit",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            } else if (direction == SwipeToDismissBoxValue.EndToStart) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFD32F2F),
                                    Color(0xFFE53935)
                                )
                            )
                        )
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.scale(scale)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Hapus",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        },
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true
    ) {
        var isPressed by remember { mutableStateOf(false) }
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.97f else 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            label = "cardScale"
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        isPressed = true
                        onClick()
                    }
                ),
            elevation = CardDefaults.cardElevation(6.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isFinished) Color(0xFFF5F5F5) else Color.White
            )
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(160.dp)
                        .background(statusColor)
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsSoccer,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isFinished) Color.Gray else Color(0xFF2E7D32)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = match.competition,
                                fontSize = 12.sp,
                                color = if (isFinished) Color.Gray else Color(0xFF2E7D32),
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(statusColor.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = if (isFinished) "Selesai" else match.getStatus(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = match.homeTeam,
                            fontSize = 16.sp,
                            fontWeight = if (isFinished) FontWeight.Normal else FontWeight.Bold,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1f),
                            color = if (isFinished) Color.Gray else Color(0xFF1B5E20)
                        )

                        Box(
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .clip(CircleShape)
                                .background(if (isFinished) Color(0xFFE0E0E0) else Color(0xFFE8F5E9))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "VS",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isFinished) Color.Gray else Color(0xFF2E7D32)
                            )
                        }

                        Text(
                            text = match.awayTeam,
                            fontSize = 16.sp,
                            fontWeight = if (isFinished) FontWeight.Normal else FontWeight.Bold,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.weight(1f),
                            color = if (isFinished) Color.Gray else Color(0xFF1B5E20)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = dateFormat.format(match.matchDateTime),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AccessTime,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = timeFormat.format(match.matchDateTime),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    if (match.venue.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Place,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = match.venue,
                                fontSize = 11.sp,
                                color = Color.Gray,
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(statusColor.copy(alpha = 0.1f))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                if (isFinished) Icons.Default.Check else Icons.Default.Timer,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = statusColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isFinished) {
                                    "Pertandingan Selesai"
                                } else {
                                    val totalSeconds = remainingMillis / 1000
                                    val h = totalSeconds / 3600
                                    val m = (totalSeconds % 3600) / 60
                                    val s = totalSeconds % 60
                                    String.format("%02d:%02d:%02d", h, m, s)
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        }
                    }
                }
            }
        }

        LaunchedEffect(isPressed) {
            if (isPressed) {
                delay(100)
                isPressed = false
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirm = false
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color(0xFFE53935),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    "Hapus Pertandingan",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Yakin ingin menghapus pertandingan",
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = match.getTitle(),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF2E7D32)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = match.competition,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showDeleteConfirm = false
                    },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFF2E7D32))
                ) {
                    Text(
                        "Batal",
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun EmptyStateView(selectedTab: Int, searchQuery: String = "") {

    if (searchQuery.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.SearchOff,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tidak ditemukan",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tidak ada pertandingan yang cocok dengan \"$searchQuery\"",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
        return
    }

    val (title, subtitle, icon) = when (selectedTab) {
        0 -> Triple(
            "Belum ada pertandingan",
            "Tap tombol + untuk menambah pertandingan favorit kamu",
            Icons.Default.SportsSoccer
        )
        1 -> Triple(
            "Tidak ada pertandingan mendatang",
            "Belum ada jadwal pertandingan yang akan datang",
            Icons.Default.Event
        )
        2 -> Triple(
            "Tidak ada pertandingan hari ini",
            "Hari ini tidak ada pertandingan favorit kamu",
            Icons.Default.Today
        )
        3 -> Triple(
            "Belum ada pertandingan selesai",
            "Pertandingan yang sudah selesai akan muncul di sini",
            Icons.Default.CheckCircle
        )
        else -> Triple(
            "Belum ada data",
            "",
            Icons.Default.Info
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.White.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        if (subtitle.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MatchOptionsDialog(
    match: FootballMatch,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onOpenUrl: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(match.getTitle(), fontWeight = FontWeight.Bold)
                Text(
                    text = match.competition,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Normal
                )
            }
        },
        text = {
            Column {
                DialogOption("Edit", Icons.Default.Edit) { onEdit() }

                if (match.matchUrl.isNotEmpty()) {
                    DialogOption("Buka Link Pertandingan", Icons.Default.Share) { onOpenUrl() }
                }

                if (!match.isPast()) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "Pengingat",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                match.getTimeUntilMatch(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))
                DialogOption("Hapus", Icons.Default.Delete, isDestructive = true) {
                    showDeleteConfirm = true
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup", color = Color(0xFF2E7D32))
            }
        },
        shape = RoundedCornerShape(16.dp)
    )

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Hapus Pertandingan") },
            text = { Text("Yakin ingin menghapus ${match.getTitle()}?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935)
                    )
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteConfirm = false },
                    border = BorderStroke(1.dp, Color(0xFF2E7D32))
                ) {
                    Text("Batal", color = Color(0xFF2E7D32))
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun DialogOption(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = if (isDestructive) Color(0xFFE53935) else Color(0xFF2E7D32)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text,
            fontSize = 16.sp,
            color = if (isDestructive) Color(0xFFE53935) else Color.Unspecified
        )
    }
}

private fun filterMatches(
    tabPosition: Int,
    allMatches: List<FootballMatch>
): List<FootballMatch> {
    return when (tabPosition) {
        0 -> {
            allMatches.sortedWith(compareBy<FootballMatch> { match ->
                when {
                    match.isPast() -> 4
                    match.isUpcoming(60) -> 1
                    match.isToday() -> 2
                    else -> 3
                }
            }.thenBy {
                if (it.isPast()) -it.matchDateTime.time else it.matchDateTime.time
            })
        }
        1 -> allMatches.filter { !it.isPast() && !it.isToday() }.sortedBy { it.matchDateTime }
        2 -> allMatches.filter { it.isToday() && !it.isPast() }.sortedBy { it.matchDateTime }
        3 -> allMatches.filter { it.isPast() }.sortedByDescending { it.matchDateTime }
        else -> allMatches
    }
}

private fun searchMatches(
    allMatches: List<FootballMatch>,
    keyword: String
): List<FootballMatch> {
    if (keyword.isBlank()) return allMatches

    val filtered = allMatches.filter { match ->
        match.homeTeam.contains(keyword, ignoreCase = true) ||
                match.awayTeam.contains(keyword, ignoreCase = true) ||
                match.competition.contains(keyword, ignoreCase = true) ||
                match.venue.contains(keyword, ignoreCase = true)
    }

    return filtered.sortedWith(compareBy<FootballMatch> { match ->
        when {
            match.isPast() -> 4
            match.isUpcoming(60) -> 1
            match.isToday() -> 2
            else -> 3
        }
    }.thenBy {
        if (it.isPast()) -it.matchDateTime.time else it.matchDateTime.time
    })
}