package com.example.ui.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Dual-tone soft shadow card modifier:
 * - Ambient drop shadow bottom-right
 * - Top-left luminous highlight
 * - Smooth rounded corners and crisp boundary outline
 */
@Composable
fun Modifier.tactileCard(
    shape: RoundedCornerShape = RoundedCornerShape(22.dp),
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    elevation: Dp = 6.dp
): Modifier = composed {
    val bg = backgroundColor ?: MaterialTheme.colorScheme.surface
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    val dropShadowColor = if (isDark) {
        Color.Black.copy(alpha = 0.45f)
    } else {
        Color(0xFF0F172A).copy(alpha = 0.06f)
    }

    val highlightColor = if (isDark) {
        Color.White.copy(alpha = 0.08f)
    } else {
        Color.White.copy(alpha = 0.90f)
    }

    val outlineColor = borderColor ?: if (isDark) {
        DarkOutlineBorder
    } else {
        LightOutlineBorder.copy(alpha = 0.8f)
    }

    this
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = dropShadowColor,
            spotColor = dropShadowColor
        )
        .clip(shape)
        .background(bg)
        .border(width = 1.dp, color = outlineColor, shape = shape)
        .drawBehind {
            // Top-left ambient luminous highlight line
            val strokePx = 2f
            drawLine(
                color = highlightColor,
                start = Offset(x = 16f, y = strokePx),
                end = Offset(x = size.width - 16f, y = strokePx),
                strokeWidth = strokePx
            )
        }
}

/**
 * Physical 3D squircle button with beveled bottom lip (2.5dp)
 * and smooth spring physics press offset (shifts y by 2dp on press).
 */
@Composable
fun Modifier.tactileButton(
    onClick: () -> Unit,
    shape: RoundedCornerShape = RoundedCornerShape(14.dp),
    baseColor: Color? = null,
    lipColor: Color? = null,
    borderColor: Color? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null
): Modifier = composed {
    val actualSource = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by actualSource.collectIsPressedAsState()
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    val pressOffsetY by animateDpAsState(
        targetValue = if (isPressed && enabled) 2.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "tactile_btn_press"
    )

    val bottomLipHeight by animateDpAsState(
        targetValue = if (isPressed && enabled) 0.5.dp else 2.5.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "tactile_btn_lip"
    )

    val resolvedBase = baseColor ?: if (isDark) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        MaterialTheme.colorScheme.surface
    }

    val resolvedLip = lipColor ?: if (isDark) {
        MaterialTheme.colorScheme.surfaceContainerLowest
    } else {
        Color(0xFFCBD5E1)
    }

    val resolvedBorder = borderColor ?: if (isDark) {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    } else {
        LightOutlineBorder
    }

    this
        .clip(shape)
        .background(resolvedLip)
        .clickable(
            interactionSource = actualSource,
            indication = null,
            enabled = enabled,
            onClick = onClick
        )
        .padding(bottom = bottomLipHeight)
        .offset(y = pressOffsetY)
        .background(resolvedBase, shape)
        .border(width = 1.dp, color = resolvedBorder, shape = shape)
        .drawBehind {
            // Crisp top reflection
            val topHighlight = if (isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.60f)
            drawLine(
                color = topHighlight,
                start = Offset(x = 6f, y = 1.5f),
                end = Offset(x = size.width - 6f, y = 1.5f),
                strokeWidth = 2f
            )
        }
}

/**
 * Inner-shadow effect simulating a sunken display/well for numeric displays,
 * timer readouts, and secondary chips.
 */
@Composable
fun Modifier.tactileInset(
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
    backgroundColor: Color? = null,
    borderColor: Color? = null
): Modifier = composed {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val bg = backgroundColor ?: if (isDark) {
        DarkSurfaceInset
    } else {
        LightSurfaceInset
    }

    val borderCol = borderColor ?: if (isDark) {
        DarkOutlineBorder.copy(alpha = 0.5f)
    } else {
        Color(0xFFD1D5DB).copy(alpha = 0.7f)
    }

    val insetShadowTop = if (isDark) Color.Black.copy(alpha = 0.40f) else Color(0xFF0F172A).copy(alpha = 0.08f)

    this
        .clip(shape)
        .background(bg)
        .border(width = 1.dp, color = borderCol, shape = shape)
        .drawBehind {
            // Subtle top inner deboss gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(insetShadowTop, Color.Transparent),
                    startY = 0f,
                    endY = 16f
                ),
                size = Size(size.width, 16f)
            )
        }
}

/**
 * Frosted Glass Dock Modifier:
 * Semi-transparent background (alpha 0.88f), 1dp top border highlight, and soft rounded silhouette.
 */
@Composable
fun Modifier.frostedDock(
    shape: RoundedCornerShape = RoundedCornerShape(28.dp),
    elevation: Dp = 12.dp
): Modifier = composed {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val baseBg = if (isDark) {
        DarkSurface.copy(alpha = 0.88f)
    } else {
        Color.White.copy(alpha = 0.90f)
    }

    val dockShadow = if (isDark) Color.Black.copy(alpha = 0.55f) else Color(0xFF0F172A).copy(alpha = 0.10f)
    val highlight = if (isDark) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.85f)
    val outlineCol = if (isDark) DarkOutlineBorder.copy(alpha = 0.7f) else LightOutlineBorder.copy(alpha = 0.9f)

    this
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = dockShadow,
            spotColor = dockShadow
        )
        .clip(shape)
        .background(baseBg)
        .border(width = 1.dp, color = outlineCol, shape = shape)
        .drawBehind {
            // Top highlight line for frosted glass realism
            drawLine(
                color = highlight,
                start = Offset(x = 24f, y = 1.5f),
                end = Offset(x = size.width - 24f, y = 1.5f),
                strokeWidth = 2.5f
            )
        }
}
