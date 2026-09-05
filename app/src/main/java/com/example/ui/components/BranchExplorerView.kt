package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@Composable
fun BranchExplorerView(
    skills: List<SkillNode>,
    selectedBranchFilter: BranchId?,
    onSkillClick: (SkillNode) -> Unit,
    onStatusToggle: (SkillNode) -> Unit,
    modifier: Modifier = Modifier
) {
    // Branches to display
    val branchesToDisplay = if (selectedBranchFilter != null) {
        listOf(selectedBranchFilter)
    } else {
        BranchId.values().toList()
    }

    // Default expanded state: expand .NET Backend by default
    val expandedBranches = remember { mutableStateMapOf<BranchId, Boolean>().apply {
        put(BranchId.BACKEND_DOTNET, true)
    } }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(branchesToDisplay, key = { it.name }) { branch ->
            val branchSkills = skills.filter { it.branchId == branch }
            val isExpanded = expandedBranches[branch] ?: false

            val total = branchSkills.size
            val mastered = branchSkills.count { it.status == SkillStatus.COMPLETED || it.status == SkillStatus.STRONG }
            val percent = if (total > 0) ((mastered.toFloat() / total) * 100).toInt() else 0

            BranchCard(
                branch = branch,
                skills = branchSkills,
                isExpanded = isExpanded,
                completionPercent = percent,
                onToggleExpand = { expandedBranches[branch] = !isExpanded },
                onSkillClick = onSkillClick,
                onStatusToggle = onStatusToggle
            )
        }
    }
}

@Composable
private fun BranchCard(
    branch: BranchId,
    skills: List<SkillNode>,
    isExpanded: Boolean,
    completionPercent: Int,
    onToggleExpand: () -> Unit,
    onSkillClick: (SkillNode) -> Unit,
    onStatusToggle: (SkillNode) -> Unit
) {
    val categories = remember(skills) { skills.groupBy { it.category } }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                1.dp,
                if (branch.isCorePriority) branch.accentColor.copy(alpha = 0.5f) else BorderSubtle,
                RoundedCornerShape(14.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = PanelNavy)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Branch Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpand)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = branch.iconEmoji, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = branch.title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            if (branch.isCorePriority) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Priority",
                                    tint = BranchDotNet,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${branch.priorityLabel} • ${skills.size} skills",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = branch.accentColor,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Progress Indicator Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(PanelNavyElevated)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "$completionPercent%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = if (completionPercent > 50) StatusCompleted else TextSecondary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = TextSecondary
                    )
                }
            }

            // Expanded Skill Categories
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PanelNavyElevated.copy(alpha = 0.5f))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    categories.forEach { (categoryName, catSkills) ->
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Category Title
                            Text(
                                text = categoryName.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary,
                                    letterSpacing = 1.sp,
                                    fontSize = 10.sp
                                )
                            )

                            // Skill Items Grid / List
                            catSkills.forEach { skill ->
                                SkillItemRow(
                                    skill = skill,
                                    onClick = { onSkillClick(skill) },
                                    onStatusToggle = { onStatusToggle(skill) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SkillItemRow(
    skill: SkillNode,
    onClick: () -> Unit,
    onStatusToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(PanelNavy)
            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(skill.status.color)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = skill.name,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                )
                if (skill.personalNotes.isNotBlank()) {
                    val firstNoteLine = skill.personalNotes.lineSequence().firstOrNull { it.isNotBlank() } ?: ""
                    val preview = if (firstNoteLine.length > 45) firstNoteLine.take(42) + "..." else firstNoteLine
                    Text(
                        text = "📝 $preview",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AccentAmber,
                            fontSize = 9.5.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Quick status toggle pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(skill.status.color.copy(alpha = 0.15f))
                .border(1.dp, skill.status.color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .clickable(onClick = onStatusToggle)
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .testTag("status_toggle_${skill.id}")
        ) {
            Text(
                text = skill.status.label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = skill.status.color,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}
