package com.example

/*
 * NOTE: Robolectric and Roborazzi dependencies are not configured in this project's gradle files.
 * Uncomment once Roborazzi / Robolectric dependencies are added to gradle.
 *
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.locale.AppLanguage
import com.example.ui.locale.GymPlannerLocalizationProvider
import com.example.ui.screens.OnboardingScreen
import com.example.ui.theme.GymPlannerTheme
import com.example.ui.theme.ThemeMode
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent {
      GymPlannerTheme(themeMode = ThemeMode.DARK) {
        GymPlannerLocalizationProvider(language = AppLanguage.PERSIAN) {
          OnboardingScreen(
            onComplete = { _, _ -> },
            onSkip = {}
          )
        }
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/onboarding.png")
  }
}
*/
