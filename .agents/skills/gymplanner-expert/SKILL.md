---
name: gymplanner-expert
description: >-
  Expert guidance, architectural patterns, custom animations, and release workflows
  for the GymPlanner Android application and related tactile fitness trackers.
  Use when developing, debugging, extending, or maintaining GymPlanner: including Jetpack Compose MVI UI,
  Room/DataStore offline-first storage, Duolingo-style dynamic spotlight coachmark tours,
  3D motion splash screens, Shamsi (Jalali) mathematical calendar conversions, tactile spring physics,
  GitHub release binary automation, and RTL localization.
---

# GymPlanner Expert Engineering & Workflow Skill

This skill documents the comprehensive architecture, design systems, algorithms, animation mechanics, and deployment workflows engineered for the **GymPlanner** application.

---

## 1. Quick Architecture Index

| Component | Responsibility | Reference / Source File |
| :--- | :--- | :--- |
| **MVI / Presentation** | Single-state unidirectional data flow via StateFlow | [`GymPlannerViewModel.kt`](file:///C:/Users/NariManoV/.gemini/antigravity/scratch/FitPlan/app/src/main/java/com/example/ui/GymPlannerViewModel.kt) |
| **Offline-First Storage** | Room SQLite 2.6+ database & DataStore preferences | [`GymPlannerRepository.kt`](file:///C:/Users/NariManoV/.gemini/antigravity/scratch/FitPlan/app/src/main/java/com/example/data/repository/GymPlannerRepository.kt), [`AppDatabase.kt`](file:///C:/Users/NariManoV/.gemini/antigravity/scratch/FitPlan/app/src/main/java/com/example/data/local/AppDatabase.kt) |
| **3D Motion Splash** | Decoupled stylus Bezier arc lift-off & lockstep loading | [`SplashScreen.kt`](file:///C:/Users/NariManoV/.gemini/antigravity/scratch/FitPlan/app/src/main/java/com/example/ui/screens/SplashScreen.kt) |
| **Spotlight Coachmark Tour** | Duolingo/Myket style live-screen spotlight walkthrough | [`SpotlightTourOverlay.kt`](file:///C:/Users/NariManoV/.gemini/antigravity/scratch/FitPlan/app/src/main/java/com/example/ui/components/SpotlightTourOverlay.kt), [`TourModels.kt`](file:///C:/Users/NariManoV/.gemini/antigravity/scratch/FitPlan/app/src/main/java/com/example/ui/tour/TourModels.kt) |
| **Persian Calendar Engine** | Bidirectional Shamsi <-> Gregorian mathematical converter | [`PersianDateHelper.kt`](file:///C:/Users/NariManoV/.gemini/antigravity/scratch/FitPlan/app/src/main/java/com/example/util/PersianDateHelper.kt) |
| **Tactile Design System** | Spring physics navigation dock, rubber-band tabs & dark theme | [`TactileExtensions.kt`](file:///C:/Users/NariManoV/.gemini/antigravity/scratch/FitPlan/app/src/main/java/com/example/ui/theme/TactileExtensions.kt), [`BottomNavigationBar.kt`](file:///C:/Users/NariManoV/.gemini/antigravity/scratch/FitPlan/app/src/main/java/com/example/ui/components/BottomNavigationBar.kt) |
| **Release & Git Automation** | GitHub Releases binary asset streaming & security hygiene | [Release Workflow Guide](references/release-workflow.md) |

---

## 2. Core Workflows & Procedures

### Workflow A: Maintaining & Extending the 3D Motion Splash
When modifying the application's launch experience:
1. **Logo Geometry:** Ensure the base graphic (`ic_splash_logo_base.png`) is the pristine clipboard surface with checkmarks and NO pen.
2. **Single Stylus Lift:** The stylus (`ic_splash_pen.png`) must initialize directly on its natural rest position on the board, lift off using a cubic Bezier elevation curve, scale up by 1.18x with elevated shadow, and arc towards the loading bar.
3. **Synchronous Fill:** The loading bar progress (0.0f to 1.0f) and the stylus tip contact coordinate must be locked in lockstep:
   `stylusX = barStartX + (barWidth * progress)`.
4. **Completion:** Trigger a soft tap pulse and radiant ripple before transitioning seamlessly to `GymPlannerApp`.
*See detailed equations in [references/animations.md](references/animations.md).*

---

### Workflow B: Adding Steps to the Dynamic Spotlight Coachmark Tour
When introducing a new feature that needs onboarding spotlight:
1. Add a new enum value to `TourStep` in `TourModels.kt` specifying `targetKey`, `targetTab`, and required screen navigation.
2. Tag the Compose target component using the custom modifier:
   `Modifier.reportTourTarget(TourTargetKey.YOUR_FEATURE_KEY)`.
3. Provide localized strings in `AppStrings.kt` (Persian and English) for the step title and description.
4. Ensure `SpotlightTourOverlay` executes `CompositingStrategy.Offscreen` with `BlendMode.Clear` on the target bounds and computes smart directional arrows pointing to the highlighted element.
*See implementation patterns in [references/animations.md](references/animations.md).*

---

### Workflow C: Persian Calendar & Date Conversion Standards
When handling dates, workout logging, or weekly views:
1. **Storage Format:** Always store dates in the local database as Gregorian timestamps or `YYYY-MM-DD` strings for standard indexing.
2. **Presentation Format:** Always convert to Shamsi using `PersianDateHelper.gregorianToPersian(gy, gm, gd)` for user display.
3. **Calendar Interactivity:** Use `PersianDateHelper.persianToGregorian(jy, jm, jd)` to sync user selections in `MonthlyCalendarView` back into database queries.
4. **Typography:** Format all numerical digits to Persian digits (`۰-۹`) using `PersianDateHelper.toPersianDigits()`.
5. **No Text Breaking:** For Persian weekday badge chips (e.g. «پنجشنبه»), always enforce:
   `maxLines = 1, softWrap = false, letterSpacing = 0.sp, Modifier.wrapContentSize()`.
*See mathematical tables and leap-year rules in [references/persian-calendar.md](references/persian-calendar.md).*

---

### Workflow D: GitHub Release & APK Deployment
When deploying a new APK version or attaching release binaries:
1. Build the APK via Google AI Studio or Gradle: `./gradlew assembleDebug`.
2. Inspect and verify binary size and checksums (SHA-256 and MD5).
3. Upload binary to GitHub Releases via `curl.exe` with chunked binary streaming to prevent socket write timeouts.
4. Clean up any redundant duplicate APKs from git repo sources (keeping release binaries in Releases Assets).
5. Enforce strict RTL markdown (`<div dir="rtl" align="right">`) in `README.md` and release notes.
*See the automated Python runbook in [references/release-workflow.md](references/release-workflow.md).*
