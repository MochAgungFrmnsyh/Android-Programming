package com.example.pertemuan3

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pertemuan3.ui.theme.Pertemuan3Theme

@Composable
fun KartuIdentitasMahasiswa() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFD7ECFF))
            .padding(20.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.foto_mahasiswa),
                contentDescription = "Foto Mahasiswa",
                modifier = Modifier
                    .width(90.dp)
                    .height(120.dp)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Kartu Mahasiswa",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D47A1)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row {
                    Text(
                        text = "Nama:",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(100.dp)
                    )
                    Text(
                        text = "Mochammad Agung Firmansyah",
                        textAlign = TextAlign.Start
                    )
                }
                Row {
                    Text(
                        text = "NIM:",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(100.dp)
                    )
                    Text(text = "23010047")
                }
                Row {
                    Text(
                        text = "Jurusan:",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(100.dp)
                    )
                    Text(text = "D3 TEKNIK INFORMATIKA")
                }
                Row {
                    Text(
                        text = "Universitas:",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(100.dp)
                    )
                    Text(text = "STMIK Mardira Indonesia")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KartuIdentitasMahasiswaPreview() {
    Pertemuan3Theme {
        KartuIdentitasMahasiswa()
    }
}
