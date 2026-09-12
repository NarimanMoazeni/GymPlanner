package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import kotlin.math.sin

/**
 * Ultra-smooth, logo-synchronized Splash Screen for GymPlanner.
 * 
 * Key refinements:
 * 1. Visual Authenticity: Uses the exact official app logo (ic_launcher_foreground),
 *    and the pen is drawn to exactly mirror the logo's mint-and-white stylus with clip and cap.
 * 2. Absolute Synchrony: The entire canvas and animation are strictly locked to LTR coordinates.
 *    The pen tip's X position is mathematically identical to the leading edge of the progress fill,
 *    so it colors directly ON the track from left to right with zero divergence.
 * 3. Fast & Seamless: Blends effortlessly with the Android 12+ system splash window.
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val language = LocalCurrentLanguage.current

    // Animations
    val logoScale = remember { Animatable(0.85f) }
    val logoAlpha = remember { Animatable(0f) }
    val elementsAlpha = remember { Animatable(0f) }

    // Pen transition phase:
    // descentPhase: 0.0f (at logo resting position) -> 1.0f (landed on loading track start)
    val penDescent = remember { Animatable(0f) }

    // Progress of drawing the bar: 0.0f -> 1.0f
    val progress = remember { Animatable(0f) }

    // Finish flourish
    val penBounce = remember { Animatable(0f) }
    val completionPulse = remember { Animatable(0f) }
    val screenExitAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        // --- PHASE 1: Logo & Stage Materialization (0 - 400ms) ---
        launch {
            logoAlpha.animateTo(1f, animationSpec = tween(350, easing = FastOutSlowInEasing))
        }
        launch {
            logoScale.animateTo(1f, animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow))
        }
        launch {
            elementsAlpha.animateTo(1f, animationSpec = tween(400, easing = LinearEasing))
        }

        delay(350)

        // --- PHASE 2: Pen Swoop Down Directly to Track Start (350ms - 750ms) ---
        penDescent.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMediumLow)
        )

        // --- PHASE 3: Pen Colors the Track (750ms - 2150ms, duration = 1400ms) ---
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1400, easing = FastOutSlowInEasing)
        )

        // --- PHASE 4: Completion Tap, Pulse Wave & Smooth Exit (2150ms - 2500ms) ---
        launch {
            penBounce.animateTo(1f, animationSpec = spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium))
        }
        launch {
            completionPulse.animateTo(1f, animationSpec = tween(250, easing = FastOutSlowInEasing))
        }

        delay(300)

        // Fade out splash screen
        screenExitAlpha.animateTo(0f, animationSpec = tween(220, easing = FastOutSlowInEasing))

        // Trigger finish callback
        onSplashFinished()
    }

    // Force strictly LTR coordinate system for the splash animation so RTL locales
    // never invert the pen movement or progress bar direction.
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
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value)
            ) {
                // Stage Container: Logo + Ambient Glow + Unified Track & Pen Canvas
                Box(
                    modifier = Modifier.size(280.dp, 280.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    // 1. Soft Ambient Mint/Teal Glow behind the Official Logo
                    Canvas(
                        modifier = Modifier
                            .size(170.dp)
                            .align(Alignment.TopCenter)
                    ) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF2EE59D).copy(alpha = 0.22f),
                                    Color(0xFF10B981).copy(alpha = 0.08f),
                                    Color.Transparent
                                ),
                                radius = size.width * 0.75f
                            )
                        )
                    }

                    // 2. Official App Logo (ic_launcher_foreground) matching user's exact design
                    Image(
                        painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                        contentDescription = strings.appName,
                        modifier = Modifier
                            .size(155.dp)
                            .align(Alignment.TopCenter)
                            .clip(RoundedCornerShape(32.dp))
                    )

                    // 3. Unified Synchronized Canvas (Track + Progress Fill + Logo-Matched Pen)
                    // Placed directly below the logo. Width = 280.dp, Height = 100.dp
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .align(Alignment.BottomCenter)
                            .alpha(elementsAlpha.value)
                    ) {
                        val canvasW = size.width
                        val trackWidth = 230.dp.toPx()
                        val trackHeight = 13.dp.toPx()
                        val trackLeft = (canvasW - trackWidth) / 2f
                        val trackRight = trackLeft + trackWidth
                        val trackTop = 22.dp.toPx()
                        val trackCenterY = trackTop + (trackHeight / 2f)

                        // 1. Draw Pre-existing Loading Track (Inset capsule well)
                        // Inset well background
                        drawRoundRect(
                            color = Color(0xFF0F141F),
                            topLeft = Offset(trackLeft, trackTop),
                            size = Size(trackWidth, trackHeight),
                            cornerRadius = CornerRadius(trackHeight / 2f, trackHeight / 2f)
                        )
                        // Track border: faint mint outline (pulsing on completion)
                        val borderColor = if (completionPulse.value > 0.05f) {
                            Color(0xFF2EE59D)
                        } else {
                            Color(0xFF1E2C3D)
                        }
                        drawRoundRect(
                            color = borderColor,
                            topLeft = Offset(trackLeft, trackTop),
                            size = Size(trackWidth, trackHeight),
                            cornerRadius = CornerRadius(trackHeight / 2f, trackHeight / 2f),
                            style = Stroke(width = 1.2.dp.toPx())
                        )

                        // 2. Synchronized Progress Fill
                        val p = progress.value.coerceIn(0f, 1f)
                        val currentFillWidth = trackWidth * p
                        val tipContactX = trackLeft + currentFillWidth

                        if (p > 0.005f) {
                            // Draw neon gradient fill up to exact tipContactX
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
                                size = Size(currentFillWidth, trackHeight),
                                cornerRadius = CornerRadius(trackHeight / 2f, trackHeight / 2f)
                            )

                            // Top glossy highlight reflection line
                            drawLine(
                                color = Color.White.copy(alpha = 0.40f),
                                start = Offset(trackLeft + 3.dp.toPx(), trackTop + 2.dp.toPx()),
                                end = Offset(tipContactX - 2.dp.toPx(), trackTop + 2.dp.toPx()),
                                strokeWidth = 1.5.dp.toPx(),
                                cap = StrokeCap.Round
                            )

                            // Glowing contact spark orb directly where the pen tip touches the track
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color.White,
                                        Color(0xFF2EE59D).copy(alpha = 0.85f),
                                        Color.Transparent
                                    ),
                                    center = Offset(tipContactX, trackCenterY),
                                    radius = 8.dp.toPx()
                                ),
                                center = Offset(tipContactX, trackCenterY),
                                radius = 8.dp.toPx()
                            )
                        }

                        // 3. Completion Pulse Flash
                        if (completionPulse.value > 0.05f) {
                            drawRoundRect(
                                color = Color.White.copy(alpha = (1f - completionPulse.value) * 0.5f),
                                topLeft = Offset(trackLeft, trackTop),
                                size = Size(trackWidth, trackHeight),
                                cornerRadius = CornerRadius(trackHeight / 2f, trackHeight / 2f)
                            )
                        }

                        // 4. Calculate Pen Coordinates
                        // Resting position near bottom-right of logo:
                        val penRestX = trackRight - 15.dp.toPx()
                        val penRestY = -40.dp.toPx() // above canvas top, near logo pen position

                        // When descending (Phase 2): interpolate from (penRestX, penRestY) to (trackLeft, trackCenterY)
                        val descentProgress = penDescent.value
                        val penCurrentX: Float
                        val penCurrentY: Float
                        val penRotation: Float

                        if (descentProgress < 1f) {
                            // Interpolate swoop down
                            penCurrentX = penRestX + (trackLeft - penRestX) * descentProgress
                            penCurrentY = penRestY + (trackCenterY - penRestY) * descentProgress
                            penRotation = -30f + (-12f * descentProgress) // tilt to -42 degrees
                        } else {
                            // Phase 3 & 4: Locked strictly to the front edge of the progress fill!
                            // tipContactX is GUARANTEED to be the exact front of the colored track
                            val wobbleY = if (p in 0.02f..0.98f) {
                                (sin(p * 40f) * 2.2.dp.toPx())
                            } else {
                                0f
                            }
                            val bounceLiftY = penBounce.value * 12.dp.toPx()

                            penCurrentX = tipContactX
                            penCurrentY = trackCenterY + wobbleY - bounceLiftY
                            penRotation = if (penBounce.value > 0.1f) -28f else -42f
                        }

                        // Draw the official-styled Mint & White Stylus Pen with tip at (penCurrentX, penCurrentY)
                        drawOfficialLogoStylusPen(
                            tipX = penCurrentX,
                            tipY = penCurrentY,
                            rotationDegrees = penRotation,
                            isActivelyDrawing = p in 0.02f..0.98f
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // App Brand Name & Tagline
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

                    Text(
                        text = strings.splashTagline,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            letterSpacing = 0.sp
                        ),
                        color = Color(0xFF94A3B8)
                    )

                    // Live Percentage Counter (with Persian Digits)
                    val percentInt = (progress.value * 100).toInt().coerceIn(0, 100)
                    Text(
                        text = "${formatDigits(percentInt, language)}٪",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 0.sp
                        ),
                        color = if (percentInt == 100) Color(0xFF2EE59D) else SleekIndigo,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

/**
 * Renders the exact stylus pen from the GymPlanner logo:
 * - Crisp white conical tip with dark writing nib.
 * - Vibrant mint-green (0xFF2EE59D) ergonomic barrel with depth facet lines.
 * - Crisp white side pocket clip.
 * - Crisp white clicker button cap at top.
 * 
 * The tip of the pen is positioned exactly at (tipX, tipY).
 */
