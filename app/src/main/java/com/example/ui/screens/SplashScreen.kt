package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.LocalCurrentLanguage
import com.example.ui.locale.formatDigits
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.KineticGreen
import com.example.ui.theme.SleekIndigo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * High-performance motion graphic Splash Screen for GymPlanner.
 * 
 * Choreography:
 * 1. Initial State: Minimalist floating clipboard and fitness emblem fade in;
 *    the loading bar track is ALREADY present and waiting to be colored.
 * 2. Descent: The 3D-angled pencil descends smoothly from the clipboard toward the track start.
 * 3. Active Coloring: The pencil draws across the track with rhythmic micro-wobbles,
 *    painting a vibrant neon gradient behind it while emitting glowing sparks from the tip.
 * 4. Completion: At 100%, the pencil lifts with a small tap, the bar pulses, and the screen
 *    smoothly crossfades into the main application.
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val language = LocalCurrentLanguage.current

    // Animation progress controllers
    val logoScale = remember { Animatable(0.75f) }
    val logoAlpha = remember { Animatable(0f) }
    val elementsAlpha = remember { Animatable(0f) }

    // Pencil spatial coordinates (relative to center stage)
    val pencilX = remember { Animatable(60f) }
    val pencilY = remember { Animatable(-110f) }
    val pencilRotation = remember { Animatable(-35f) }
    val pencilScale = remember { Animatable(0.9f) }

    // Progress of coloring the bar (0.0f -> 1.0f)
    val progress = remember { Animatable(0f) }
    val completionPulse = remember { Animatable(0f) }
    val screenExitAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        // --- PHASE 1: Logo and Stage Materialization (0 - 450ms) ---
        launch {
            logoAlpha.animateTo(1f, animationSpec = tween(400, easing = FastOutSlowInEasing))
        }
        launch {
            logoScale.animateTo(1f, animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow))
        }
        launch {
            elementsAlpha.animateTo(1f, animationSpec = tween(450, easing = LinearEasing))
        }

        delay(400)

        // --- PHASE 2: Pencil Swoop Down to Track (400ms - 850ms) ---
        launch {
            pencilX.animateTo(-100f, animationSpec = tween(450, easing = FastOutSlowInEasing))
        }
        launch {
            pencilY.animateTo(145f, animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow))
        }
        launch {
            pencilRotation.animateTo(-42f, animationSpec = tween(450, easing = FastOutSlowInEasing))
        }
        launch {
            pencilScale.animateTo(1.05f, animationSpec = tween(450))
        }

        delay(450)

        // --- PHASE 3: Pencil Colors the Track (850ms - 2250ms, duration = 1400ms) ---
        launch {
            pencilX.animateTo(100f, animationSpec = tween(1400, easing = FastOutSlowInEasing))
        }
        launch {
            progress.animateTo(1f, animationSpec = tween(1400, easing = FastOutSlowInEasing))
        }

        delay(1400)

        // --- PHASE 4: Completion Tap, Glow Pulse & Exit Transition (2250ms - 2600ms) ---
        launch {
            pencilY.animateTo(125f, animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium))
        }
        launch {
            pencilRotation.animateTo(-25f, animationSpec = tween(200))
        }
        launch {
            completionPulse.animateTo(1f, animationSpec = tween(250, easing = FastOutSlowInEasing))
        }

        delay(300)

        // Fade out splash screen
        screenExitAlpha.animateTo(0f, animationSpec = tween(250, easing = FastOutSlowInEasing))

        // Trigger finish callback
        onSplashFinished()
    }

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
                .scale(logoScale.value)
                .alpha(logoAlpha.value)
        ) {
            // Stage Area: Contains Clipboard Logo and the Floating/Coloring Pencil
            Box(
                modifier = Modifier
                    .size(280.dp, 320.dp),
                contentAlignment = Alignment.Center
            ) {
                // 1. Floating Minimalist Clipboard Logo (Base)
                ClipboardLogoEmblem(
                    modifier = Modifier.offset(y = (-30).dp)
                )

                // 2. Pre-existing Loading Bar Track (Always present on stage)
                PreExistingLoadingTrack(
                    progress = progress.value,
                    completionPulse = completionPulse.value,
                    modifier = Modifier
                        .offset(y = 145.dp)
                        .alpha(elementsAlpha.value)
                )

                // 3. Dynamic Animated Pencil with hand-coloring micro-wobble
                val coloringMicroWobble = if (progress.value > 0.01f && progress.value < 0.99f) {
                    (sin(progress.value * 45f) * 2.8f)
                } else {
                    0f
                }

                Canvas(
                    modifier = Modifier
                        .size(60.dp, 60.dp)
                        .offset(
                            x = pencilX.value.dp,
                            y = (pencilY.value + coloringMicroWobble).dp
                        )
                        .scale(pencilScale.value)
                ) {
                    drawRealisticPencil(
                        rotationDegrees = pencilRotation.value,
                        isDrawing = progress.value > 0.02f && progress.value < 0.98f
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Brand Name & Dynamic Persian / English Tagline
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.alpha(elementsAlpha.value)
            ) {
                Text(
                    text = strings.appName,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp,
                        letterSpacing = 0.sp
                    ),
                    color = Color.White
                )

                Text(
                    text = strings.splashTagline,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        letterSpacing = 0.sp
                    ),
                    color = Color(0xFF94A3B8)
                )

                // Progress Percentage Counter (Persian Digits)
                val percentInt = (progress.value * 100).toInt().coerceIn(0, 100)
                Text(
                    text = "${formatDigits(percentInt, language)}٪",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.sp
                    ),
                    color = if (percentInt == 100) KineticGreen else SleekIndigo,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun ClipboardLogoEmblem(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(130.dp, 160.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Subtle Radial Ambient Glow behind clipboard
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ElectricIndigo.copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    center = Offset(width / 2f, height / 2f),
                    radius = width * 0.9f
                )
            )

            // 2. Clipboard Body (Tactile dark slate card)
            val boardCorner = 18.dp.toPx()
            val boardRect = Size(width * 0.85f, height * 0.92f)
            val boardTopLeft = Offset((width - boardRect.width) / 2f, height * 0.08f)

            // Board Surface
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E2638),
                        Color(0xFF141926)
                    )
                ),
                topLeft = boardTopLeft,
                size = boardRect,
                cornerRadius = CornerRadius(boardCorner, boardCorner)
            )

            // Outer crisp edge
            drawRoundRect(
                color = Color(0xFF334155),
                topLeft = boardTopLeft,
                size = boardRect,
                cornerRadius = CornerRadius(boardCorner, boardCorner),
                style = Stroke(width = 1.5.dp.toPx())
            )

            // 3. Inner White/Light Note Sheet
            val sheetMarginX = 10.dp.toPx()
            val sheetTop = boardTopLeft.y + 18.dp.toPx()
            val sheetWidth = boardRect.width - (sheetMarginX * 2)
            val sheetHeight = boardRect.height - 24.dp.toPx()
            val sheetCorner = 10.dp.toPx()

            drawRoundRect(
                color = Color(0xFF0F1420),
                topLeft = Offset(boardTopLeft.x + sheetMarginX, sheetTop),
                size = Size(sheetWidth, sheetHeight),
                cornerRadius = CornerRadius(sheetCorner, sheetCorner)
            )

            // 4. Subtle Planner Ruling Lines
            val lineYStart = sheetTop + 42.dp.toPx()
            val lineSpacing = 14.dp.toPx()
            val lineWidth = sheetWidth * 0.75f
            val lineStartX = boardTopLeft.x + sheetMarginX + (sheetWidth - lineWidth) / 2f

            for (i in 0..2) {
                val y = lineYStart + (i * lineSpacing)
                drawLine(
                    color = Color(0xFF26334A),
                    start = Offset(lineStartX, y),
                    end = Offset(lineStartX + lineWidth, y),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // 5. Metallic Top Binder Clip
            val clipWidth = width * 0.38f
            val clipHeight = 16.dp.toPx()
            val clipX = (width - clipWidth) / 2f
            val clipY = boardTopLeft.y - 6.dp.toPx()

            // Clip base
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF94A3B8), Color(0xFF475569))
                ),
                topLeft = Offset(clipX, clipY),
                size = Size(clipWidth, clipHeight),
                cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx())
            )

            // Clip inner hole
            drawCircle(
                color = Color(0xFF0E1116),
                radius = 3.dp.toPx(),
                center = Offset(width / 2f, clipY + clipHeight / 2f)
            )
        }

        // 6. Fitness Dumbbell Badge on Clipboard Sheet
        Box(
            modifier = Modifier
                .offset(y = (-6).dp)
                .size(38.dp)
                .clip(CircleShape)
                .background(ElectricIndigo.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = KineticGreen,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun PreExistingLoadingTrack(
    progress: Float,
    completionPulse: Float,
    modifier: Modifier = Modifier
) {
    val trackWidth = 220.dp
    val trackHeight = 12.dp

    Box(
        modifier = modifier
            .width(trackWidth)
            .height(trackHeight)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF0B0F15))
            .border(
                width = 1.dp,
                color = if (completionPulse > 0.1f) KineticGreen else Color(0xFF1E293B),
                shape = RoundedCornerShape(6.dp)
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Inset well inner shadow simulation
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.45f),
                        Color.Transparent
                    )
                )
            )

            // 2. Active colored progress filled by pencil
            if (progress > 0.005f) {
                val fillWidth = width * progress.coerceIn(0f, 1f)

                // Neon colored trail painted by pencil
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            ElectricIndigo,
                            SleekIndigo,
                            KineticGreen
                        )
                    ),
                    topLeft = Offset(0f, 0f),
                    size = Size(fillWidth, height),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )

                // Top glossy highlight reflection on the painted line
                drawLine(
                    color = Color.White.copy(alpha = 0.35f),
                    start = Offset(2.dp.toPx(), 2.dp.toPx()),
                    end = Offset(fillWidth - 2.dp.toPx(), 2.dp.toPx()),
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Contact Glow Spark at the pencil tip contact point
                val sparkX = fillWidth
                val sparkY = height / 2f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            KineticGreen.copy(alpha = 0.8f),
                            Color.Transparent
                        ),
                        center = Offset(sparkX, sparkY),
                        radius = 9.dp.toPx()
                    ),
                    center = Offset(sparkX, sparkY),
                    radius = 9.dp.toPx()
                )
            }

            // 3. Completion Flash wave
            if (completionPulse > 0.05f) {
                drawRect(
                    color = Color.White.copy(alpha = (1f - completionPulse) * 0.4f)
                )
            }
        }
    }
}

