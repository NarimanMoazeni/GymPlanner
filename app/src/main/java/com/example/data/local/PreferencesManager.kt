package com.example.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.WeightUnit
import com.example.ui.locale.AppLanguage
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "fitplan_preferences")

data class AppPreferences(
    val language: AppLanguage = AppLanguage.PERSIAN,
    val themeMode: ThemeMode = ThemeMode.DARK,
    val weightUnit: WeightUnit = WeightUnit.KG,
    val workoutReminderEnabled: Boolean = true,
    val lastBackupTimestamp: Long = 0L,
    val lastBackupSizeKb: Int = 0,
    val weeklyGoalCustomText: String = "",
    val userWeightKg: Float = 67.0f,
    val userHeightCm: Float = 187.0f,
    val weightHistoryRaw: String = ""
) {
    val weeklyGoal: String
        get() = weeklyGoalCustomText
}

class PreferencesManager(private val context: Context) {

    private object Keys {
        val LANGUAGE = stringPreferencesKey("language")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val WEIGHT_UNIT = stringPreferencesKey("weight_unit")
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val LAST_BACKUP_TIME = longPreferencesKey("last_backup_time")
        val LAST_BACKUP_SIZE = intPreferencesKey("last_backup_size")
        val WEEKLY_GOAL = stringPreferencesKey("weekly_goal")
        val USER_WEIGHT = floatPreferencesKey("user_weight")
        val USER_HEIGHT = floatPreferencesKey("user_height")
        val WEIGHT_HISTORY = stringPreferencesKey("weight_history")
    }

    val preferencesFlow: Flow<AppPreferences> = context.dataStore.data.map { prefs ->
        val langStr = prefs[Keys.LANGUAGE] ?: "fa"
        val language = if (langStr == "en") AppLanguage.ENGLISH else AppLanguage.PERSIAN

        val themeStr = prefs[Keys.THEME_MODE] ?: ThemeMode.DARK.name
        val themeMode = try {
            ThemeMode.valueOf(themeStr)
        } catch (_: Exception) {
            ThemeMode.DARK
        }

        val unitStr = prefs[Keys.WEIGHT_UNIT] ?: WeightUnit.KG.name
        val weightUnit = try {
            WeightUnit.valueOf(unitStr)
        } catch (_: Exception) {
            WeightUnit.KG
        }

        val reminder = prefs[Keys.REMINDER_ENABLED] ?: true
        val backupTime = prefs[Keys.LAST_BACKUP_TIME] ?: 0L
        val backupSize = prefs[Keys.LAST_BACKUP_SIZE] ?: 0
        val goalText = prefs[Keys.WEEKLY_GOAL] ?: ""
        val weight = prefs[Keys.USER_WEIGHT] ?: 67.0f
        val height = prefs[Keys.USER_HEIGHT] ?: 187.0f
        val history = prefs[Keys.WEIGHT_HISTORY] ?: ""

        AppPreferences(
            language = language,
            themeMode = themeMode,
            weightUnit = weightUnit,
            workoutReminderEnabled = reminder,
            lastBackupTimestamp = backupTime,
            lastBackupSizeKb = backupSize,
            weeklyGoalCustomText = goalText,
            userWeightKg = weight,
            userHeightCm = height,
            weightHistoryRaw = history
        )
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { prefs ->
            prefs[Keys.LANGUAGE] = language.code
        }
    }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = themeMode.name
        }
    }

    suspend fun setWeightUnit(unit: WeightUnit) {
        context.dataStore.edit { prefs ->
            prefs[Keys.WEIGHT_UNIT] = unit.name
        }
    }

    suspend fun setReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.REMINDER_ENABLED] = enabled
        }
    }

    suspend fun setWeeklyGoal(goal: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.WEEKLY_GOAL] = goal
        }
    }

    suspend fun updateBackupInfo(timestamp: Long, sizeKb: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.LAST_BACKUP_TIME] = timestamp
            prefs[Keys.LAST_BACKUP_SIZE] = sizeKb
        }
    }

    suspend fun setUserWeight(weight: Float) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_WEIGHT] = weight
        }
    }

    suspend fun setUserHeight(height: Float) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_HEIGHT] = height
        }
    }

    suspend fun logWeightEntry(weight: Float, dateKey: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_WEIGHT] = weight
            val currentHistory = prefs[Keys.WEIGHT_HISTORY] ?: ""
            val newEntry = "$dateKey:$weight"
            val updated = if (currentHistory.isBlank()) newEntry else "$currentHistory;$newEntry"
            prefs[Keys.WEIGHT_HISTORY] = updated
        }
    }
}
