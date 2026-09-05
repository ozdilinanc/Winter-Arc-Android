package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.RewardNotification
import com.example.data.model.SkillBadge
import com.example.data.model.UserXpProfile
import com.example.ui.theme.*
import com.example.ui.util.rememberHapticEngine

@Composable
fun RewardCelebrationDialog(
    reward: RewardNotification,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticEngine = rememberHapticEngine()

    LaunchedEffect(reward) {
        if (reward.isLevelUp) {
            hapticEngine.vibrateLevelUp()
        } else {
            hapticEngine.vibrateSkillCompleted()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
                .padding(24.dp)
                .testTag("reward_celebration_dialog"),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 440.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            if (reward.isLevelUp) listOf(AccentGold, AccentPurple, AccentCyan)
                            else listOf(AccentCyan, AccentPurple)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Hero Icon with Glowing Halo
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        if (reward.isLevelUp) AccentGold.copy(alpha = 0.35f)
                                        else AccentCyan.copy(alpha = 0.35f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .border(
                                width = 2.dp,
                                brush = Brush.linearGradient(
                                    listOf(AccentCyan, if (reward.isLevelUp) AccentGold else AccentPurple)
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (reward.isLevelUp) "⚡" else if (reward.newBadges.isNotEmpty()) "🏆" else "🎯",
                            fontSize = 38.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title Header
                    val celebrationHeader = when {
                        reward.isLevelUp -> "LEVEL UP!"
                        reward.newBadges.isNotEmpty() -> "BADGE UNLOCKED!"
                        else -> "SKILL COMPLETED!"
                    }

                    Text(
                        text = celebrationHeader,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = if (reward.isLevelUp) AccentGold else TextPrimary,
                            fontSize = 22.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Subtitle / Rank Description
                    val subText = if (reward.isLevelUp) {
                        "Congratulations! You reached Level ${reward.newLevel} • ${UserXpProfile.calculateRankTitle(reward.newLevel)}"
                    } else if (reward.newBadges.isNotEmpty()) {
                        "You unlocked a new engineering milestone badge!"
                    } else {
                        "Great work! Mastery progress registered to your roadmap."
                    }

                    Text(
                        text = subText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            lineHeight = 20.sp,
                            fontSize = 13.5.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Skill & XP Earned Pill
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = PanelNavyHighlight,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "COMPLETED NODE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        color = TextMuted,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Text(
                                    text = reward.skillName,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary,
                                        fontSize = 13.sp
                                    ),
                                    maxLines = 1
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AccentAmber.copy(alpha = 0.15f))
                                    .border(1.dp, AccentAmber.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "+${reward.xpEarned} XP",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = AccentGold,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }
                    }

                    // Newly Unlocked Badges List
                    if (reward.newBadges.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "NEW BADGES EARNED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan,
                                letterSpacing = 1.2.sp
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            reward.newBadges.forEach { badge ->
                                BadgeRewardItem(badge = badge)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Primary Action Button
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("dismiss_reward_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentCyan
                        )
                    ) {
                        Text(
                            text = "Claim Rewards & Continue",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgeRewardItem(
    badge: SkillBadge,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = PanelNavy,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderActive)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(AccentCyan.copy(alpha = 0.2f), AccentPurple.copy(alpha = 0.2f)))
                    )
                    .border(1.dp, AccentCyan.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = badge.iconEmoji, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = badge.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 13.5.sp
                    )
                )
                Text(
                    text = badge.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "+${badge.xpReward} XP",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = AccentAmber,
                    fontSize = 11.sp
                )
            )
        }
    }
}