private fun DrawScope.drawRealisticPencil(
    rotationDegrees: Float,
    isDrawing: Boolean
) {
    rotate(degrees = rotationDegrees, pivot = Offset.Zero) {
        val pencilLength = 46.dp.toPx()
        val pencilWidth = 9.dp.toPx()
        val halfW = pencilWidth / 2f

        // 1. Wooden cone section
        val coneHeight = 12.dp.toPx()
        val woodPath = Path().apply {
            moveTo(0f, 0f)
            lineTo(-halfW, -coneHeight)
            lineTo(halfW, -coneHeight)
            close()
        }
        drawPath(path = woodPath, color = Color(0xFFD4A373))

        // 2. Graphite/Lead tip
        val leadHeight = 4.5.dp.toPx()
        val leadW = halfW * (leadHeight / coneHeight)
        val leadPath = Path().apply {
            moveTo(0f, 0f)
            lineTo(-leadW, -leadHeight)
            lineTo(leadW, -leadHeight)
            close()
        }
        drawPath(
            path = leadPath,
            color = if (isDrawing) KineticGreen else Color(0xFF1E293B)
        )

        // 3. Main Hexagonal Body (Faceted 3D Stripes)
        val bodyStartY = -coneHeight
        val bodyEndY = -(pencilLength - 9.dp.toPx())
        val bodyHeight = bodyStartY - bodyEndY

        drawRect(
            color = Color(0xFF3730A3),
            topLeft = Offset(-halfW, bodyEndY),
            size = Size(pencilWidth * 0.33f, bodyHeight)
        )
        drawRect(
            color = ElectricIndigo,
            topLeft = Offset(-halfW + pencilWidth * 0.33f, bodyEndY),
            size = Size(pencilWidth * 0.34f, bodyHeight)
        )
        drawRect(
            color = SleekIndigo,
            topLeft = Offset(-halfW + pencilWidth * 0.67f, bodyEndY),
            size = Size(pencilWidth * 0.33f, bodyHeight)
        )

        // 4. Metallic Ferrule (Ring)
        val ferruleEndY = bodyEndY - 4.5.dp.toPx()
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF94A3B8),
                    Color(0xFFF1F5F9),
                    Color(0xFF64748B)
                )
            ),
            topLeft = Offset(-halfW, ferruleEndY),
            size = Size(pencilWidth, 4.5.dp.toPx())
        )

        // 5. Stylus / Eraser Rounded Cap
        val capHeight = 5.5.dp.toPx()
        val capEndY = ferruleEndY - capHeight
        drawRoundRect(
            color = Color(0xFFF43F5E),
            topLeft = Offset(-halfW, capEndY),
            size = Size(pencilWidth, capHeight + 2.dp.toPx()),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
        )

        // 6. Active Drawing Glow around tip
        if (isDrawing) {
            drawCircle(
                color = KineticGreen.copy(alpha = 0.5f),
                radius = 3.5.dp.toPx(),
                center = Offset(0f, 0f)
            )
        }
    }
}
