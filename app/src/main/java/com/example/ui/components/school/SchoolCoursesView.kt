package com.example.ui.components.school

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import java.util.UUID

@Composable
fun SchoolCoursesView(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var courses by remember {
        mutableStateOf(
            listOf(
                SchoolCourse("c1", "CENG 201", "Veri Yapıları ve Algoritmalar", 4, "3. Dönem", "AA", true),
                SchoolCourse("c2", "CENG 301", "İşletim Sistemleri Prensipleri", 4, "4. Dönem", "BA", true),
                SchoolCourse("c3", "CENG 305", "Veritabanı Yönetim Sistemleri", 3, "4. Dönem", "AA", true),
                SchoolCourse("c4", "CENG 310", "Bilgisayar Ağları & İletişim", 3, "5. Dönem", "Devam Ediyor", false),
                SchoolCourse("c5", "CENG 401", "Yazılım Mimarisi & Tasarım Desenleri", 3, "5. Dönem", "Devam Ediyor", false),
                SchoolCourse("c6", "CENG 491", "Mühendislik Bitirme Projesi I", 3, "7. Dönem", "Devam Ediyor", false)
            )
        )
    }

    var isAddDialogOpen by remember { mutableStateOf(false) }
    var newCode by remember { mutableStateOf("") }
    var newName by remember { mutableStateOf("") }
    var newCredits by remember { mutableStateOf("3") }
    var newSemester by remember { mutableStateOf("Güz Dönemi") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Back Bar
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onBack() }
                    .padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Geri",
                    tint = AccentCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "OKUL HUB'A DÖN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan,
                        letterSpacing = 1.2.sp
                    )
                )
            }
        }

        // Header & Summary
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📚", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "OKUL DERSLERİ",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Müfredat derslerini, kredilerini ve notlarını yönet.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp)
                    )
                }

                Button(
                    onClick = { isAddDialogOpen = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Ders Ekle", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // Stats Pill
        item {
            val totalCredits = courses.sumOf { it.credits }
            val completedCount = courses.count { it.isCompleted }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${courses.size}", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                        Text(text = "Toplam Ders", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.5.sp))
                    }
                    Divider(modifier = Modifier.height(24.dp).width(1.dp), color = BorderSubtle)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "$completedCount", fontWeight = FontWeight.Bold, color = StatusCompleted, fontSize = 16.sp)
                        Text(text = "Tamamlanan", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.5.sp))
                    }
                    Divider(modifier = Modifier.height(24.dp).width(1.dp), color = BorderSubtle)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "$totalCredits", fontWeight = FontWeight.Bold, color = AccentAmber, fontSize = 16.sp)
                        Text(text = "Toplam Kredi", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.5.sp))
                    }
                }
            }
        }

        // Courses List
        items(courses, key = { it.id }) { course ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        1.dp,
                        if (course.isCompleted) StatusCompleted.copy(alpha = 0.3f) else BorderSubtle,
                        RoundedCornerShape(12.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Checkbox
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (course.isCompleted) StatusCompleted else Color.Transparent)
                            .border(1.5.dp, if (course.isCompleted) StatusCompleted else TextDarkMuted, CircleShape)
                            .clickable {
                                courses = courses.map {
                                    if (it.id == course.id) it.copy(
                                        isCompleted = !it.isCompleted,
                                        grade = if (!it.isCompleted) "AA" else "Devam Ediyor"
                                    ) else it
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (course.isCompleted) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(15.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = PanelNavy,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                            ) {
                                Text(
                                    text = course.code,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = AccentCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${course.credits} Kredi • ${course.semester}",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = course.name,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (course.isCompleted) TextMuted else TextPrimary,
                                fontSize = 13.sp
                            )
                        )
                    }

                    // Grade Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = PanelNavy,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Text(
                            text = course.grade,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (course.isCompleted) StatusCompleted else TextSecondary,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Delete Button
                    IconButton(
                        onClick = { courses = courses.filter { it.id != course.id } },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Sil", tint = TextDarkMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }

    // Add Course Dialog
    if (isAddDialogOpen) {
        AlertDialog(
            onDismissRequest = { isAddDialogOpen = false },
            containerColor = PanelNavyElevated,
            title = {
                Text("Yeni Okul Dersi Ekle", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newCode,
                        onValueChange = { newCode = it },
                        label = { Text("Ders Kodu (Örn: CENG 302)", fontSize = 12.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Ders Adı", fontSize = 12.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newCredits,
                            onValueChange = { newCredits = it },
                            label = { Text("Kredi", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentCyan,
                                unfocusedBorderColor = BorderSubtle,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                        OutlinedTextField(
                            value = newSemester,
                            onValueChange = { newSemester = it },
                            label = { Text("Dönem", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1.5f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentCyan,
                                unfocusedBorderColor = BorderSubtle,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCode.isNotBlank() && newName.isNotBlank()) {
                            val newCourse = SchoolCourse(
                                id = "c_${UUID.randomUUID().toString().take(6)}",
                                code = newCode.trim(),
                                name = newName.trim(),
                                credits = newCredits.toIntOrNull() ?: 3,
                                semester = newSemester.trim(),
                                grade = "Devam Ediyor",
                                isCompleted = false
                            )
                            courses = courses + newCourse
                            newCode = ""
                            newName = ""
                            isAddDialogOpen = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
                ) {
                    Text("Dersi Ekle", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isAddDialogOpen = false }) {
                    Text("İptal", color = TextSecondary)
                }
            }
        )
    }
}
