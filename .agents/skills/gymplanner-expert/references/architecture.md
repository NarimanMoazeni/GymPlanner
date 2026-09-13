# Architecture & State Management Reference

## 1. Clean Architecture + MVI Pattern
GymPlanner follows unidirectional data flow:
```
User Action (UI Event)
        │
        ▼
GymPlannerViewModel.onEvent(event)
        │
        ▼
GymPlannerRepository (Domain & Data Orchestration)
   ├── AppDatabase (Room SQLite: WorkoutPlan, WorkoutExercise, ExerciseSet, WeightEntry)
   └── PreferencesManager (DataStore: isTutorialCompleted, language, themeMode)
        │
        ▼
MutableStateFlow<GymPlannerUiState>
        │
        ▼
GymPlannerUiState (Observed by Jetpack Compose UI)
```

## 2. Key Database Entities & DAOs
- `WorkoutPlanEntity`: id, name, dayOfWeek (0-6), muscleFocus, notes.
- `WorkoutExerciseEntity`: id, planId, exerciseName, category, orderIndex.
- `ExerciseSetEntity`: id, exerciseId, setNumber, targetReps, targetWeight, actualReps, actualWeight, isCompleted.
- `WeightEntryEntity`: id, timestamp, weightKg, notes.

## 3. DataStore Preferences
- `KEY_TUTORIAL_COMPLETED`: Boolean flag preventing re-showing onboarding/spotlight tour after initial completion. Replayable from `SettingsScreen`.
- `KEY_APP_LANGUAGE`: "fa" (Persian) or "en" (English).
- `KEY_THEME_MODE`: "DARK", "LIGHT", or "SYSTEM".