private fun DrawScope.drawOfficialLogoStylusPen(
    tipX: Float,
    tipY: Float,
    rotationDegrees: Float,
    isActivelyDrawing: Boolean
) {
    // Translate coordinate space to the pen tip, then rotate around it
    translate(left = tipX, top = tipY) {
        rotate(degrees = rotationDegrees, pivot = Offset.Zero) {
            val penLength = 48.dp.toPx()
            val penWidth = 10.dp.toPx()
            val halfW = penWidth / 2f

            // 1. Conical White Tip: extends from Y=0 to Y=-11dp
            val coneHeight = 11.dp.toPx()
            val conePath = Path().apply {
                moveTo(0f, 0f) // The exact contact point
                lineTo(-halfW, -coneHeight)
                lineTo(halfW, -coneHeight)
                close()
            }
            drawPath(path = conePath, color = Color(0xFFF8FAFC)) // Pure crisp white

            // 2. Dark Nib Point at the very tip: Y=0 to Y=-3.5dp
            val nibHeight = 3.5.dp.toPx()
            val nibW = halfW * (nibHeight / coneHeight)
            val nibPath = Path().apply {
                moveTo(0f, 0f)
                lineTo(-nibW, -nibHeight)
                lineTo(leadW = nibW, -nibHeight)
                close()
            }
            drawPath(path = nibPath, color = Color(0xFF1E293B))

            // 3. Main Mint Green Barrel (matching the logo's #2EE59D): from Y=-coneHeight to Y=-(penLength - 7dp)
            val barrelStartY = -coneHeight
            val barrelEndY = -(penLength - 7.dp.toPx())
            val barrelHeight = barrelStartY - barrelEndY

            // Darker mint outline/shadow facet
            drawRect(
                color = Color(0xFF10B981),
                topLeft = Offset(-halfW, barrelEndY),
                size = Size(penWidth * 0.35f, barrelHeight)
            )
            // Primary vibrant neon mint facet (matching logo)
            drawRect(
                color = Color(0xFF2EE59D),
                topLeft = Offset(-halfW + penWidth * 0.35f, barrelEndY),
                size = Size(penWidth * 0.40f, barrelHeight)
            )
            // Lighter highlight mint facet
            drawRect(
                color = Color(0xFF6EE7B7),
                topLeft = Offset(-halfW + penWidth * 0.75f, barrelEndY),
                size = Size(penWidth * 0.25f, barrelHeight)
            )

            // Outline stroke around barrel for crisp cartoon-style definition like the logo
            drawRect(
                color = Color(0xFF0F172A).copy(alpha = 0.6f),
                topLeft = Offset(-halfW, barrelEndY),
                size = Size(penWidth, barrelHeight),
                style = Stroke(width = 1.dp.toPx())
            )

            // 4. White Side Pocket Clip (exact feature from the logo!)
            // Attached near top of barrel, extends along right side
            val clipTop = barrelEndY + 2.dp.toPx()
            val clipHeight = 16.dp.toPx()
            val clipW = 3.dp.toPx()

            drawRoundRect(
                color = Color(0xFFF8FAFC), // White clip
                topLeft = Offset(halfW, clipTop),
                size = Size(clipW, clipHeight),
                cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx())
            )

            // 5. White Top Clicker Button Cap: from barrelEndY to barrelEndY - 6dp
            val capHeight = 6.dp.toPx()
            val capY = barrelEndY - capHeight
            drawRoundRect(
                color = Color(0xFFF8FAFC), // Crisp white cap
                topLeft = Offset(-halfW * 0.85f, capY),
                size = Size(penWidth * 0.85f, capHeight + 1.dp.toPx()),
                cornerRadius = CornerRadius(2.5.dp.toPx(), 2.5.dp.toPx())
            )

            // 6. Active Drawing Glow at the tip
            if (isActivelyDrawing) {
                drawCircle(
                    color = Color(0xFF2EE59D).copy(alpha = 0.65f),
                    radius = 4.dp.toPx(),
                    center = Offset.Zero
                )
            }
        }
    }
}
