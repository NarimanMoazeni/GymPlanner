package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.LocalCurrentLanguage
import com.example.ui.locale.formatDigits
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.KineticGreen
import com.example.ui.theme.SleekIndigo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Premium 3D Motion Graphic Splash Screen for GymPlanner.
 *
 * Choreography:
 * 1. Visual Authenticity: Displays the official high-resolution 3D App Icon
 *    (clipboard, diagonal barbell, checkmarks) floating elegantly on screen.
 * 2. True Single Pen with 3D Detachment:
 *    - The authentic stylus pen begins at its native resting spot on the clipboard.
 *    - At liftoff, it rises toward the camera with soft 3D elevation shadow,
 *      revealing the clean clipboard beneath it (no ghost duplicate).
 *    - Swoops down along a smooth parabolic curve to the loading bar.
 * 3. Lockstep Coloring:
 *    - The pen tip glides across the track from left to right.
 *    - The glowing neon fill matches the pen tip coordinate at every single frame.
 *    - Status text and live Persian percentage smoothly count up to 100%.
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val language = LocalCurrentLanguage.current

    // Resource Bitmaps
    val logoBitmap = ImageBitmap.imageResource(id = R.drawable.ic_splash_logo_base)
    val penBitmap = ImageBitmap.imageResource(id = R.drawable.ic_splash_pen)

    // Entrance and Exit Animators
    val stageScale = remember { Animatable(0.92f) }
    val stageAlpha = remember { Animatable(0f) }
    val elementsAlpha = remember { Animatable(0f) }
    val screenExitAlpha = remember { Animatable(1f) }

    // Pen Choreography Animators
    val swoopProgress = remember { Animatable(0f) } // 0f = on logo -> 1f = on loading track
    val progress = remember { Animatable(0f) }      // 0f = 0% -> 1f = 100%

    // Flourish Animators
    val penBounce = remember { Animatable(0f) }
    val completionPulse = remember { Animatable(0f) }

    // Gentle 3D Breathing Float for Logo
    val infiniteTransition = rememberInfiniteTransition(label = "logo_float_transition")
    val logoFloatY by infiniteTransition.animateFloat(
        initialValue = -3.5f,
        targetValue = 3.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_float_y"
    )

    LaunchedEffect(Unit) {
        // --- PHASE 1: Logo & Stage Materialization (0.0s - 0.5s) ---
        launch {
            stageAlpha.animateTo(1f, animationSpec = tween(400, easing = FastOutSlowInEasing))
        }
        launch {
            stageScale.animateTo(1f, animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow))
        }
        launch {
            elementsAlpha.animateTo(1f, animationSpec = tween(450, easing = LinearEasing))
        }

        delay(500)

        // --- PHASE 2: Pen 3D Liftoff & Swoop to Track (0.5s - 1.0s) ---
        swoopProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(500, easing = FastOutSlowInEasing)
        )

        // --- PHASE 3: Pen Smoothly Colors Loading Track Left-to-Right (1.0s - 2.6s) ---
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1600, easing = FastOutSlowInEasing)
        )

        // --- PHASE 4: 100% Celebration & Soft Light Pulse (2.6s - 2.9s) ---
        launch {
            penBounce.animateTo(1f, animationSpec = spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium))
        }
        launch {
            completionPulse.animateTo(1f, animationSpec = tween(280, easing = FastOutSlowInEasing))
        }

        delay(320)

        // --- PHASE 5: Seamless Crossfade into Main App ---
        screenExitAlpha.animateTo(0f, animationSpec = tween(220, easing = FastOutSlowInEasing))

        // Trigger finish callback
        onSplashFinished()
    }

    // Force strictly LTR coordinate system for the canvas stage
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(DarkBackground)
                .alpha(screenExitAlpha.value)
                .testTag("splash_screen_root"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .scale(stageScale.value)
                    .alpha(stageAlpha.value)
            ) {
                // Unified Stage Canvas (Logo + Detachable Pen + Loading Track)
                Box(
                    modifier = Modifier.size(320.dp, 330.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasW = size.width
                        val centerX = canvasW / 2f

                        // 1. Logo Position & Dimensions
                        val logoSize = 185.dp.toPx()
                        val logoCenterY = 105.dp.toPx() + logoFloatY.dp.toPx()
                        val logoTopLeft = Offset(centerX - logoSize / 2f, logoCenterY - logoSize / 2f)

                        // Ambient Soft Neon Mint Glow behind 3D Logo
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF2EE59D).copy(alpha = 0.18f),
                                    Color(0xFF10B981).copy(alpha = 0.05f),
                                    Color.Transparent
                                ),
                                radius = logoSize * 0.72f
                            ),
                            center = Offset(centerX, logoCenterY)
                        )

                        // Draw Official 3D Logo Base (Clipboard + Barbell + Checkmarks)
                        drawImage(
                            image = logoBitmap,
                            dstOffset = IntOffset(logoTopLeft.x.roundToInt(), logoTopLeft.y.roundToInt()),
                            dstSize = IntSize(logoSize.roundToInt(), logoSize.roundToInt())
                        )

                        // 2. Pre-existing Loading Track (Visible from start)
                        val trackW = 240.dp.toPx()
                        val trackH = 12.dp.toPx()
                        val trackLeft = centerX - trackW / 2f
                        val trackRight = trackLeft + trackW
                        val trackCenterY = 248.dp.toPx()
                        val trackTop = trackCenterY - trackH / 2f

                        // Track Background Inset Well
                        drawRoundRect(
                            color = Color(0xFF0F141F),
                            topLeft = Offset(trackLeft, trackTop),
                            size = Size(trackW, trackH),
                            cornerRadius = CornerRadius(trackH / 2f, trackH / 2f)
                        )

                        // Outer Crisp Track Border (Pulses mint upon 100% completion)
                        val trackBorderColor = if (completionPulse.value > 0.05f) {
                            Color(0xFF2EE59D)
                        } else {
                            Color(0xFF1E2C3D)
                        }
                        drawRoundRect(
                            color = trackBorderColor,
                            topLeft = Offset(trackLeft, trackTop),
                            size = Size(trackW, trackH),
                            cornerRadius = CornerRadius(trackH / 2f, trackH / 2f),
                            style = Stroke(width = 1.3.dp.toPx())
                        )

                        // 3. Synchronized Progress Bar Fill
                        val p = progress.value.coerceIn(0f, 1f)
                        val currentFillW = trackW * p
                        val tipContactX = trackLeft + currentFillW

                        if (p > 0.005f) {
                            // Radiant Neon Gradient Trail
                            drawRoundRect(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        ElectricIndigo,
                                        Color(0xFF2EE59D),
                                        KineticGreen
                                    ),
                                    startX = trackLeft,
                                    endX = trackRight
                                ),
                                topLeft = Offset(trackLeft, trackTop),
                                size = Size(currentFillW, trackH),
                                cornerRadius = CornerRadius(trackH / 2f, trackH / 2f)
                            )

                            // Glossy Highlight Reflection
                            drawLine(
                                color = Color.White.copy(alpha = 0.40f),
                                start = Offset(trackLeft + 3.dp.toPx(), trackTop + 2.dp.toPx()),
                                end = Offset(tipContactX - 2.dp.toPx(), trackTop + 2.dp.toPx()),
                                strokeWidth = 1.4.dp.toPx(),
                                cap = StrokeCap.Round
                            )

                            // Contact Glow Spark at the pen tip contact point
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color.White,
                                        Color(0xFF2EE59D).copy(alpha = 0.88f),
                                        Color.Transparent
                                    ),
                                    center = Offset(tipContactX, trackCenterY),
                                    radius = 8.5.dp.toPx()
                                ),
                                center = Offset(tipContactX, trackCenterY),
                                radius = 8.5.dp.toPx()
                            )
                        }

                        // Completion Pulse Wave
                        if (completionPulse.value > 0.05f) {
                            drawRoundRect(
                                color = Color.White.copy(alpha = (1f - completionPulse.value) * 0.45f),
                                topLeft = Offset(trackLeft, trackTop),
                                size = Size(trackW, trackH),
                                cornerRadius = CornerRadius(trackH / 2f, trackH / 2f)
                            )
                        }

                        // 4. Single Detachable Stylus Pen
                        // Exact resting position on the logo:
                        val penRestX = centerX + logoSize * (2f / 432f)
                        val penRestY = logoCenterY + logoSize * (102f / 432f)
                        val penRestAngle = -45.5f

                        val penLength = logoSize * (162f / 432f)
                        val penHeight = penLength * (penBitmap.height.toFloat() / penBitmap.width.toFloat())

                        val s = swoopProgress.value
                        val currentTipX: Float
                        val currentTipY: Float
                        val currentPenAngle: Float
                        val currentPenScale: Float
                        val currentPenLift: Float

                        if (s < 1f) {
                            // Phase 1 & 2: Smooth Quadratic Bézier Arc Liftoff
                            val ctrlX = centerX - 38.dp.toPx()
                            val ctrlY = logoCenterY + 58.dp.toPx()
                            currentTipX = (1 - s) * (1 - s) * penRestX + 2 * (1 - s) * s * ctrlX + s * s * trackLeft
                            currentTipY = (1 - s) * (1 - s) * penRestY + 2 * (1 - s) * s * ctrlY + s * s * trackCenterY
                            currentPenAngle = (1 - s) * penRestAngle + s * (-42f)
                            currentPenLift = sin(s * PI.toFloat())
                            currentPenScale = 1.0f + 0.16f * currentPenLift
                        } else {
                            // Phase 3 & 4: Locked directly onto the loading bar fill
                            val drawWobbleY = if (p in 0.02f..0.98f) (sin(p * 35f) * 1.5.dp.toPx()) else 0f
                            val bounceLiftY = penBounce.value * 10.dp.toPx()

                            currentTipX = tipContactX
                            currentTipY = trackCenterY + drawWobbleY - bounceLiftY
                            currentPenAngle = if (penBounce.value > 0.1f) -30f else -42f
                            currentPenScale = 1.0f
                            currentPenLift = 0f
                        }

                        // Draw the Stylus Pen Sprite with Tip Pivot
                        translate(left = currentTipX, top = currentTipY) {
                            rotate(degrees = currentPenAngle, pivot = Offset.Zero) {
                                val drawW = (penLength * currentPenScale).roundToInt()
                                val drawH = (penHeight * currentPenScale).roundToInt()

                                // Soft 3D Elevation Drop Shadow when lifted off
                                if (currentPenLift > 0.04f) {
                                    val shadowOffset = (6.dp.toPx() * currentPenLift).roundToInt()
                                    drawImage(
                                        image = penBitmap,
                                        dstOffset = IntOffset(shadowOffset, -(drawH / 2) + shadowOffset),
                                        dstSize = IntSize(drawW, drawH),
                                        colorFilter = ColorFilter.tint(
                                            Color.Black.copy(alpha = 0.35f * currentPenLift),
                                            BlendMode.SrcIn
                                        )
                                    )
                                }

                                // High-Resolution Pen Sprite
                                drawImage(
                                    image = penBitmap,
                                    dstOffset = IntOffset(0, -(drawH / 2)),
                                    dstSize = IntSize(drawW, drawH)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Brand Name & Status
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.alpha(elementsAlpha.value)
                ) {
                    Text(
                        text = strings.appName,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 26.sp,
                            letterSpacing = 0.sp
                        ),
                        color = Color.White
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.splashLoading,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                letterSpacing = 0.sp
                            ),
                            color = Color(0xFF94A3B8)
                        )

                        // Live Percentage Counter with Persian Digits
                        val percentInt = (progress.value * 100).toInt().coerceIn(0, 100)
                        Text(
                            text = "${formatDigits(percentInt, language)}٪",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 0.sp
                            ),
                            color = if (percentInt == 100) Color(0xFF2EE59D) else SleekIndigo,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                    }
                }
            }
        }
    }
}
