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
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
 * Ultra-smooth Dark Neumorphic & Tactile Activity Card
 * Redesigned based on user reference mockup with physical bevels,
 * coral delete squircle, dark titanium edit/duplicate squircles,
 * luminous mint exercise capsule, 4-way crosshair target button,
 * and 3 physical tactile pills (Neon Green Slider, Loops Reps, and Recessed Details).
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
        targetValue = if (isDone) 0.58f else 1.0f,
        animationSpec = tween(durationMillis = 280),
        label = "card_alpha"
    )

    // Main Card Outer Container
    Column(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = cardAlpha }
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF161B27),
                        Color(0xFF11141D)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF2A3449),
                        Color(0xFF181E2B)
                    )
                ),
                shape = RoundedCornerShape(26.dp)
            )
            .padding(14.dp)
            .testTag("activity_card_${activity.id}"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // TOP ROW: [Trash] [Edit] [Copy]  ---- [ Title Pill ] [^ v] ---- [Target / Done]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Quick action squircle buttons: Delete, Edit, Duplicate
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Coral/Red Delete Squircle
                TactileSquircleButton(
                    icon = Icons.Default.Delete,
                    contentDescription = strings.deleteBtn,
                    variant = SquircleVariant.CORAL_DELETE,
                    onClick = onDelete,
                    testTag = "btn_delete_${activity.id}"
                )

                // Dark Edit Squircle
                TactileSquircleButton(
                    icon = Icons.Default.Edit,
                    contentDescription = strings.editBtn,
                    variant = SquircleVariant.DARK_NEUTRAL,
                    onClick = onEdit,
                    testTag = "btn_edit_${activity.id}"
                )

                // Dark Duplicate Squircle
                TactileSquircleButton(
                    icon = Icons.Default.ContentCopy,
                    contentDescription = strings.duplicateBtn,
                    variant = SquircleVariant.DARK_NEUTRAL,
                    onClick = onDuplicate,
                    testTag = "btn_duplicate_${activity.id}"
                )
            }

            // Center: Luminous Mint Title Capsule
            TactileTitleCapsule(
                index = formatDigits(indexStr, language),
                title = activity.name,
                subtitle = activity.subtitle,
                isDone = isDone,
                onClick = onEdit,
                modifier = Modifier.weight(1f)
            )

            // Reorder Up / Down Chevrons
            TactileReorderChevrons(
                onMoveUp = onMoveUp,
                onMoveDown = onMoveDown,
                contentDescUp = strings.moveUpBtn,
                contentDescDown = strings.moveDownBtn
            )

            // 4-Way Crosshair Target / Done Action Button
            TactileTargetDoneButton(
                isDone = isDone,
                onClick = onToggleDone,
                testTag = "btn_done_activity_${activity.id}"
            )
        }

        // BOTTOM ROW: 3 Physical Tactile Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (activity.type) {
                ActivityType.WEIGHT_REPS -> {
                    val displayWeight = WeightUnit.formatDisplay(activity.weight, weightUnit)
                    // Pill 1: Glowing Green Neon Tactile Pill (Weight Slider / Knob)
                    TactileNeonGreenPill(
                        text = "${formatDigits(displayWeight, language)} $unitLabel",
                        onClick = onEdit,
                        modifier = Modifier.weight(1.05f)
                    )

                    // Pill 2: Dark Sets & Reps Pill (3 Connected Rings & Repeat Loop)
                    TactileSetsRepsPill(
                        text = "${formatDigits(activity.sets, language)} ست × ${formatDigits(activity.reps, language)}",
                        onClick = onEdit,
                        modifier = Modifier.weight(1.15f)
                    )

                    // Pill 3: Dark Mode Details Pill (Recessed Bar & Expand Corners)
                    TactileDetailsBarPill(
                        text = strings.typeWeightReps,
                        onClick = onEdit,
                        modifier = Modifier.weight(0.95f)
                    )
                }

                ActivityType.DURATION -> {
                    TactileNeonGreenPill(
                        text = "${formatDigits(activity.durationMinutes, language)} ${strings.minutesUnit}",
                        onClick = onEdit,
                        modifier = Modifier.weight(1.2f)
                    )
                    TactileDetailsBarPill(
                        text = strings.typeDuration,
                        onClick = onEdit,
                        modifier = Modifier.weight(1f)
                    )
                }

                ActivityType.DISTANCE -> {
                    TactileNeonGreenPill(
                        text = "${formatDigits(activity.distanceKm, language)} ${strings.kmUnit}",
                        onClick = onEdit,
                        modifier = Modifier.weight(1.2f)
                    )
                    TactileDetailsBarPill(
                        text = strings.typeDistance,
                        onClick = onEdit,
                        modifier = Modifier.weight(1f)
                    )
                }

                ActivityType.SIMPLE -> {
                    TactileNeonGreenPill(
                        text = strings.typeSimple,
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
                Color(0xFF220E14),
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
            .size(38.dp)
            .clip(RoundedCornerShape(13.dp))
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
                .size(width = 38.dp, height = (38 - 2.5).dp)
                .clip(RoundedCornerShape(12.dp))
                .background(faceColor)
                .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(12.dp))
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
// LUMINOUS MINT TITLE CAPSULE
// -------------------------------------------------------------

@Composable
private fun TactileTitleCapsule(
    index: String,
    title: String,
    subtitle: String,
    isDone: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressOffsetY by animateDpAsState(
        targetValue = if (isPressed) 1.5.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "capsule_offset"
    )

    Box(
        modifier = modifier
            .height(38.dp)
            .offset(y = pressOffsetY)
            .clip(RoundedCornerShape(19.dp))
            .background(
                if (isDone) Color(0xFF093620)
                else Color(0xFFB5F2D2)
            )
            .border(
                width = 1.5.dp,
                color = if (isDone) KineticGreen.copy(alpha = 0.5f) else Color(0xFF6EE7B7),
                shape = RoundedCornerShape(19.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 3.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        // Inner recessed pill matching user mockup!
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(
                    if (isDone) Color(0xFF042013)
                    else Color(0xFFDDFCE9)
                )
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$index •",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = if (isDone) KineticGreen else Color(0xFF064E3B)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (isDone) Color(0xFF86EFAC) else Color(0xFF0F3B29),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle.isNotBlank()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "($subtitle)",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = if (isDone) Color(0xFF6EE7B7).copy(alpha = 0.8f) else Color(0xFF1E6B4A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// REORDER CHEVRONS (Up & Down)
// -------------------------------------------------------------

@Composable
private fun TactileReorderChevrons(
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    contentDescUp: String,
    contentDescDown: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy((-2).dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .clickable(onClick = onMoveUp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = contentDescUp,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(18.dp)
            )
        }
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .clickable(onClick = onMoveDown),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = contentDescDown,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// -------------------------------------------------------------
// 4-WAY CROSSHAIR TARGET / DONE BUTTON
// -------------------------------------------------------------

@Composable
private fun TactileTargetDoneButton(
    isDone: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressOffsetY by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "target_offset"
    )
    val bottomLipHeight by animateDpAsState(
        targetValue = if (isPressed) 0.5.dp else 2.5.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "target_lip"
    )

    val faceColor = if (isDone) Color(0xFF0A3C23) else Color(0xFF1E2432)
    val lipColor = if (isDone) Color(0xFF042013) else Color(0xFF10141E)
    val borderColor = if (isDone) KineticGreen else Color(0xFF3B4861).copy(alpha = 0.40f)
    val iconTint = if (isDone) KineticGreen else Color(0xFF94A3B8)

    Box(
        modifier = modifier
            .size(38.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(lipColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = onClick != null,
                onClick = { onClick?.invoke() }
            )
            .padding(bottom = bottomLipHeight)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .offset(y = pressOffsetY)
                .size(width = 38.dp, height = (38 - 2.5).dp)
                .clip(RoundedCornerShape(12.dp))
                .background(faceColor)
                .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(12.dp))
                .drawBehind {
                    drawLine(
                        color = if (isDone) KineticGreen.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.12f),
                        start = Offset(x = 6f, y = 1.5f),
                        end = Offset(x = size.width - 6f, y = 1.5f),
                        strokeWidth = 2f
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = isDone,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = KineticGreen,
                    modifier = Modifier.size(18.dp)
                )
            }
            if (!isDone) {
                TactileFourWayTargetCanvas(
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Draws the 4-way crosshair target icon (central circle + 4 directional arrowheads/diamonds)
 * exactly as shown on the right of the user's reference mockup.
 */
@Composable
private fun TactileFourWayTargetCanvas(
    tint: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val centerRadius = w * 0.16f

        // Center circle
        drawCircle(
            color = tint,
            radius = centerRadius,
            center = Offset(cx, cy),
            style = Fill
        )

        val diamondLen = w * 0.22f
        val diamondHalfWidth = w * 0.12f

        // Top diamond
        val topPath = Path().apply {
            moveTo(cx, cy - centerRadius - 2f)
            lineTo(cx - diamondHalfWidth, cy - centerRadius - diamondLen * 0.6f)
            lineTo(cx, cy - centerRadius - diamondLen)
            lineTo(cx + diamondHalfWidth, cy - centerRadius - diamondLen * 0.6f)
            close()
        }
        drawPath(topPath, color = tint)

        // Bottom diamond
        val bottomPath = Path().apply {
            moveTo(cx, cy + centerRadius + 2f)
            lineTo(cx - diamondHalfWidth, cy + centerRadius + diamondLen * 0.6f)
            lineTo(cx, cy + centerRadius + diamondLen)
            lineTo(cx + diamondHalfWidth, cy + centerRadius + diamondLen * 0.6f)
            close()
        }
        drawPath(bottomPath, color = tint)

        // Left diamond
        val leftPath = Path().apply {
            moveTo(cx - centerRadius - 2f, cy)
            lineTo(cx - centerRadius - diamondLen * 0.6f, cy - diamondHalfWidth)
            lineTo(cx - centerRadius - diamondLen, cy)
            lineTo(cx - centerRadius - diamondLen * 0.6f, cy + diamondHalfWidth)
            close()
        }
        drawPath(leftPath, color = tint)

        // Right diamond
        val rightPath = Path().apply {
            moveTo(cx + centerRadius + 2f, cy)
            lineTo(cx + centerRadius + diamondLen * 0.6f, cy - diamondHalfWidth)
            lineTo(cx + centerRadius + diamondLen, cy)
            lineTo(cx + centerRadius + diamondLen * 0.6f, cy + diamondHalfWidth)
            close()
        }
        drawPath(rightPath, color = tint)
    }
}

// -------------------------------------------------------------
// 3 BOTTOM TACTILE PILLS
// -------------------------------------------------------------

/**
 * Pill 1: Glowing Green Neon Tactile Pill
 * Features an engraved horizontal slider/knob indicator on the left + weight value
 */
@Composable
private fun TactileNeonGreenPill(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressOffsetY by animateDpAsState(
        targetValue = if (isPressed) 2.5.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "neon_offset"
    )
    val bottomBevelHeight by animateDpAsState(
        targetValue = if (isPressed) 0.5.dp else 3.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "neon_bevel"
    )

    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(Color(0xFF0A4E24)) // Dark green bottom lip
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(bottom = bottomBevelHeight)
    ) {
        Box(
            modifier = Modifier
                .offset(y = pressOffsetY)
                .fillMaxWidth()
                .height(39.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF22C55E),
                            Color(0xFF15803D)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFF86EFAC).copy(alpha = 0.75f),
                    shape = RoundedCornerShape(12.dp)
                )
                .drawBehind {
                    // Physical top bevel highlight
                    drawLine(
                        color = Color.White.copy(alpha = 0.45f),
                        start = Offset(x = 10f, y = 1.5f),
                        end = Offset(x = size.width - 10f, y = 1.5f),
                        strokeWidth = 2f
                    )
                }
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Engraved slider knob indicator (— ◉ —)
                TactileSliderTrackCanvas(
                    tint = Color(0xFF09391B),
                    modifier = Modifier.size(width = 24.dp, height = 12.dp)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.3.sp
                    ),
                    color = Color(0xFF042911),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Draws the slider/knob indicator: horizontal line with a center knob circle (— ◉ —)
 */
@Composable
private fun TactileSliderTrackCanvas(
    tint: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cy = h / 2f

        // Horizontal bar
        drawLine(
            color = tint.copy(alpha = 0.8f),
            start = Offset(0f, cy),
            end = Offset(w, cy),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Center ring and dot
        drawCircle(
            color = tint,
            radius = 4.5.dp.toPx(),
            center = Offset(w / 2f, cy),
            style = Stroke(width = 2.dp.toPx())
        )
        drawCircle(
            color = tint,
            radius = 2.dp.toPx(),
            center = Offset(w / 2f, cy),
            style = Fill
        )
    }
}

/**
 * Pill 2: Dark Sets & Reps Pill
 * Features 3 connected circular track rings + loop repeat arrow
 */
@Composable
private fun TactileSetsRepsPill(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressOffsetY by animateDpAsState(
        targetValue = if (isPressed) 2.5.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "sets_offset"
    )
    val bottomBevelHeight by animateDpAsState(
        targetValue = if (isPressed) 0.5.dp else 3.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "sets_bevel"
    )

    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(Color(0xFF0E121B)) // Deep dark shadow lip
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(bottom = bottomBevelHeight)
    ) {
        Box(
            modifier = Modifier
                .offset(y = pressOffsetY)
                .fillMaxWidth()
                .height(39.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1B202D))
                .border(
                    width = 1.dp,
                    color = Color(0xFF333E53).copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                )
                .drawBehind {
                    // Bevel highlight
                    drawLine(
                        color = Color.White.copy(alpha = 0.12f),
                        start = Offset(x = 10f, y = 1.5f),
                        end = Offset(x = size.width - 10f, y = 1.5f),
                        strokeWidth = 2f
                    )
                }
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // 3 Connected Rings & Repeat Loop
                TactileSetsLoopsCanvas(
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(width = 24.dp, height = 12.dp)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFFE2E8F0),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Draws 3 small connected rings with an arc repeat symbol ( ◯ - ◯ - ◯ ↻ )
 */
@Composable
private fun TactileSetsLoopsCanvas(
    tint: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cy = h / 2f
        val r = 2.8.dp.toPx()
        val spacing = w / 4f

        for (i in 0..2) {
            val cx = spacing * (i + 0.65f)
            drawCircle(
                color = tint,
                radius = r,
                center = Offset(cx, cy),
                style = Stroke(width = 1.5.dp.toPx())
            )
            if (i < 2) {
                drawLine(
                    color = tint.copy(alpha = 0.6f),
                    start = Offset(cx + r, cy),
                    end = Offset(cx + spacing - r, cy),
                    strokeWidth = 1.2.dp.toPx()
                )
            }
        }

        // Small loop arrow on right
        val loopCx = w - 3.dp.toPx()
        drawArc(
            color = tint,
            startAngle = 45f,
            sweepAngle = 270f,
            useCenter = false,
            topLeft = Offset(loopCx - 3.dp.toPx(), cy - 3.dp.toPx()),
            size = Size(6.dp.toPx(), 6.dp.toPx()),
            style = Stroke(width = 1.5.dp.toPx())
        )
    }
}

/**
 * Pill 3: Dark Mode Details Pill
 * Features an inner recessed bar & expand icon corners
 */
@Composable
private fun TactileDetailsBarPill(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressOffsetY by animateDpAsState(
        targetValue = if (isPressed) 2.5.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "detail_offset"
    )
    val bottomBevelHeight by animateDpAsState(
        targetValue = if (isPressed) 0.5.dp else 3.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "detail_bevel"
    )

    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(Color(0xFF0E121B))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(bottom = bottomBevelHeight)
    ) {
        Box(
            modifier = Modifier
                .offset(y = pressOffsetY)
                .fillMaxWidth()
                .height(39.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1B202D))
                .border(
                    width = 1.dp,
                    color = Color(0xFF333E53).copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                )
                .drawBehind {
                    drawLine(
                        color = Color.White.copy(alpha = 0.12f),
                        start = Offset(x = 10f, y = 1.5f),
                        end = Offset(x = size.width - 10f, y = 1.5f),
                        strokeWidth = 2f
                    )
                }
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Recessed Bar & Expand Symbol
                TactileExpandBarCanvas(
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(width = 20.dp, height = 12.dp)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color(0xFF94A3B8),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Draws a recessed mini bar with diagonal expand corner arrows [ ▬▬  ⤢ ]
 */
@Composable
private fun TactileExpandBarCanvas(
    tint: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cy = h / 2f

        // Rounded mini capsule bar
        drawRoundRect(
            color = tint.copy(alpha = 0.4f),
            topLeft = Offset(0f, cy - 3.dp.toPx()),
            size = Size(w * 0.55f, 6.dp.toPx()),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
            style = Fill
        )

        // Diagonal expand corners (top-right and bottom-left)
        val exStart = w * 0.68f
        val exSize = w * 0.30f
        drawLine(
            color = tint,
            start = Offset(exStart, cy + 3.dp.toPx()),
            end = Offset(exStart + exSize, cy - 3.dp.toPx()),
            strokeWidth = 1.6.dp.toPx()
        )
        // Top-right tip
        drawLine(
            color = tint,
            start = Offset(exStart + exSize - 3.dp.toPx(), cy - 3.dp.toPx()),
            end = Offset(exStart + exSize, cy - 3.dp.toPx()),
            strokeWidth = 1.6.dp.toPx()
        )
        drawLine(
            color = tint,
            start = Offset(exStart + exSize, cy - 3.dp.toPx()),
            end = Offset(exStart + exSize, cy - 3.dp.toPx() + 3.dp.toPx()),
            strokeWidth = 1.6.dp.toPx()
        )
    }
}
