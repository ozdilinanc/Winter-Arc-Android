package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SkillNode
import com.example.data.model.SkillStatus
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillDetailSheet(
    skill: SkillNode,
    onDismiss: () -> Unit,
    onStatusChange: (SkillStatus) -> Unit,
    onSaveNotes: (String) -> Unit,
    onAddResource: (String) -> Unit,
    onAddProject: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddResourceDialog by remember { mutableStateOf(false) }
    var showAddProjectDialog by remember { mutableStateOf(false) }
    var newResourceInput by remember { mutableStateOf("") }
    var newProjectInput by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = PanelNavy,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BorderActive)
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Branch Tag & Priority
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${skill.branchId.iconEmoji} ${skill.branchId.title}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = skill.branchId.accentColor,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${skill.category}",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(PanelNavyElevated)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(4.dp))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = skill.priorityTag,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (skill.branchId.isCorePriority) BranchDotNet else TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Skill Name
            Text(
                text = skill.name,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Status Selector Chips
            Text(
                text = "MASTERY STATUS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SkillStatus.values().forEach { status ->
                    val isSelected = skill.status == status
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) status.color.copy(alpha = 0.22f) else PanelNavyElevated)
                            .border(
                                1.dp,
                                if (isSelected) status.color else BorderSubtle,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onStatusChange(status) }
                            .padding(vertical = 8.dp)
                            .testTag("status_chip_${status.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) status.color else TextMuted)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = status.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) TextPrimary else TextMuted
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Description Section
            SectionHeader(title = "LEARNING ROADMAP & SCOPE")
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = skill.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextPrimary,
                    lineHeight = 22.sp
                )
            )

            // Prerequisites Section
            if (skill.prerequisites.isNotEmpty()) {
                Spacer(modifier = Modifier.height(18.dp))
                SectionHeader(title = "PREREQUISITES")
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    skill.prerequisites.forEach { prereq ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PanelNavyElevated)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = prereq,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }

            // Recommended Resources
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(title = "RECOMMENDED RESOURCES")
                IconButton(
                    onClick = { showAddResourceDialog = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Resource",
                        tint = AccentCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            skill.recommendedResources.forEach { resource ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = AccentCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = resource,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontFamily = FontFamily.SansSerif
                        )
                    )
                }
            }

            // Connected Projects & Output
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(title = "CONNECTED PROJECTS & OUTPUT")
                IconButton(
                    onClick = { showAddProjectDialog = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Connect Project",
                        tint = AccentCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            if (skill.connectedProjects.isEmpty()) {
                Text(
                    text = "No projects linked yet. Tap '+' to link your backend, mobile, or graduation project.",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                )
            } else {
                skill.connectedProjects.forEach { proj ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = StatusCompleted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = proj,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            // Personal Study Notes & Key Takeaways
            Spacer(modifier = Modifier.height(18.dp))
            SkillPersonalNotesSection(
                skillName = skill.name,
                initialNotes = skill.personalNotes,
                onSaveNotes = onSaveNotes
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Actions: Close & Mark Completed
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Close")
                }

                Button(
                    onClick = {
                        val nextStatus = when (skill.status) {
                            SkillStatus.COMPLETED -> SkillStatus.STRONG
                            SkillStatus.STRONG -> SkillStatus.STRONG
                            else -> SkillStatus.COMPLETED
                        }
                        onStatusChange(nextStatus)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusCompleted),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (skill.status == SkillStatus.COMPLETED) "Set Strong" else "Complete",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Add Resource Dialog
    if (showAddResourceDialog) {
        AlertDialog(
            onDismissRequest = { showAddResourceDialog = false },
            containerColor = PanelNavyElevated,
            title = {
                Text(
                    text = "Add Recommended Resource",
                    style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary)
                )
            },
            text = {
                OutlinedTextField(
                    value = newResourceInput,
                    onValueChange = { newResourceInput = it },
                    placeholder = { Text("Book, RFC, official documentation URL...") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = BorderSubtle
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newResourceInput.isNotBlank()) {
                            onAddResource(newResourceInput.trim())
                            newResourceInput = ""
                            showAddResourceDialog = false
                        }
                    }
                ) {
                    Text("Add", color = AccentCyan)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddResourceDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // Add Project Dialog
    if (showAddProjectDialog) {
        AlertDialog(
            onDismissRequest = { showAddProjectDialog = false },
            containerColor = PanelNavyElevated,
            title = {
                Text(
                    text = "Connect Project to ${skill.name}",
                    style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary)
                )
            },
            text = {
                OutlinedTextField(
                    value = newProjectInput,
                    onValueChange = { newProjectInput = it },
                    placeholder = { Text("e.g. Pharmacy Management Backend") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = BorderSubtle
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newProjectInput.isNotBlank()) {
                            onAddProject(newProjectInput.trim())
                            newProjectInput = ""
                            showAddProjectDialog = false
                        }
                    }
                ) {
                    Text("Connect", color = AccentCyan)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddProjectDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 1.sp,
            fontSize = 10.sp
        )
    )
}
