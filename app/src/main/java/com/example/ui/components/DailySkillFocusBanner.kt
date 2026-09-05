package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BranchId
import com.example.data.model.SkillNode
import com.example.data.model.SkillStatus
import com.example.ui.theme.*

/**
 * An interactive daily UI banner encouraging the user to complete one 'In Progress' skill node,
 * earning XP rewards and advancing their engineering rank.
 */
@Composable
fun DailySkillFocusBanner(
    focusSkill: SkillNode?,
    inProgressCount: Int,
    currentIndex: Int,
    isVisible: Boolean,
    onCompleteSkill: (SkillNode) -> Unit,
    onOpenSkill: (SkillNode) -> Unit,
    onCycleNextSkill: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible && focusSkill != null,
        enter = expandVertically(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
        exit = shrinkVertically(animationSpec = tween(250)) + fadeOut(animationSpec = tween(250))
    ) {
        if (focusSkill == null) return@AnimatedVisibility

        val isInProgress = focusSkill.status == SkillStatus.IN_PROGRESS
        val isCore = focusSkill.branchId == BranchId.BACKEND_DOTNET ||
                focusSkill.branchId == BranchId.COMPUTER_SCIENCE
        val xpBonus = if (isCore) 150 else 100

        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        listOf(
                            AccentAmber.copy(alpha = 0.6f),
                            AccentCyan.copy(alpha = 0.4f),
                            BorderSubtle
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .testTag("daily_focus_banner"),
            colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Glowing Pill Badge
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(
                                            AccentAmber.copy(alpha = 0.35f),
                                            Color.Transparent
                                        )
                                    )
                                )
                                .border(1.dp, AccentAmber.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "⚡", fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "DAILY FOCUS GOAL",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.2.sp,
                                        color = AccentAmber,
                                        fontSize = 10.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = AccentAmber.copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(
                                        0.5.dp,
                                        AccentAmber.copy(alpha = 0.4f)
                                    )
                                ) {
                                    Text(
                                        text = "+$xpBonus XP",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            color = AccentGold
                                        ),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }

                            Text(
                                text = if (isInProgress) {
                                    "Finish your active learning node today to maintain your momentum!"
                                } else {
                                    "Pick a roadmap node to begin your daily learning sprint!"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // Dismiss Button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(PanelNavyHighlight)
                            .testTag("dismiss_daily_banner_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss daily banner",
                            tint = TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Node Details Box
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onOpenSkill(focusSkill) },
                    color = PanelNavyHighlight,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Branch Tag
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(focusSkill.branchId.accentColor.copy(alpha = 0.12f))
                                    .border(
                                        0.5.dp,
                                        focusSkill.branchId.accentColor.copy(alpha = 0.4f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = focusSkill.branchId.iconEmoji, fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = focusSkill.branchId.shortName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = focusSkill.branchId.accentColor,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.sp
                                    )
                                )
                            }

                            // Status Tag & Optional Cycle Counter
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isInProgress) StatusLearning.copy(alpha = 0.15f) else StatusNotStarted.copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(
                                        0.5.dp,
                                        if (isInProgress) StatusLearning.copy(alpha = 0.5f) else StatusNotStarted.copy(alpha = 0.4f)
                                    )
                                ) {
                                    Text(
                                        text = if (isInProgress) "IN PROGRESS" else "RECOMMENDED",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            color = if (isInProgress) StatusLearning else TextMuted
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                if (inProgressCount > 1) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = onCycleNextSkill,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(PanelNavy)
                                            .testTag("cycle_daily_skill_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "Next in progress skill",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Skill Name
                        Text(
                            text = focusSkill.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 14.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // Skill Description
                        Text(
                            text = focusSkill.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (focusSkill.personalNotes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = PanelNavyElevated,
                                border = androidx.compose.foundation.BorderStroke(
                                    0.5.dp,
                                    AccentAmber.copy(alpha = 0.4f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("📝", fontSize = 11.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = focusSkill.personalNotes,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.sp,
                                            color = AccentAmber,
                                            lineHeight = 15.sp
                                        ),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mark Completed Button
                    Button(
                        onClick = { onCompleteSkill(focusSkill) },
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("banner_complete_skill_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StatusCompleted
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Complete",
                            modifier = Modifier.size(15.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Mark Completed",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        )
                    }

                    // Open / Inspect Node Button
                    OutlinedButton(
                        onClick = { onOpenSkill(focusSkill) },
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("banner_inspect_skill_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AccentCyan
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.5f)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Details",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Details",
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}
