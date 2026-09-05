package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SkillNode
import com.example.ui.theme.*

data class DailyHabitItem(
    val id: String,
    val title: String,
    val category: String,
    val icon: String,
    val xpValue: Int,
    val isCompleted: Boolean = false
)

@Composable
fun DailyTrackerView(
    focusSkill: SkillNode?,
    onCompleteSkill: ((SkillNode) -> Unit)? = null,
    onCycleFocus: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var habits by remember {
        mutableStateOf(
            listOf(
                DailyHabitItem("h1", "2 Saat Kesintisiz Derin Kodlama", "Odak", "⚡", 50, false),
                DailyHabitItem("h2", "1 Bölüm OSTEP / CS:APP / DDIA Okuması", "Genel Kültür", "📖", 35, false),
                DailyHabitItem("h3", "1 GitHub Commiti & Temiz Dokümantasyon", "Portföy", "🚀", 30, false),
                DailyHabitItem("h4", "30 Dk Teknik İngilizce Makale / Video", "Dil", "🇬🇧", 25, false)
            )
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "GÜNLÜK TAKİP",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            letterSpacing = 1.2.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Disiplin motivasyonu yener. Günlük ivmeni koru.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    )
                }

                // Daily Streak Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PanelNavyElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentAmber.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = AccentAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Seri: 5 Gün",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AccentAmber,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // Active Focus Skill Card
        if (focusSkill != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, AccentCyan.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🎯 GÜNÜN ODAK YETENEĞİ",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AccentCyan,
                                    letterSpacing = 1.sp,
                                    fontSize = 10.5.sp
                                )
                            )

                            if (onCycleFocus != null) {
                                IconButton(
                                    onClick = onCycleFocus,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Değiştir",
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = focusSkill.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )

                        Text(
                            text = focusSkill.category + " • " + focusSkill.branchId.title,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = focusSkill.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            ),
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (onCompleteSkill != null) {
                            Button(
                                onClick = { onCompleteSkill(focusSkill) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AccentCyan
                                )
                            ) {
                                Text(
                                    text = "Bugün Tamamladım (+75 XP)",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Daily Habit Checklist
        item {
            Text(
                text = "GÜNLÜK RUTİNLER & ALIŞKANLIKLAR",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted,
                    letterSpacing = 1.sp,
                    fontSize = 11.sp
                ),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(habits.size, key = { habits[it].id }) { index ->
            val habit = habits[index]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        1.dp,
                        if (habit.isCompleted) StatusCompleted.copy(alpha = 0.5f) else BorderSubtle,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        habits = habits.toMutableList().also {
                            it[index] = habit.copy(isCompleted = !habit.isCompleted)
                        }
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (habit.isCompleted) PanelNavyHighlight else PanelNavyElevated
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                if (habit.isCompleted) StatusCompleted else Color.Transparent
                            )
                            .border(
                                1.5.dp,
                                if (habit.isCompleted) StatusCompleted else TextDarkMuted,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (habit.isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(text = habit.icon, fontSize = 18.sp)

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = habit.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (habit.isCompleted) TextMuted else TextPrimary,
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = habit.category,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextDarkMuted,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Text(
                        text = "+${habit.xpValue} XP",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (habit.isCompleted) StatusCompleted else AccentAmber,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}
