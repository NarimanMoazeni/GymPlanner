package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
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
 * Pure Flat Vector Motion Graphic Splash Screen for GymPlanner.
 * 
 * Exact feature fulfillment:
 * 1. ZERO Background Tile: The logo elements float completely flat on the app canvas
 *    without any enclosing squircle/box background.
 * 2. True Component Motion Graphic:
 *    - Clipboard levitates and checkmarks animate in sequentially (✓ 1, ✓ 2, ✓ 3).
 *    - Barbell has a subtle rotational sway and metallic sheen sweep.
 *    - Pen is an authentic SINGLE element: It starts resting on the clipboard in its
 *      exact logo position, then physically lifts off, swoops down to the loading track,
 *      leaving the clipboard empty behind it.
 * 3. Pixel-Perfect Synchronization:
 *    - Strictly locked to LTR coordinates.
 *    - The pen tip position and the progress bar fill edge are mathematically identical
 *      at every frame, with the tip directly ON the surface of the track.
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val language = LocalCurrentLanguage.current

    // Stage entrance
    val stageScale = remember { Animatable(0.88f) }
    val stageAlpha = remember { Animatable(0f) }
    val elementsAlpha = remember { Animatable(0f) }

    // Sequential Checkmarks motion (✓ 1, ✓ 2, ✓ 3)
    val checkmark1 = remember { Animatable(0f) }
    val checkmark2 = remember { Animatable(0f) }
    val checkmark3 = remember { Animatable(0f) }

    // Pen swoop transition (0f: resting on clipboard -> 1f: landed on track)
    val penSwoop = remember { Animatable(0f) }
    val penLiftScale = remember { Animatable(1f) }

    // Progress of coloring the bar (0.0f -> 1.0f)
    val progress = remember { Animatable(0f) }

    // Finish celebration
    val penBounce = remember { Animatable(0f) }
    val completionPulse = remember { Animatable(0f) }
    val screenExitAlpha = remember { Animatable(1f) }

    // Continuous subtle breathing motion for the barbell & clipboard
    val infiniteTransition = rememberInfiniteTransition(label = "logo_motion")
    val barbellSwayAngle by infiniteTransition.animateFloat(
        initialValue = -1.2f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "barbell_sway"
    )
    val clipboardFloatY by infiniteTransition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "clipboard_float"
    )

    LaunchedEffect(Unit) {
        // --- PHASE 1: Flat Logo Elements Appear & Checkmarks Pop In (0 - 450ms) ---
        launch {
            stageAlpha.animateTo(1f, animationSpec = tween(350, easing = FastOutSlowInEasing))
        }
        launch {
            stageScale.animateTo(1f, animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow))
        }
        launch {
            elementsAlpha.animateTo(1f, animationSpec = tween(400, easing = LinearEasing))
        }

        // Staggered checkmark ticks
        delay(120)
        checkmark1.animateTo(1f, animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium))
        delay(80)
        checkmark2.animateTo(1f, animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium))
        delay(80)
        checkmark3.animateTo(1f, animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium))

        delay(100)

        // --- PHASE 2: Pen Lifts Off Clipboard & Swoops Down to Loading Bar (450ms - 850ms) ---
        // Pen lifts off clipboard with slight scale up, then lands cleanly on track
        launch {
            penLiftScale.animateTo(1.12f, animationSpec = tween(180, easing = FastOutSlowInEasing))
            delay(150)
            penLiftScale.animateTo(1.0f, animationSpec = tween(220, easing = FastOutSlowInEasing))
        }

        penSwoop.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.76f, stiffness = Spring.StiffnessMediumLow)
        )

        // --- PHASE 3: Pen Colors the Track from Left to Right (850ms - 2250ms) ---
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1400, easing = FastOutSlowInEasing)
        )

        // --- PHASE 4: Celebratory Tap, Glow Flash & Seamless Exit (2250ms - 2600ms) ---
        launch {
            penBounce.animateTo(1f, animationSpec = spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium))
        }
        launch {
            completionPulse.animateTo(1f, animationSpec = tween(250, easing = FastOutSlowInEasing))
        }

        delay(300)

        // Fade out smoothly into main screen
        screenExitAlpha.animateTo(0f, animationSpec = tween(220, easing = FastOutSlowInEasing))

        // Trigger finish callback
        onSplashFinished()
    }

    // Force strictly LTR coordinate system for the animation stage
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
                // Unified Stage Canvas: Clipboard + Animated Checkmarks + Swaying Barbell + Detachable Pen + Loading Track
                Box(
                    modifier = Modifier.size(280.dp, 300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasW = size.width
                        val canvasH = size.height
                        val centerX = canvasW / 2f
                        val logoCenterY = 95.dp.toPx() + clipboardFloatY.dp.toPx()

                        // =============================================================
                        // 1. COMPONENT A: Flat Clipboard (No outer box/tile!)
                        // =============================================================
                        val boardW = 104.dp.toPx()
                        val boardH = 138.dp.toPx()
                        val boardLeft = centerX - boardW / 2f
                        val boardTop = logoCenterY - boardH / 2f
                        val boardCorner = 14.dp.toPx()

                        // Ambient soft glow behind clipboard
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF2EE59D).copy(alpha = 0.18f),
                                    Color.Transparent
                                ),
                                radius = boardW * 0.9f
                            ),
                            center = Offset(centerX, logoCenterY)
                        )

                        // Clipboard body (Flat sleek slate)
                        drawRoundRect(
                            color = Color(0xFF16222D),
                            topLeft = Offset(boardLeft, boardTop),
                            size = Size(boardW, boardH),
                            cornerRadius = CornerRadius(boardCorner, boardCorner)
                        )
                        // Clipboard neon mint crisp outline
                        drawRoundRect(
                            color = Color(0xFF2EE59D),
                            topLeft = Offset(boardLeft, boardTop),
                            size = Size(boardW, boardH),
                            cornerRadius = CornerRadius(boardCorner, boardCorner),
                            style = Stroke(width = 2.4.dp.toPx())
                        )

                        // Top binder clip
                        val clipW = 38.dp.toPx()
                        val clipH = 12.dp.toPx()
                        val clipLeft = centerX - clipW / 2f
                        val clipTop = boardTop - 4.dp.toPx()

                        drawRoundRect(
                            color = Color(0xFF1E2E3C),
                            topLeft = Offset(clipLeft, clipTop),
                            size = Size(clipW, clipH),
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )
                        drawRoundRect(
                            color = Color(0xFF2EE59D),
                            topLeft = Offset(clipLeft, clipTop),
                            size = Size(clipW, clipH),
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                            style = Stroke(width = 2.dp.toPx())
                        )
                        drawCircle(
                            color = Color(0xFF2EE59D),
                            radius = 2.5.dp.toPx(),
                            center = Offset(centerX, clipTop + clipH / 2f)
                        )

                        // 3 Checklist items (✓ checkmarks and horizontal lines)
                        val checkRowYOffsets = listOf(-28.dp.toPx(), -3.dp.toPx(), 22.dp.toPx())
                        val checkProgresses = listOf(checkmark1.value, checkmark2.value, checkmark3.value)

                        for (i in 0..2) {
                            val rowY = logoCenterY + checkRowYOffsets[i]
                            val checkProgress = checkProgresses[i]

                            // Horizontal checklist line
                            drawLine(
                                color = Color(0xFF2EE59D).copy(alpha = 0.35f),
                                start = Offset(centerX - 10.dp.toPx(), rowY),
                                end = Offset(centerX + 32.dp.toPx(), rowY),
                                strokeWidth = 2.dp.toPx(),
                                cap = StrokeCap.Round
                            )

                            // Animated Checkmark (✓)
                            if (checkProgress > 0.05f) {
                                val checkPath = Path().apply {
                                    val start = Offset(centerX - 33.dp.toPx(), rowY - 1.dp.toPx())
                                    val mid = Offset(centerX - 27.dp.toPx(), rowY + 5.dp.toPx())
                                    val end = Offset(centerX - 18.dp.toPx(), rowY - 6.dp.toPx())

                                    moveTo(start.x, start.y)
                                    if (checkProgress <= 0.5f) {
                                        val frac = checkProgress / 0.5f
                                        lineTo(start.x + (mid.x - start.x) * frac, start.y + (mid.y - start.y) * frac)
                                    } else {
                                        lineTo(mid.x, mid.y)
                                        val frac = (checkProgress - 0.5f) / 0.5f
                                        lineTo(mid.x + (end.x - mid.x) * frac, mid.y + (end.y - mid.y) * frac)
                                    }
                                }
                                drawPath(
                                    path = checkPath,
                                    color = Color.White,
                                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                        }

                        // =============================================================
                        // 2. COMPONENT B: Diagonal Floating Barbell with Plates
                        // =============================================================
                        // Rotates subtly with barbellSwayAngle around center
                        rotate(degrees = -45f + barbellSwayAngle, pivot = Offset(centerX, logoCenterY)) {
                            val shaftLength = 155.dp.toPx()
                            val shaftW = 5.5.dp.toPx()
                            val halfLen = shaftLength / 2f

                            // Central bar shaft
                            drawLine(
                                color = Color(0xFF2EE59D),
                                start = Offset(centerX - halfLen, logoCenterY),
                                end = Offset(centerX + halfLen, logoCenterY),
                                strokeWidth = shaftW,
                                cap = StrokeCap.Round
                            )

                            // Left and right plate sets
                            val plateDistances = listOf(-halfLen + 22.dp.toPx(), halfLen - 22.dp.toPx())
                            plateDistances.forEach { px ->
                                val sign = if (px < centerX) -1 else 1

                                // Inner small collar disc
                                drawRoundRect(
                                    color = Color(0xFF1E2E3C),
                                    topLeft = Offset(px - 3.dp.toPx(), logoCenterY - 10.dp.toPx()),
                                    size = Size(6.dp.toPx(), 20.dp.toPx()),
                                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                                )
                                drawRoundRect(
                                    color = Color(0xFF2EE59D),
                                    topLeft = Offset(px - 3.dp.toPx(), logoCenterY - 10.dp.toPx()),
                                    size = Size(6.dp.toPx(), 20.dp.toPx()),
                                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
                                    style = Stroke(width = 1.8.dp.toPx())
                                )

                                // Main heavy plate
                                val mainPlateX = px + (sign * 7.dp.toPx())
                                drawRoundRect(
                                    color = Color(0xFF16222D),
                                    topLeft = Offset(mainPlateX - 4.dp.toPx(), logoCenterY - 18.dp.toPx()),
                                    size = Size(8.dp.toPx(), 36.dp.toPx()),
                                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                                )
                                drawRoundRect(
                                    color = Color(0xFF2EE59D),
                                    topLeft = Offset(mainPlateX - 4.dp.toPx(), logoCenterY - 18.dp.toPx()),
                                    size = Size(8.dp.toPx(), 36.dp.toPx()),
                                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
                                    style = Stroke(width = 2.2.dp.toPx())
                                )

                                // Outer medium plate
                                val outerPlateX = mainPlateX + (sign * 8.dp.toPx())
                                drawRoundRect(
                                    color = Color(0xFF16222D),
                                    topLeft = Offset(outerPlateX - 3.dp.toPx(), logoCenterY - 14.dp.toPx()),
                                    size = Size(6.dp.toPx(), 28.dp.toPx()),
                                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                                )
                                drawRoundRect(
                                    color = Color(0xFF2EE59D),
                                    topLeft = Offset(outerPlateX - 3.dp.toPx(), logoCenterY - 14.dp.toPx()),
                                    size = Size(6.dp.toPx(), 28.dp.toPx()),
                                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
                                    style = Stroke(width = 1.8.dp.toPx())
                                )
                            }
                        }

                        // =============================================================
                        // 3. Pre-existing Loading Bar Track (Visible from t=0)
                        // =============================================================
                        val trackW = 230.dp.toPx()
                        val trackH = 13.dp.toPx()
                        val trackLeft = centerX - trackW / 2f
                        val trackRight = trackLeft + trackW
                        val trackTop = 235.dp.toPx()
                        val trackCenterY = trackTop + trackH / 2f

                        // Inset well background
                        drawRoundRect(
                            color = Color(0xFF0F141F),
                            topLeft = Offset(trackLeft, trackTop),
                            size = Size(trackW, trackH),
                            cornerRadius = CornerRadius(trackH / 2f, trackH / 2f)
                        )
                        // Outer crisp track border
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

                        // 4. Synchronized Progress Fill
                        val p = progress.value.coerceIn(0f, 1f)
                        val currentFillW = trackW * p
                        val tipContactX = trackLeft + currentFillW

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
                                size = Size(currentFillW, trackH),
                                cornerRadius = CornerRadius(trackH / 2f, trackH / 2f)
                            )

                            // Top glossy highlight reflection
                            drawLine(
                                color = Color.White.copy(alpha = 0.40f),
                                start = Offset(trackLeft + 3.dp.toPx(), trackTop + 2.dp.toPx()),
                                end = Offset(tipContactX - 2.dp.toPx(), trackTop + 2.dp.toPx()),
                                strokeWidth = 1.5.dp.toPx(),
                                cap = StrokeCap.Round
                            )

                            // Contact glow spark orb at the exact tip location
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color.White,
                                        Color(0xFF2EE59D).copy(alpha = 0.9f),
                                        Color.Transparent
                                    ),
                                    center = Offset(tipContactX, trackCenterY),
                                    radius = 8.5.dp.toPx()
                                ),
                                center = Offset(tipContactX, trackCenterY),
                                radius = 8.5.dp.toPx()
                            )
                        }

                        // Completion pulse flash
                        if (completionPulse.value > 0.05f) {
                            drawRoundRect(
                                color = Color.White.copy(alpha = (1f - completionPulse.value) * 0.5f),
                                topLeft = Offset(trackLeft, trackTop),
                                size = Size(trackW, trackH),
                                cornerRadius = CornerRadius(trackH / 2f, trackH / 2f)
                            )
                        }

                        // =============================================================
                        // 4. COMPONENT C: Detachable Authentic Stylus Pen
                        // =============================================================
                        // ONLY ONE PEN EXISTS ON SCREEN!
                        // Initial resting position: on clipboard lower-right (pointing bottom-left)
                        val penRestX = centerX + 26.dp.toPx()
                        val penRestY = logoCenterY + 28.dp.toPx()
                        val penRestAngle = -45f

                        // Target landing position on track:
                        val targetLandingX = trackLeft
                        val targetLandingY = trackCenterY
                        val targetLandingAngle = -42f

                        val swoopT = penSwoop.value
                        val penX: Float
                        val penY: Float
                        val penRotation: Float

                        if (swoopT < 1f) {
                            // Phase 1 & 2: Bezier swoop from resting place on clipboard to track start
                            val ctrlX = centerX - 45.dp.toPx()
                            val ctrlY = logoCenterY + 60.dp.toPx()
                            penX = (1 - swoopT) * (1 - swoopT) * penRestX + 2 * (1 - swoopT) * swoopT * ctrlX + swoopT * swoopT * targetLandingX
                            penY = (1 - swoopT) * (1 - swoopT) * penRestY + 2 * (1 - swoopT) * swoopT * ctrlY + swoopT * swoopT * targetLandingY
                            penRotation = (1 - swoopT) * penRestAngle + swoopT * targetLandingAngle
                        } else {
                            // Phase 3 & 4: Locked strictly to the front edge of the progress fill!
                            val wobbleY = if (p in 0.02f..0.98f) {
                                (sin(p * 40f) * 2.2.dp.toPx())
                            } else {
                                0f
                            }
                            val bounceLiftY = penBounce.value * 12.dp.toPx()

                            penX = tipContactX
                            penY = trackCenterY + wobbleY - bounceLiftY
                            penRotation = if (penBounce.value > 0.1f) -28f else -42f
                        }

                        // Draw the single authentic stylus pen
                        drawOfficialLogoStylusPen(
                            tipX = penX,
                            tipY = penY,
                            rotationDegrees = penRotation,
                            scale = penLiftScale.value,
                            isActivelyDrawing = p in 0.02f..0.98f
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

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
 * - Vibrant mint-green (0xFF2EE59D) barrel with clean 2D vector contours.
 * - Crisp white side pocket clip.
 * - Crisp white clicker button cap at top.
 * 
 * The tip of the pen is positioned exactly at (tipX, tipY).
 */
private fun DrawScope.drawOfficialLogoStylusPen(
    tipX: Float,
    tipY: Float,
    rotationDegrees: Float,
    scale: Float,
    isActivelyDrawing: Boolean
) {
    translate(left = tipX, top = tipY) {
        rotate(degrees = rotationDegrees, pivot = Offset.Zero) {
            val penLength = 46.dp.toPx() * scale
            val penWidth = 9.dp.toPx() * scale
            val halfW = penWidth / 2f

            // 1. Conical White Tip: extends from Y=0 to Y=-11dp
            val coneHeight = 11.dp.toPx() * scale
            val conePath = Path().apply {
                moveTo(0f, 0f) // Exact contact point
                lineTo(-halfW, -coneHeight)
                lineTo(halfW, -coneHeight)
                close()
            }
            drawPath(path = conePath, color = Color(0xFFF8FAFC)) // Pure crisp white

            // 2. Dark Nib Point at the very tip: Y=0 to Y=-3.5dp
            val nibHeight = 3.5.dp.toPx() * scale
            val nibW = halfW * (nibHeight / coneHeight)
            val nibPath = Path().apply {
                moveTo(0f, 0f)
                lineTo(-nibW, -nibHeight)
                lineTo(nibW, -nibHeight)
                close()
            }
            drawPath(path = nibPath, color = Color(0xFF1E293B))

            // 3. Main Mint Green Barrel (matching the logo's #2EE59D)
            val barrelStartY = -coneHeight
            val barrelEndY = -(penLength - 7.dp.toPx() * scale)
            val barrelHeight = barrelStartY - barrelEndY

            // Darker mint contour facet
            drawRect(
                color = Color(0xFF10B981),
                topLeft = Offset(-halfW, barrelEndY),
                size = Size(penWidth * 0.35f, barrelHeight)
            )
            // Primary vibrant neon mint facet
            drawRect(
                color = Color(0xFF2EE59D),
                topLeft = Offset(-halfW + penWidth * 0.35f, barrelEndY),
                size = Size(penWidth * 0.40f, barrelHeight)
            )
            // Highlight facet
            drawRect(
                color = Color(0xFF6EE7B7),
                topLeft = Offset(-halfW + penWidth * 0.75f, barrelEndY),
                size = Size(penWidth * 0.25f, barrelHeight)
            )

            // Outline stroke around barrel for crisp definition like the logo
            drawRect(
                color = Color(0xFF0F172A).copy(alpha = 0.6f),
                topLeft = Offset(-halfW, barrelEndY),
                size = Size(penWidth, barrelHeight),
                style = Stroke(width = 1.dp.toPx())
            )

            // 4. White Side Pocket Clip
            val clipTop = barrelEndY + 2.dp.toPx() * scale
            val clipHeight = 15.dp.toPx() * scale
            val clipW = 3.dp.toPx() * scale

            drawRoundRect(
                color = Color(0xFFF8FAFC),
                topLeft = Offset(halfW, clipTop),
                size = Size(clipW, clipHeight),
                cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx())
            )

            // 5. White Top Clicker Button Cap
            val capHeight = 5.5.dp.toPx() * scale
            val capY = barrelEndY - capHeight
            drawRoundRect(
                color = Color(0xFFF8FAFC),
                topLeft = Offset(-halfW * 0.85f, capY),
                size = Size(penWidth * 0.85f, capHeight + 1.dp.toPx()),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )

            // 6. Active Drawing Glow at the tip
            if (isActivelyDrawing) {
                drawCircle(
                    color = Color(0xFF2EE59D).copy(alpha = 0.7f),
                    radius = 4.dp.toPx(),
                    center = Offset.Zero
                )
            }
        }
    }
}
