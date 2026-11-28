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
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.ui.graphics.luminance
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
import com.example.ui.theme.CoralDeleteDark
import com.example.ui.theme.CoralDeleteLight
import com.example.ui.theme.CoralDeleteSurfaceDark
import com.example.ui.theme.CoralDeleteSurfaceLight
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.KineticGreen
import com.example.ui.theme.SleekIndigo
import com.example.ui.theme.tactileCard
import com.example.ui.theme.tactileInset

/**
 * Tactile Planner Activity Card for both Light and Dark modes:
 * - Natural symmetric layout (Start: Checkbox + Title; End: Reorder + Actions)
 * - 40dp Tactile Squircle Action Buttons with 8dp spacing
 * - 32dp Tactile Completion Checkbox with bouncy scale animation
 * - Tactile Metric Pills dynamically adapting to Light and Dark themes
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
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    val cardAlpha by animateFloatAsState(
        targetValue = if (isDone) 0.60f else 1.0f,
        animationSpec = tween(durationMillis = 260),
        label = "card_alpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = cardAlpha }
            .tactileCard(
                shape = RoundedCornerShape(22.dp),
                backgroundColor = if (isDone) {
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                } else {
                    MaterialTheme.colorScheme.surface
                },
                borderColor = if (isDone) {
                    EmeraldGreen.copy(alpha = 0.40f)
                } else {
                    null
                }
            )
            .padding(14.dp)
            .testTag("activity_card_${activity.id}"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ROW 1 (HEADER): Full Width Exercise Title with Checkbox, Index & Smooth Marquee
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onEdit),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 32.dp Tactile Circular Completion Checkbox with bouncy spring animation
            TactileCompletionCheckbox(
                isDone = isDone,
                onClick = onToggleDone,
                testTag = "btn_done_activity_${activity.id}"
            )

            // Index Pill
            Text(
                text = formatDigits(indexStr, language),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.sp
                ),
                maxLines = 1,
                softWrap = false,
                color = if (isDone) EmeraldGreen else MaterialTheme.colorScheme.primary
            )

            // Exercise Title + Subtitle taking full remaining width
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = activity.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    modifier = Modifier.basicMarquee(
                        iterations = Int.MAX_VALUE,
                        repeatDelayMillis = 1500,
                        initialDelayMillis = 2000
                    )
                )
                if (activity.subtitle.isNotBlank()) {
                    Text(
                        text = activity.subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        modifier = Modifier.basicMarquee(
                            iterations = Int.MAX_VALUE,
                            repeatDelayMillis = 1500,
                            initialDelayMillis = 2000
                        )
                    )
                }
            }
        }

        // ROW 2: 3 Tactile Metric Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (activity.type) {
                ActivityType.WEIGHT_REPS -> {
                    val displayWeight = WeightUnit.formatDisplay(activity.weight, weightUnit)
                    // Pill 1: Highlighted Tactile Pill (Weight) with Dumbbell
                    TactileMetricPill(
                        icon = Icons.Default.FitnessCenter,
                        text = "${formatDigits(displayWeight, language)} $unitLabel",
                        isHighlight = true,
                        onClick = onEdit,
                        modifier = Modifier.weight(1.05f)
                    )

                    // Pill 2: Inset Matte Pill (Sets & Reps)
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

                    // Pill 3: Inset Matte Pill (Type / Details)
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

        // ROW 3: Action Controls Bar (Reorder chevrons on start + Duplicate, Edit, Delete Squircles on end)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reorder Up / Down Chevrons in compact pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0xFF1E2130) else Color(0xFFEDEEF6))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                IconButton(
                    onClick = onMoveUp,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = strings.moveUpBtn,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(
                    onClick = onMoveDown,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = strings.moveDownBtn,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 3 Tactile Squircles with 40dp touch target and 8dp spacing
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TactileSquircleButton(
                    icon = Icons.Default.ContentCopy,
                    contentDescription = strings.duplicateBtn,
                    variant = SquircleVariant.NEUTRAL,
                    onClick = onDuplicate,
                    testTag = "btn_duplicate_${activity.id}"
                )

                TactileSquircleButton(
                    icon = Icons.Default.Edit,
                    contentDescription = strings.editBtn,
                    variant = SquircleVariant.NEUTRAL,
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
    }
}

// -------------------------------------------------------------
// 32.dp TACTILE COMPLETION CHECKBOX (Bouncy Spring Animation)
// -------------------------------------------------------------

@Composable
private fun TactileCompletionCheckbox(
    isDone: Boolean,
    onClick: (() -> Unit)?,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "checkbox_scale"
    )

    val bgCol = if (isDone) {
        EmeraldGreen
    } else if (isDark) {
        MaterialTheme.colorScheme.surfaceContainerLowest
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }

    val borderCol = if (isDone) {
        EmeraldGreen
    } else if (isDark) {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
    }

    Box(
        modifier = modifier
            .size(32.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .background(bgCol)
            .border(width = 2.dp, color = borderCol, shape = CircleShape)
            .clickable(
                enabled = onClick != null,
                interactionSource = interactionSource,
                indication = null,
                onClick = { onClick?.invoke() }
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isDone,
            enter = scaleIn(animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow)) + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Done",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// -------------------------------------------------------------
// SQUIRCLE BUTTONS (40dp Touch Target, Coral Delete, Neutral Edit & Copy)
// -------------------------------------------------------------

private enum class SquircleVariant {
    CORAL_DELETE,
    NEUTRAL
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
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

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
            if (isDark) {
                listOf(
                    CoralDeleteSurfaceDark,
                    Color(0xFF200E14),
                    CoralDeleteDark.copy(alpha = 0.35f),
                    CoralDeleteDark
                )
            } else {
                listOf(
                    CoralDeleteSurfaceLight,
                    Color(0xFFFECACA),
                    CoralDeleteLight.copy(alpha = 0.25f),
                    CoralDeleteLight
                )
            }
        }
        SquircleVariant.NEUTRAL -> {
            if (isDark) {
                listOf(
                    MaterialTheme.colorScheme.surfaceContainerHigh,
                    MaterialTheme.colorScheme.surfaceContainerLowest,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.40f),
                    MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                listOf(
                    MaterialTheme.colorScheme.surface,
                    Color(0xFFE2E8F0),
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.70f),
                    MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

    Box(
        modifier = modifier
            .size(40.dp)
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
                .size(width = 40.dp, height = (40 - 2.5).dp)
                .clip(RoundedCornerShape(11.dp))
                .background(faceColor)
                .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(11.dp))
                .drawBehind {
                    // Fine top highlight
                    val hl = if (isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.60f)
                    drawLine(
                        color = hl,
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
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// -------------------------------------------------------------
// TACTILE METRIC PILL (Highlight vs Debossed Matte Inset)
// -------------------------------------------------------------

@Composable
fun TactileMetricPill(
    icon: ImageVector,
    text: String,
    isHighlight: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
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

    if (isHighlight) {
        // Highlighted Elevated Tactile Pill (Emerald in Dark / Indigo or Emerald in Light)
        val faceBg = if (isDark) {
            Brush.verticalGradient(
                listOf(
                    EmeraldGreen,
                    Color(0xFF059669)
                )
            )
        } else {
            Brush.verticalGradient(
                listOf(
                    ElectricIndigo,
                    Color(0xFF4338CA)
                )
            )
        }
        val lipColor = if (isDark) Color(0xFF064E3B) else Color(0xFF312E81)
        val borderColor = if (isDark) Color(0xFF6EE7B7).copy(alpha = 0.6f) else Color(0xFFA5B4FC).copy(alpha = 0.5f)
        val contentTint = Color.White

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
                        color = Color.White.copy(alpha = 0.35f),
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
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.sp
                        ),
                        color = contentTint,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    } else {
        // Debossed Matte Inset Pill (Tactile Inset Well)
        Box(
            modifier = modifier
                .height(38.dp)
                .tactileInset(shape = RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
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
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
