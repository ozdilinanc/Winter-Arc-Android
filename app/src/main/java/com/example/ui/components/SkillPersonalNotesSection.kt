package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Dedicated, structured component for students to attach short, text-based personal notes
 * directly to a skill node. Allows documenting learning resources, key takeaways, and gotchas.
 */
@Composable
fun SkillPersonalNotesSection(
    skillName: String,
    initialNotes: String,
    onSaveNotes: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember(initialNotes) { mutableStateOf(initialNotes) }
    var isPreviewMode by remember { mutableStateOf(false) }
    var isSavedConfirmationVisible by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current

    val isDirty = text != initialNotes
    val charLimit = 500
    val remainingChars = charLimit - text.length

    fun insertTemplate(prefix: String) {
        val updated = if (text.isBlank()) {
            prefix
        } else if (text.endsWith("\n")) {
            "$text$prefix"
        } else {
            "$text\n$prefix"
        }
        text = updated
        onSaveNotes(updated)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PanelNavyElevated)
            .border(1.dp, if (text.isNotBlank()) AccentAmber.copy(alpha = 0.35f) else BorderSubtle, RoundedCornerShape(12.dp))
            .padding(14.dp)
            .testTag("skill_personal_notes_section")
    ) {
        // Section Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(AccentAmber.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📝", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "PERSONAL NOTES & TAKEAWAYS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.1.sp,
                            color = AccentAmber
                        )
                    )
                    Text(
                        text = "Document study links, core principles & gotchas",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    )
                }
            }

            // Sync Status Pill & Mode Toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isDirty) StatusLearning.copy(alpha = 0.15f) else StatusCompleted.copy(alpha = 0.15f),
                    border = BorderStroke(
                        0.5.dp,
                        if (isDirty) StatusLearning.copy(alpha = 0.4f) else StatusCompleted.copy(alpha = 0.4f)
                    )
                ) {
                    Text(
                        text = if (isDirty) "Draft" else "Saved",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (isDirty) StatusLearning else StatusCompleted
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (text.isNotBlank()) {
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { isPreviewMode = !isPreviewMode },
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(PanelNavyHighlight)
                            .testTag("skill_notes_preview_toggle")
                    ) {
                        Icon(
                            imageVector = if (isPreviewMode) Icons.Default.Edit else Icons.Default.Visibility,
                            contentDescription = if (isPreviewMode) "Edit Notes" else "Preview Notes",
                            tint = AccentCyan,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Tag Templates Bar
        if (!isPreviewMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick Tag:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                )

                // Key Takeaway Tag
                TemplateTagChip(
                    label = "💡 Takeaway",
                    onClick = { insertTemplate("💡 Key Takeaway: ") },
                    testTag = "skill_notes_template_takeaway",
                    accentColor = AccentAmber
                )

                // Resource Tag
                TemplateTagChip(
                    label = "📚 Resource",
                    onClick = { insertTemplate("📚 Resource: ") },
                    testTag = "skill_notes_template_resource",
                    accentColor = AccentCyan
                )

                // Gotcha Tag
                TemplateTagChip(
                    label = "⚠️ Gotcha",
                    onClick = { insertTemplate("⚠️ Gotcha: ") },
                    testTag = "skill_notes_template_gotcha",
                    accentColor = StatusLearning
                )

                // Summary Tag
                TemplateTagChip(
                    label = "⚡ Summary",
                    onClick = { insertTemplate("⚡ Summary: ") },
                    testTag = "skill_notes_template_summary",
                    accentColor = AccentGold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Main Editor or Formatted Preview View
        if (isPreviewMode && text.isNotBlank()) {
            // Formatted Takeaway / Resource Viewer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(PanelNavy)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val lines = text.lines().filter { it.isNotBlank() }
                if (lines.isEmpty()) {
                    Text(
                        text = "No notes entered yet.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                } else {
                    lines.forEach { line ->
                        val trimmed = line.trim()
                        when {
                            trimmed.startsWith("💡") || trimmed.contains("takeaway", ignoreCase = true) -> {
                                FormattedNoteCard(
                                    content = trimmed,
                                    borderColor = AccentAmber.copy(alpha = 0.5f),
                                    bgColor = AccentAmber.copy(alpha = 0.08f)
                                )
                            }
                            trimmed.startsWith("📚") || trimmed.contains("http://") || trimmed.contains("https://") -> {
                                FormattedNoteCard(
                                    content = trimmed,
                                    borderColor = AccentCyan.copy(alpha = 0.5f),
                                    bgColor = AccentCyan.copy(alpha = 0.08f)
                                )
                            }
                            trimmed.startsWith("⚠️") || trimmed.contains("gotcha", ignoreCase = true) -> {
                                FormattedNoteCard(
                                    content = trimmed,
                                    borderColor = StatusLearning.copy(alpha = 0.5f),
                                    bgColor = StatusLearning.copy(alpha = 0.08f)
                                )
                            }
                            else -> {
                                Text(
                                    text = trimmed,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextPrimary,
                                        lineHeight = 18.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Text Input Box
            OutlinedTextField(
                value = text,
                onValueChange = { newText ->
                    if (newText.length <= charLimit + 100) {
                        text = newText
                        onSaveNotes(newText)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 90.dp, max = 180.dp)
                    .testTag("skill_personal_notes_input"),
                placeholder = {
                    Text(
                        "Attach key takeaways or links for $skillName...\nTap a quick tag above (💡 Takeaway, 📚 Resource) to format.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMuted,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = PanelNavy,
                    unfocusedContainerColor = PanelNavy,
                    focusedBorderColor = AccentCyan,
                    unfocusedBorderColor = BorderSubtle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp,
                    color = TextPrimary
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Bottom Footer: Char count + Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Character counter
            Text(
                text = "$remainingChars chars left",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = if (remainingChars < 30) StatusLearning else TextMuted
                )
            )

            // Button actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (text.isNotBlank()) {
                    // Copy to clipboard
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(text))
                            isSavedConfirmationVisible = true
                        },
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(PanelNavyHighlight)
                            .testTag("skill_notes_copy_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy notes",
                            tint = TextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                    }

                    // Clear notes button
                    IconButton(
                        onClick = {
                            text = ""
                            onSaveNotes("")
                        },
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(PanelNavyHighlight)
                            .testTag("skill_notes_clear_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear notes",
                            tint = TextMuted,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                // Explicit Save Note Button
                Button(
                    onClick = {
                        onSaveNotes(text)
                        isSavedConfirmationVisible = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDirty) AccentCyan else PanelNavyHighlight,
                        contentColor = if (isDirty) Color.Black else TextSecondary
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier
                        .height(28.dp)
                        .testTag("skill_notes_save_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isDirty) "Save Note" else "Saved",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Temporary feedback notification
        AnimatedVisibility(
            visible = isSavedConfirmationVisible,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(1800)
                isSavedConfirmationVisible = false
            }
            Text(
                text = "✓ Note securely saved to local database",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = StatusCompleted,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun TemplateTagChip(
    label: String,
    onClick: () -> Unit,
    testTag: String,
    accentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = PanelNavyHighlight,
        border = BorderStroke(0.5.dp, accentColor.copy(alpha = 0.5f)),
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = accentColor,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun FormattedNoteCard(
    content: String,
    borderColor: Color,
    bgColor: Color
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bgColor,
        border = BorderStroke(0.5.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.5.sp,
                color = TextPrimary,
                lineHeight = 17.sp
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
        )
    }
}
