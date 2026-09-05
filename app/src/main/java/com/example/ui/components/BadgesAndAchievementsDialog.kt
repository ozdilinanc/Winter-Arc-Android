package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.SkillBadge
import com.example.data.model.UserXpProfile
import com.example.ui.theme.*

@Composable
fun BadgesAndAchievementsDialog(
    userXp: UserXpProfile,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
                .padding(horizontal = 16.dp, vertical = 28.dp)
                .testTag("achievements_dialog"),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.88f)
                    .widthIn(max = 500.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = PanelNavy)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ENGINEERING PROFILE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = "Achievements & XP",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 18.sp
                                )
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PanelNavyElevated)
                                .testTag("close_achievements_dialog_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Level & XP Progress Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = PanelNavyElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderActive)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(listOf(AccentCyan, AccentPurple))
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "L${userXp.level}",
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                fontWeight = FontWeight.Black,
                                                color = Color.White,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 15.sp
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = userXp.rankTitle,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary,
                                                fontSize = 14.sp
                                            )
                                        )
                                        Text(
                                            text = "Level ${userXp.level} Engineer",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = AccentCyan,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${userXp.totalXp} XP",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            color = AccentGold,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 16.sp
                                        )
                                    )
                                    Text(
                                        text = "${userXp.completedSkillCount} Skills Completed",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TextMuted,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // XP Progress Bar to next level
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Level Progress",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                )
                                Text(
                                    text = "${userXp.currentLevelXp} / ${userXp.xpForNextLevel} XP to Level ${userXp.level + 1}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextSecondary,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(BorderSubtle)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction = userXp.levelProgressPercent.coerceIn(0.02f, 1f))
                                        .fillMaxHeight()
                                        .background(
                                            Brush.horizontalGradient(listOf(AccentCyan, AccentPurple))
                                        )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Badges Section Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MILESTONE BADGES (${userXp.unlockedBadges.size}/${userXp.allBadges.size})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.2.sp,
                                color = TextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Badges List
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(userXp.allBadges) { badge ->
                            BadgeCard(badge = badge)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgeCard(
    badge: SkillBadge,
    modifier: Modifier = Modifier
) {
    val isUnlocked = badge.isUnlocked

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (isUnlocked) PanelNavyElevated else PanelNavy.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isUnlocked) AccentIndigo.copy(alpha = 0.4f) else BorderSubtle.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) Brush.linearGradient(
                            listOf(AccentCyan.copy(alpha = 0.25f), AccentPurple.copy(alpha = 0.25f))
                        ) else Brush.linearGradient(listOf(BorderSubtle, PanelNavy))
                    )
                    .border(
                        1.dp,
                        if (isUnlocked) AccentCyan.copy(alpha = 0.6f) else BorderSubtle,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isUnlocked) badge.iconEmoji else "🔒",
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = badge.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlocked) TextPrimary else TextMuted,
                            fontSize = 13.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${badge.category}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.5.sp,
                            color = if (isUnlocked) AccentCyan else TextDarkMuted
                        )
                    )
                }

                Text(
                    text = badge.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (isUnlocked) TextSecondary else TextDarkMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "+${badge.xpReward} XP",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) AccentAmber else TextDarkMuted,
                        fontSize = 10.5.sp
                    )
                )
                if (isUnlocked) {
                    Text(
                        text = "UNLOCKED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = StatusCompleted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.5.sp
                        )
                    )
                } else {
                    Text(
                        text = "LOCKED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextDarkMuted,
                            fontSize = 8.5.sp
                        )
                    )
                }
            }
        }
    }
}
