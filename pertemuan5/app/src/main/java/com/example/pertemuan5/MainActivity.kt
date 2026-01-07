package com.example.pertemuan5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pertemuan5.ui.theme.Pertemuan5Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Pertemuan5Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    FormPendaftaran(
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
    }
}

@Composable
fun FormPendaftaran(modifier: Modifier = Modifier) {

    var nama by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isChecked by remember { mutableStateOf(false) }
    var showPreview by remember { mutableStateOf(false) }
    var previewNama by remember { mutableStateOf("") }
    var previewEmail by remember { mutableStateOf("") }


    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Form Pendaftaran",
                style = MaterialTheme.typography.headlineSmall
            )

            OutlinedTextField(
                value = nama,
                onValueChange = { nama = it },
                label = { Text("Nama Lengkap") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { isChecked = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Setuju Syarat & Ketentuan")
            }

            Button(
                onClick = {
                    // Simpan data form ke preview
                    previewNama = nama
                    previewEmail = email

                    // Munculkan preview
                    showPreview = true

                    // Kosongkan form
                    nama = ""
                    email = ""
                    isChecked = false
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isChecked && nama.isNotEmpty() && email.isNotEmpty()
            ) {
                Text("Daftar")
            }

            if (showPreview) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Text("--- Data Preview ---")
                    Text("Nama: $previewNama")
                    Text("Email: $previewEmail")
                    Text("Setuju: true")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewForm() {
    Pertemuan5Theme {
        FormPendaftaran()
    }
}