package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.locale.AppLanguage
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.LocalCurrentLanguage
import com.example.ui.locale.formatDigits
import com.example.ui.theme.AmberGold
import com.example.ui.theme.EmeraldGreen
import com.example.ui.tour.AppTourSteps
import com.example.ui.tour.SpotlightShape
import com.example.ui.tour.TourStep
import com.example.ui.tour.TourTarget
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun SpotlightTourOverlay(
    currentStepIndex: Int,
    targetBounds: Map<TourTarget, Rect>,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val language = LocalCurrentLanguage.current
    val density = LocalDensity.current

    val totalSteps = AppTourSteps.size
    val currentStep = AppTourSteps.getOrElse(currentStepIndex) { AppTourSteps.first() }
    val rawTargetRect = targetBounds[currentStep.target]

    // Pulsating border infinite animation
    val infiniteTransition = rememberInfiniteTransition(label = "spotlight_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )
    val pulseExpand by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_expand"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            // Consume clicks so user cannot click through the tour scrim
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {}
    ) {
        val screenWidthPx = constraints.maxWidth.toFloat()
        val screenHeightPx = constraints.maxHeight.toFloat()

        // Fallback target area if element not positioned yet
        val targetRect = rawTargetRect ?: Rect(
            left = screenWidthPx * 0.15f,
            top = screenHeightPx * 0.4f,
            right = screenWidthPx * 0.85f,
            bottom = screenHeightPx * 0.5f
        )

        val paddingPx = with(density) { (8.dp + pulseExpand.dp).toPx() }
        val cornerRadiusPx = with(density) { 20.dp.toPx() }

        // 1. Offscreen Canvas for Scrim + Clear Cutout + Glowing Ring
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        ) {
            // Draw dimmed dark background scrim
            drawRect(color = Color.Black.copy(alpha = 0.76f))

            if (currentStep.target != TourTarget.TOUR_FINISH) {
                // Cut out the spotlight area to reveal live screen underneath
                if (currentStep.shape == SpotlightShape.CIRCLE) {
                    val radius = max(targetRect.width, targetRect.height) / 2f + paddingPx
                    drawCircle(
                        color = Color.Transparent,
                        center = targetRect.center,
                        radius = radius,
                        blendMode = BlendMode.Clear
                    )
                    // Draw outer glowing ring
                    drawCircle(
                        color = currentStep.accentColor.copy(alpha = pulseAlpha),
                        center = targetRect.center,
                        radius = radius,
                        style = Stroke(width = with(density) { 3.2.dp.toPx() })
                    )
                } else {
                    val cutoutTopLeft = Offset(
                        x = (targetRect.left - paddingPx).coerceAtLeast(4f),
                        y = (targetRect.top - paddingPx).coerceAtLeast(4f)
                    )
                    val cutoutSize = Size(
                        width = (targetRect.width + paddingPx * 2).coerceAtMost(size.width - 8f),
                        height = targetRect.height + paddingPx * 2
                    )

                    drawRoundRect(
                        color = Color.Transparent,
                        topLeft = cutoutTopLeft,
                        size = cutoutSize,
                        cornerRadius = CornerRadius(cornerRadiusPx),
                        blendMode = BlendMode.Clear
                    )
                    // Draw outer glowing border
                    drawRoundRect(
                        color = currentStep.accentColor.copy(alpha = pulseAlpha),
                        topLeft = cutoutTopLeft,
                        size = cutoutSize,
                        cornerRadius = CornerRadius(cornerRadiusPx),
                        style = Stroke(width = with(density) { 3.2.dp.toPx() })
                    )
                }
            }
        }

        // 2. Duolingo-style Floating Tooltip Speech Bubble
        val isLandscape = screenWidthPx > screenHeightPx
        val isTargetInBottomHalf = targetRect.center.y > (screenHeightPx * 0.48f)

        // Calculate vertical placement for tooltip
        val tooltipY = if (currentStep.target == TourTarget.TOUR_FINISH) {
            // Centered for finish screen
            ((screenHeightPx - with(density) { 220.dp.toPx() }) / 2f).coerceAtLeast(16f).roundToInt()
        } else if (isLandscape) {
            val idealY = if (isTargetInBottomHalf) {
                (targetRect.top - paddingPx - with(density) { 190.dp.toPx() })
            } else {
                (targetRect.bottom + paddingPx + with(density) { 8.dp.toPx() })
            }
            idealY.coerceIn(
                with(density) { 8.dp.toPx() },
                (screenHeightPx - with(density) { 210.dp.toPx() }).coerceAtLeast(with(density) { 8.dp.toPx() })
            ).roundToInt()
        } else if (isTargetInBottomHalf) {
            // Place ABOVE target
            (targetRect.top - paddingPx - with(density) { 260.dp.toPx() }).coerceAtLeast(with(density) { 60.dp.toPx() }).roundToInt()
        } else {
            // Place BELOW target
            (targetRect.bottom + paddingPx + with(density) { 14.dp.toPx() }).coerceAtMost(screenHeightPx - with(density) { 300.dp.toPx() }).roundToInt()
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(x = 0, y = tooltipY) }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = if (isLandscape) 460.dp else 410.dp)
                    .heightIn(max = if (isLandscape) (screenHeightPx / density.density - 16).dp else 450.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top pointer arrow (pointing UP if tooltip is placed below target)
                if (currentStep.target != TourTarget.TOUR_FINISH && !isTargetInBottomHalf) {
                    val arrowTargetXPx = (targetRect.center.x - with(density) { 16.dp.toPx() }).coerceIn(
                        with(density) { 32.dp.toPx() },
                        screenWidthPx - with(density) { 64.dp.toPx() }
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                    ) {
                        Canvas(
                            modifier = Modifier
                                .offset { IntOffset(x = arrowTargetXPx.roundToInt(), y = 0) }
                                .size(width = 24.dp, height = 12.dp)
                        ) {
                            val path = Path().apply {
                                moveTo(size.width / 2f, 0f)
                                lineTo(size.width, size.height)
                                lineTo(0f, size.height)
                                close()
                            }
                            drawPath(path = path, color = Color(0xFF161F2E))
                        }
                    }
                }

                // Speech Bubble Card Surface
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 20.dp, shape = RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF161F2E),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        currentStep.accentColor.copy(alpha = 0.6f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Header Row: Step counter capsule + Skip action
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Step Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF222F44))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                val counterText = strings.tourStepBadge.format(
                                    formatDigits(currentStepIndex + 1, language),
                                    formatDigits(totalSteps, language)
                                )
                                Text(
                                    text = counterText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp
                                    ),
                                    color = currentStep.accentColor
                                )
                            }

                            // Skip Button
                            TextButton(
                                onClick = onDismiss,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = strings.tourSkipBtn,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        // Content Row: Mascot Avatar + Title & Description
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Athletic Mascot / Icon Badge
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(
                                        Brush.sweepGradient(
                                            listOf(
                                                currentStep.accentColor,
                                                AmberGold,
                                                currentStep.accentColor
                                            )
                                        )
                                    )
                                    .padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFF0F172A)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = currentStep.icon,
                                        contentDescription = null,
                                        tint = currentStep.accentColor,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = currentStep.title(strings),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    ),
                                    color = Color.White
                                )
                                Text(
                                    text = currentStep.description(strings),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 12.5.sp,
                                        lineHeight = 18.sp
                                    ),
                                    color = Color(0xFFCBD5E1)
                                )
                            }
                        }

                        // Bottom Actions: Previous & Next Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (currentStepIndex > 0) {
                                OutlinedButton(
                                    onClick = onPrev,
                                    modifier = Modifier
                                        .weight(0.9f)
                                        .height(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFFCBD5E1)
                                    )
                                ) {
                                    Text(
                                        text = strings.tourPrevBtn,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            val isLast = currentStepIndex == totalSteps - 1
                            Button(
                                onClick = onNext,
                                modifier = Modifier
                                    .weight(if (currentStepIndex > 0) 1.3f else 1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = currentStep.accentColor,
                                    contentColor = if (currentStep.accentColor == AmberGold) Color.Black else Color.White
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = if (isLast) strings.tourStartBtn else strings.tourNextBtn,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    )
                                    Icon(
                                        imageVector = if (isLast) Icons.Default.Check else {
                                            if (language == AppLanguage.PERSIAN) Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Filled.ArrowForward
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom pointer arrow (pointing DOWN if tooltip is placed above target)
                if (currentStep.target != TourTarget.TOUR_FINISH && isTargetInBottomHalf) {
                    val arrowTargetXPx = (targetRect.center.x - with(density) { 16.dp.toPx() }).coerceIn(
                        with(density) { 32.dp.toPx() },
                        screenWidthPx - with(density) { 64.dp.toPx() }
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                    ) {
                        Canvas(
                            modifier = Modifier
                                .offset { IntOffset(x = arrowTargetXPx.roundToInt(), y = 0) }
                                .size(width = 24.dp, height = 12.dp)
                        ) {
                            val path = Path().apply {
                                moveTo(0f, 0f)
                                lineTo(size.width, 0f)
                                lineTo(size.width / 2f, size.height)
                                close()
                            }
                            drawPath(path = path, color = Color(0xFF161F2E))
                        }
                    }
                }
            }
        }
    }
}
