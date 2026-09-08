package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ActivityEntity
import com.example.data.model.ActivityType
import com.example.data.model.WeightUnit
import com.example.ui.locale.AppLanguage
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.LocalCurrentLanguage
import com.example.ui.locale.formatDigits
import com.example.ui.theme.KineticGreen

/**
 * Modern Planner Activity Card with Tactile 3D Aesthetics:
 * - Natural symmetric layout (Checkbox + Title on Start, Reorder + Actions on End)
 * - Tactile Squircle Action Buttons (Coral Delete, Titanium Edit & Duplicate)
 * - 3 Tactile Beveled Metric Pills (Vibrant Neon Green + Slate Dark) with clean Material Icons
 */
@Composable
fun PlannerActivityCard(
    activity: ActivityEntity,
    displayIndex: Int,
    weightUnit: WeightUnit,
    isDone: Boolean = false,
    onToggleDone: (() -> Unit)? = null,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val language = LocalCurrentLanguage.current
    val indexStr = String.format("%02d", displayIndex)
    val isPersian = language == AppLanguage.PERSIAN
    val unitLabel = WeightUnit.getUnitLabel(weightUnit, isPersian)

    val cardAlpha by animateFloatAsState(
        targetValue = if (isDone) 0.65f else 1.0f,
        animationSpec = tween(durationMillis = 280),
        label = "card_alpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = cardAlpha }
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF161B27),
                        Color(0xFF10131B)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        if (isDone) KineticGreen.copy(alpha = 0.35f) else Color(0xFF2B364C),
                        Color(0xFF171D29)
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(14.dp)
            .testTag("activity_card_${activity.id}"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // TOP ROW: Natural symmetry restored:
        // [START] Checkbox Circle + Index Badge + Activity Name & Subtitle
        // [END] Move Up/Down + [Duplicate] [Edit] [Delete]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Start section: Checkbox + Title / Subtitle
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onEdit),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Done Checkbox Circle (Previous intuitive component)
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(if (isDone) KineticGreen else Color(0xFF141923))
                        .border(
                            width = 2.dp,
                            color = if (isDone) KineticGreen else Color(0xFF3F4E6B),
                            shape = CircleShape
                        )
                        .clickable(
                            enabled = onToggleDone != null,
                            onClick = { onToggleDone?.invoke() }
                        )
                        .testTag("btn_done_activity_${activity.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = isDone,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Done",
                            tint = Color(0xFF072111),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                // Title + Subtitle Column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = formatDigits(indexStr, language),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = if (isDone) KineticGreen else Color(0xFF7E92B3)
                        )
                        Text(
                            text = activity.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                            ),
                            color = if (isDone) Color(0xFF94A3B8) else Color(0xFFF1F5F9),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (activity.subtitle.isNotBlank()) {
                        Text(
                            text = activity.subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            color = Color(0xFF7E92B3),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // End section: Up/Down Reorder + Tactile Squircles [Duplicate, Edit, Delete]
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Reorder Up / Down Chevrons
                Column(
                    verticalArrangement = Arrangement.spacedBy((-6).dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = onMoveUp,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = strings.moveUpBtn,
                            tint = Color(0xFF889BB7),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = onMoveDown,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = strings.moveDownBtn,
                            tint = Color(0xFF889BB7),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // 3 Tactile Squircles: Duplicate, Edit, Delete
                TactileSquircleButton(
                    icon = Icons.Default.ContentCopy,
                    contentDescription = strings.duplicateBtn,
                    variant = SquircleVariant.DARK_NEUTRAL,
                    onClick = onDuplicate,
                    testTag = "btn_duplicate_${activity.id}"
                )

                TactileSquircleButton(
                    icon = Icons.Default.Edit,
                    contentDescription = strings.editBtn,
                    variant = SquircleVariant.DARK_NEUTRAL,
                    onClick = onEdit,
                    testTag = "btn_edit_${activity.id}"
                )

                TactileSquircleButton(
                    icon = Icons.Default.Delete,
                    contentDescription = strings.deleteBtn,
                    variant = SquircleVariant.CORAL_DELETE,
                    onClick = onDelete,
                    testTag = "btn_delete_${activity.id}"
                )
            }
        }

        // BOTTOM ROW: 3 Tactile Beveled Metric Pills with clean icons & typography
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (activity.type) {
                ActivityType.WEIGHT_REPS -> {
                    val displayWeight = WeightUnit.formatDisplay(activity.weight, weightUnit)
                    // Pill 1: Tactile Neon Green (Weight) with Dumbbell Icon
                    TactileMetricPill(
                        icon = Icons.Default.FitnessCenter,
                        text = "${formatDigits(displayWeight, language)} $unitLabel",
                        isHighlight = true,
                        onClick = onEdit,
                        modifier = Modifier.weight(1.05f)
                    )

                    // Pill 2: Tactile Dark (Sets & Reps) with Repeat/Cycle Icon
                    val repsText = if (isPersian) {
                        "${formatDigits(activity.sets, language)} ست × ${formatDigits(activity.reps, language)}"
                    } else {
                        "${formatDigits(activity.sets, language)} sets × ${formatDigits(activity.reps, language)}"
                    }
                    TactileMetricPill(
                        icon = Icons.Default.Refresh,
                        text = repsText,
                        isHighlight = false,
                        onClick = onEdit,
                        modifier = Modifier.weight(1.15f)
                    )

                    // Pill 3: Tactile Dark (Type / Details) with Dumbbell Icon
                    TactileMetricPill(
                        icon = Icons.Default.FitnessCenter,
                        text = strings.typeWeightReps,
                        isHighlight = false,
                        onClick = onEdit,
                        modifier = Modifier.weight(1.0f)
                    )
                }

                ActivityType.DURATION -> {
                    TactileMetricPill(
                        icon = Icons.Default.Timer,
                        text = "${formatDigits(activity.durationMinutes, language)} ${strings.minutesUnit}",
                        isHighlight = true,
                        onClick = onEdit,
                        modifier = Modifier.weight(1.2f)
                    )
                    TactileMetricPill(
                        icon = Icons.Default.Timer,
                        text = strings.typeDuration,
                        isHighlight = false,
                        onClick = onEdit,
                        modifier = Modifier.weight(1f)
                    )
                }

                ActivityType.DISTANCE -> {
                    TactileMetricPill(
                        icon = Icons.Default.Straighten,
                        text = "${formatDigits(activity.distanceKm, language)} ${strings.kmUnit}",
                        isHighlight = true,
                        onClick = onEdit,
                        modifier = Modifier.weight(1.2f)
                    )
                    TactileMetricPill(
                        icon = Icons.Default.Straighten,
                        text = strings.typeDistance,
                        isHighlight = false,
                        onClick = onEdit,
                        modifier = Modifier.weight(1f)
                    )
                }

                ActivityType.SIMPLE -> {
                    TactileMetricPill(
                        icon = Icons.Default.FitnessCenter,
                        text = strings.typeSimple,
                        isHighlight = true,
                        onClick = onEdit,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SQUIRCLE BUTTONS (Trash, Edit, Copy)
// -------------------------------------------------------------

private enum class SquircleVariant {
    CORAL_DELETE,
    DARK_NEUTRAL
}

@Composable
private fun TactileSquircleButton(
    icon: ImageVector,
    contentDescription: String?,
    variant: SquircleVariant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressOffsetY by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "squircle_offset"
    )
    val bottomLipHeight by animateDpAsState(
        targetValue = if (isPressed) 0.5.dp else 2.5.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "squircle_lip"
    )

    val (faceColor, lipColor, borderColor, iconColor) = when (variant) {
        SquircleVariant.CORAL_DELETE -> {
            listOf(
                Color(0xFF381C24),
                Color(0xFF200E14),
                Color(0xFFF87171).copy(alpha = 0.28f),
                Color(0xFFF87171)
            )
        }
        SquircleVariant.DARK_NEUTRAL -> {
            listOf(
                Color(0xFF1E2432),
                Color(0xFF10141E),
                Color(0xFF3B4861).copy(alpha = 0.40f),
                Color(0xFFCBD5E1)
            )
        }
    }

    Box(
        modifier = modifier
            .size(36.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(lipColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(bottom = bottomLipHeight)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .offset(y = pressOffsetY)
                .size(width = 36.dp, height = (36 - 2.5).dp)
                .clip(RoundedCornerShape(11.dp))
                .background(faceColor)
                .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(11.dp))
                .drawBehind {
                    // Fine top highlight
                    drawLine(
                        color = Color.White.copy(alpha = 0.12f),
                        start = Offset(x = 6f, y = 1.5f),
                        end = Offset(x = size.width - 6f, y = 1.5f),
                        strokeWidth = 2f
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconColor,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// -------------------------------------------------------------
// TACTILE METRIC PILL (Clean Material Icons + 3D Beveled Tactile)
// -------------------------------------------------------------

@Composable
private fun TactileMetricPill(
    icon: ImageVector,
    text: String,
    isHighlight: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressOffsetY by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "pill_offset"
    )
    val bottomLipHeight by animateDpAsState(
        targetValue = if (isPressed) 0.5.dp else 2.5.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "pill_lip"
    )

    val lipColor = if (isHighlight) Color(0xFF094721) else Color(0xFF0B0F16)
    val faceBg = if (isHighlight) {
        Brush.verticalGradient(
            listOf(
                Color(0xFF22C55E),
                Color(0xFF15803D)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0xFF1D2331),
                Color(0xFF151923)
            )
        )
    }
    val borderColor = if (isHighlight) Color(0xFF86EFAC).copy(alpha = 0.70f) else Color(0xFF333E53).copy(alpha = 0.45f)
    val contentTint = if (isHighlight) Color(0xFF042911) else Color(0xFF94A3B8)
    val textTint = if (isHighlight) Color(0xFF042911) else Color(0xFFE2E8F0)

    Box(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(lipColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(bottom = bottomLipHeight)
    ) {
        Box(
            modifier = Modifier
                .offset(y = pressOffsetY)
                .fillMaxWidth()
                .height((38 - 2.5).dp)
                .clip(RoundedCornerShape(11.dp))
                .background(faceBg)
                .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(11.dp))
                .drawBehind {
                    drawLine(
                        color = if (isHighlight) Color.White.copy(alpha = 0.40f) else Color.White.copy(alpha = 0.10f),
                        start = Offset(x = 8f, y = 1.5f),
                        end = Offset(x = size.width - 8f, y = 1.5f),
                        strokeWidth = 2f
                    )
                }
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentTint,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isHighlight) FontWeight.Black else FontWeight.SemiBold,
                        letterSpacing = 0.2.sp
                    ),
                    color = textTint,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
