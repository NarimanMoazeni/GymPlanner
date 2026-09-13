# Custom Animations & Visual Effects Reference

## 1. 3D Motion Splash Screen (`SplashScreen.kt`)
- **Brand Geometry:**
  - Base Board: `ic_splash_logo_base.png` (clipboard with checkmarks, no stylus).
  - Removable Stylus: `ic_splash_pen.png` (barbell stylus).
- **Trajectory Calculation:**
  ```kotlin
  // Cubic Bezier Arc elevation for the stylus
  val progress = transition.animateFloat(...)
  val stylusX = lerp(initialX, targetBarX, progress)
  val arcHeight = sin(progress * Math.PI) * 120.dp
  val stylusY = lerp(initialY, targetBarY, progress) - arcHeight
  val stylusScale = 1.0f + sin(progress * Math.PI).toFloat() * 0.18f
  ```
- **Lockstep Filling:**
  Stylus tip contacts the loading bar exactly where the neon fill head advances.

---

## 2. Dynamic Spotlight Coachmark Tour (`SpotlightTourOverlay.kt`)
- **Off-Screen Clear Compositing:**
  ```kotlin
  Canvas(
      modifier = Modifier
          .fillMaxSize()
          .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
  ) {
      // 1. Semi-transparent black scrim
      drawRect(color = Color.Black.copy(alpha = 0.76f))
      // 2. Cutout target bounds with Clear blend mode
      drawRoundRect(
          topLeft = targetRect.topLeft,
          size = targetRect.size,
          cornerRadius = CornerRadius(16.dp.toPx()),
          blendMode = BlendMode.Clear
      )
  }
  ```
- **Pulsing Neon Glow:**
  An animated stroke (0.35 to 1.0 alpha) pulses continuously around the target bounds using `infiniteRepeatable`.
- **Directional Arrow Calculation:**
  If target is in bottom half of screen: tooltip placed above target with downward-pointing triangle arrow.
  If target is in top half: tooltip placed below target with upward-pointing triangle arrow.
