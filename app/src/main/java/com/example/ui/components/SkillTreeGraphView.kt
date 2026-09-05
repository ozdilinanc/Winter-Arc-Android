package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterCenterFocus
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BranchId
import com.example.data.model.SkillNode
import com.example.data.model.SkillStatus
import com.example.ui.theme.*
import kotlin.math.*

private data class RenderNode(
    val skill: SkillNode,
    val center: Offset,
    val radius: Float,
    val parentCenter: Offset
)

private data class BranchHub(
    val branch: BranchId,
    val center: Offset,
    val radius: Float
)

@OptIn(ExperimentalTextApi::class)
@Composable
fun SkillTreeGraphView(
    skills: List<SkillNode>,
    selectedSkill: SkillNode?,
    onSkillClick: (SkillNode) -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(0.75f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val textMeasurer = rememberTextMeasurer()

    // Layout computation
    val branchAngles = remember {
        mapOf(
            BranchId.BACKEND_DOTNET to -90.0,         // Straight top (highest priority)
            BranchId.DISTRIBUTED_DEVOPS to -55.0,     // Top right
            BranchId.ANDROID_MOBILE to -20.0,         // Right upper
            BranchId.COMPUTER_SCIENCE to 15.0,        // Right lower
            BranchId.GRADUATION_PROJECT to 50.0,      // Bottom right
            BranchId.ENGINEERING_TOOLS to 90.0,       // Straight bottom
            BranchId.CYBER_SECURITY to 130.0,         // Bottom left
            BranchId.ENGLISH to 160.0,                // Left lower
            BranchId.PORTFOLIO_OUTPUT to 190.0,       // Left
            BranchId.KNOWLEDGE_MANAGEMENT to 220.0,   // Left upper
            BranchId.CERTIFICATES to 245.0,           // Top-left
            BranchId.SECOND_LANGUAGE to 265.0         // Top-left outer
        )
    }

    val (branchHubs, renderNodes) = remember(skills) {
        val hubs = mutableListOf<BranchHub>()
        val nodes = mutableListOf<RenderNode>()

        val hubRadiusDistance = 420f

        BranchId.values().forEach { branch ->
            val angleDeg = branchAngles[branch] ?: 0.0
            val angleRad = Math.toRadians(angleDeg)
            val hubX = (hubRadiusDistance * cos(angleRad)).toFloat()
            val hubY = (hubRadiusDistance * sin(angleRad)).toFloat()
            val hubCenter = Offset(hubX, hubY)
            hubs.add(BranchHub(branch, hubCenter, 34f))

            val branchSkills = skills.filter { it.branchId == branch }
            val categories = branchSkills.groupBy { it.category }

            var catIndex = 0
            val totalCats = categories.size.coerceAtLeast(1)

            categories.forEach { (_, catSkills) ->
                // Sub-arc spread around the hub angle
                val catAngleSpread = if (totalCats > 1) {
                    val spreadRange = 55.0 // degrees spread
                    val step = spreadRange / (totalCats - 1)
                    angleDeg - (spreadRange / 2.0) + (catIndex * step)
                } else {
                    angleDeg
                }
                val catAngleRad = Math.toRadians(catAngleSpread)

                catSkills.forEachIndexed { skillIdx, skill ->
                    // Distance tiers from hub
                    val tierDistance = 160f + (skillIdx * 75f)
                    val jitterAngle = catAngleRad + ((skillIdx % 3 - 1) * 0.12)

                    val nodeX = hubX + (tierDistance * cos(jitterAngle)).toFloat()
                    val nodeY = hubY + (tierDistance * sin(jitterAngle)).toFloat()
                    val nodeCenter = Offset(nodeX, nodeY)

                    val parentPoint = if (skillIdx == 0) hubCenter else {
                        // Connect to prior node in category
                        val prevIdx = skillIdx - 1
                        val prevDist = 160f + (prevIdx * 75f)
                        val prevJitter = catAngleRad + ((prevIdx % 3 - 1) * 0.12)
                        Offset(
                            hubX + (prevDist * cos(prevJitter)).toFloat(),
                            hubY + (prevDist * sin(prevJitter)).toFloat()
                        )
                    }

                    nodes.add(
                        RenderNode(
                            skill = skill,
                            center = nodeCenter,
                            radius = 20f,
                            parentCenter = parentPoint
                        )
                    )
                }
                catIndex++
            }
        }

        Pair(hubs, nodes)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasDark)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.35f, 2.8f)
                    offset += pan
                }
            }
            .pointerInput(renderNodes, offset, scale) {
                detectTapGestures { tapOffset ->
                    // Transform screen tap coordinate into canvas coordinate space
                    val canvasCenterX = size.width / 2f + offset.x
                    val canvasCenterY = size.height / 2f + offset.y

                    val localTapX = (tapOffset.x - canvasCenterX) / scale
                    val localTapY = (tapOffset.y - canvasCenterY) / scale

                    // Check if tapped on any node (with generous 36dp touch buffer)
                    val hitRadius = 36f
                    val clicked = renderNodes.find { node ->
                        val dx = node.center.x - localTapX
                        val dy = node.center.y - localTapY
                        (dx * dx + dy * dy) <= (hitRadius * hitRadius)
                    }

                    if (clicked != null) {
                        onSkillClick(clicked.skill)
                    } else {
                        // Check if tapped a branch hub
                        val clickedHub = branchHubs.find { hub ->
                            val dx = hub.center.x - localTapX
                            val dy = hub.center.y - localTapY
                            (dx * dx + dy * dy) <= (50f * 50f)
                        }
                        if (clickedHub != null) {
                            val firstSkill = skills.find { it.branchId == clickedHub.branch }
                            if (firstSkill != null) onSkillClick(firstSkill)
                        }
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerCanvas = Offset(size.width / 2f + offset.x, size.height / 2f + offset.y)

            // Draw subtle background grid
            drawEngineeringGrid(centerCanvas, scale)

            // Draw radial orbital guides
            drawOrbitRings(centerCanvas, scale)

            // 1. Draw connecting lines from Central Node to Branch Hubs
            branchHubs.forEach { hub ->
                val startScreen = toScreen(Offset.Zero, centerCanvas, scale)
                val hubScreen = toScreen(hub.center, centerCanvas, scale)
                val isPriority = hub.branch == BranchId.BACKEND_DOTNET

                // Subtle glow line
                drawLine(
                    color = if (isPriority) BranchDotNet.copy(alpha = 0.5f) else BorderActive.copy(alpha = 0.4f),
                    start = startScreen,
                    end = hubScreen,
                    strokeWidth = (if (isPriority) 2.5f else 1.5f) * scale,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f * scale, 6f * scale), 0f)
                )
            }

            // 2. Draw connecting lines between skills
            renderNodes.forEach { node ->
                val parentScreen = toScreen(node.parentCenter, centerCanvas, scale)
                val nodeScreen = toScreen(node.center, centerCanvas, scale)
                val isMastered = node.skill.status == SkillStatus.COMPLETED || node.skill.status == SkillStatus.STRONG
                val isLearning = node.skill.status == SkillStatus.IN_PROGRESS

                val lineColor = when {
                    node.skill.status == SkillStatus.STRONG -> StatusStrong.copy(alpha = 0.85f)
                    node.skill.status == SkillStatus.COMPLETED -> StatusCompleted.copy(alpha = 0.75f)
                    node.skill.status == SkillStatus.PRACTICED -> StatusPracticed.copy(alpha = 0.6f)
                    isLearning -> StatusLearning.copy(alpha = 0.55f)
                    else -> BorderSubtle.copy(alpha = 0.4f)
                }

                // Glow halo for completed/strong connections
                if (isMastered) {
                    drawLine(
                        color = lineColor.copy(alpha = 0.22f),
                        start = parentScreen,
                        end = nodeScreen,
                        strokeWidth = 6f * scale,
                        cap = StrokeCap.Round
                    )
                }

                drawLine(
                    color = lineColor,
                    start = parentScreen,
                    end = nodeScreen,
                    strokeWidth = (if (isMastered) 2f else 1.2f) * scale,
                    cap = StrokeCap.Round
                )
            }

            // 3. Draw Branch Hub Nodes
            branchHubs.forEach { hub ->
                val screenPos = toScreen(hub.center, centerCanvas, scale)
                val hubRadiusScreen = hub.radius * scale

                // Hub Outer Ring
                drawCircle(
                    color = hub.branch.accentColor.copy(alpha = 0.2f),
                    radius = hubRadiusScreen + (6f * scale),
                    center = screenPos
                )
                drawCircle(
                    color = PanelNavyElevated,
                    radius = hubRadiusScreen,
                    center = screenPos
                )
                drawCircle(
                    color = hub.branch.accentColor,
                    radius = hubRadiusScreen,
                    center = screenPos,
                    style = Stroke(width = 1.8f * scale)
                )

                // Hub label text
                if (scale > 0.45f) {
                    val hubText = "${hub.branch.iconEmoji} ${hub.branch.shortName}"
                    val textLayout = textMeasurer.measure(
                        text = AnnotatedString(hubText),
                        style = TextStyle(
                            color = TextPrimary,
                            fontSize = (11f * scale).coerceAtLeast(8f).sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    drawText(
                        textLayoutResult = textLayout,
                        topLeft = Offset(
                            screenPos.x - textLayout.size.width / 2f,
                            screenPos.y + hubRadiusScreen + (4f * scale)
                        )
                    )
                }
            }

            // 4. Draw Individual Skill Nodes
            renderNodes.forEach { node ->
                val screenPos = toScreen(node.center, centerCanvas, scale)
                val nodeRadiusScreen = node.radius * scale
                val isSelected = selectedSkill?.id == node.skill.id
                val statusColor = node.skill.status.color

                // Selection highlight ring
                if (isSelected) {
                    drawCircle(
                        color = AccentCyan.copy(alpha = 0.35f),
                        radius = nodeRadiusScreen + (12f * scale),
                        center = screenPos
                    )
                    drawCircle(
                        color = AccentCyan,
                        radius = nodeRadiusScreen + (7f * scale),
                        center = screenPos,
                        style = Stroke(width = 2f * scale)
                    )
                }

                // Node background
                drawCircle(
                    color = PanelNavy,
                    radius = nodeRadiusScreen,
                    center = screenPos
                )

                // Status glow effect
                if (node.skill.status == SkillStatus.STRONG) {
                    drawCircle(
                        color = StatusStrong.copy(alpha = 0.3f),
                        radius = nodeRadiusScreen + (4f * scale),
                        center = screenPos
                    )
                }

                // Node outer ring
                val strokeW = when (node.skill.status) {
                    SkillStatus.STRONG -> 2.8f * scale
                    SkillStatus.COMPLETED -> 2.2f * scale
                    SkillStatus.PRACTICED -> 1.8f * scale
                    SkillStatus.IN_PROGRESS, SkillStatus.LEARNING -> 1.6f * scale
                    SkillStatus.NOT_STARTED -> 1.0f * scale
                }

                drawCircle(
                    color = statusColor,
                    radius = nodeRadiusScreen,
                    center = screenPos,
                    style = Stroke(width = strokeW)
                )

                // Inner status dot
                val innerDotRadius = when (node.skill.status) {
                    SkillStatus.STRONG -> 7f * scale
                    SkillStatus.COMPLETED -> 5.5f * scale
                    SkillStatus.PRACTICED -> 4f * scale
                    SkillStatus.IN_PROGRESS, SkillStatus.LEARNING -> 3f * scale
                    SkillStatus.NOT_STARTED -> 1.5f * scale
                }
                drawCircle(
                    color = statusColor,
                    radius = innerDotRadius,
                    center = screenPos
                )

                // Indicator for attached personal notes
                if (node.skill.personalNotes.isNotBlank()) {
                    val noteBadgeCenter = Offset(
                        screenPos.x + (nodeRadiusScreen * 0.72f),
                        screenPos.y - (nodeRadiusScreen * 0.72f)
                    )
                    val noteRadius = 4.5f * scale
                    drawCircle(
                        color = CanvasDark,
                        radius = noteRadius + (1.5f * scale),
                        center = noteBadgeCenter
                    )
                    drawCircle(
                        color = AccentAmber,
                        radius = noteRadius,
                        center = noteBadgeCenter
                    )
                }

                // Node text label (drawn when scaled sufficiently)
                if (scale > 0.55f) {
                    val labelStyle = TextStyle(
                        color = if (isSelected) AccentCyan else if (node.skill.status == SkillStatus.NOT_STARTED) TextMuted else TextPrimary,
                        fontSize = (9f * scale).coerceIn(8f, 13f).sp,
                        fontWeight = if (isSelected || node.skill.status == SkillStatus.STRONG) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = FontFamily.SansSerif
                    )

                    val textLayout = textMeasurer.measure(
                        text = AnnotatedString(node.skill.name),
                        style = labelStyle
                    )

                    // Draw pill background behind text for readability
                    val labelTopLeft = Offset(
                        screenPos.x - textLayout.size.width / 2f,
                        screenPos.y + nodeRadiusScreen + (3f * scale)
                    )
                    drawRoundRect(
                        color = CanvasDark.copy(alpha = 0.85f),
                        topLeft = Offset(labelTopLeft.x - 3f, labelTopLeft.y - 1f),
                        size = androidx.compose.ui.geometry.Size(
                            textLayout.size.width + 6f,
                            textLayout.size.height + 2f
                        ),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
                    )

                    drawText(
                        textLayoutResult = textLayout,
                        topLeft = labelTopLeft
                    )
                }
            }

            // 5. Draw Central Root Node: 🎓 MEZUNİYET / GRADUATION
            val centralScreenPos = toScreen(Offset.Zero, centerCanvas, scale)
            val centralRadius = 52f * scale

            // Multi-tier glowing orbit around graduation center (shadow-[0_0_40px_rgba(99,102,241,0.25)])
            drawCircle(
                color = AccentCyan.copy(alpha = 0.08f),
                radius = centralRadius + (28f * scale),
                center = centralScreenPos
            )
            drawCircle(
                color = AccentCyan.copy(alpha = 0.18f),
                radius = centralRadius + (14f * scale),
                center = centralScreenPos
            )
            drawCircle(
                color = PanelNavy,
                radius = centralRadius,
                center = centralScreenPos
            )
            drawCircle(
                color = AccentCyan,
                radius = centralRadius,
                center = centralScreenPos,
                style = Stroke(width = 2.6f * scale)
            )

            // GOAL label
            if (scale > 0.45f) {
                val goalLayout = textMeasurer.measure(
                    text = AnnotatedString("GOAL"),
                    style = TextStyle(
                        color = AccentIndigo,
                        fontSize = (7.5f * scale).coerceIn(6f, 10f).sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.6.sp
                    )
                )
                drawText(
                    textLayoutResult = goalLayout,
                    topLeft = Offset(
                        centralScreenPos.x - goalLayout.size.width / 2f,
                        centralScreenPos.y - (24f * scale)
                    )
                )
            }

            // Central Node Text: 🎓 GRADUATION
            val mezuniyetLayout = textMeasurer.measure(
                text = AnnotatedString("🎓 GRADUATION"),
                style = TextStyle(
                    color = TextPrimary,
                    fontSize = (10f * scale).coerceAtLeast(7.5f).sp,
                    fontWeight = FontWeight.Black
                )
            )
            drawText(
                textLayoutResult = mezuniyetLayout,
                topLeft = Offset(
                    centralScreenPos.x - mezuniyetLayout.size.width / 2f,
                    centralScreenPos.y - (mezuniyetLayout.size.height / 2f) - (3f * scale)
                )
            )

            val subtitleLayout = textMeasurer.measure(
                text = AnnotatedString("Strong Engineer"),
                style = TextStyle(
                    color = TextSecondary,
                    fontSize = (7.5f * scale).coerceAtLeast(6f).sp,
                    fontWeight = FontWeight.Normal
                )
            )
            drawText(
                textLayoutResult = subtitleLayout,
                topLeft = Offset(
                    centralScreenPos.x - subtitleLayout.size.width / 2f,
                    centralScreenPos.y + (7f * scale)
                )
            )

            // Active pill badge at bottom
            if (scale > 0.45f) {
                val badgeLayout = textMeasurer.measure(
                    text = AnnotatedString("ACTIVE"),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = (7f * scale).coerceIn(5.5f, 9f).sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                val badgePillWidth = badgeLayout.size.width + (12f * scale)
                val badgePillHeight = badgeLayout.size.height + (4f * scale)
                val badgeTopLeft = Offset(
                    centralScreenPos.x - badgePillWidth / 2f,
                    centralScreenPos.y + centralRadius - (badgePillHeight / 2f)
                )
                drawRoundRect(
                    color = AccentCyan,
                    topLeft = badgeTopLeft,
                    size = androidx.compose.ui.geometry.Size(badgePillWidth, badgePillHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(badgePillHeight / 2f, badgePillHeight / 2f)
                )
                drawText(
                    textLayoutResult = badgeLayout,
                    topLeft = Offset(
                        badgeTopLeft.x + (6f * scale),
                        badgeTopLeft.y + (2f * scale)
                    )
                )
            }
        }

        // Floating Control Overlay (Zoom In, Zoom Out, Recenter)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = { scale = (scale * 1.25f).coerceAtMost(2.8f) },
                modifier = Modifier.size(40.dp).testTag("zoom_in_btn"),
                containerColor = PanelNavyElevated,
                contentColor = AccentCyan,
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In", modifier = Modifier.size(20.dp))
            }

            FloatingActionButton(
                onClick = { scale = (scale / 1.25f).coerceAtLeast(0.35f) },
                modifier = Modifier.size(40.dp).testTag("zoom_out_btn"),
                containerColor = PanelNavyElevated,
                contentColor = TextSecondary,
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out", modifier = Modifier.size(20.dp))
            }

            FloatingActionButton(
                onClick = {
                    scale = 0.75f
                    offset = Offset.Zero
                },
                modifier = Modifier.size(40.dp).testTag("recenter_btn"),
                containerColor = PanelNavyHighlight,
                contentColor = AccentCyan,
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.FilterCenterFocus, contentDescription = "Recenter", modifier = Modifier.size(20.dp))
            }
        }

        // Mini status legend on top left of graph
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PanelNavy.copy(alpha = 0.9f))
                .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Zoom: ${(scale * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = "• Tap node for details",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

private fun toScreen(point: Offset, centerCanvas: Offset, scale: Float): Offset {
    return Offset(
        centerCanvas.x + (point.x * scale),
        centerCanvas.y + (point.y * scale)
    )
}

private fun DrawScope.drawEngineeringGrid(center: Offset, scale: Float) {
    val dotSpacing = 28f * scale
    val width = size.width
    val height = size.height

    var x = center.x % dotSpacing
    if (x < 0) x += dotSpacing
    while (x < width) {
        var y = center.y % dotSpacing
        if (y < 0) y += dotSpacing
        while (y < height) {
            drawCircle(
                color = Color(0xFF334155).copy(alpha = 0.35f),
                radius = 1.1f,
                center = Offset(x, y)
            )
            y += dotSpacing
        }
        x += dotSpacing
    }
}

private fun DrawScope.drawOrbitRings(center: Offset, scale: Float) {
    val rings = listOf(420f, 600f, 800f)
    rings.forEach { radius ->
        drawCircle(
            color = BorderSubtle.copy(alpha = 0.2f),
            radius = radius * scale,
            center = center,
            style = Stroke(
                width = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 10f), 0f)
            )
        )
    }
}
